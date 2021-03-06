package metro.k.cover;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import metro.k.cover.api.ApiRequestTrainInfo;
import metro.k.cover.api.ApiRquestRailwaysInfo;
import metro.k.cover.lock.LockRailwaysInfoAdapter;
import metro.k.cover.railways.RailwaysInfo;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.railways.Station;
import metro.k.cover.railways.StationsAdapter;
import metro.k.cover.traininfo.TrainInfo;
import metro.k.cover.traininfo.TrainInfoListener;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

public class MetroCoverApplication extends Application {

	// 全駅名リスト
	private static ArrayAdapter<Station> sStationAllListAdapter;

	// 登録している路線の遅延情報リスト
	private static ArrayAdapter<RailwaysInfo> sRailwaysInfoAdapter;
	private static String mLastUpdateTime;

	// 登録している駅の時刻情報リスト
	private static ArrayList<TrainInfo> sTrainInfoArrayList;

	// 季節
	public static int sCurrentSeason;
	public static final int SEASON_SPRING = 0;
	public static final int SEASON_SUMMER = 1;
	public static final int SEASON_AUTUMN = 2;
	public static final int SEASON_WINTER = 3;

	@Override
	public void onCreate() {
		super.onCreate();
		setCurrentSeason();
		asyncCreateRailwaysInfoList(getApplicationContext());
		createAllStationList();
	}

	/**
	 * 現在の季節を設定する
	 */
	private void setCurrentSeason() {
		Calendar cal = Calendar.getInstance();
		final int month = cal.get(Calendar.MONTH) + 1;
		if (month >= 3 && month <= 5) {
			sCurrentSeason = SEASON_SPRING;
		} else if (month >= 6 && month <= 8) {
			sCurrentSeason = SEASON_SUMMER;
		} else if (month >= 9 && month <= 11) {
			sCurrentSeason = SEASON_AUTUMN;
		} else {
			sCurrentSeason = SEASON_WINTER;
		}
	}

	/**
	 * 駅時刻表データをセットする
	 * 
	 * @param trainInfoList
	 */
	public static void setTrainInfoList(ArrayList<TrainInfo> trainInfoList) {
		if (trainInfoList == null) {
			return;
		}
		sTrainInfoArrayList = trainInfoList;
	}

	/**
	 * セットしてある駅時刻表データを取得する
	 * 
	 * @return
	 */
	public static ArrayList<TrainInfo> getTrainInfoList() {
		return sTrainInfoArrayList;
	}

	/**
	 * 駅時刻表データリストをThread立てて取得する
	 * 
	 * @param context
	 */
	synchronized public static void asyncCreateTrainInfoList(
			final Context context) {
		new Thread("asyncCreateTrainInfoList") {
			@Override
			public void run() {
				syncCreateTrainInfoList(context);
			}
		}.start();
	}

	/**
	 * 駅時刻表リストを取得する. 同期的に扱うが呼び出しもとでサブスレッドを立てて使用すること
	 * 
	 * @param context
	 */
	synchronized public static void syncCreateTrainInfoList(
			final Context context) {
		if (context == null) {
			return;
		}
		if (!Utilities.isOnline(context)) {
			return;
		}

		final String title = PreferenceCommon.getStationNameForAPI(context);
		final String direction = PreferenceCommon.getTrainDirection(context);
		if (Utilities.isInvalidStr(title) || Utilities.isInvalidStr(direction)) {
			if (sTrainInfoArrayList != null) {
				sTrainInfoArrayList.clear();
			}
			return;
		}
		ApiRequestTrainInfo request = new ApiRequestTrainInfo(context);
		request.requestTrainInfo(title, direction);
		request.setListener(new TrainInfoListener() {
			@Override
			public void failedToCreateTimeTable() {

			}

			@Override
			public void completeCreateTimeTable(ArrayList<TrainInfo> timetable) {
				PreferenceCommon.setTimeLoadedTrainTimeTable(context,
						System.currentTimeMillis());
				setTrainInfoList(timetable);
			}
		});
	}

	/**
	 * 遅延情報の最終更新日時
	 * 
	 * @return
	 */
	public static String getLastUpdateTime() {
		return mLastUpdateTime == null ? "-" : mLastUpdateTime;
	}

