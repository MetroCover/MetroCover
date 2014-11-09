package metro.k.cover;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

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
		final String[] array = str.split(",", 0);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
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
	 * 指定の文言のトースト表示
	 * 
	 * @param context
	 * @param message
	 */
	public static void showToast(final Context context, final String message) {
		try {
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
		}
	}

	/**
	 * Network確認
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isOnline(Context context) {
		try {
			final ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (cm == null) {
				return false;
			}
			final NetworkInfo info = cm.getActiveNetworkInfo();
			if (info == null) {
				return false;
			}

			if (info.isConnected()) {
				return true;
			}
		} catch (SecurityException e) {
			return false;
		}

		return false;
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
