package metro.k.cover;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public final class Utilities {

	// パターンセキュリティの最小の石数
	public static final int PATTERN_MINIMUM_LENGTH = 4;

	// パターンロック横画面対応時のStateを覚えておくためのKey
	public static final String CONFIGURATION_STATE_STATE = "lock_pattern_configuration_state_state";
	public static final String CONFIGURATION_STATE_PATTERN = "lock_pattern_configuration_state_pattern";

	// パターンロック画面に設定画面からきたかどうかの判定のKey
	public static final String KEY_PATTERN_IS_FROM_SETTING = "lock_pattern_from_setting";

	// パスワードロック画面に設定画面からきたかどうかの判定のKey
	public static final String KEY_PASSWORD_IS_FROM_SETTING = "lock_password_from_setting";

	// Homeeの壁紙を取得する
	public static final String INTENT = "com.cfinc.launcehr2.THEMES";
	public static final String ACTION = "android.intent.category.DEFAULT";

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
	 * Buttonに独自フォントを入れる
	 * 
	 * @param button
	 * @param am
	 * @param res
	 */
	public static void setFontButtonView(final Button button,
			final AssetManager am, final Resources res) {
		if (button == null || am == null || res == null) {
			return;
		}

		button.setTypeface(Typeface.createFromAsset(am,
				res.getString(R.string.font_free_wing)));
	}

	/**
	 * EditTextに独自フォントを入れる
	 * 
	 * @param et
	 * @param am
	 * @param res
	 */
	public static void setFontEditTextView(final EditText et,
			final AssetManager am, final Resources res) {
		if (et == null || am == null || res == null) {
			return;
		}

		et.setTypeface(Typeface.createFromAsset(am,
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

	/**
	 * エラー発生時に共通エラーを発生させるstartActivity()
	 * 
	 * @param intent
	 * @param context
	 */
	public static void startActivitySafely(final Intent intent,
			final Context context) {
		if (intent == null || context == null) {
			showErrorCommonToast(context);
			return;
		}

		try {
			context.startActivity(intent);
			return;
		} catch (Exception e) {
		}

		showErrorCommonToast(context);
	}

	/**
	 * 共通エラーのトースト表示
	 * 
	 * @param context
	 */
	public static void showErrorCommonToast(final Context context) {
		try {
			Toast.makeText(context,
					context.getResources().getString(R.string.common_err),
					Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
	}

	/**
	 * Homee壁紙きせかえの壁紙のテーマアプリパッケージのリストを取得する
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getHomeeWallpapers(final Context context) {
		Intent intent = new Intent(INTENT);
		intent.addCategory(ACTION);

		final PackageManager pm = context.getPackageManager();
		if (pm == null) {
			return null;
		}

		List<ResolveInfo> themes = new ArrayList<ResolveInfo>();
		try {
			themes = pm.queryIntentActivities(intent, 0);
		} catch (Exception e) {
		}
		ThemeComparator c = new ThemeComparator();
		Collections.sort(themes, c);

		final int size = themes.size();
		ArrayList<String> packageList = new ArrayList<String>();
		if (size == 0) {
			return packageList;
		}

		for (int i = 0; i < size; i++) {
			String appPackageName = (themes.get(i)).activityInfo.packageName
					.toString();
			packageList.add(appPackageName);
		}
		return packageList;
	}

	/**
	 * Homeeのテーマパッケージをソートする
	 * 
	 * @author kohirose
	 * 
	 */
	public static class ThemeComparator implements Comparator<ResolveInfo> {
		@Override
		public int compare(ResolveInfo o1, ResolveInfo o2) {
			final File f_1 = new File(
					o1.activityInfo.applicationInfo.publicSourceDir);
			final File f_2 = new File(
					o2.activityInfo.applicationInfo.publicSourceDir);
			final Integer value_1 = (int) f_1.lastModified();
			final Integer value_2 = (int) f_2.lastModified();
			final int result = value_2.compareTo(value_1);
			return result;
		}
	}

	/**
	 * 指定のアプリがインストール済みかどうか
	 * 
	 * @param context
	 * @param pkgname
	 * @return
	 */
	public static boolean findInstallApp(final Context context,
			final String pkgname) {
		final PackageManager pm = context.getPackageManager();
		if (pm == null)
			return false;

		final List<ApplicationInfo> list = pm.getInstalledApplications(0);
		if (list == null)
			return false;
		final int size = list.size();
		if (size == 0)
			return false;

		for (int i = 0; i < size; i++) {
			if (list.get(i).packageName.equals(pkgname)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 画面サイズを取得する
	 * 
	 * @param context
	 * @return
	 */
	public static int[] getWindowSize(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display disp = wm.getDefaultDisplay();
		Point size = new Point();
		disp.getSize(size);
		final int mX = size.x;
		final int mY = size.y;
		final int[] s = { mX, mY };
		return s;
	}

	/**
	 * Drawableのリサイズ
	 * 
	 * @param image
	 * @param width
	 * @param height
	 * @param c
	 * @return
	 */
	public static Drawable resizeFit(Drawable image, int width, int height,
			Context c) {
		Bitmap b = ((BitmapDrawable) image).getBitmap();
		Bitmap bitmapResized = null;
		try {
			bitmapResized = Bitmap.createScaledBitmap(b, width, height, true);
		} catch (OutOfMemoryError oom) {
			bitmapResized = b;
		}
		return new BitmapDrawable(c.getResources(), bitmapResized);
	}

	private static KeyguardManager mKeyguardManager;
	private static KeyguardManager.KeyguardLock mKeyguardLock;

	/**
	 * キーガード(OS標準の画面ロック)を無効にする
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public static void disableKeyguard(Context context) {
		if (context == null) {
			return;
		}

		if (mKeyguardManager == null) {
			mKeyguardManager = (KeyguardManager) context
					.getApplicationContext().getSystemService(
							Context.KEYGUARD_SERVICE);
			mKeyguardLock = mKeyguardManager.newKeyguardLock("LockUtil");
		}

		try {
			mKeyguardLock.disableKeyguard();
		} catch (SecurityException se) {
		}
	}

	/**
	 * キーガード(OS標準の画面ロック)を有効にする
	 */
	@SuppressWarnings("deprecation")
	public static void enableKeyguard(final Context context) {
		if (context == null) {
			return;
		}

		if (mKeyguardLock != null) {
			mKeyguardLock.reenableKeyguard();
			mKeyguardManager = null;
			mKeyguardLock = null;
		} else {
			if (mKeyguardManager == null) {
				mKeyguardManager = (KeyguardManager) context
						.getApplicationContext().getSystemService(
								Context.KEYGUARD_SERVICE);
			}

			if (mKeyguardLock == null) {
				try {
					mKeyguardLock = mKeyguardManager
							.newKeyguardLock("LockUtil");
					mKeyguardLock.reenableKeyguard();
				} catch (SecurityException e) {
				}
				mKeyguardManager = null;
				mKeyguardLock = null;
			}
		}
	}

	public static void disableKeyguardWindow(final Activity activity) {
		try {
			Window window = activity.getWindow();
			window.setFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED,
					WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			window.setFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
					WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		} catch (Exception e) {
		}
	}

	public static void enableKeyguardWindow(final Activity activity) {
		try {
			Window window = activity.getWindow();
			window.clearFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
			window.clearFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		} catch (Exception e) {
		}
	}

	/**
	 * バージョンによってsetBackground()メソッド変える
	 * 
	 * @param view
	 * @param d
	 */
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable d) {
		if (view == null || d == null) {
			return;
		}

		if (Build.VERSION.SDK_INT >= 16) {
			view.setBackground(d);
		} else {
			view.setBackgroundDrawable(d);
		}
	}
}
