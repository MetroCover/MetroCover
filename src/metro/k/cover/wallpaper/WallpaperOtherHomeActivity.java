package metro.k.cover.wallpaper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.TextViewWithFont;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
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

/**
 * 着せ替えホームアプリの壁紙一覧から設定
 * 
 * @author kohirose
 * 
 */
public class WallpaperOtherHomeActivity extends FragmentActivity implements
		OnItemClickListener {

	private int mWindowWidth = 0;
	private int mWindowHeight = 0;

	private final String PARAM_DRAWABLE = "drawable";

	private ArrayList<Drawable> mRealList = new ArrayList<Drawable>();
	private ArrayList<Drawable> mThumbList = new ArrayList<Drawable>();

	private GridView mGridView;
	private TextViewWithFont mEmptyView;

	private int mPage = -1;
	private int mHomeAppID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final int[] windows = Utilities.getWindowSize(this);
		mWindowWidth = windows[0];
		mWindowHeight = windows[1];
		final Intent in = getIntent();
		if (in == null) {
			Utilities.showErrorCommonToast(this);
			finish();
			return;
		}
		mPage = in.getIntExtra(WallpaperDialogFragment.KEY_SELECTED_PAGE, -1);
		mHomeAppID = in.getIntExtra(
				WallpaperUtilities.KEY_OTHER_HOMEAPP_WALLPAPER,
				WallpaperUtilities.HOMEE_APP_ID);
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
		setContentView(R.layout.wallpaper_other_home);

		// Title
		TextViewWithFont titleView = (TextViewWithFont) findViewById(R.id.wallpaper_other_home_titleview);
		if (mHomeAppID != WallpaperUtilities.HOMEE_APP_ID) {
			titleView.setText(R.string.wallpaper_plushome_title);
		}

		// Empty
		mEmptyView = (TextViewWithFont) findViewById(R.id.wallpaper_other_home_emptyview);

		// GridView
		mGridView = (GridView) findViewById(R.id.wallpaper_other_home_gridview);
		setupGridView();
	}

	private void setEmptyView() {
		if (mGridView == null || mEmptyView == null) {
			return;
		}

		mGridView.setVisibility(View.GONE);
		mEmptyView.setVisibility(View.VISIBLE);
	}

	/**
	 * GridViewの設定
	 */
	private void setupGridView() {
		if (mGridView == null) {
			return;
		}

		final ArrayList<String> pkgList = getThemePackagesList();
		if (pkgList == null) {
			setEmptyView();
			return;
		}

		final int pkgsize = pkgList.size();
		if (pkgsize == 0) {
			setEmptyView();
			return;
		}

		// buzzHomeだけ特殊
		if (mHomeAppID == WallpaperUtilities.BUZZHOME_APP_ID) {
			Drawable[] ds;
			for (int i = 0; i < pkgsize; i++) {
				ds = getBuzzDrawableResource(pkgList.get(i));
				if (ds == null) {
					continue;
				}
				for (int j = 0; j < ds.length; j++) {
					mRealList.add(ds[j]);
				}
			}
		} else {
			for (int i = 0; i < pkgsize; i++) {
				mRealList.add(getScreen(pkgList.get(i)));
			}
		}

		final int size = mRealList.size();
		if (size == 0) {
			setEmptyView();
			return;
		}

		for (int i = 0; i < size; i++) {
			mThumbList.add(Utilities.resizeFit(mRealList.get(i),
					mWindowWidth / 2, mWindowHeight / 2, this));
		}

		GridAdapter adapter = new GridAdapter(this.getApplicationContext(),
				R.layout.wallpaper_other_home_grid_items, mThumbList);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
	}

	/**
	 * 指定のホームアプリのテーマのパッケージを取得する
	 * 
	 * @return
	 */
	private ArrayList<String> getThemePackagesList() {
		if (mHomeAppID == WallpaperUtilities.HOMEE_APP_ID) {
			return WallpaperUtilities.getHomeeWallpapers(this);
		} else if (mHomeAppID == WallpaperUtilities.PLUSHOME_APP_ID) {
			return WallpaperUtilities.getPlusHomeWallpapers(this);
		} else if (mHomeAppID == WallpaperUtilities.BUZZHOME_APP_ID) {
			return WallpaperUtilities.getBuzzHomeWallpapers(this);
		} else {
			return null;
		}
	}

	/**
	 * 指定したパッケージ名のHomeeのテーマアプリのロッック画面画像を取得する
	 * 
	 * @param packageName
	 * @return
	 */
	private Drawable getScreen(final String packageName) {
		if (mHomeAppID == WallpaperUtilities.HOMEE_APP_ID) {
			final String str = getResources().getString(
					R.string.theme_homee_bg_image);
			return getDrawableResource(packageName, str);
		} else if (mHomeAppID == WallpaperUtilities.PLUSHOME_APP_ID) {
			final String str = getResources().getString(
					R.string.theme_plushome_bg_image);
			return getDrawableResource(packageName, str);
		} else {
			return null;
		}
	}

	/**
	 * 指定したパッケージ名のテーマアプリの画面画像を取得する
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

	/**
	 * 指定したパッケージ名のbuzzHomeテーマアプリの画面画像を取得する
	 * 
	 * @param packageName
	 * @param resName
	 * @return
	 */
	private Drawable[] getBuzzDrawableResource(final String packageName) {
		ArrayList<String> nameList = getBuzzDrawableNames(packageName);
		if (nameList == null) {
			return null;
		}
		final int size = nameList.size();
		if (size == 0) {
			return null;
		}

		Resources res = null;
		Drawable[] previews = new Drawable[size];
		try {
			res = getPackageManager().getResourcesForApplication(packageName);
			if (res == null) {
				return null;
			}
			final AssetManager assets = res.getAssets();
			InputStream is = null;
			Drawable d = null;
			for (int i = 0; i < size; i++) {
				try {
					String name = nameList.get(i);
					is = assets.open("homepack/" + name);
					d = Drawable.createFromStream(is, name);
					previews[i] = d;
				} catch (Exception e) {
					continue;
				}
			}
		} catch (NameNotFoundException e) {
		} catch (NotFoundException e) {
		} catch (RuntimeException e) {
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return previews;
	}

	/**
	 * BuzzHomeの壁紙名を複数取得する
	 * 
	 * @param packageName
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private ArrayList<String> getBuzzDrawableNames(final String packageName) {
		Resources res = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			res = getPackageManager().getResourcesForApplication(packageName);
			if (res == null) {
				return null;
			}

			final AssetManager am = res.getAssets();
			String[] files = null;
			try {
				files = am.list("homepack");
			} catch (IOException e) {
			}
			for (String file : files) {
				try {
					if (file.toLowerCase().endsWith(".jpg")) {
						list.add(file);
					}
				} catch (Exception e) {
					continue;
				}
			}
		} catch (NameNotFoundException e) {
		} catch (NotFoundException e) {
		} catch (RuntimeException e) {
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return list;
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
						.findViewById(R.id.wallpaper_other_home_grid_imageview);
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
		Utilities.showToast(getApplicationContext(),
				getResources().getString(R.string.wallpaper_complete_msg));
	}
}
