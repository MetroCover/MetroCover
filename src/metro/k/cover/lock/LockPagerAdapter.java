package metro.k.cover.lock;

import java.util.ArrayList;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.wallpaper.WallpaperBitmapDB;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LockPagerAdapter extends PagerAdapter {
	public static final String PAGE_LOCK = "page_lock";
	public static final String PAGE_PAGE_TRAIN_INFO_1 = "page_train_info_1";
	public static final String PAGE_PAGE_TRAIN_INFO_2 = "page_train_info_2";
	private Context mContext;
	private ArrayList<String> mList;
	private Object mPrimaryItem;
	private LayoutInflater mLayoutInflater;
	private int mTestNum1 = 0;
	private int mTestNum2 = 0;
	private int mLastPosition = 0;

	public LockPagerAdapter(Context context) {
		mContext = context;
		mList = new ArrayList<String>();
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	public void add(String pageName) {
		mList.add(pageName);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View pageView = null;
		String pageName = mList.get(position);
		if (pageName.equals(PAGE_LOCK)) {
			RelativeLayout lockLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_lock, null);
			pageView = lockLayout;
		} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_train_info, null);
			TextView tv = (TextView) trainInfoLayout
					.findViewById(R.id.page_train_info_test);
			tv.setText(String.valueOf(mTestNum1));
			pageView = trainInfoLayout;
		} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_train_info, null);
			TextView tv = (TextView) trainInfoLayout
					.findViewById(R.id.page_train_info_test);
			tv.setText(String.valueOf(mTestNum2));
			pageView = trainInfoLayout;
		}
		setBackgrondLoadBitmap(pageView, position);
		container.addView(pageView);
		return pageView;
	}

	@SuppressLint("NewApi")
	private void setBackgrondLoadBitmap(final View view, final int position) {
		Bitmap bmp;
		WallpaperBitmapDB db;
		String keyCache = "";
		String keyDB = "";

		if (position == 0) {
			keyCache = ImageCache.KEY_WALLPAPER_LEFT_CACHE;
			keyDB = WallpaperBitmapDB.KEY_WALLPAPER_LEFT_DB;
		} else if (position == 1) {
			keyCache = ImageCache.KEY_WALLPAPER_CENTER_CACHE;
			keyDB = WallpaperBitmapDB.KEY_WALLPAPER_CENTER_DB;
		} else {
			keyCache = ImageCache.KEY_WALLPAPER_RIGHT_CACHE;
			keyDB = WallpaperBitmapDB.KEY_WALLPAPER_RIGHT_DB;
		}

		bmp = ImageCache.getImageBmp(keyCache);
		if (bmp == null) {
			db = new WallpaperBitmapDB(mContext);
			bmp = db.getBitmp(keyDB);
			if (bmp != null) {
				compatibleSetBackground(view, bmp);
			}
		} else {
			compatibleSetBackground(view, bmp);
		}
	}

	@SuppressLint("NewApi")
	private void compatibleSetBackground(final View layout, final Bitmap bmp) {
		if (layout == null || bmp == null) {
			return;
		}

		final Resources res = mContext.getResources();
		if (Build.VERSION.SDK_INT >= 16) {
			layout.setBackground(new BitmapDrawable(res, bmp));
		} else {
			layout.setBackgroundDrawable(new BitmapDrawable(res, bmp));
		}
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		if (mLastPosition == position) {
			return;
		}
		mLastPosition = position;
		mPrimaryItem = object;
		countup(position);
	}

	public Object getPrimaryItem() {
		return mPrimaryItem;
	}

	private void countup(int position) {
		String pageName = mList.get(position);
		int count = 0;
		if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
			count = mTestNum1++;
		} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
			count = mTestNum2++;
		} else {
			return;
		}
		RelativeLayout TrainInfoLayout = (RelativeLayout) getPrimaryItem();
		TextView textView = (TextView) TrainInfoLayout
				.findViewById(R.id.page_train_info_test);
		textView.setText(String.valueOf(count));
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object keyObject) {
		return view == keyObject;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}