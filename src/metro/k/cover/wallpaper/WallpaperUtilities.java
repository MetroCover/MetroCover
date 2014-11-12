package metro.k.cover.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public final class WallpaperUtilities {

	// 他のホームアプリの壁紙の一覧を表示するためのKey
	public static final String KEY_OTHER_HOMEAPP_WALLPAPER = "key_other_home_app_wallpaper";
	public final static int HOME_APPS_SIZE = 4;
	public static final int HOMEE_APP_ID = 0;
	public static final int PLUSHOME_APP_ID = 1;
	public static final int BUZZHOME_APP_ID = 2;
	public static final int DODORUHOME_APP_ID = 3;

	// Homeeの壁紙を取得する
	public static final String INTENT = "com.cfinc.launcehr2.THEMES";
	public static final String ACTION = "android.intent.category.DEFAULT";

	// Homeeの壁紙選択時のrequestCode
	public static final int REQUEST_CODE_HOMEE_WALLPAPER = 1231;

	// 壁紙設定のViewPagerのページ番号を覚えるKEY
	public static final String KEY_PAGE_NUMBER = "page_number";

	// ViewPagerの枚数
	public static final int MAX_PAGE = 3;

	// ViewPagerのページ番号
	public static final int PAGE_LEFT = 0;
	public static final int PAGE_CENTER = 1;
	public static final int PAGE_RIGHT = 2;

	public static final int REQUEST_PICK_PICTURE_LEFT = 0;
	public static final int REQUEST_PICK_PICTURE_CENTER = 1;
	public static final int REQUEST_PICK_PICTURE_RIGHT = 2;

	// ViewPagerエフェクト選択してサンプルにIDを送る用のKey
	public static final String KEY_WALLPAPER_EFFECT_SELECT_ID = "key_wallpaper_effect_select_id";

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
	 * Homee,[+]Home,buzzHome,ドドルランチャーのテーマがそれぞれインストールされているかリストで返す
	 * 
	 * @param context
	 * @return
	 */
	public static boolean[] getHomeAppThemesInstalled(final Context context) {
		boolean[] list = new boolean[HOME_APPS_SIZE];
		if (context == null) {
			for (int i = 0; i < HOME_APPS_SIZE; i++) {
				list[i] = false;
			}
			return list;
		}

		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			for (int i = 0; i < HOME_APPS_SIZE; i++) {
				list[i] = false;
			}
			return list;
		}

		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		final Resources res = context.getResources();
		final String plushome_theme_head = res
				.getString(R.string.pkg_plus_home_theme_head);
		final String homee_theme_head = res
				.getString(R.string.pkg_homee_theme_head);
		final String buzz_theme_head = res
				.getString(R.string.pkg_buzz_home_theme_head);
		final String dodoru_theme_head = res
				.getString(R.string.pkg_dodo_home_theme_head);
		for (ApplicationInfo info : applicationInfo) {
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				continue;
			}
			String pkg = info.packageName;
			if (!list[HOMEE_APP_ID]) {
				if (pkg.startsWith(homee_theme_head)) {
					list[HOMEE_APP_ID] = true;
					continue;
				}
			}
			if (!list[PLUSHOME_APP_ID]) {
				if (pkg.startsWith(plushome_theme_head)) {
					list[PLUSHOME_APP_ID] = true;
					continue;
				}
			}

			if (!list[BUZZHOME_APP_ID]) {
				if (pkg.startsWith(buzz_theme_head)) {
					list[BUZZHOME_APP_ID] = true;
					continue;
				}
			}

			if (!list[DODORUHOME_APP_ID]) {
				if (pkg.startsWith(dodoru_theme_head)) {
					list[DODORUHOME_APP_ID] = true;
					continue;
				}
			}

			if (list[HOMEE_APP_ID] && list[PLUSHOME_APP_ID]
					&& list[BUZZHOME_APP_ID] && list[DODORUHOME_APP_ID]) {
				break;
			}
		}
		return list;
	}

	/**
	 * [+]HOMEの壁紙のパッケージ名を取得する
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getPlusHomeWallpapers(final Context context) {
		if (context == null) {
			return null;
		}

		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			return null;
		}

		final ArrayList<String> list = new ArrayList<String>();
		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		final String plushome_theme_head = context.getResources().getString(
				R.string.pkg_plus_home_theme_head);
		for (ApplicationInfo info : applicationInfo) {
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				continue;
			}
			String pkg = info.packageName;
			if (pkg.startsWith(plushome_theme_head)) {
				list.add(pkg);
			}
		}
		return list;
	}

	/**
	 * buzzHOMEの壁紙のパッケージ名を取得する
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getBuzzHomeWallpapers(final Context context) {
		if (context == null) {
			return null;
		}

		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			return null;
		}

		final ArrayList<String> list = new ArrayList<String>();
		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		final String buzzhome_theme_head = context.getResources().getString(
				R.string.pkg_buzz_home_theme_head);
		for (ApplicationInfo info : applicationInfo) {
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				continue;
			}
			String pkg = info.packageName;
			if (pkg.startsWith(buzzhome_theme_head)) {
				list.add(pkg);
			}
		}
		return list;
	}

	/**
	 * ドドルランチャーの壁紙のパッケージ名を取得する
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getDodolHomeWallpapers(final Context context) {
		if (context == null) {
			return null;
		}

		final PackageManager packageManager = context.getPackageManager();
		if (packageManager == null) {
			return null;
		}

		final ArrayList<String> list = new ArrayList<String>();
		List<ApplicationInfo> applicationInfo = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		final String dodol_theme_head = context.getResources().getString(
				R.string.pkg_dodo_home_theme_head);
		for (ApplicationInfo info : applicationInfo) {
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM) {
				continue;
			}
			String pkg = info.packageName;
			if (pkg.startsWith(dodol_theme_head)) {
				list.add(pkg);
			}
		}
		return list;
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
	 * 壁紙をDBに保存する(SubThread)
	 * 
	 * @param context
	 * @param dbKey
	 * @param bmp
	 */
	public static void assyncSaveBmpDB(final Context context,
			final String dbKey, final Bitmap bmp) {
		if (bmp == null || context == null || Utilities.isInvalidStr(dbKey)) {
			return;
		}
		new Thread("save") {
			@Override
			public void run() {
				syncSaveBmpDB(context, dbKey, bmp);
			}
		}.start();
	}

	/**
	 * 壁紙をDBに保存する(MainThread)
	 * 
	 * @param context
	 * @param dbKey
	 * @param bmp
	 */
	public static void syncSaveBmpDB(final Context context, final String dbKey,
			final Bitmap bmp) {
		WallpaperBitmapDB db = new WallpaperBitmapDB(context);
		try {
			db.setBitmap(dbKey, bmp);
		} catch (Exception e) {
		} catch (OutOfMemoryError oom) {
		} finally {
			if (db != null) {
				try {
					db.close();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * ホーム画面の壁紙取得
	 * 
	 * @param context
	 * @return
	 */
	@SuppressLint("ServiceCast")
	public static final Bitmap getSystemWallpaper(final Context context) {
		if (context == null) {
			return null;
		}

		final WallpaperManager wm = (WallpaperManager) context
				.getSystemService(Context.WALLPAPER_SERVICE);
		if (wm == null) {
			return null;
		}

		final Drawable d = wm.getDrawable();
		if (d == null) {
			return null;
		}

		return ((BitmapDrawable) d).getBitmap();
	}
}
