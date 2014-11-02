package metro.k.cover.tutorial;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import metro.k.cover.R;
import metro.k.cover.Utilities;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

		@Override
		public void run() {
			final Resources res = getResources();
			HttpParams params = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(params,
					res.getInteger(R.integer.http_connection_timeout_msec));
			HttpConnectionParams.setSoTimeout(params,
					res.getInteger(R.integer.http_so_timeout_msec));
			DefaultHttpClient httpClient = new DefaultHttpClient(params);

			HttpGet request = new HttpGet(mUrl);
			try {
				httpClient.execute(request,
						new ResponseHandler<List<String>>() {
							@SuppressWarnings("unchecked")
							@Override
							public List<String> handleResponse(
									HttpResponse response)
									throws ClientProtocolException, IOException {
								if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
									ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
									response.getEntity().writeTo(outputStream);
									String data = outputStream.toString();
									try {
										JSONObject rootObject = new JSONObject(
												data);
										mStationList[mPosition].add(rootObject);
									} catch (JSONException e) {
									} catch (Exception e) {
									}
								}
								return null;
							}
						});
			} catch (IOException e) {
			} catch (Exception e) {
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
		final String key = "";
		for (int i = 0; i < size; i++) {
			requestList.add(header + "?rdf:type=odpt:Railway="
					+ railways.get(i) + ":acl:consumerKey=" + key);
		}
		return requestList;
	}
}
