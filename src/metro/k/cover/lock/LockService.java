package metro.k.cover.lock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.view.WindowManager;

public class LockService extends Service {

	ScreenStateReceiver mScreenStateReceiver = new ScreenStateReceiver();
	OnLockPageChangeListener mOnLockPageChangeListener = new OnLockPageChangeListener();
	LockPagerAdapter mLockPagerAdapter;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		setupReceiver();
	}

	private void setupReceiver() {
		IntentFilter screenStateFilter = new IntentFilter();
		screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
		screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mScreenStateReceiver, screenStateFilter);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		if (intent == null) {
			setupReceiver();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mScreenStateReceiver);
	}

	public class ScreenStateReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				setupViewPager();
			}
		}

	}

	private void setupViewPager() {
		try {
			mLockPagerAdapter = new LockPagerAdapter(getApplicationContext());
			mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_1);
			mLockPagerAdapter.add(LockPagerAdapter.PAGE_LOCK);
			mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_2);
			ViewPager viewPager = new ViewPager(getApplicationContext());
			viewPager.setAdapter(mLockPagerAdapter);
			viewPager.setCurrentItem(1);
			viewPager.setOnPageChangeListener(mOnLockPageChangeListener);
			WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_SYSTEM_ALERT, 0, PixelFormat.TRANSLUCENT);
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
			wm.addView(viewPager, params);
		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
