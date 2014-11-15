package metro.k.cover;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public final class Utilities {

	private static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
	
	public static boolean isOver24HourTimeTableLoaded(Context context) {
		long timeLoadedTimeTabel = PreferenceCommon.getTimeLoadedTrainTimeTable(context);
		if (System.currentTimeMillis() - timeLoadedTimeTabel  > Utilities.ONE_DAY_MILLIS) {
			return true;
		} else {
			return false;
		}
	}
	
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
	 * パッケージ名から他のアプリを起動する
	 * 
	 * @param context
	 * @param pkg
	 */
	public static void startOtherApp(final Context context, final String pkg) {
		final PackageManager pm = context.getPackageManager();
		if (pm == null) {
			showErrorCommonToast(context);
			return;
		}

		Intent intent = pm.getLaunchIntentForPackage(pkg);
		startActivitySafely(intent, context);
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

	/**
	 * バージョンによってsetBackground()メソッド変える
	 * 
	 * @param context
	 * @param view
	 * @param bmp
	 */
	@SuppressLint("NewApi")
	public static void setBackground(final Context context, View view,
			Bitmap bmp) {
		if (view == null || bmp == null) {
			return;
		}

		if (Build.VERSION.SDK_INT >= 16) {
			view.setBackground(new BitmapDrawable(context.getResources(), bmp));
		} else {
			view.setBackgroundDrawable(new BitmapDrawable(context
					.getResources(), bmp));
		}
	}

	/**
	 * 現在の季節を取得する
	 * 
	 * @return
	 */
	public static int getCurrentSeason() {
		return MetroCoverApplication.sCurrentSeason;
	}

	/**
	 * 季節に合わせた背景をこっそり入れる
	 * 
	 * @param context
	 * @param view
	 */
	public static void setSeasonsBackground(final Context context,
			final View view) {
		if (context == null || view == null) {
			return;
		}

		final int season = getCurrentSeason();
		Drawable bg = null;
		final Resources res = context.getResources();
		switch (season) {
		case MetroCoverApplication.SEASON_SPRING:
			bg = res.getDrawable(R.drawable.bg_spring);
			break;
		case MetroCoverApplication.SEASON_SUMMER:
			bg = res.getDrawable(R.drawable.bg_summer);
			break;
		case MetroCoverApplication.SEASON_AUTUMN:
			bg = res.getDrawable(R.drawable.bg_autumn);
			break;
		case MetroCoverApplication.SEASON_WINTER:
			bg = res.getDrawable(R.drawable.bg_winter);
			break;
		default:
			break;
		}

		if (bg != null) {
			setBackground(view, bg);
		}
	}
}
