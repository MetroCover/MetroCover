package metro.k.cover.lock;

import java.util.List;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.Utilities;
import metro.k.cover.railways.RailwaysInfoUpdateService;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
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
		if (context == null)
			return false;

		final ActivityManager am = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		if (am == null)
			return false;

		final List<ActivityManager.RunningServiceInfo> serviceInfo = am
				.getRunningServices(Integer.MAX_VALUE);
		if (serviceInfo == null)
			return false;

		final int size = serviceInfo.size();
		if (size == 0)
			return false;

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
		if (intent == null)
			return;

		final String action = intent.getAction();
		if (action == null)
			return;

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
			final Context c = getApplicationContext();
			final LockUtilities lu = LockUtilities.getInstance();
			TelephonyManager tManager = null;
			try {
				tManager = (TelephonyManager) c
						.getSystemService(Context.TELEPHONY_SERVICE);
			} catch (Exception e) {
			}
			if (tManager == null) {
				try {
					tManager = (TelephonyManager) this
							.getSystemService(Context.TELEPHONY_SERVICE);
				} catch (Exception e) {
					return;
				}
			}
			if (tManager == null
					|| tManager.getCallState() == TelephonyManager.CALL_STATE_OFFHOOK) {
				return;
			}

			if (isMetroCover) {
				MetroCoverApplication app = (MetroCoverApplication) getApplication();
				app.createRailwaysInfoList();
				lu.lock(c, true);
				disableKeyguard();
				scheduleUpdateService();
			} else {
				cancelUpdateScheduleService();
			}
		}
	}

	/**
	 * 情報更新用のServiceスケジュール登録
	 */
	private void scheduleUpdateService() {
		Context context = getBaseContext();
		if (isSetPending(context)) {
			return;
		}
		Intent intent = new Intent(context, RailwaysInfoUpdateService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, -1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(ALARM_SERVICE);
		alarmManager.setInexactRepeating(AlarmManager.RTC,
				System.currentTimeMillis(), 1000 * 60 * 60, pendingIntent);
	}

	/**
	 * 情報更新用Serviceのスケジュール解除
	 */
	private void cancelUpdateScheduleService() {
		Context context = getBaseContext();
		if (!isSetPending(context)) {
			return;
		}
		Intent intent = new Intent(context, RailwaysInfoUpdateService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, -1,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}

	/**
	 * 情報更新用Serviceのスケジュールが設定されているかどうかを返す。
	 * 
	 * @param context
	 */
	private boolean isSetPending(Context context) {
		Intent intent = new Intent(context, RailwaysInfoUpdateService.class);
		PendingIntent p = PendingIntent.getService(context, -1, intent,
				PendingIntent.FLAG_NO_CREATE);
		if (p == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	public void disableKeyguard() {
		LockUtilities.getInstance().disableKeyguard(getApplicationContext());
	}
}