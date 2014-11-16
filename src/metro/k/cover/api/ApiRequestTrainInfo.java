package metro.k.cover.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.traininfo.TrainInfo;
import metro.k.cover.traininfo.TrainInfoListener;

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

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class ApiRequestTrainInfo {

	private final int MAX_TIME_TABLE_SIZE = 10;
	private TrainInfoListener trainInfoListener;
	private Context mContext;
	private int mNow;

	public ApiRequestTrainInfo(Context context) {
		mContext = context;
	}

	public void requestTrainInfo(String station, String direction) {
		if (!Utilities.isOnline(mContext)) {
			trainInfoListener.failedToCreateTimeTable();
			return;
		}

		if (Utilities.isInvalidStr(station)
				|| Utilities.isInvalidStr(direction)) {
			trainInfoListener.failedToCreateTimeTable();
			return;
		}

		Uri.Builder builder = new Uri.Builder();
		builder.scheme("https");
		builder.encodedAuthority("api.tokyometroapp.jp");
		builder.path("/api/v2/datapoints/");

		builder.appendQueryParameter("rdf:type", "odpt:StationTimetable");
		builder.appendQueryParameter("odpt:station", station);
		if (!TextUtils.isEmpty(direction)) {
			builder.appendQueryParameter("odpt:railDirection", direction);
		}
		builder.appendQueryParameter("acl:consumerKey",
				mContext.getString(R.string.api_id));
		HttpGet request = new HttpGet(builder.build().toString());
		DefaultHttpClient httpClient = new DefaultHttpClient();
		try {
			String result = httpClient.execute(request,
					new ResponseHandler<String>() {
						@Override
						public String handleResponse(HttpResponse response)
								throws ClientProtocolException, IOException {
							switch (response.getStatusLine().getStatusCode()) {
							case HttpStatus.SC_OK:
								return EntityUtils.toString(
										response.getEntity(), "UTF-8");
							default:
								trainInfoListener.failedToCreateTimeTable();
								throw new RuntimeException("");
							}
						}
					});
			parse(result);
		} catch (Exception e) {
			trainInfoListener.failedToCreateTimeTable();
		} catch (OutOfMemoryError oom) {
			trainInfoListener.failedToCreateTimeTable();
			System.gc();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
	}

	private void parse(String response) {
		try {
			final Calendar calendar = Calendar.getInstance();
			final int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
			final int newMinute = calendar.get(Calendar.MINUTE);
			mNow = nowHour * 100 + newMinute;
			final int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

			JSONArray rootArray = new JSONArray(response);
			final int rootArrayLength = rootArray.length();
			for (int i = 0; i < rootArrayLength; i++) {
				JSONObject trainObject = rootArray.getJSONObject(i);
				if (dayOfWeek == Calendar.SATURDAY) {
					JSONArray weekdaysArray = trainObject
							.getJSONArray("odpt:saturdays");
					String railDirection = trainObject
							.getString("odpt:railDirection");
					parseChildren(weekdaysArray, railDirection);
				} else if (dayOfWeek == Calendar.SUNDAY) {
					JSONArray weekdaysArray = trainObject
							.getJSONArray("odpt:holidays");
					String railDirection = trainObject
							.getString("odpt:railDirection");
					parseChildren(weekdaysArray, railDirection);
				} else {
					JSONArray weekdaysArray = trainObject
							.getJSONArray("odpt:weekdays");
					String railDirection = trainObject
							.getString("odpt:railDirection");
					parseChildren(weekdaysArray, railDirection);
				}
			}
		} catch (JSONException e) {
			trainInfoListener.failedToCreateTimeTable();
		}
	}

	private void parseChildren(JSONArray jsonArray, String railDirection) {
		try {
			final int weekdaysArrayLength = jsonArray.length();
			ArrayList<TrainInfo> trainInfoList = new ArrayList<TrainInfo>();
			for (int j = 0; j < weekdaysArrayLength; j++) {
				JSONObject jsonObject = jsonArray.getJSONObject(j);
				final String destinationStation = jsonObject
						.getString("odpt:destinationStation");
				final String trainType = jsonObject.getString("odpt:trainType");
				final String departureTime = jsonObject
						.getString("odpt:departureTime");

				String[] s = departureTime.split(":");
				final int hour = Integer.parseInt(s[0]);
				final int minute = Integer.parseInt(s[1]);
				final int time = hour * 100 + minute;
				if (time < mNow && time > 300) {
					continue;
				}
				TrainInfo trainInfo = new TrainInfo(minute, hour,
						destinationStation, trainType, railDirection);
				trainInfoList.add(trainInfo);
				if (trainInfoList.size() > MAX_TIME_TABLE_SIZE) {
					break;
				}
			}
			trainInfoListener.completeCreateTimeTable(trainInfoList);
		} catch (JSONException e) {
			trainInfoListener.failedToCreateTimeTable();
		}
	}

	public void setListener(TrainInfoListener listener) {
		trainInfoListener = listener;
	}

	public void removeListener() {
		trainInfoListener = null;
	}
}
