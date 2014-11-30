package metro.k.cover.wallpaper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.circularprogressbar.CircularProgressBar;
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
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
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

	private GridView mGridView;
	private TextViewWithFont mEmptyView;

	private int mPage = -1;
	private int mHomeAppID;

	private CircularProgressBar mCircularProgressBar;
	private Handler mHandler = null;

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
		createHanelr();
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

		// cpb
		mCircularProgressBar = (CircularProgressBar) findViewById(R.id.wallpaper_other_home_loading);

		// Title
		TextViewWithFont titleView = (TextViewWithFont) findViewById(R.id.wallpaper_other_home_titleview);
		if (mHomeAppID == WallpaperUtilities.HOMEE_APP_ID) {
			titleView.setText(R.string.wallpaper_homee_title);
		} else if (mHomeAppID == WallpaperUtilities.PLUSHOME_APP_ID) {
			titleView.setText(R.string.wallpaper_plushome_title);
		} else if (mHomeAppID == WallpaperUtilities.BUZZHOME_APP_ID) {
			titleView.setText(R.string.wallpaper_buzzhome_title);
		} else if (mHomeAppID == WallpaperUtilities.DODORUHOME_APP_ID) {
			titleView.setText(R.string.wallpaper_dodol_title);
		}

		// Empty
		mEmptyView = (TextViewWithFont) findViewById(R.id.wallpaper_other_home_emptyview);

		// GridView
		mGridView = (GridView) findViewById(R.id.wallpaper_other_home_gridview);
		setupGridView();
	}

	private void setEmptyView() {
		if (mGridView == null || mEmptyView == null
				|| mCircularProgressBar == null || mHandler == null) {
			return;
		}

		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mGridView.setVisibility(View.GONE);
				mEmptyView.setVisibility(View.VISIBLE);
				mCircularProgressBar.setVisibility(View.GONE);
			}
		});
	}

	/**
	 * GridViewの設定
	 */
	private void setupGridView() {
		if (mGridView == null || mHandler == null) {
			return;
		}

		mCircularProgressBar.setVisibility(View.VISIBLE);
		new Thread("setupgrid") {
			@Override
			public void run() {
				final ArrayList<String> pkgList = getThemePackagesList();
				if (pkgList == null) {
					setEmptyView();
					return;
				}

				setScreen(pkgList);
				final int size = mRealList.size();
				if (size == 0) {
					setEmptyView();
					return;
				}

				Message msg = new Message();
				mHandler.sendMessage(msg);
			}
		}.start();
	}

	@SuppressLint("HandlerLeak")
	private void createHanelr() {
		if (mHandler != null) {
			return;
		}
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg == null) {
					return;
				}
				createGridview();
			}
		};
	}

	/**
	 * GridViewの最終設定
	 */
	private void createGridview() {
		GridAdapter adapter = new GridAdapter(getApplicationContext(),
				R.layout.wallpaper_other_home_grid_items, mRealList);
		mGridView.setAdapter(adapter);
		mGridView.setOnItemClickListener(this);
		mCircularProgressBar.setVisibility(View.GONE);
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
		} else if (mHomeAppID == WallpaperUtilities.DODORUHOME_APP_ID) {
			return WallpaperUtilities.getDodolHomeWallpapers(this);
		} else {
			return null;
		}
	}

	/**
	 * 指定アプリの壁紙取得してリストに入れる
	 * 
	 * @param packageName
	 * @return
	 */
	private void setScreen(final ArrayList<String> pkgList) {
		final int pkgsize = pkgList.size();
		if (pkgsize == 0) {
			setEmptyView();
			return;
		}

		// Homee
		if (mHomeAppID == WallpaperUtilities.HOMEE_APP_ID) {
			Drawable[] ds;
			for (int i = 0; i < pkgsize; i++) {
				ds = getHomeeDrawableResource(pkgList.get(i));
				if (ds == null) {
					continue;
				}
				for (int j = 0; j < ds.length; j++) {
					if (ds[j] != null) {
						mRealList.add(ds[j]);
					}
				}
			}
			return;
		}

		// buzzHOME
		if (mHomeAppID == WallpaperUtilities.BUZZHOME_APP_ID) {
			Drawable[] ds;
			for (int i = 0; i < pkgsize; i++) {
				ds = getBuzzDrawableResource(pkgList.get(i));
				if (ds == null) {
					continue;
				}
				for (int j = 0; j < ds.length; j++) {
					if (ds[j] != null) {
						mRealList.add(ds[j]);
					}
				}
			}
		}

		// [+]HOME
		if (mHomeAppID == WallpaperUtilities.PLUSHOME_APP_ID) {
			final String str = getResources().getString(
					R.string.theme_plushome_bg_image);
			for (int i = 0; i < pkgsize; i++) {
				mRealList.add(getDrawableResource(pkgList.get(i), str));
			}
			return;
		}

		// ドドルランチャー
		if (mHomeAppID == WallpaperUtilities.DODORUHOME_APP_ID) {
			final String str = getResources().getString(
					R.string.theme_dodol_bg_image);
			for (int i = 0; i < pkgsize; i++) {
				mRealList.add(getDrawableResource(pkgList.get(i), str));
			}
			return;
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
	 * Homeeの壁紙を「３枚取得する」
	 * 
	 * @param packageName
	 * @return
	 */
	private Drawable[] getHomeeDrawableResource(final String packageName) {
		Drawable[] previews = new Drawable[3];
		try {
			final Resources res = getResources();
			previews[0] = getDrawableResource(packageName,
					res.getString(R.string.theme_homee_bg_image));
			previews[1] = getDrawableResource(packageName,
					res.getString(R.string.theme_homee_bg_image_allapps));
			previews[2] = getDrawableResource(packageName,
					res.getString(R.string.theme_homee_bg_image_lock));
		} catch (NotFoundException e) {
		} catch (RuntimeException e) {
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		return previews;
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

		private LayoutParams lp;
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
			lp = new AbsListView.LayoutParams(new AbsListView.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			lp.width = mWindowWidth / 2;
			lp.height = mWindowHeight / 2;
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
				convertView.setLayoutParams(lp);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.imageView.setImageDrawable(d);

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

		final Drawable d = mRealList.get(position);
		if (d == null) {
			Utilities.showErrorCommonToast(this);
			return;
		}

		final Bitmap bmp = ((BitmapDrawable) d).getBitmap();
		ImageCache.setImageBmp(cahceKey, bmp);
		WallpaperUtilities.syncSaveBmpDB(getApplicationContext(), dbKey, bmp);

		Intent intent = new Intent(this, WallpaperDetailActivity.class);
		Bundle bundle = new Bundle();
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		bundle.putInt(WallpaperUtilities.KEY_PAGE_NUMBER, mPage);
		intent.putExtras(bundle);
		if (Utilities.startActivitySafely(intent, this)) {
			Utilities.showToast(getApplicationContext(), getResources()
					.getString(R.string.wallpaper_complete_msg));
			System.gc();
		}
	}
}