	/**
	 * 登録した遅延情報リストを取得する
	 * 
	 * @return
	 */
	public static ArrayAdapter<RailwaysInfo> getRailwaysInfoList() {
		return sRailwaysInfoAdapter;
	}

	/**
	 * 遅延情報リストを内部でThread立てて取得する
	 * 
	 * @param context
	 */
	synchronized public static void asyncCreateRailwaysInfoList(
			final Context context) {
		new Thread("asyncCreateRailwaysInfoList") {
			@Override
			public void run() {
				syncCreateRailwaysInfoList(context);
			}
		}.start();
	}

	/**
	 * 遅延情報リストを取得する. 同期的に扱うが呼び出しもとでサブスレッドを立てて使用すること
	 * 
	 * @param context
	 */
	@SuppressLint("SimpleDateFormat")
	synchronized public static void syncCreateRailwaysInfoList(
			final Context context) {
		if (!Utilities.isOnline(context)) {
			return;
		}
		final String str = PreferenceCommon.getRailwaysNameForAPI(context);
		if (Utilities.isInvalidStr(str)) {
			if (sRailwaysInfoAdapter != null) {
				sRailwaysInfoAdapter.clear();
			}
			return;
		}

		ApiRquestRailwaysInfo info = ApiRquestRailwaysInfo.getInstance();
		info.openConnection();
		final ArrayList<String> list = Utilities.getSplitStr(str);
		final ArrayList<RailwaysInfo> infos = info.getApiRquestRailwaysInfo(
				context, list);
		info.closeConnection();
		if (infos == null) {
			if (sRailwaysInfoAdapter != null) {
				sRailwaysInfoAdapter.clear();
			}
			return;
		}
		sRailwaysInfoAdapter = new LockRailwaysInfoAdapter(context,
				R.layout.lock_railways_info_at);
		final int size = infos.size();
		if (size > 0) {
			for (int i = 0; i < size; i++) {
				sRailwaysInfoAdapter.add(infos.get(i));
			}
			Date date = new Date();
			SimpleDateFormat sdf = null;
			if (Locale.getDefault().equals(Locale.JAPAN)) {
				sdf = new SimpleDateFormat("yyyy'年'MM'月'dd'日'　kk'時'mm'分'ss'秒'");
			} else {
				sdf = new SimpleDateFormat("MM'/'dd'/'yyyy　kk':'mm");
			}
			mLastUpdateTime = sdf.format(date);
		}
	}

	/**
	 * 全ての駅情報を取得する
	 * 
	 * @return
	 */
	public static ArrayAdapter<Station> getAllStationList() {
		return sStationAllListAdapter;
	}

	/**
	 * アプリ起動時に全駅名リストを作成しておく
	 */
	private void createAllStationList() {
		if (sStationAllListAdapter != null) {
			if (sStationAllListAdapter.getCount() != 0) {
				return;
			}
		}

		new Thread("createAllStationList") {
			@Override
			public void run() {
				final ArrayList<String> ids = RailwaysUtilities
						.getAllRailwaysCode();
				if (ids == null) {
					return;
				}
				final int size = ids.size();
				if (size == 0) {
					return;
				}

				sStationAllListAdapter = new StationsAdapter(
						getApplicationContext(),
						R.layout.list_icon_title_radio_at);
				final ArrayList<String> names = RailwaysUtilities
						.getAllRailwaysName(getApplicationContext());
				for (int i = 0; i < size; i++) {
					String code = ids.get(i);
					List<String> stations = RailwaysUtilities.getStationList(
							getApplicationContext(), code);
					List<String> apiNames = RailwaysUtilities
							.getStationListForAPI(getApplicationContext(), code);
					if (stations == null || apiNames == null) {
						continue;
					}

					String railway = names.get(i);
					ArrayList<Drawable> icons = RailwaysUtilities
							.getStationIconList(getApplicationContext(), code);
					for (int j = 0; j < stations.size(); j++) {
						if (j == 0) {
							sStationAllListAdapter.add(new Station(railway,
									railway, null, ""));
						}
						sStationAllListAdapter
								.add(new Station(railway, stations.get(j),
										icons.get(j), apiNames.get(j)));
					}
				}
			}
		}.start();
	}
}
