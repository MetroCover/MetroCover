package metro.k.cover.lock;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.R;
import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

@SuppressLint("HandlerLeak")
public class LockUtilities {

	// パターンセキュリティの最小の石数
	public static final int PATTERN_MINIMUM_LENGTH = 4;

	// パターンロック横画面対応時のStateを覚えておくためのKey
	public static final String CONFIGURATION_STATE_STATE = "lock_pattern_configuration_state_state";
	public static final String CONFIGURATION_STATE_PATTERN = "lock_pattern_configuration_state_pattern";

	// パターンロック画面に設定画面からきたかどうかの判定のKey
	public static final String KEY_PATTERN_IS_FROM_SETTING = "lock_pattern_from_setting";

	// パスワードロック画面に設定画面からきたかどうかの判定のKey
	public static final String KEY_PASSWORD_IS_FROM_SETTING = "lock_password_from_setting";

	private static LockUtilities sInstance = new LockUtilities();

	// 時計の表示
	public static final int CLOCK_TYPE_12 = 0;
	public static final int CLOCK_TYPE_24 = 1;

	// LockView
	private View mLockView = null;
	private WindowManager mWindowManager = null;

	// Keyguard
	private static KeyguardManager mKeyguard = null;
	@SuppressWarnings("deprecation")
	private static KeyguardManager.KeyguardLock mLock = null;

	private LockUtilities() {
	}

	public static LockUtilities getInstance() {
		if (sInstance == null) {
			sInstance = new LockUtilities();
		}
		return sInstance;
	}

	/**
	 * ロック画面を作成する
	 * 
	 * @param context
	 * @return
	 */
	private View createLockView(final Context context) {
		View lockView = LayoutInflater.from(context.getApplicationContext())
				.inflate(R.layout.locking, null, false);
		lockView.setOnKeyListener(new OnKeyListener() {
			private int repeatCount = 0;
			private boolean isChangedMannerMode = false;

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				final int action = event.getAction();
				if (action == KeyEvent.ACTION_DOWN) {
					repeatCount = event.getRepeatCount();
					if (repeatCount > 5) {
						switch (keyCode) {
						// ボリュームダウンボタン
						case KeyEvent.KEYCODE_VOLUME_DOWN:
							// マナーモード切り替え
							if (!isChangedMannerMode) {
								isChangedMannerMode = true;
								final AudioManager am = (AudioManager) context
										.getApplicationContext()
										.getSystemService(Context.AUDIO_SERVICE);
								final int ringerMode = am.getRingerMode();
								if (ringerMode == AudioManager.RINGER_MODE_NORMAL) {
									// マナーモードに設定
									am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
									((Vibrator) context.getApplicationContext()
											.getSystemService(
													Context.VIBRATOR_SERVICE))
											.vibrate(300);
								} else {
									// マナーモードを解除
									am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
								}
							}
							break;
						// シャッターボタン
						case KeyEvent.KEYCODE_CAMERA:
							// hardwareFlash(!mIsFlashLightOn);
							// mIsFlashLightOn = !mIsFlashLightOn;
							break;
						// カメラフォーカスボタン
						case KeyEvent.KEYCODE_FOCUS:
							// hardwareFlash(false);
							// mIsFlashLightOn = false;
							break;
						}
						repeatCount = 0;
					}
				}

				if (action == KeyEvent.ACTION_UP) {
					isChangedMannerMode = false;
				}

				return false;
			}
		});

