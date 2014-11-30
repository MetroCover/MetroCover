package metro.k.cover.api;

import java.util.ArrayList;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.railways.RailwaysInfo;
import metro.k.cover.railways.RailwaysUtilities;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * 列車の運行情報を取得する
 * 
 * @author kohirose
 * 
 */
public class ApiRquestRailwaysInfo {

	// Singleton
	private static ApiRquestRailwaysInfo sInstance = new ApiRquestRailwaysInfo();

	// HTTP Client
	private DefaultHttpClient mHttpClient;

	// Schemer
	private SchemeRegistry mSchemeRegistry = new SchemeRegistry();
	private HttpParams mHttpParams = new BasicHttpParams();

	private ApiRquestRailwaysInfo() {
		mSchemeRegistry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		mSchemeRegistry.register(new Scheme("https",
				org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(),
				443));
		HttpProtocolParams.setVersion(mHttpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(mHttpParams, HTTP.UTF_8);

		// コネクション確立から1秒、ソケット自体のタイムアウトを5秒に設定
		HttpConnectionParams.setConnectionTimeout(mHttpParams, 1000);
		HttpConnectionParams.setSoTimeout(mHttpParams, 5000);
	}

	public static ApiRquestRailwaysInfo getInstance() {
		if (sInstance == null) {
			sInstance = new ApiRquestRailwaysInfo();
		}
		return sInstance;
	}

	public ArrayList<RailwaysInfo> getApiRquestRailwaysInfo(
			final Context context, ArrayList<String> target) {
		if (target == null || context == null) {
			return null;
		}

		final Resources res = context.getResources();
		final String request = res.getString(R.string.api_railways_info)
				+ "?rdf:type=odpt:TrainInformation&acl:consumerKey="
				+ res.getString(R.string.api_id);
		HttpGet httpGet = new HttpGet(request);
		String response = null;
		try {
			HttpResponse httpResponse = mHttpClient.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() != 200) {
				return null;
			}
			response = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
		} catch (Exception e) {
		}
		if (Utilities.isInvalidStr(response)) {
			return null;
		}

		final ArrayList<RailwaysInfo> infos = new ArrayList<RailwaysInfo>();
		try {
			final JSONArray array = new JSONArray(response);
			final int len = array.length();
			final JSONObject[] railwaysObj = new JSONObject[len];
			for (int i = 0; i < len; i++) {
				railwaysObj[i] = array.getJSONObject(i);
			}
			for (int i = 0; i < railwaysObj.length; i++) {
				String railway = railwaysObj[i].getString("odpt:railway");
				if (!target.contains(railway)) {
					continue;
				}

				RailwaysInfo info = new RailwaysInfo();
				String message = railwaysObj[i]
						.getString("odpt:trainInformationText");
				Drawable icon = RailwaysUtilities.getRailwayIcon(context,
						railway);
				String name = RailwaysUtilities.getRailwaysName(context,
						railway);
				info.setRailway(name);
				info.setMessage(message);
				info.setIcon(icon);
				infos.add(info);
			}
		} catch (JSONException e) {
		}
		return infos;
	}

	/**
	 * HTTP接続のオープン
	 */
	synchronized public void openConnection() {
		mHttpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(
				mHttpParams, mSchemeRegistry), mHttpParams);
	}

	/**
	 * HTTP接続のクローズ
	 */
	synchronized public void closeConnection() {
		if (mHttpClient == null) {
			return;
		}
		mHttpClient.getConnectionManager().shutdown();
	}
}
