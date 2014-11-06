package metro.k.cover.wallpaper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import metro.k.cover.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

public final class WallpaperUtilities {

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
}
