package metro.k.cover.wallpaper;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.wallpaper.WallpaperDetailPagerAdapter.WallpaperFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.WindowManager;

/**
 * 壁紙設定
 * 
 * @author kohirose
 * 
 */
public class WallpaperDetailActivity extends FragmentActivity {

	private static ViewPager mViewPager;
	private static WallpaperDetailPagerAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void setupViews() {
		setContentView(R.layout.activity_wallpaper_detail);
		mViewPager = (ViewPager) findViewById(R.id.wallpaper_detail_viewpager);
		mAdapter = new WallpaperDetailPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == WallpaperDetailPagerAdapter.REQUEST_PICK_PICTURE_LEFT
				|| requestCode == WallpaperDetailPagerAdapter.REQUEST_PICK_PICTURE_CENTER
				|| requestCode == WallpaperDetailPagerAdapter.REQUEST_PICK_PICTURE_RIGHT) {
			if (resultCode == RESULT_OK) {
				final String[] columns = { MediaColumns.DATA };
				Cursor cur = getContentResolver().query(data.getData(),
						columns, null, null, null);
				cur.moveToNext();

				final String filePath = cur.getString(0);
				if (filePath == null) {
					Utilities.showErrorCommonToast(getApplicationContext());
					return;
				}

				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(filePath, options);

				WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
				Display disp = wm.getDefaultDisplay();
				Point size = new Point();
				disp.getSize(size);

				int srcWidth = options.outWidth;
				int srcHeight = options.outHeight;
				if (srcHeight > size.y || srcWidth > size.x) {
					if (srcWidth > srcHeight) {
						options.inSampleSize = Math.round((float) srcHeight
								/ (float) size.y);
					} else {
						options.inSampleSize = Math.round((float) srcWidth
								/ (float) size.x);
					}
				} else {
					options.inSampleSize = 1;
				}

				options.inJustDecodeBounds = false;
				Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
				if (bitmap == null) {
					Utilities.showErrorCommonToast(getApplicationContext());
					return;
				}

				String cahceKey = ImageCache.KEY_WALLPAPER_LEFT_CACHE;
				String dbKey = WallpaperBitmapDB.KEY_WALLPAPER_LEFT_DB;
				int position = WallpaperDetailPagerAdapter.PAGE_LEFT;
				if (requestCode == WallpaperDetailPagerAdapter.REQUEST_PICK_PICTURE_CENTER) {
					position = WallpaperDetailPagerAdapter.PAGE_CENTER;
					cahceKey = ImageCache.KEY_WALLPAPER_CENTER_CACHE;
					dbKey = WallpaperBitmapDB.KEY_WALLPAPER_CENTER_DB;
				} else if (requestCode == WallpaperDetailPagerAdapter.REQUEST_PICK_PICTURE_RIGHT) {
					position = WallpaperDetailPagerAdapter.PAGE_RIGHT;
					cahceKey = ImageCache.KEY_WALLPAPER_RIGHT_CACHE;
					dbKey = WallpaperBitmapDB.KEY_WALLPAPER_RIGHT_DB;
				}

				ImageCache.setImageBmp(cahceKey, bitmap);
				saveBmpDB(dbKey, bitmap);

				WallpaperFragment f = (WallpaperFragment) mAdapter
						.getItem(position);
				f.setWallpaperImage(f.getRootView());
				mViewPager.setAdapter(mAdapter);
				mViewPager.setCurrentItem(position);
			}
		}
	}

	private void saveBmpDB(final String dbKey, final Bitmap bmp) {
		if (bmp == null) {
			return;
		}
		new Thread("save") {
			@Override
			public void run() {
				WallpaperBitmapDB db = new WallpaperBitmapDB(
						getApplicationContext());
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
		}.start();
	}

	public static ViewPager getViewPager() {
		return mViewPager;
	}

	public static WallpaperDetailPagerAdapter getWallpaperDetailPagerAdapter() {
		return mAdapter;
	}
}
