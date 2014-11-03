package metro.k.cover.tutorial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import metro.k.cover.R;
import metro.k.cover.Utilities;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ParseException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorialThird extends Fragment implements OnClickListener {

	@SuppressWarnings("rawtypes")
	private ArrayList[] mStationList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_third, null);

		final Resources res = getResources();
		final AssetManager am = getActivity().getAssets();

		// Title
		final TextView titleView = (TextView) view
				.findViewById(R.id.tutorial_third_title);
		Utilities.setFontTextView(titleView, am, res);

		// Finish
		final TextView finishView = (TextView) view
				.findViewById(R.id.tutorial_third_finish_btn);
		Utilities.setFontTextView(finishView, am, res);
		finishView.setOnClickListener(this);

		request();

		return view;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.tutorial_third_finish_btn == viewId) {
			getActivity().finish();
			return;
		}
	}

	/**
	 * エラー時の描画
	 */
	private void setErrorView() {

	}

	/**
	 * 路線から全駅名を取得する
	 */
	@SuppressWarnings("unchecked")
	private void request() {
		final ArrayList<String> urls = getRequestURLs();
		if (urls == null) {
			setErrorView();
			return;
		}

		final int size = urls.size();
		if (size == 0) {
			setErrorView();
			return;
		}

		mStationList = new ArrayList[size];
		final ArrayList<String> titles = TutorialSecond.getCheckedNameList();
		ExecutorService exec = Executors.newFixedThreadPool(size);
		for (int i = 0; i < size; i++) {
			mStationList[i] = new ArrayList<String>();
			mStationList[i].add(titles.get(i));
			exec.submit(new RequestRunnable(urls.get(i), i));
		}
	}

	/**
	 * 実際にリクエストするRunnable
	 * 
	 * @author kohirose
	 * 
	 */
	private class RequestRunnable implements Runnable {

		private String mUrl = "";
		private int mPosition = -1;

		public RequestRunnable(final String url, final int position) {
			this.mUrl = url;
			this.mPosition = position;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void run() {
			final Resources res = getResources();
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params,
					res.getInteger(R.integer.http_connection_timeout_msec));
			HttpConnectionParams.setSoTimeout(params,
					res.getInteger(R.integer.http_so_timeout_msec));

			HttpUriRequest httpRequest = new HttpGet(mUrl);
			HttpResponse httpResponse = null;

			try {
				httpResponse = httpClient.execute(httpRequest);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			String json = null;
			if (httpResponse != null
					&& httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				try {
					json = EntityUtils.toString(httpEntity);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						httpEntity.consumeContent();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			httpClient.getConnectionManager().shutdown();

			try {
				final JSONArray array = new JSONArray(json);
				final JSONObject[] stationObj = new JSONObject[array.length()];
				for (int i = 0; i < array.length(); i++) {
					stationObj[i] = array.getJSONObject(i);
				}
				JSONArray itemArray = null;
				for (int i = 0; i < stationObj.length; i++) {
					itemArray = stationObj[i].getJSONArray("odpt:stationOrder");
				}
				if (itemArray != null) {
					final int size = itemArray.length();
					for (int i = 0; i < size; i++) {
						String staticon = itemArray.getJSONObject(i).getString(
								"odpt:station");
						mStationList[mPosition].add(staticon);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				// 例外処理
			}
		}
	}

	/**
	 * APIリクエストURL取得
	 * 
	 * @return
	 */
	private ArrayList<String> getRequestURLs() {
		final Resources res = getResources();
		final ArrayList<String> railways = TutorialSecond.getCheckedList();
		if (railways == null) {
			return null;
		}

		final int size = railways.size();
		if (size == 0) {
			return null;
		}

		final ArrayList<String> requestList = new ArrayList<String>();
		final String header = res.getString(R.string.api_railways_info);
		final String key = "14f420dec5cf32d3c092fbe8a9f3ecb065e5473c9a16be79049131a7d0ea1bff";
		for (int i = 0; i < size; i++) {
			String url = header + "?rdf:type=odpt:Railway&odpt:lineCode="
					+ railways.get(i) + "&acl:consumerKey=" + key;
			requestList.add(url);
		}

		return requestList;
	}
}
