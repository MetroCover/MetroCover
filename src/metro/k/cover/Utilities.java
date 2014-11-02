package metro.k.cover;

import java.util.ArrayList;

public final class Utilities {

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
}
