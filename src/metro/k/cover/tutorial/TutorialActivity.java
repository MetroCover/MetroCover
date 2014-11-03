package metro.k.cover.tutorial;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

public class TutorialActivity extends FragmentActivity {

	private static ViewPager mViewPager;

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
		mViewPager = (ViewPager) findViewById(R.id.tutorial_viewpager);
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
}