		// ロック画面でイベント検出できるようにする
		lockView.setFocusableInTouchMode(true);
		return lockView;
	}

	/**
	 * ロック画面表示用のパラメータを作成する
	 * 
	 * @return
	 */
	private LayoutParams createLayoutParams(Context context) {
		// ロック画面表示用のパラメータ
		LayoutParams params = new LayoutParams();

		// ステータスバーは表示しておく
		params.flags = LayoutParams.FLAG_LAYOUT_IN_SCREEN;

		// 縦固定
		params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

		// ロック画面ビューのサイズ
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.MATCH_PARENT;

		params.type = LayoutParams.TYPE_PHONE;

		// 背景の透過
		params.format = PixelFormat.TRANSLUCENT;

		return params;
	}

	public void lock(Context context) {
		lock(context.getApplicationContext(), false);
	}

	/**
	 * 画面をロック（ロック画面を表示）する
	 * 
	 * @param context
	 */
	public synchronized void lock(Context context, boolean isForce) {
		if (isShowing()) {
			return;
		}

		// WindowManagerを取得
		if (mWindowManager == null) {
			mWindowManager = (WindowManager) context.getApplicationContext()
					.getSystemService(Context.WINDOW_SERVICE);
		}
		// ロック画面を作成
		mLockView = createLockView(context.getApplicationContext());

		try {
			// ロック画面をWindowManagerにアタッチ
			mWindowManager.addView(mLockView,
					createLayoutParams(context.getApplicationContext()));
			MetroCoverApplication.createRailwaysInfoList(context
					.getApplicationContext());
		} catch (Exception e) {
			// LockService is dead. Don't retry
		}
	}

	/**
	 * 画面ロックを解除する
	 */
	public synchronized void unlock(Context context) {
		if (!isShowing()) {
			return;
		}

		Animation animation = new AlphaAnimation(1, 0);
		animation.setDuration(300);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				try {
					mWindowManager.removeView(mLockView);
				} catch (IllegalArgumentException e) {
				} catch (Exception e) {
				} finally {
					terminate();
				}
			}
		});
		mLockView.findViewById(R.id.lock_layout_customview).startAnimation(
				animation);
	}

	public synchronized void unlock(int delay, final Context context) {
		new Handler() {
			@Override
			public void handleMessage(Message msg) {
				unlock(context.getApplicationContext());
			}
		}.sendMessageDelayed(new Message(), delay);
	}

	public void terminate() {
		try {
			mLockView = null;
			mWindowManager = null;
		} catch (NullPointerException e) {
		}
	}

	public boolean isShowing() {
		return mLockView != null;
	}

	/********************************************************
	 * キーガード
	 *********************************************************/
	/**
	 * キーガード(OS標準の画面ロック)を無効にする
	 * 
	 * @param context
	 */
	@SuppressWarnings("deprecation")
	public void disableKeyguard(Context context) {

		// 初期化して
		if (mKeyguard == null) {
			mKeyguard = (KeyguardManager) context.getApplicationContext()
					.getSystemService(Context.KEYGUARD_SERVICE);
			mLock = mKeyguard.newKeyguardLock("LockUtil");
		}

		// キーガードを無効化
		try {
			mLock.disableKeyguard();
		} catch (SecurityException se) {
		}
	}

	/**
	 * キーガード(OS標準の画面ロック)を有効にするO
	 */
	@SuppressWarnings("deprecation")
	public void enableKeyguard(Context context) {

		// キーガードを有効化
		if (mLock != null) {
			mLock.reenableKeyguard();
			mKeyguard = null;
			mLock = null;
		} else {
			if (mKeyguard == null) {
				mKeyguard = (KeyguardManager) context.getApplicationContext()
						.getSystemService(Context.KEYGUARD_SERVICE);
			}

			if (mLock == null) {
				try {
					mLock = mKeyguard.newKeyguardLock("LockUtil");
					mLock.reenableKeyguard();
				} catch (SecurityException e) {
				}
				mKeyguard = null;
				mLock = null;
			}
		}
	}

	/**
	 * パターンロックのマスターコード比較
	 * 
	 * @param context
	 * @param inputPattern
	 * @return
	 */
	public boolean getMasterPattern(Context context, String inputPattern) {
		if (inputPattern == null) {
			return false;
		}

		if (inputPattern.length() < PATTERN_MINIMUM_LENGTH) {
			return false;
		}

		final String masterPattern = context.getResources().getString(
				R.string.lock_pattern_master);
		return masterPattern.equals(inputPattern);
	}

	/**
	 * 曜日を取得
	 * 
	 * @param context
	 * @param weekId
	 * @return
	 */
	public String getDatOfWeek(final Context context, final int weekId) {
		final Resources res = context.getResources();
		switch (weekId) {
		case 0:
			return res.getString(R.string.day_of_week_saturday);
		case 1:
			return res.getString(R.string.day_of_week_sunday);
		case 2:
			return res.getString(R.string.day_of_week_monday);
		case 3:
			return res.getString(R.string.day_of_week_tuesday);
		case 4:
			return res.getString(R.string.day_of_week_wednesday);
		case 5:
			return res.getString(R.string.day_of_week_thursday);
		case 6:
			return res.getString(R.string.day_of_week_friday);
		default:
			break;
		}
		return "";
	}
}
