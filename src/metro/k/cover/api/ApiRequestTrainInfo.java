package metro.k.cover.api;

import java.io.IOException;

import metro.k.cover.R;
import metro.k.cover.TrainDBProvider;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

public class ApiRequestTrainInfo {

	private Context mContext;
	private ContentValues mContentValues;
	private String mRequestRailway;
	private String mRequestStation;

	public ApiRequestTrainInfo(Context context) {
		mContext = context;
	}

	public void requestTrainInfo(final String railway, final String station) {
		mRequestRailway = railway;
		mRequestStation = station;
		if (isNetworkConnected()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					request();
				}
			}).start();
		}
	}

	private boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(mContext.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (cm == null || networkInfo.isConnected() == false) {
			return false;
		} else {
			return true;
		}
	}

	private void request() {
		Log.v("test", "requestTrainInfo()");
		Uri.Builder builder = new Uri.Builder();
		builder.scheme("https");
		builder.encodedAuthority("api.tokyometroapp.jp");
		builder.path("/api/v2/datapoints/");
		
		builder.appendQueryParameter("rdf:type", "odpt:StationTimetable");
		builder.appendQueryParameter("odpt:station", "odpt.Station:TokyoMetro.Ginza.Shibuya"); //test
		builder.appendQueryParameter("acl:consumerKey", mContext.getString(R.string.api_id));
		HttpGet request = new HttpGet(builder.build().toString());
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			String result = httpClient.execute(request, new ResponseHandler<String>() {
				@Override
				public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
					switch (response.getStatusLine().getStatusCode()) {
					case HttpStatus.SC_OK:
						return EntityUtils.toString(response.getEntity(), "UTF-8");
					default:
						throw new RuntimeException("");
					}
				}
			});
			parse(result);
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private void parse(String response) {
		Log.v("test", "parseTrainInfo()");
		try {
			JSONArray rootArray = new JSONArray(response);
			final int rootArrayLength = rootArray.length();
			for (int i = 0; i < rootArrayLength; i++) {
				JSONObject trainObject = rootArray.getJSONObject(i);
				final String sameAs = trainObject.getString("owl:sameAs");
				final String trainType = trainObject.getString("odpt:trainType");
				final String terminalStation = trainObject.getString("odpt:terminalStation");

				if (trainObject.has("odpt:weekdays")) {
					JSONArray weekdaysArray = trainObject.getJSONArray("odpt:weekdays");
					final int weekdaysArrayLength = weekdaysArray.length();
					for (int j = 0; j < weekdaysArrayLength; j++) {
						JSONObject weekdaysObject = weekdaysArray.getJSONObject(j);
						insert(sameAs, trainType, terminalStation, TrainDBProvider.DayType.WEEKDAYS, weekdaysObject);
					}
				}
				if (trainObject.has("odpt:saturdays")) {
					JSONArray saturdaysArray = trainObject.getJSONArray("odpt:saturdays");
					final int saturdaysArrayLength = saturdaysArray.length();
					for (int j = 0; j < saturdaysArrayLength; j++) {
						JSONObject saturdaysObject = saturdaysArray.getJSONObject(j);
						insert(sameAs, trainType, terminalStation, TrainDBProvider.DayType.SATURDAYS, saturdaysObject);
					}
				}
				if (trainObject.has("odpt:holidays")) {
					JSONArray holidaysArray = trainObject.getJSONArray("odpt:holidays");
					final int holidaysArrayLength = holidaysArray.length();
					for (int j = 0; j < holidaysArrayLength; j++) {
						JSONObject holidaysObject = holidaysArray.getJSONObject(j);
						insert(sameAs, trainType, terminalStation, TrainDBProvider.DayType.HOLIDAYS, holidaysObject);
					}
				}
			}
			Log.v("test", "parse finish");
			//requestRailwayInfo();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void insert(String sameAs, String trainType, String terminalStation, int dayType, JSONObject timetableObject) {
		if (!timetableObject.has("odpt:departureTime")) {
			return;
		}
		try {
			final String departureStation = timetableObject.getString("odpt:departureStation");
			if (!departureStation.equals("odpt.Station:TokyoMetro.Ginza.Shibuya")) {
				return;
			}
			final String index = sameAs + departureStation;
			if (indexExists(index)) {
				return;
			}
			final String departureTime = timetableObject.getString("odpt:departureTime");
			String[] s = departureTime.split(":");
			int hour = Integer.parseInt(s[0]);
			int minute = Integer.parseInt(s[1]);
			final int time = hour * 100 + minute;
			ContentValues values = getContentValues();
			values.put(TrainDBProvider.Columns.TABLE_INDEX, index);
			values.put(TrainDBProvider.Columns.TRAIN_TYPE, trainType);
			values.put(TrainDBProvider.Columns.TERMINAL_STATION, terminalStation);
			values.put(TrainDBProvider.Columns.DAY_TYPE, dayType);
			values.put(TrainDBProvider.Columns.TIME, time);
			values.put(TrainDBProvider.Columns.HOUR, hour);
			values.put(TrainDBProvider.Columns.MINUTE, minute);
			values.put(TrainDBProvider.Columns.STATION_CODE, departureStation);
			mContext.getContentResolver().insert(TrainDBProvider.CONTENT_URI, values);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean indexExists(String index) {
		String[] projection = { TrainDBProvider.Columns.TABLE_INDEX };
		String selection = TrainDBProvider.Columns.TABLE_INDEX + " = ?";
		String[] args = { index };
		Cursor cursor = mContext.getContentResolver().query(TrainDBProvider.CONTENT_URI, projection, selection, args, null);
		int count = cursor.getCount();
		final boolean ret = (count > 0 ? true : false);
//		cursor.moveToNext();
//		final int stationIndex = cursor.getInt(0);
//		final boolean ret = (stationIndex > -1 ? true : false);
		cursor.close();
		return ret;
	}

	private ContentValues getContentValues() {
		if (mContentValues == null) {
			mContentValues = new ContentValues();
		} else {
			mContentValues.clear();
		}
		return mContentValues;
	}
}
