package metro.k.cover.lock;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

public class LockActivity extends Activity {

	LockPagerAdapter mLockPagerAdapter;
	OnLockPageChangeListener mOnLockPageChangeListener = new OnLockPageChangeListener();
	WindowManager mWindowManager;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViewPager();
	}

	private void setupViewPager() {
		LockPagerAdapter mLockPagerAdapter = new LockPagerAdapter(this);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_1);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_LOCK);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_2);
		mViewPager = new ViewPager(this);
		mViewPager.setAdapter(mLockPagerAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setOnPageChangeListener(mOnLockPageChangeListener);
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 0, PixelFormat.TRANSLUCENT);
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mWindowManager.addView(mViewPager, params);
	}

	private class OnLockPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int state) {
			switch (state) {
			case ViewPager.SCROLL_STATE_IDLE:
				break;
			case ViewPager.SCROLL_STATE_SETTLING:
				break;
			case ViewPager.SCROLL_STATE_DRAGGING:
				break;
			default:
				break;
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
		}
	}

	@Override
	public void finish() {
		mWindowManager.removeView(mViewPager);
		super.finish();
	}
}
