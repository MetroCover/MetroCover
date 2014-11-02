package metro.k.cover.lock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockService extends Service {

	ScreenStateReceiver mScreenStateReceiver = new ScreenStateReceiver();

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
				Intent intentStartLock = new Intent(LockService.this, LockActivity.class);
				intentStartLock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				LockService.this.startActivity(intentStartLock);
			}
		}
	}

}
