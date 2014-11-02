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

	// チュートリアルを起動したかどうか
	private static final String KEY_SET_TUTORIAL_OPEND = "set_tutorial_opend";

	public static void setTutorialOpend(final Context context,
			final boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(KEY_SET_TUTORIAL_OPEND, flag);
		editor.apply();
	}

	public static boolean getTutorialOpend(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_SET_TUTORIAL_OPEND, false);
	}

	/*******************
	 * ロック画面
	 *******************/

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

	// ロック画面のセキュリティのタイプ
	private static String KEY_SET_SECURITY_TYPE = "set_security_type";

	public static void setSecurityType(final Context context, final int type) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(KEY_SET_SECURITY_TYPE, type);
		editor.apply();
	}

	public static int getSecurityType(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getInt(KEY_SET_SECURITY_TYPE, context.getResources()
				.getInteger(R.integer.lock_security_type_none));
	}

	// 現在設定されているパターンロックのパターン
	private static final String KEY_SET_CURRENT_PATTERN = "set_current_pattern";

	public static void setCurrentPattern(final Context context,
			final String pattern) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(KEY_SET_CURRENT_PATTERN, pattern);
		editor.apply();
	}

	public static String getCurrentPattern(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getString(KEY_SET_CURRENT_PATTERN, "");
	}

	// パターンロックのバイブを付けるかどうか
	private static final String KEY_SET_PATTERN_VIB = "set_pattern_vib";

	public static void setLockPatternVib(final Context context,
			final boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(KEY_SET_PATTERN_VIB, flag);
		editor.apply();
	}

	public static boolean getLockPatternVib(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_SET_PATTERN_VIB, true);
	}

	// パターンロックの軌跡を付けるかどうか
	private static final String KEY_SET_PATTERN_TRACK = "set_pattern_track";

	public static void setLockPatternTrack(final Context context,
			final boolean flag) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(KEY_SET_PATTERN_TRACK, flag);
		editor.apply();
	}

	public static boolean getLockPatternTrack(final Context context) {
		SharedPreferences sp = context.getSharedPreferences(PREFERENCE_KEY,
				Context.MODE_PRIVATE);
		return sp.getBoolean(KEY_SET_PATTERN_TRACK, true);
	}
}