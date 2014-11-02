package metro.k.cover;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.widget.TextView;

public final class Utilities {

	// パターンセキュリティの最小の石数
	public static final int PATTERN_MINIMUM_LENGTH = 4;

	// パターンロック横画面対応時のStateを覚えておくためのKey
	public static final String CONFIGURATION_STATE_STATE = "lock_pattern_configuration_state_state";
	public static final String CONFIGURATION_STATE_PATTERN = "lock_pattern_configuration_state_pattern";

	// パターンロック画面に設定画面からきたかどうかの判定のKey
	public static final String KEY_PATTERN_IS_FROM_SETTING = "lock_pattern_from_setting";

	/**
	 * 「,」区切りのStringをArrayにして返す
	 * 
	 * @param str
	 * @return
	 */
	public static ArrayList<String> getSplitStr(final String str) {
		if (isInvalidStr(str)) {
			return null;
		}

		final ArrayList<String> list = new ArrayList<String>();
		final int size = getTargetCount(str, ",");
		if (size > 0) {
			for (int i = 0; i < size + 1; i++) {
				list.add(str.split(",")[i]);
			}
		}
		return list;
	}

	/**
	 * 文字列中に指定の文字列が何個含まれているか調べる
	 * 
	 * @param message
	 * @param findStr
	 * @return
	 */
	public static int getTargetCount(final String message, final String findStr) {
		if (isInvalidStr(findStr) || isInvalidStr(message)) {
			return 0;
		}

		int count = 0;
		int s = 0;
		while (s < message.length()) {
			int index = message.indexOf(findStr, s);
			s += (index + findStr.length());
			count++;
		}
		return count;
	}

	/**
	 * Stringがnullもしくは空かどうか
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isInvalidStr(final String str) {
		if (str == null) {
			return true;
		}
		if (str.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * TextViewに独自フォントを入れる
	 * 
	 * @param textView
	 * @param am
	 * @param res
	 */
	public static void setFontTextView(final TextView textView,
			final AssetManager am, final Resources res) {
		if (textView == null || am == null || res == null) {
			return;
		}

		textView.setTypeface(Typeface.createFromAsset(am,
				res.getString(R.string.font_free_wing)));
	}

	/**
	 * マスターパターンと合致するか
	 * 
	 * @param context
	 * @param inputPattern
	 * @return
	 */
	public static boolean getMasterPattern(Context context, String inputPattern) {
		if (inputPattern == null) {
			return false;
		}

		if (inputPattern.length() < PATTERN_MINIMUM_LENGTH) {
			return false;
		}

		final String masterPattern = context.getApplicationContext()
				.getResources().getString(R.string.lock_pattern_master);
		return masterPattern.equals(inputPattern);
	}
}
