package metro.k.cover;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import metro.k.cover.api.ApiRquestRailwaysInfo;
import metro.k.cover.lock.LockRailwaysInfoAdapter;
import metro.k.cover.railways.RailwaysInfo;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.railways.Station;
import metro.k.cover.railways.StationsAdapter;
import android.annotation.SuppressLint;
import android.app.Application;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

public class MetroCoverApplication extends Application {

	// 全駅名リスト
	public static ArrayAdapter<Station> sStationAllListAdapter;

	// 登録している路線の遅延情報リスト
	public static ArrayAdapter<RailwaysInfo> sRailwaysInfoAdapter;
	private static String mLastUpdateTime;

	@Override
	public void onCreate() {
		super.onCreate();
		createRailwaysInfoList();
		createAllStationList();
	}

	/**
	 * 遅延情報の最終更新日時
	 * 
	 * @return
	 */
	public static String getLastUpdateTime() {
		return mLastUpdateTime == null ? "-" : mLastUpdateTime;
	}

	@SuppressLint("SimpleDateFormat")
	public void createRailwaysInfoList() {
		new Thread("createRailwaysInfoList") {
			@Override
			public void run() {
				if (!Utilities.isOnline(getApplicationContext())) {
					return;
				}
				ApiRquestRailwaysInfo info = ApiRquestRailwaysInfo
						.getInstance();
				info.openConnection();
				final String str = PreferenceCommon
						.getRailwaysResponseName(getApplicationContext());
				final ArrayList<String> list = Utilities.getSplitStr(str);
				final ArrayList<RailwaysInfo> infos = info
						.getApiRquestRailwaysInfo(getApplicationContext(), list);
				info.closeConnection();
				if (infos == null) {
					if (sRailwaysInfoAdapter != null) {
						sRailwaysInfoAdapter.clear();
					}
					return;
				}
				sRailwaysInfoAdapter = new LockRailwaysInfoAdapter(
						getApplicationContext(), R.layout.lock_railways_info_at);
				final int size = infos.size();
				if (size > 0) {
					for (int i = 0; i < size; i++) {
						sRailwaysInfoAdapter.add(infos.get(i));
					}
					Date date = new Date();
					SimpleDateFormat sdf = null;
					if (Locale.getDefault().equals(Locale.JAPAN)) {
						sdf = new SimpleDateFormat(
								"yyyy'年'MM'月'dd'日'　kk'時'mm'分'ss'秒'");
					} else {
						sdf = new SimpleDateFormat("MM'/'dd'/'yyyy　kk':'mm");
					}
					mLastUpdateTime = sdf.format(date);
				}
			}
		}.start();
	}

	/**
	 * アプリ起動時に遅延情報リストを取得しておく
	 */
	/**
	 * アプリ起動時に全駅名リストを作成しておく
	 */
	private void createAllStationList() {
		new Thread("createAllStationList") {
			@Override
			public void run() {
				final ArrayList<String> checkedIdList = RailwaysUtilities
						.getAllRailwaysCode();
				final ArrayList<String> checkedNameList = RailwaysUtilities
						.getAllRailwaysName(getApplicationContext());
				if (checkedIdList == null) {
					return;
				}
				final int size = checkedIdList.size();
				if (size == 0) {
					return;
				}

				sStationAllListAdapter = new StationsAdapter(
						getApplicationContext(),
						R.layout.list_icon_title_radio_at);
				for (int i = 0; i < size; i++) {
					String code = checkedIdList.get(i);
					String railway = checkedNameList.get(i);
					List<String> stations = RailwaysUtilities.getStationList(
							getApplicationContext(), code);
					ArrayList<Drawable> icons = RailwaysUtilities
							.getStationIconList(getApplicationContext(), code);
					if (stations == null) {
						continue;
					}
					for (int j = 0; j < stations.size(); j++) {
						if (j == 0) {
							sStationAllListAdapter.add(new Station(railway,
									railway, null));
						}
						sStationAllListAdapter.add(new Station(railway,
								stations.get(j), icons.get(j)));
					}
				}
			}
		}.start();
	}
}
