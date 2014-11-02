package metro.k.cover;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreference管理クラス
 * 
 * @author kohirose
 * 
 */
public final class PreferenceCommon {

	private static final String PREFERENCE_KEY = "metro_cover_preference_key";

	// Metro Coverをロック画面に出すか出さないか
	private static final String KEY_SET_METROCOVER = "set_metro_cover";

	public static void setMetroCover(final Context context, final boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(KEY_SET_METROCOVER, flag);
		editor.apply();
	}

	public static boolean getMetroCover(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_SET_METROCOVER, false);
	}

	// 遅延情報を出す路線
	private static final String KEY_SET_RAILWAYS_INFO = "set_railways_info";

	public static void setRailwaysInfomation(final Context context,
			final String railways) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_SET_RAILWAYS_INFO, railways);
		editor.apply();
	}

	public static String getRailwaysInfomation(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getString(KEY_SET_RAILWAYS_INFO, "");
	}
}