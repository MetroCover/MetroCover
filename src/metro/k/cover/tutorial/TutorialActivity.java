package metro.k.cover.tutorial;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.view.JazzyViewPager;
import metro.k.cover.wallpaper.WallpaperUtilities;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

public class TutorialActivity extends FragmentActivity {

	private static JazzyViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (PreferenceCommon.getTutorialOpend(getApplicationContext())) {
			startSettingActivity();
		} else {
			setupViews();
			PreferenceCommon.setTutorialOpend(getApplicationContext(), true);
		}
	}

	/**
	 * 設定画面に遷移
	 */
	private void startSettingActivity() {
		Intent intent = new Intent(this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(intent);
			finish();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		PreferenceCommon.setRailwaysInfomation(getApplicationContext(),
				getCheckedRailways());
		PreferenceCommon.setRailwaysNameForAPI(getApplicationContext(),
				getCheckedRailwaysResponses());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mViewPager == null) {
			return true;
		}

		// 2ページ目以降でのバックキーは前ページに戻す
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& mViewPager.getCurrentItem() != 0) {
			setPrevPage();
			return true;
		}

		// 1ページ目のバックキーを無効にする
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& mViewPager.getCurrentItem() == 0) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void setupViews() {
		setContentView(R.layout.activity_tutorial);
		RelativeLayout root = (RelativeLayout) findViewById(R.id.tutorial_root);
		final Drawable d = new BitmapDrawable(getResources(),
				WallpaperUtilities.getSystemWallpaper(this));
		Utilities.setBackground(root, d);
		mViewPager = (JazzyViewPager) findViewById(R.id.tutorial_viewpager);
		mViewPager.setTransitionEffect(PreferenceCommon
				.getViewPagerEffect(this));
		mViewPager.setPageMargin(30);
		mViewPager.setAdapter(new TutorialPagerAdapter(
				getSupportFragmentManager()));
	}

	/**
	 * 次のページへ
	 */
	public static void setNextPage() {
		if (mViewPager == null) {
			return;
		}
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
	}

	/**
	 * 前のページへ
	 */
	public static void setPrevPage() {
		if (mViewPager == null) {
			return;
		}
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
	}

	/**
	 * チェックを入れた路線を取得
	 * 
	 * @return
	 */
	private String getCheckedRailways() {
		return TutorialSecond.getCheckedStr();
	}

	private String getCheckedRailwaysResponses() {
		return TutorialSecond.getCheckedResponseNmae();
	}

	public static JazzyViewPager getViewPager() {
		return mViewPager;
	}
}
