package metro.k.cover.tutorial;

import metro.k.cover.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;

public class TutorialActivity extends Activity {

	private ViewPager mViewPager;

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
	}

	/**
	 * 次のページへ
	 */
	private void setNextPage() {
		if (mViewPager == null) {
			return;
		}
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
	}

	/**
	 * 前のページへ
	 */
	private void setPrevPage() {
		if (mViewPager == null) {
			return;
		}
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
	}
}
