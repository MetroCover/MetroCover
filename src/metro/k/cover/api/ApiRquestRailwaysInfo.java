package metro.k.cover.api;

import java.util.ArrayList;

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
import android.graphics.drawable.Drawable;

/**
 * 列車の運行情報を取得する
 * 
 * @author kohirose
 * 
 */
public class ApiRquestRailwaysInfo {

	// URLのヘッダー
	private final String REQUEST_URL = "https://api.tokyometroapp.jp/api/v2/datapoints?rdf:type=odpt:TrainInformation&acl:consumerKey=14f420dec5cf32d3c092fbe8a9f3ecb065e5473c9a16be79049131a7d0ea1bff";

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
		HttpGet httpGet = new HttpGet(REQUEST_URL);
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
			final JSONObject[] railwaysObj = new JSONObject[array.length()];
			for (int i = 0; i < array.length(); i++) {
				railwaysObj[i] = array.getJSONObject(i);
			}
			for (int i = 0; i < railwaysObj.length; i++) {
				RailwaysInfo info = new RailwaysInfo();
				String railway = railwaysObj[i].getString("odpt:railway");
				String message = railwaysObj[i]
						.getString("odpt:trainInformationText");
				Drawable icon = RailwaysUtilities.getRailwayIcon(context,
						railway);
				String name = RailwaysUtilities.getRailwaysName(context,
						railway);
				if (target.contains(railway)) {
					info.setRailway(name);
					info.setMessage(message);
					info.setIcon(icon);
					infos.add(info);
				}
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
