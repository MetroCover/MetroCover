package metro.k.cover.wallpaper;

import java.util.ArrayList;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Homeeの壁紙一覧から設定
 * 
 * @author kohirose
 * 
 */
public class WallpaperHomeeActivity extends FragmentActivity implements
		OnItemClickListener {

	private int mWindowWidth = 0;
	private int mWindowHeight = 0;

	private final String PARAM_DRAWABLE = "drawable";

	private ArrayList<Drawable> mRealList = new ArrayList<Drawable>();
	private ArrayList<Drawable> mThumbList = new ArrayList<Drawable>();

	private int mPage = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int[] windows = Utilities.getWindowSize(this);
		mWindowWidth = windows[0];
		mWindowHeight = windows[1];
		mPage = getIntent().getIntExtra(
				WallpaperDialogFragment.KEY_SELECTED_PAGE, -1);
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
		setContentView(R.layout.wallpaper_homee);

		// Title
		TextView titleView = (TextView) findViewById(R.id.wallpaper_homee_titleview);
		Utilities.setFontTextView(titleView, getAssets(), getResources());

		// GridView
		setupGridView();
	}

	/**
	 * GridViewの設定
	 */
	private void setupGridView() {
		final ArrayList<String> pkgList = getHomeeThemePackagesList();
		if (pkgList == null) {
			return;
		}

		final int size = pkgList.size();
		if (size == 0) {
			return;
		}

		for (int i = 0; i < size; i++) {
			mRealList.add(getHomeeLockscreen(pkgList.get(i)));
		}

		for (int i = 0; i < mRealList.size(); i++) {
			mThumbList.add(Utilities.resizeFit(mRealList.get(i),
					mWindowWidth / 2, mWindowHeight / 2, this));
		}

		GridView gridview = (GridView) findViewById(R.id.wallpaper_homee_gridview);
		GridAdapter adapter = new GridAdapter(this.getApplicationContext(),
				R.layout.wallpaper_homee_grid_items, mThumbList);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(this);
	}

	/**
	 * Homeeのテーマのパッケージを取得する
	 * 
	 * @return
	 */
	private ArrayList<String> getHomeeThemePackagesList() {
		return WallpaperUtilities.getHomeeWallpapers(this);
	}

	/**
	 * 指定したパッケージ名のHomeeのテーマアプリのロッック画面画像を取得する
	 * 
	 * @param packageName
	 * @return
	 */
	private Drawable getHomeeLockscreen(final String packageName) {
		final String str = getResources().getString(
				R.string.theme_lock_bg_image);
		return getDrawableResource(packageName, str);
	}

	/**
	 * 指定したパッケージ名のHomeeのテーマアプリのロッック画面画像を取得する
	 * 
	 * @param packageName
	 * @param resName
	 * @return
	 */
	private Drawable getDrawableResource(final String packageName,
			String resName) {

		Resources res = null;
		Drawable preview = null;

		try {
			res = getPackageManager().getResourcesForApplication(packageName);
			if (res == null) {
				return null;
			}
			final int resId = res.getIdentifier(resName, PARAM_DRAWABLE,
					packageName);
			preview = res.getDrawable(resId);
		} catch (NameNotFoundException e) {
		} catch (NotFoundException e) {
		} catch (RuntimeException e) {
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return preview;
	}

	class ViewHolder {
		ImageView imageView;
	}

	class GridAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private int layoutId;
		private ArrayList<Drawable> imgList;

		public GridAdapter(Context context, int layoutId,
				ArrayList<Drawable> imgList) {
			super();
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.layoutId = layoutId;
			this.imgList = imgList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Drawable d = imgList.get(position);

			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(layoutId, parent, false);
				holder = new ViewHolder();
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.wallpaper_homee_grid_imageview);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Bitmap bmp = ((BitmapDrawable) d).getBitmap();
			holder.imageView.setImageBitmap(bmp);

			return convertView;
		}

		@Override
		public int getCount() {
			return imgList.size();
		}

		@Override
		public Object getItem(int position) {
			return imgList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		String cahceKey = ImageCache.KEY_WALLPAPER_RIGHT_CACHE;
		String dbKey = WallpaperBitmapDB.KEY_WALLPAPER_RIGHT_DB;
		if (mPage == WallpaperUtilities.PAGE_LEFT) {
			cahceKey = ImageCache.KEY_WALLPAPER_LEFT_CACHE;
			dbKey = WallpaperBitmapDB.KEY_WALLPAPER_LEFT_DB;
		} else if (mPage == WallpaperUtilities.PAGE_CENTER) {
			cahceKey = ImageCache.KEY_WALLPAPER_CENTER_CACHE;
			dbKey = WallpaperBitmapDB.KEY_WALLPAPER_CENTER_DB;
		}

		final Bitmap bmp = ((BitmapDrawable) mRealList.get(position))
				.getBitmap();
		ImageCache.setImageBmp(cahceKey, bmp);
		WallpaperUtilities.syncSaveBmpDB(getApplicationContext(), dbKey, bmp);

		Intent intent = new Intent(this, WallpaperDetailActivity.class);
		Bundle bundle = new Bundle();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		bundle.putInt(WallpaperUtilities.KEY_PAGE_NUMBER, mPage);
		intent.putExtras(bundle);
		Utilities.startActivitySafely(intent, this);
	}
}
