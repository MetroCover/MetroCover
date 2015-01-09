package metro.k.cover.lock;

import java.util.List;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.Utilities;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.IBinder;
import android.telephony.TelephonyManager;

public class LockService extends Service {

	// レシーバー
	private LockReceiver mReceiver = null;

	private boolean isMetroCover = true;

	// サービスが起動中かどうか
	public static boolean isServiceRunning(final Context context) {
		if (context == null) {
			return false;
		}

		final ActivityManager am = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		if (am == null) {
			return false;
		}

		final List<ActivityManager.RunningServiceInfo> serviceInfo = am
				.getRunningServices(Integer.MAX_VALUE);
		if (serviceInfo == null) {
			return false;
		}

		final int size = serviceInfo.size();
		if (size == 0) {
			return false;
		}

		for (int i = 0; i < size; i++) {
			try {
				if (serviceInfo.get(i).service.getClassName().equals(
						"metro.k.cover.lock.LockService")) {
					return true;
				}
			} catch (NullPointerException e) {
				continue;
			}
		}
		return false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerLockReceiver();
	}

	private void registerLockReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		filter.addAction(Intent.ACTION_BOOT_COMPLETED);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);

		mReceiver = new LockReceiver();

		try {
			getApplicationContext().registerReceiver(mReceiver, filter);
			return;
		} catch (Exception e) {
		}

		try {
			registerReceiver(mReceiver, filter);
		} catch (Exception e) {
		}
	}

	private void unregisterLockReceiver() {
		try {
			getApplicationContext().unregisterReceiver(mReceiver);
			return;
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(mReceiver);
		} catch (Exception e) {
		}
	}

	private void startLockService() {
		if (!isMetroCover) {
			return;
		}

		try {
			startService(new Intent(getApplicationContext(), getClass()));
			return;
		} catch (Exception e) {
		}
		try {
			startService(new Intent(this, getClass()));
		} catch (Exception e) {
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterLockReceiver();
		stopSelf();
		startLockService();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		return START_STICKY;
	}

	/**
	 * onStart時の処理
	 * 
	 * @param intent
	 */
	private void handleCommand(final Intent intent) {
		if (intent == null) {
			return;
		}

		final String action = intent.getAction();
		if (Utilities.isInvalidStr(action)) {
			return;
		}

		isMetroCover = PreferenceCommon.getMetroCover(getApplicationContext());
		lock(action);
	}

	private void lock(String action) {
		if (Utilities.isInvalidStr(action)) {
			return;
		}

		if (Intent.ACTION_BOOT_COMPLETED.equals(action)
				|| Intent.ACTION_SCREEN_OFF.equals(action)
				|| Intent.ACTION_SCREEN_ON.equals(action)) {
			final LockUtilities lu = LockUtilities.getInstance();
			if (!checkTekephonyState()) {
				return;
			}

			if (isMetroCover) {
				lu.lock(this, true);
				disableKeyguard();
			} else {
				stopSelf();
			}
		}
	}

	private boolean checkTekephonyState() {
		TelephonyManager tManager = null;
		try {
			tManager = (TelephonyManager) getApplicationContext()
					.getSystemService(Context.TELEPHONY_SERVICE);
		} catch (Exception e) {
		}

		if (tManager == null) {
			try {
				tManager = (TelephonyManager) this
						.getSystemService(Context.TELEPHONY_SERVICE);
			} catch (Exception e) {
				return false;
			}
		}
		if (tManager == null
				|| tManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
			return false;
		}

		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void disableKeyguard() {
		LockUtilities.getInstance().disableKeyguard(getApplicationContext());
	}
}