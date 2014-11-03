package metro.k.cover.wallpaper;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/**
 * 壁紙設定
 * 
 * @author kohirose
 * 
 */
public class WallpaperDetailActivity extends FragmentActivity {

	private static ViewPager mViewPager;
	private static WallpaperDetailPagerAdapter mAdapter;

	public static final int REQUEST_CODE_HOMEE_WALLPAPER = 1231;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final Intent intent = getIntent();
		final int page = intent.getIntExtra(
				WallpaperHomeeActivity.KEY_PAGE_NUMBER, -1);
		if (page < 0) {
			setupViews(1);
			return;
		}

		setupViews(page);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void setupViews(final int page) {
		setContentView(R.layout.activity_wallpaper_detail);
		mViewPager = (ViewPager) findViewById(R.id.wallpaper_detail_viewpager);
		mAdapter = new WallpaperDetailPagerAdapter(getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setCurrentItem(page);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// ギャラリーの壁紙
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

				final int[] windows = Utilities.getWindowSize(this);
				final int windowX = windows[0];
				final int windowY = windows[1];

				int srcWidth = options.outWidth;
				int srcHeight = options.outHeight;
				if (srcHeight > windowY || srcWidth > windowX) {
					if (srcWidth > srcHeight) {
						options.inSampleSize = Math.round((float) srcHeight
								/ (float) windowY);
					} else {
						options.inSampleSize = Math.round((float) srcWidth
								/ (float) windowX);
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

				setupViews(position);
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
