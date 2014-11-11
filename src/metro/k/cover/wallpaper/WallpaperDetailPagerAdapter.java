package metro.k.cover.wallpaper;

import java.util.ArrayList;

import metro.k.cover.ImageCache;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.JazzyViewPager;
import metro.k.cover.view.TextViewWithFont;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * 壁紙設定のViewPagerアダプター
 * 
 * @author kohirose
 * 
 */
public class WallpaperDetailPagerAdapter extends FragmentStatePagerAdapter {

	private Handler mHandler = new Handler();

	public WallpaperDetailPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		((JazzyViewPager) WallpaperDetailActivity.getViewPager())
				.setObjectForPosition(obj, position);
		return obj;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		if (object != null) {
			return ((Fragment) object).getView() == view;
		} else {
			return false;
		}
	}

	@Override
	public Fragment getItem(int position) {
		return new WallpaperFragment(position);
	}

	@Override
	public int getCount() {
		return WallpaperUtilities.MAX_PAGE;
	}

	/**
	 * 1ページ目（壁紙左）
	 * 
	 * @author kohirose
	 * 
	 */
	public class WallpaperFragment extends Fragment {

		private View mRootView;
		private int mPage;
		private ImageView mMainImageView;
		private TextViewWithFont mTitleTextView;

		public WallpaperFragment(final int page) {
			mPage = page;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mRootView = inflater.inflate(R.layout.wallpaper_detail, null);

			// タイトル
			setTitleView(mRootView);

			// 壁紙と削除機能
			setWallpaperImage(mRootView);

			return mRootView;
		}

		public View getRootView() {
			return mRootView;
		}

		/**
		 * タイトルの切り替え
		 * 
		 * @param view
		 */
		private void setTitleView(final View view) {
			if (view == null) {
				return;
			}

			final Resources res = getResources();
			mTitleTextView = (TextViewWithFont) view
					.findViewById(R.id.wallpaper_detail_titleview);
			if (mPage == WallpaperUtilities.PAGE_LEFT) {
				mTitleTextView.setText(res
						.getString(R.string.lock_screen_image_left));
			} else if (mPage == WallpaperUtilities.PAGE_CENTER) {
				mTitleTextView.setText(res
						.getString(R.string.lock_screen_image_center));
			} else {
				mTitleTextView.setText(res
						.getString(R.string.lock_screen_image_right));
			}
		}

		/**
		 * 壁紙と削除
		 * 
		 * @param view
		 */
		public void setWallpaperImage(final View view) {
			if (view == null) {
				return;
			}

			// キャッシュKey
			String cacheKey = ImageCache.KEY_WALLPAPER_RIGHT_CACHE;
			if (mPage == WallpaperUtilities.PAGE_LEFT) {
				cacheKey = ImageCache.KEY_WALLPAPER_LEFT_CACHE;
			} else if (mPage == WallpaperUtilities.PAGE_CENTER) {
				cacheKey = ImageCache.KEY_WALLPAPER_CENTER_CACHE;
			} else {
				cacheKey = ImageCache.KEY_WALLPAPER_RIGHT_CACHE;
			}

			// DB Key
			String dbKey = WallpaperBitmapDB.KEY_WALLPAPER_RIGHT_DB;
			if (mPage == WallpaperUtilities.PAGE_LEFT) {
				dbKey = WallpaperBitmapDB.KEY_WALLPAPER_LEFT_DB;
			} else if (mPage == WallpaperUtilities.PAGE_CENTER) {
				dbKey = WallpaperBitmapDB.KEY_WALLPAPER_CENTER_DB;
			} else {
				dbKey = WallpaperBitmapDB.KEY_WALLPAPER_RIGHT_DB;
			}

			// 削除ボタン
			ImageView deleteView = (ImageView) view
					.findViewById(R.id.wallpaper_detail_delete_image);

			// 設定中の画像
			mMainImageView = (ImageView) view
					.findViewById(R.id.wallpaper_detail_image);

			// 設定するボタン
			ImageView addImageView = (ImageView) view
					.findViewById(R.id.wallpaper_detail_add_image);

			final Bitmap cahce = ImageCache.getImageBmp(cacheKey);
			int col = -1;
			if (cahce != null) {
				Utilities.setBackground(mRootView, new BitmapDrawable(
						getResources(), cahce));
				mMainImageView.setVisibility(View.VISIBLE);
				deleteView.setVisibility(View.VISIBLE);
				deleteView.setOnClickListener(getDeleteListener(getActivity(),
						cahce, cacheKey, dbKey, mPage));
				addImageView.setVisibility(View.GONE);
				addImageView.setOnClickListener(null);
				col = getResources().getColor(R.color.metro_main_tranc_color);
			} else {
				WallpaperBitmapDB db = new WallpaperBitmapDB(getActivity());
				final Bitmap dbBmp = db.getBitmp(dbKey);
				if (dbBmp != null) {
					Utilities.setBackground(mRootView, new BitmapDrawable(
							getResources(), dbBmp));
					mMainImageView.setVisibility(View.VISIBLE);
					deleteView.setVisibility(View.VISIBLE);
					deleteView.setOnClickListener(getDeleteListener(
							getActivity(), cahce, cacheKey, dbKey, mPage));
					addImageView.setVisibility(View.GONE);
					addImageView.setOnClickListener(null);
					col = getResources().getColor(
							R.color.metro_main_tranc_color);
				} else {
					mMainImageView.setVisibility(View.GONE);
					deleteView.setVisibility(View.GONE);
					deleteView.setOnClickListener(null);
					addImageView.setVisibility(View.VISIBLE);
					addImageView.setOnClickListener(getWallpaerListener(
							getActivity(), mPage));
					col = getResources().getColor(R.color.metro_main_color);
				}
			}

			mTitleTextView.setBackgroundColor(col);
		}
	}

	/**
	 * 削除ボタン押下時
	 * 
	 * @param context
	 * @param bmp
	 * @param cacheKey
	 * @param dbKey
	 * @param imageView
	 * @return
	 */
	public View.OnClickListener getDeleteListener(final Context context,
			final Bitmap bmp, final String cacheKey, final String dbKey,
			final int position) {
		View.OnClickListener li = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new Thread("Delete") {
					@Override
					public void run() {
						ImageCache.clearCacheBmp(cacheKey);
						WallpaperBitmapDB db = new WallpaperBitmapDB(context);
						db.delteBitmap(dbKey);
						if (mHandler != null) {
							mHandler.post(getDeleteTask(context, position));
						}
					}
				}.start();
			}
		};
		return li;
	}

	/**
	 * 削除タスク
	 * 
	 * @param position
	 * @return
	 */
	private Runnable getDeleteTask(final Context context, final int position) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				final ViewPager vp = WallpaperDetailActivity.getViewPager();
				final WallpaperDetailPagerAdapter adpter = WallpaperDetailActivity
						.getWallpaperDetailPagerAdapter();
				if (vp != null && adpter != null) {
					vp.setAdapter(adpter);
					vp.setCurrentItem(position);
					Utilities.showToast(context, context.getResources()
							.getString(R.string.wallpaper_delete_msg));
				}

			}
		};
		return task;
	}

	/**
	 * 壁紙選択へ
	 * 
	 * @param activity
	 * @param page
	 * @return
	 */
	public View.OnClickListener getWallpaerListener(
			final FragmentActivity activity, final int page) {
		View.OnClickListener li = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final ArrayList<String> homees = WallpaperUtilities
						.getHomeeWallpapers(activity);
				final ArrayList<String> plushomes = WallpaperUtilities
						.getPlusHomeWallpapers(activity);
				boolean plushome = false;
				boolean homee = false;
				if (homees != null) {
					if (homees.size() > 0) {
						homee = true;
					}
				}
				if (plushomes != null) {
					if (plushomes.size() > 0) {
						plushome = true;
					}
				}

				if (homee || plushome) {
					buildSelectDialog(activity, page, plushome, homee);
				} else {
					pickupGallery(activity, page);
				}
			}
		};
		return li;
	}

	/**
	 * 暗黙的Intentを投げる
	 * 
	 * @param activity
	 * @param page
	 */
	private void pickupGallery(final FragmentActivity activity, final int page) {
		int requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_LEFT;
		if (page == WallpaperUtilities.PAGE_LEFT) {
			requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_LEFT;
		} else if (page == WallpaperUtilities.PAGE_CENTER) {
			requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_CENTER;
		} else {
			requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_RIGHT;
		}

		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		try {
			activity.startActivityForResult(intent, requestId);
		} catch (Exception e) {
		}
	}

	/**
	 * 壁紙選択ダイアログ
	 * 
	 * @param activity
	 * @param page
	 */
	private void buildSelectDialog(final FragmentActivity activity,
			final int page, final boolean isPlushome, final boolean isHomee) {
		try {
			final android.app.FragmentManager fm = activity
					.getFragmentManager();
			final WallpaperDialogFragment dialog = new WallpaperDialogFragment();
			dialog.showThemeDialogFragment("wallpaper_dialog", page, isHomee,
					isPlushome);
			final FragmentTransaction ft = fm.beginTransaction();
			ft.add(dialog, "wallpaper_dialog");
			ft.commit();
		} catch (Exception e) {
		}
	}
}
