package metro.k.cover.lock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.api.ApiRequestTrainInfo;
import metro.k.cover.circularprogressbar.CircularProgressBar;
import metro.k.cover.lock.LockPatternView.Cell;
import metro.k.cover.lock.LockPatternView.DisplayMode;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.traininfo.TrainInfo;
import metro.k.cover.traininfo.TrainInfoListener;
import metro.k.cover.view.ButtonWithFont;
import metro.k.cover.view.EditTextWithFont;
import metro.k.cover.view.JazzyViewPager;
import metro.k.cover.view.JazzyViewPager.TransitionEffect;
import metro.k.cover.view.TextViewWithFont;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v4.view.PagerAdapter;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ロック画面のView
 */
@SuppressLint("HandlerLeak")
public class LockLayout extends FrameLayout implements View.OnClickListener {

	// Security Layout Resources
	private View mPassView;
	private View mPatternView;
	private int mSecurity;
	private String mPassword;
	private String mSecurityPattern;

	private String mPattern;
	private boolean isPatternPressed = false;

	// 着信系
	private LinearLayout mTelLayout;
	private static final int TEL_INFO_NUMBER = 0;
	private static final int TEL_INFO_CACHED_NAME = 1;
	private static final String TEL_MISSED_CALL_NUMBER = "-2";

	// ステータスバー
	private int mStatusBarHeight = -1;

	// vibe
	private Vibrator mVib;
	private static final int VIBELATE_TIME = 100;
	private boolean isEnableLock;

	// Pager
	private LockPagerAdapter mLockPagerAdapter;
	private JazzyViewPager mViewPager;
	private TransitionEffect mEffect = TransitionEffect.RotateDown;

	// unlock
	private LinearLayout mUnlockLayout;
	private ImageView mUnlockButon;
	private ImageView mUnlockCameraButon;
	private ImageView mUnlockMessageButon;

	// Clock
	private int mClockType = LockUtilities.CLOCK_TYPE_24;
	private LinearLayout mClockLinearLayout;
	private ImageView mClockSep;
	private TextViewWithFont mClockTextView;
	private TextViewWithFont mDataTextView;
	private static Calendar mCalendar = Calendar.getInstance();
	private int mClockColorID;

	// Battery
	private TextViewWithFont mBatteryView;

	// TrainInfo
	private int[] trainTypeIdArray = { R.id.lock_train_info_train_type_0,
			R.id.lock_train_info_train_type_1,
			R.id.lock_train_info_train_type_2,
			R.id.lock_train_info_train_type_3 };
	private int[] departureTimeIdArray = {
			R.id.lock_train_info_departure_time_0,
			R.id.lock_train_info_departure_time_1,
			R.id.lock_train_info_departure_time_2,
			R.id.lock_train_info_departure_time_3 };
	private final int TRAIN_TIME_TABLE_SIZE = 4;
	private TrainTimer mTrainTimer;
	private TextView mRemainingTimeTextView;

	public LockLayout(Context context) {
		super(context);
	}

	public LockLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LockLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**************************************
	 * Override
	 **************************************/
	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final Context c = getContext().getApplicationContext();
		regist(c);
		initPref(c);
		initialize();
		getTelInfo(c);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		disableTelephonyReceiver(getContext().getApplicationContext());
	}

	/**
	 * ステータスバーの制御
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		final Context c = getContext().getApplicationContext();
		Method collapse;
		try {
			if (!hasFocus) {
				super.onWindowFocusChanged(hasFocus);
				Object service = c.getSystemService("statusbar");
				Class<?> statusbarManager = Class
						.forName("android.app.StatusBarManager");
				if (Build.VERSION.SDK_INT <= 16) {
					collapse = statusbarManager.getMethod("collapse");
				} else {
					collapse = statusbarManager.getMethod("collapsePanels");
				}
				collapse.setAccessible(true);
				collapse.invoke(service);
			}
		} catch (Exception e) {
		} finally {
			LockUtilities.getInstance().lock(c, true);
			LockUtilities.getInstance().disableKeyguard(c);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		if (changed) {
			if (mStatusBarHeight == -1) {
				Rect rect = new Rect();
				getWindowVisibleDisplayFrame(rect);
				mStatusBarHeight = rect.top;
				findViewById(R.id.lock_foregroundLayout).setPadding(0,
						mStatusBarHeight, 0, 0);
			}
		}
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		// 不在着信
		if (R.id.telinfo_layout == viewId) {
			checkSecurityInfo(LockUtilities.UNLOCK_ID_MISSED_CALL);
			return;
		}
	}

	/**************************************
	 * Lock画面の設定
	 **************************************/

	/**
	 * Viewの準備
	 */
	private void initialize() {
		final Context context = getContext().getApplicationContext();

		// vibe
		mVib = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

		// 電話情報の受信開始
		final TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Viewpager
		mLockPagerAdapter = new LockPagerAdapter(context);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_1);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_LOCK);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_2);
		mViewPager = (JazzyViewPager) findViewById(R.id.lock_layout_viewpager);
		mViewPager.setAdapter(mLockPagerAdapter);
		mViewPager.setCurrentItem(1);
		mViewPager.setTransitionEffect(mEffect);
	}

	/**
	 * ロック画面を表示するReceiver登録
	 * 
	 * @param c
	 */
	private void regist(final Context c) {
		if (c == null) {
			return;
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
		c.registerReceiver(mBroadcastReceiver, filter);
	}

	/**
	 * Preferenceのデータを取得
	 * 
	 * @param context
	 */
	private void initPref(final Context context) {
		mSecurity = PreferenceCommon.getSecurityType(context);
		mPassword = PreferenceCommon.getCurrentPassword(context);
		mSecurityPattern = PreferenceCommon.getCurrentPattern(context);
		isEnableLock = PreferenceCommon.getMetroCover(context);

		if (isEnableLock) {
			LockUtilities.getInstance().disableKeyguard(context);
		}

		mEffect = PreferenceCommon.getViewPagerEffect(context);
		mClockType = PreferenceCommon.getClockType(context);
		mClockColorID = PreferenceCommon.getClockColor(context);
	}

	/**
	 * その他のBroadcastReceiver（ユーザー動作）
	 */
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			final Context c = getContext().getApplicationContext();

			// セキュリティチェックの際にスクリーンオフされたらセキュリティのViewを廃棄
			if (action.equals(Intent.ACTION_SCREEN_OFF)) {
				if (mPassView != null) {
					InputMethodManager inputMethodManager = (InputMethodManager) c
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(
							mPassView.getWindowToken(), 0);
					removePassView(mPassView);
				}
				if (mPatternView != null) {
					removePassView(mPatternView);
				}
				if (mViewPager != null) {
					if (mViewPager.getCurrentItem() == 2) {
						if (mTrainTimer != null) {
							mTrainTimer.cancel();
						}
						mViewPager.setCurrentItem(1);
					}
				}
				unregistClockReceiver();
				return;
			}

			if (action.equals(Intent.ACTION_SCREEN_ON)) {
				if (mLockPagerAdapter != null) {
					mLockPagerAdapter.initClock(context, mClockType);
				}
				registClockReceiver();
			}
		}
	};

	/**
	 * サービスが止まっていたら起動をかける
	 */
	private void checkAndRestart() {
		final Context c = getContext().getApplicationContext();
		final boolean isRunnning = LockService.isServiceRunning(c);
		if (isRunnning) {
			return;
		}

		try {
			c.startService(new Intent(c, LockService.class));
			LockUtilities.getInstance().disableKeyguard(c);
		} catch (Exception e) {
		}
	}

	/**************************************
	 * 着信系
	 **************************************/

	/**
	 * 着信のReceiverを解除
	 * 
	 * @param context
	 */
	private void disableTelephonyReceiver(Context context) {
		context.unregisterReceiver(mBroadcastReceiver);

		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
	}

	/**
	 * 着信のリスナー
	 */
	private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
		private int mState = TelephonyManager.CALL_STATE_IDLE;

		public int getState() {
			return mState;
		}

		// 電話コール状態の変化時に呼ばれる
		@Override
		public void onCallStateChanged(int state, String number) {
			final Resources res = getResources();
			mState = state;
			String str = res.getString(R.string.lock_state_telinfo);
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				str += res.getString(R.string.lock_get_calling);
				// ロック中に着信があったらアンロックする
				if (LockUtilities.getInstance().isShowing()) {
					LockUtilities.getInstance().unlock(getContext());
				}
			}
			if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				str += res.getString(R.string.lock_start_calling);
			}
			if (state == TelephonyManager.CALL_STATE_IDLE) {
				str += res.getString(R.string.lock_state_calling);
			}
			str += " " + number;
			super.onCallStateChanged(state, number);
		}
	};

	/**
	 * 着信履歴取得
	 * 
	 * @param context
	 */
	private void getTelInfo(final Context context) {
		if (context == null) {
			return;
		}

		List<String>[] telInfo = null;
		try {
			telInfo = getMissedCall(context);
		} catch (Exception e) {
		}

		if (telInfo == null) {
			return;
		}

		final int size = telInfo[TEL_INFO_NUMBER].size();
		if (size == 0) {
			return;
		}

		setMissedCallView(telInfo[TEL_INFO_NUMBER]);
	}

	/**
	 * 不在着信表示
	 * 
	 * @param list
	 */
	private void setMissedCallView(final List<String> list) {
		if (list == null) {
			return;
		}
		final int size = list.size();
		if (size == 0) {
			return;
		}

		final Context context = getContext().getApplicationContext();
		final Resources res = getResources();
		final View telView = LayoutInflater.from(context).inflate(
				R.layout.telinfo, null);
		mTelLayout = (LinearLayout) telView.findViewById(R.id.telinfo_layout);
		final int sideMargin = (int) res
				.getDimension(R.dimen.lockscreen_tel_margin);
		final int bottomMargin = (int) res
				.getDimension(R.dimen.lockscreen_tel_margin_bottom);
		mTelLayout.setPadding(0, bottomMargin, 0, bottomMargin);
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		layoutParams.setMargins(sideMargin, 0, sideMargin, bottomMargin);
		mTelLayout.setLayoutParams(layoutParams);

		// your tel-number
		TextViewWithFont number = (TextViewWithFont) telView
				.findViewById(R.id.telinfo_number);
		if (size != 0) {
			number.setText(size + res.getString(R.string.lock_matter));
		}
		mTelLayout.setOnClickListener(this);
		this.addView(telView);
	}

	/**
	 * 不在着信取得
	 * 
	 * @param context
	 * @return
	 */
	private List<String>[] getMissedCall(final Context context) {
		@SuppressWarnings("unchecked")
		List<String>[] telInfo = new ArrayList[2];
		final ContentResolver cr = context.getContentResolver();

		if (telInfo[TEL_INFO_NUMBER] == null) {
			for (int i = 0; i < telInfo.length; i++) {
				telInfo[i] = new ArrayList<String>();
			}
		}

		if (telInfo[TEL_INFO_NUMBER].size() != 0) {
			telInfo[TEL_INFO_NUMBER].clear();
			telInfo[TEL_INFO_CACHED_NAME].clear();
		}

		// 取得する情報
		final String[] projection = new String[] { CallLog.Calls.CACHED_NAME,
				CallLog.Calls.NUMBER, };

		// 情報の条件
		final String selection = CallLog.Calls.NEW + "=? AND "
				+ CallLog.Calls.TYPE + "=" + CallLog.Calls.MISSED_TYPE;
		final String[] selectionArgs = new String[] { "1" };

		// ソート順
		final String order = CallLog.Calls.DATE + " DESC";

		final Cursor c = cr.query(CallLog.Calls.CONTENT_URI, projection,
				selection, selectionArgs, order);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String number = c.getString(c
							.getColumnIndex(CallLog.Calls.NUMBER));
					String cachedName = c.getString(c
							.getColumnIndex(CallLog.Calls.CACHED_NAME));
					if (number.equals(TEL_MISSED_CALL_NUMBER)) {
						// 非通知
						number = context.getResources().getString(
								R.string.lock_missed_call_number_withheld);
					}
					telInfo[TEL_INFO_NUMBER].add(number);
					if (cachedName != null) {
						telInfo[TEL_INFO_CACHED_NAME].add(cachedName);
					} else {
						telInfo[TEL_INFO_CACHED_NAME].add("");
					}
				} while (c.moveToNext());
			}
			c.close();
		}

		return telInfo;
	}

	/**************************************
	 * セキュリティ系
	 **************************************/

	/**
	 * パスワードロックのView設定
	 */
	private void addPasswordSecurityView(final int unlockId) {
		final Context c = getContext().getApplicationContext();
		mPassView = LayoutInflater.from(c).inflate(R.layout.input_password,
				null, false);
		final TextViewWithFont title = (TextViewWithFont) mPassView
				.findViewById(R.id.lock_pass_title);
		final EditTextWithFont edittext = (EditTextWithFont) mPassView
				.findViewById(R.id.lock_pass_edittext);
		final ButtonWithFont comp = (ButtonWithFont) mPassView
				.findViewById(R.id.lock_pass_comp_btn);
		final ButtonWithFont cancel = (ButtonWithFont) mPassView
				.findViewById(R.id.lock_pass_cancel_btn);

		this.addView(mPassView);
		edittext.requestFocus();
		final InputMethodManager imm = (InputMethodManager) c
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
		comp.setOnClickListener(getPasswordOKListener(edittext, title, unlockId));
		cancel.setOnClickListener(getPasswordCancelListener());
	}

	/**
	 * 間違ったパスワードが入力されたとき
	 * 
	 * @param et
	 * @param tv
	 */
	private void setWrongPasswordView(final EditTextWithFont et,
			final TextViewWithFont tv) {
		if (et == null || tv == null || mVib == null) {
			return;
		}
		mVib.vibrate(VIBELATE_TIME);
		et.setText("");
		tv.setText(getResources().getString(R.string.lock_wrong_pass));
	}

	/**
	 * パスワードロックのOKボタン押下
	 * 
	 * @param et
	 * @param tv
	 * @return
	 */
	private final View.OnClickListener getPasswordOKListener(
			final EditTextWithFont et, final TextViewWithFont tv,
			final int unlockId) {
		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String pass = et.getText().toString();
				if (pass.equals("")) {
					setWrongPasswordView(et, tv);
					return;
				}

				if (mPassword.equals(pass)
						|| getResources().getString(
								R.string.lock_password_master).equals(pass)) {
					checkStarter(unlockId);
				} else {
					setWrongPasswordView(et, tv);
				}
			}
		};
		return listener;
	}

	/**
	 * パスワードロックのキャンセルボタン押下
	 * 
	 * @return
	 */
	private final View.OnClickListener getPasswordCancelListener() {
		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Context c = getContext().getApplicationContext();
				final InputMethodManager imm = (InputMethodManager) c
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				removePassView(mPassView);
			}
		};
		return listener;
	}

	/**
	 * パターンロックのView設定
	 */
	private void addPatternSecurityView(final int unlockId) {
		final Context c = getContext().getApplicationContext();
		mPatternView = LayoutInflater.from(c).inflate(R.layout.input_pattern,
				null, false);
		final LockPatternView patternView = (LockPatternView) mPatternView
				.findViewById(R.id.lock_patternView);
		final TextViewWithFont titleView = (TextViewWithFont) mPatternView
				.findViewById(R.id.lock_pattern_title);
		final ButtonWithFont cancelBtn = (ButtonWithFont) mPatternView
				.findViewById(R.id.lock_pattern_cancel_btn);

		patternView.setOnPatternListener(getPatternListener(patternView,
				titleView, unlockId));
		cancelBtn.setOnClickListener(getPatternCancelListener(patternView));
		this.addView(mPatternView);
	}

	/**
	 * パターンロックのパターンリスナー
	 * 
	 * @param patternView
	 * @param tv
	 * @return
	 */
	private final LockPatternView.OnPatternListener getPatternListener(
			final LockPatternView patternView, final TextViewWithFont tv,
			final int unlockId) {
		final LockPatternView.OnPatternListener listener = new LockPatternView.OnPatternListener() {

			@Override
			public void onPatternStart() {
			}

			@Override
			public void onPatternDetected(List<Cell> dPattern) {
				String onlyNumStr = "";
				for (int i = 0; i < dPattern.size(); i++) {
					onlyNumStr += dPattern.get(i).toMailString();
				}
				mPattern = dPattern.toString();
				isPatternPressed = true;

				if (mPattern.equals(mSecurityPattern)
						|| LockUtilities.getInstance().getMasterPattern(
								getContext().getApplicationContext(),
								onlyNumStr)) {
					tv.setVisibility(View.INVISIBLE);
					checkStarter(unlockId);
				} else {
					tv.setVisibility(View.VISIBLE);
					patternView.setDisplayMode(DisplayMode.Wrong);
					patternView.enableInput();
				}
			}

			@Override
			public void onPatternCleared() {
				mPattern = "";
			}

			@Override
			public void onPatternCellAdded(List<Cell> pattern) {
			}
		};
		return listener;
	}

	/**
	 * パターンロックのキャンセルボタン押下
	 * 
	 * @param patternView
	 * @return
	 */
	private final View.OnClickListener getPatternCancelListener(
			final LockPatternView patternView) {
		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (patternView == null) {
					return;
				}

				mPattern = "";
				if (isPatternPressed) {
					patternView.clearPattern();
					patternView.enableInput();
					isPatternPressed = false;
				} else {
					removePassView(mPatternView);
				}
			}
		};
		return listener;
	}

	/**
	 * セキュリティタイプの判定
	 */
	private void checkSecurityInfo(final int unlockId) {
		checkAndRestart();
		// パスワード
		final Resources res = getResources();
		if (res.getInteger(R.integer.lock_security_type_password) == mSecurity) {
			addPasswordSecurityView(unlockId);
			hideCalenderLayout();
			hideUnlockLayout();
			return;
		}
		// パターン
		if (res.getInteger(R.integer.lock_security_type_pattern) == mSecurity) {
			addPatternSecurityView(unlockId);
			hideCalenderLayout();
			hideUnlockLayout();
			return;
		}
		// セリュリティなし
		checkStarter(unlockId);
	}

	/**
	 * 起動するものがあるのかどうか
	 * 
	 * @param unlockId
	 */
	private void checkStarter(final int unlockId) {
		switch (unlockId) {
		case LockUtilities.UNLOCK_ID_UNLOCK:
			LockUtilities.getInstance().unlock(getContext());
			break;
		case LockUtilities.UNLOCK_ID_CAMERA:
			LockUtilities.getInstance().startCamera(getContext());
			break;
		case LockUtilities.UNLOCK_ID_MESSANGER:
			LockUtilities.getInstance().startLINE(getContext());
			break;
		case LockUtilities.UNLOCK_ID_MISSED_CALL:
			LockUtilities.getInstance().startTelephone(getContext());
		default:
			break;
		}
	}

	/**
	 * 時計を非表示
	 */
	private void hideCalenderLayout() {
		if (mClockLinearLayout == null) {
			return;
		}
		if (mClockLinearLayout.getVisibility() == View.INVISIBLE) {
			return;
		}
		mClockLinearLayout.setVisibility(View.INVISIBLE);
	}

	/**
	 * 解除レイアウトを非表示
	 */
	private void hideUnlockLayout() {
		if (mUnlockLayout == null) {
			return;
		}
		if (mUnlockLayout.getVisibility() == View.INVISIBLE) {
			return;
		}
		mUnlockLayout.setVisibility(View.INVISIBLE);
	}

	/**
	 * パスワードおよびパターンロックのViewの破棄
	 * 
	 * @param v
	 */
	private void removePassView(View v) {
		this.removeView(v);
		if (mClockLinearLayout != null) {
			if (mClockLinearLayout.getVisibility() != View.VISIBLE) {
				mClockLinearLayout.setVisibility(View.VISIBLE);
			}
		}
		if (mUnlockLayout != null) {
			if (mUnlockLayout.getVisibility() != View.VISIBLE) {
				mUnlockLayout.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 時計&バッテリーのBroadcastReceiver
	 */
	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (context == null || intent == null)
				return;

			if (mLockPagerAdapter == null) {
				return;
			}

			final String action = intent.getAction();
			if (action.equals(Intent.ACTION_TIME_TICK)) {
				mLockPagerAdapter.initClock(context, mClockType);
				return;
			}

			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				final int battery = intent.getIntExtra("level", 0);
				mLockPagerAdapter.initBatteryView(battery);
			}
		}
	};

	/**************************************
	 * 時計系
	 **************************************/

	/**
	 * 時計更新のBroadcastReceiverを解除
	 */
	private void unregistClockReceiver() {
		try {
			getContext().getApplicationContext().unregisterReceiver(
					mUpdateReceiver);
		} catch (Exception e) {
		}
	}

	/**
	 * 時計更新のBroadcastReceiverを登録
	 */
	private void registClockReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		try {
			getContext().getApplicationContext().registerReceiver(
					mUpdateReceiver, filter);
		} catch (Exception e) {
		}
	}

	/**
	 * ロック画面Viewpagerのアダプター
	 * 
	 * @author kohirose
	 * 
	 */
	public class LockPagerAdapter extends PagerAdapter implements
			TrainInfoListener {

		public static final String PAGE_LOCK = "page_lock";
		public static final String PAGE_PAGE_TRAIN_INFO_1 = "page_train_info_1";
		public static final String PAGE_PAGE_TRAIN_INFO_2 = "page_train_info_2";

		private int MASSAGE_TRAIN_INFO_LIST = 0;
		private int MASSAGE_TRAIN_INFO_ERROR = 1;
		private Context mContext;
		private ArrayList<String> mList;
		private Object mPrimaryItem;
		private LayoutInflater mLayoutInflater;
		private int mLastPosition = 0;
		private ApiRequestTrainInfo mApiRequestTrainInfo;
		private Handler mHandler;

		public LockPagerAdapter(Context context) {
			mContext = context;
			mList = new ArrayList<String>();
			mLayoutInflater = LayoutInflater.from(mContext);
			mApiRequestTrainInfo = new ApiRequestTrainInfo(context);
			mApiRequestTrainInfo.setListener(this);
			createHandler();
		}

		private void createHandler() {
			if (mHandler != null) {
				return;
			}

			mHandler = new Handler() {
				public void handleMessage(Message message) {
					if (mViewPager == null) {
						return;
					}

					final int pageId = mViewPager.getCurrentItem();
					final int what = message.what;
					if (pageId == 2) {
						if (what == MASSAGE_TRAIN_INFO_LIST) {
							drawingTrainInfoView(MetroCoverApplication
									.getTrainInfoList());
						} else {
							RelativeLayout layout = (RelativeLayout) mLockPagerAdapter
									.getPrimaryItem();
							if (layout == null) {
								return;
							}

							CircularProgressBar progress = (CircularProgressBar) layout
									.findViewById(R.id.lock_traininfo_loading);
							setEmptyTrainInfoView(layout, progress);
						}
					}
				};
			};
		}

		public void add(String pageName) {
			mList.add(pageName);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View pageView = null;
			final String pageName = mList.get(position);
			if (pageName.equals(PAGE_LOCK)) {
				pageView = getCenterLayout();
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
				pageView = getleftLayout();
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
				pageView = getRightLayout();
			}
			LockUtilities.getInstance().setBackgrondLoadBitmap(mContext,
					pageView, position);
			container.addView(pageView);
			mViewPager.setObjectForPosition(pageView, position);
			return pageView;
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			super.setPrimaryItem(container, position, object);
			if (mLastPosition == position) {
				return;
			}
			mLastPosition = position;
			mPrimaryItem = object;
			countup(position);
		}

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object keyObject) {
			return view == keyObject;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		/**
		 * 左画面のレイアウト取得
		 * 
		 * @return
		 */
		private RelativeLayout getleftLayout() {
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.lock_railways_info, null);
			return trainInfoLayout;
		}

		/**
		 * 右画面のレイアウト取得
		 * 
		 * @return
		 */
		private RelativeLayout getRightLayout() {
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.lock_train_info, null);
			return trainInfoLayout;
		}

		/**
		 * ロック解除画面のレイアウト取得
		 * 
		 * @return
		 */
		private RelativeLayout getCenterLayout() {
			RelativeLayout lockLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_lock, null);
			mClockLinearLayout = (LinearLayout) lockLayout
					.findViewById(R.id.lock_data_layout);
			mClockSep = (ImageView) lockLayout.findViewById(R.id.lock_time_sep);
			mClockTextView = (TextViewWithFont) lockLayout
					.findViewById(R.id.lock_time_textview);
			mDataTextView = (TextViewWithFont) lockLayout
					.findViewById(R.id.lock_date_textview);
			mBatteryView = (TextViewWithFont) lockLayout
					.findViewById(R.id.lock_battery_textview);
			mUnlockLayout = (LinearLayout) lockLayout
					.findViewById(R.id.lock_unlock_layout);
			mUnlockButon = (ImageView) lockLayout
					.findViewById(R.id.lock_unlock_button);
			mUnlockCameraButon = (ImageView) lockLayout
					.findViewById(R.id.lock_camera_button);
			mUnlockMessageButon = (ImageView) lockLayout
					.findViewById(R.id.lock_messanger_button);
			mUnlockButon
					.setOnClickListener(getUnlockListener(LockUtilities.UNLOCK_ID_UNLOCK));
			mUnlockCameraButon
					.setOnClickListener(getUnlockListener(LockUtilities.UNLOCK_ID_CAMERA));
			mUnlockMessageButon
					.setOnClickListener(getUnlockListener(LockUtilities.UNLOCK_ID_MESSANGER));
			initClock(mContext, mClockType);
			Intent bat = mContext.registerReceiver(null, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
			initBatteryView(bat.getIntExtra("level", 0));
			return lockLayout;
		}

		/**
		 * ロック解除のリスナー
		 * 
		 * @return
		 */
		private View.OnClickListener getUnlockListener(final int unlockId) {
			View.OnClickListener li = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mVib.vibrate(VIBELATE_TIME);
					checkSecurityInfo(unlockId);
				}
			};
			return li;
		}

		public Object getPrimaryItem() {
			return mPrimaryItem;
		}

		private void countup(int position) {
			final String pageName = mList.get(position);
			if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
				readRailwaysInfo();
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						final String stationName = PreferenceCommon
								.getStationNameForAPI(mContext);
						final String trainDirection = PreferenceCommon
								.getTrainDirection(mContext);
						mApiRequestTrainInfo.requestTrainInfo(stationName,
								trainDirection);
					}
				}).start();
			}
		}

		@Override
		public void completeCreateTimeTable(ArrayList<TrainInfo> timetable) {
			PreferenceCommon.setTimeLoadedTrainTimeTable(mContext,
					System.currentTimeMillis());
			Message msg = mHandler.obtainMessage();
			msg.what = MASSAGE_TRAIN_INFO_LIST;
			MetroCoverApplication.setTrainInfoList(timetable);
			mHandler.sendMessage(msg);
		}

		@Override
		public void failedToCreateTimeTable() {
			Message msg = mHandler.obtainMessage();
			msg.what = MASSAGE_TRAIN_INFO_ERROR;
			mHandler.sendMessage(msg);
		}

		/**
		 * 路線遅延情報のViewを作成する
		 */
		private void readRailwaysInfo() {
			RelativeLayout layout = (RelativeLayout) getPrimaryItem();
			RelativeLayout listLayout = (RelativeLayout) layout
					.findViewById(R.id.lock_railways_info_mainlayout);
			ListView listview = (ListView) layout
					.findViewById(R.id.lock_railways_info_listview);
			RelativeLayout empty = (RelativeLayout) layout
					.findViewById(R.id.lock_railways_empty_view);
			CircularProgressBar cpb = (CircularProgressBar) layout
					.findViewById(R.id.lock_railways_loading);
			TextViewWithFont title = (TextViewWithFont) layout
					.findViewById(R.id.lock_railways_empty_title);
			TextViewWithFont msg = (TextViewWithFont) layout
					.findViewById(R.id.lock_railways_empty_message);
			TextViewWithFont lastupdate = (TextViewWithFont) layout
					.findViewById(R.id.lock_railways_info_lastupdate);
			ImageView reflesh = (ImageView) layout
					.findViewById(R.id.lock_railways_info_reflesh);
			ImageView reflesh_empty = (ImageView) layout
					.findViewById(R.id.lock_railways_info_empty_reflesh);
			if (LockUtilities.getInstance().isInvalidAdapter(
					MetroCoverApplication.getRailwaysInfoList())) {
				setEmptyRailwayInfoView(listLayout, empty, cpb, title, msg,
						reflesh_empty);
				return;
			}
			reflesh.setOnClickListener(getRefleshListner());
			lastupdate.setText(MetroCoverApplication.getLastUpdateTime());
			listview.setAdapter(MetroCoverApplication.getRailwaysInfoList());
			empty.setVisibility(View.INVISIBLE);
			listLayout.setVisibility(View.VISIBLE);
		}

		/**
		 * 更新ボタン押下時の処理
		 * 
		 * @return
		 */
		private View.OnClickListener getRefleshListner() {
			View.OnClickListener li = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					refleshRailsInfo();
				}
			};
			return li;
		}

		/**
		 * 路線情報の更新
		 */
		synchronized private void refleshRailsInfo() {
			final Handler h = new Handler();
			new Thread("reflesh") {
				@Override
				public void run() {
					MetroCoverApplication.syncCreateRailwaysInfoList(mContext);
					h.post(mReadRailwaysInfoTask);
				}
			}.start();
		}

		private Runnable mReadRailwaysInfoTask = new Runnable() {
			@Override
			public void run() {
				readRailwaysInfo();
			}
		};

		/**
		 * 登録の路線が空の場合もしくはエラーの場合
		 * 
		 * @param listLayout
		 * @param empty
		 * @param cpb
		 * @param title
		 * @param msg
		 */
		private void setEmptyRailwayInfoView(RelativeLayout listLayout,
				RelativeLayout empty, CircularProgressBar cpb,
				TextViewWithFont title, TextViewWithFont msg,
				ImageView reflesh_empty) {
			if (listLayout == null || empty == null || cpb == null
					|| title == null || msg == null || reflesh_empty == null) {
				return;
			}

			listLayout.setVisibility(View.GONE);
			empty.setVisibility(View.VISIBLE);
			title.setVisibility(View.VISIBLE);
			msg.setVisibility(View.VISIBLE);
			if (!Utilities.isOnline(mContext)) {
				msg.setText(mContext.getResources().getString(
						R.string.err_msg_offline));
			}
			cpb.setVisibility(View.GONE);
			reflesh_empty.setVisibility(View.VISIBLE);
			reflesh_empty.setOnClickListener(getRefleshListner());
		}

		/**
		 * 時計の描画
		 * 
		 * @param context
		 * @param is24h
		 */
		private void initClock(final Context context, final int type) {
			if (mClockLinearLayout == null || mClockTextView == null
					|| mDataTextView == null) {
				return;
			}

			final boolean is24h = type == LockUtilities.CLOCK_TYPE_24;
			Date date = new Date();
			mCalendar.setTime(date);
			final int am_pm = mCalendar.get(Calendar.AM_PM);
			final int hour = mCalendar.get(is24h ? Calendar.HOUR_OF_DAY
					: Calendar.HOUR);
			final int minute = mCalendar.get(Calendar.MINUTE);
			final int year = mCalendar.get(Calendar.YEAR);
			final int month = mCalendar.get(Calendar.MONTH) + 1;
			final int day = mCalendar.get(Calendar.DAY_OF_MONTH);
			final int week_of_day = mCalendar.get(Calendar.DAY_OF_WEEK);

			final Resources res = getResources();
			final int color = res.getColor(mClockColorID);

			// 時間
			final String minuteStr = Utilities.getTwoDigits(minute);
			final String col = res.getString(R.string.colon);
			boolean isAm = am_pm == 0;
			String timeStr = "";
			StringBuilder timeBuilder = new StringBuilder();
			if (is24h) {
				timeBuilder.append(hour);
				timeBuilder.append(col);
				timeBuilder.append(minuteStr);
				timeStr = timeBuilder.toString();
			} else {
				final String am = res.getString(R.string.clock_am);
				final String pm = res.getString(R.string.clock_pm);
				timeBuilder.append(hour);
				timeBuilder.append(col);
				timeBuilder.append(minuteStr);
				timeBuilder.append(" ");
				timeBuilder.append((isAm ? am : pm));
				timeStr = timeBuilder.toString();
			}
			mClockTextView.setText(timeStr);
			mClockTextView.setTextColor(color);

			// 日にち
			boolean isJp = false;
			Locale locale = Locale.getDefault();
			if (locale.equals(Locale.JAPAN)) {
				isJp = true;
			}
			String dateStr = "";
			StringBuilder builder = new StringBuilder();
			final String wod = LockUtilities.getInstance().getDatOfWeek(
					context, week_of_day);
			if (isJp) {
				final String y = res.getString(R.string.date_year);
				final String m = res.getString(R.string.date_month);
				final String d = res.getString(R.string.date_day);
				builder.append(year);
				builder.append(y);
				builder.append(month);
				builder.append(m);
				builder.append(day);
				builder.append(d);
				builder.append(wod);
				dateStr = builder.toString();
			} else {
				builder.append(wod);
				builder.append(" ");
				builder.append(month);
				builder.append("/");
				builder.append(day);
				builder.append("/");
				builder.append(year);
				dateStr = builder.toString();
			}
			mDataTextView.setText(dateStr);
			mDataTextView.setTextColor(color);
			mClockSep.setBackgroundColor(color);
		}

		/**
		 * バッテリー状態の更新
		 * 
		 * @param battery
		 */
		private void initBatteryView(final int battery) {
			if (mBatteryView == null) {
				return;
			}

			StringBuilder builder = new StringBuilder();
			builder.append(String.valueOf(battery));
			builder.append("%");
			mBatteryView.setText(builder.toString());
			mBatteryView.setTextColor(getResources().getColor(mClockColorID));
		}
	}

	/**
	 * 未登録の場合
	 * 
	 * @param rootView
	 * @param cpb
	 */
	private void setEmptyTrainInfoView(RelativeLayout rootView,
			CircularProgressBar cpb) {
		if (rootView == null || cpb == null) {
			return;
		}
		LinearLayout main = (LinearLayout) rootView
				.findViewById(R.id.lock_train_info_title_layout);
		LinearLayout empty = (LinearLayout) rootView
				.findViewById(R.id.lock_traininfo_empty_message);
		if (main == null || empty == null) {
			return;
		}
		empty.setVisibility(View.VISIBLE);
		main.setVisibility(View.INVISIBLE);
		cpb.setVisibility(View.GONE);
	}

	/**
	 * 駅時刻表レイアウト描画
	 * 
	 * @param trainInfoList
	 */
	private void drawingTrainInfoView(ArrayList<TrainInfo> trainInfoList) {
		// Invalid
		if (mViewPager == null) {
			return;
		}

		if (mViewPager.getCurrentItem() != 2) {
			return;
		}

		// 空の場合
		RelativeLayout layout = (RelativeLayout) mLockPagerAdapter
				.getPrimaryItem();
		CircularProgressBar progress = (CircularProgressBar) layout
				.findViewById(R.id.lock_traininfo_loading);
		if (trainInfoList == null) {
			setEmptyTrainInfoView(layout, progress);
			return;
		}
		final int size = trainInfoList.size();
		if (size == 0) {
			setEmptyTrainInfoView(layout, progress);
			return;
		}

		// 正常
		Context context = getContext().getApplicationContext();

		// 駅名
		final String stationName = PreferenceCommon.getStationName(context);
		TextView tvStation = (TextView) layout
				.findViewById(R.id.lock_train_info_station);
		if (tvStation == null) {
			setEmptyTrainInfoView(layout, progress);
			return;
		}
		tvStation.setText(stationName);
		if (stationName.equals(context.getString(R.string.nothing))) {
			setEmptyTrainInfoView(layout, progress);
			return;
		}

		// 路線名
		TextView tvRailway = (TextView) layout
				.findViewById(R.id.lock_train_info_railway);
		final String railwayName = PreferenceCommon
				.getStationsRailwayName(context);
		tvRailway.setText(railwayName);

		// 駅アイコン
		final Drawable icon = RailwaysUtilities.getStationIcon(context,
				stationName,
				RailwaysUtilities.getRailwayCodeFromName(context, railwayName));
		if (icon != null) {
			ImageView iconView = (ImageView) layout
					.findViewById(R.id.lock_train_info_icon);
			iconView.setImageDrawable(icon);
			iconView.setVisibility(View.VISIBLE);
		}

		// 方面
		TextView tvRailDirection = (TextView) layout
				.findViewById(R.id.lock_train_info_rail_direction);
		final String bound = context.getString(R.string.bound);
		tvRailDirection.setText(PreferenceCommon.getTrainDirectionName(context)
				+ " " + bound);

		// 時刻表
		TrainInfo trainInfo0 = trainInfoList.get(0);
		int departureTimeHour = trainInfo0.getHour();
		if (0 <= departureTimeHour && departureTimeHour < 4) {
			departureTimeHour = departureTimeHour + 24;
		}
		final int departureTimeMinute = trainInfo0.getMinute();
		mRemainingTimeTextView = (TextView) layout
				.findViewById(R.id.lock_train_info_remaining_time);
		final long remainingTimeMillis = getRemainingTimeMillis(
				departureTimeHour, departureTimeMinute);
		if (mTrainTimer != null) {
			mTrainTimer.cancel();
			mTrainTimer = null;
		}

		mTrainTimer = new TrainTimer(remainingTimeMillis);
		mTrainTimer.start();

		final int listSize = Math.min(TRAIN_TIME_TABLE_SIZE, size);
		for (int i = 0; i < listSize; i++) {
			TrainInfo trainInfo = trainInfoList.get(i);
			if (trainInfo == null) {
				continue;
			}

			TextView tvDepartureTime = (TextView) layout
					.findViewById(departureTimeIdArray[i]);
			final int hour = trainInfo.getHour();
			final int minute = trainInfo.getMinute();
			final String departureTimeText = context.getString(
					R.string.departure_time, hour,
					Utilities.getTwoDigits(minute));
			tvDepartureTime.setText(departureTimeText);

			TextView tvTrainType = (TextView) layout
					.findViewById(trainTypeIdArray[i]);
			final String trainTypeForApi = trainInfo.getTrainType();
			tvTrainType.setText(conversionTrainTypeText(trainTypeForApi));
		}

		View sep = (View) layout.findViewById(R.id.divider_0);
		sep.setVisibility(View.VISIBLE);
		sep = (View) layout.findViewById(R.id.divider_1);
		sep.setVisibility(View.VISIBLE);
		sep = (View) layout.findViewById(R.id.divider_2);
		sep.setVisibility(View.VISIBLE);
		sep = (View) layout.findViewById(R.id.divider_3);
		sep.setVisibility(View.VISIBLE);
		progress.setVisibility(View.GONE);
	}

	private String conversionTrainTypeText(String trainTypeForApi) {
		Context context = getContext().getApplicationContext();
		String trainType = "";
		if (trainTypeForApi.equals(context
				.getString(R.string.train_type_local_response))) {
			trainType = context.getString(R.string.train_type_local);
		} else if (trainTypeForApi.equals(context
				.getString(R.string.train_type_express_response))) {
			trainType = context.getString(R.string.train_type_express);
		} else if (trainTypeForApi.equals(context
				.getString(R.string.train_type_rapid_response))) {
			trainType = context.getString(R.string.train_type_rapid);
		} else if (trainTypeForApi.equals(context
				.getString(R.string.train_type_limited_express_response))) {
			trainType = context.getString(R.string.train_type_limited_express);
		}
		return trainType;
	}

	private long getRemainingTimeMillis(final long hour, final long minute) {
		long departureTimeMillis = (hour * 60 + minute)
				* Utilities.ONE_MUNUTE_MILLIS;
		final Calendar calendar = Calendar.getInstance();
		int currentTimeHour = calendar.get(Calendar.HOUR_OF_DAY);
		if (0 <= currentTimeHour && currentTimeHour < 4
				&& currentTimeHour > hour) {
			currentTimeHour = currentTimeHour + 24;
		}

		final int currentTimeMinute = calendar.get(Calendar.MINUTE);
		final int currentTimeSecond = calendar.get(Calendar.SECOND);
		long currentTimeMillis = ((currentTimeHour * 60 + currentTimeMinute) * 60 + currentTimeSecond) * 1000;
		return departureTimeMillis - currentTimeMillis;
	}

	private long[] getFormatedRemainingTime(long remainingTime) {
		long[] formatedRemainingTime = { 0, 0, 0 };
		if (remainingTime > Utilities.ONE_HOUR_MILLIS) {
			final long hour = remainingTime / Utilities.ONE_HOUR_MILLIS;
			formatedRemainingTime[0] = hour;
			remainingTime = remainingTime - hour * Utilities.ONE_HOUR_MILLIS;
		}
		if (remainingTime > Utilities.ONE_MUNUTE_MILLIS) {
			final long minute = remainingTime / Utilities.ONE_MUNUTE_MILLIS;
			formatedRemainingTime[1] = minute;
			remainingTime = remainingTime - minute
					* Utilities.ONE_MUNUTE_MILLIS;
		}
		if (remainingTime > Utilities.ONE_SECOND_MILLIS) {
			final long second = remainingTime / Utilities.ONE_SECOND_MILLIS;
			formatedRemainingTime[2] = second;
		}
		return formatedRemainingTime;
	}

	private String buildRemainingTimeText(long[] formatedRemainingTime) {
		Context context = getContext().getApplicationContext();
		StringBuilder sb = new StringBuilder();
		if (formatedRemainingTime[0] > 0) {
			sb.append(formatedRemainingTime[0]);
			sb.append(context.getString(R.string.remaining_time_hour));
		}
		if (formatedRemainingTime[1] > 0) {
			sb.append(formatedRemainingTime[1]);
			sb.append(context.getString(R.string.remaining_time_minute));
		}
		sb.append(formatedRemainingTime[2]);
		sb.append(context.getString(R.string.remaining_time_second));
		return sb.toString();
	}

	/**
	 * カウントダウンタイマー
	 * 
	 * @author ktsukag
	 * 
	 */
	public class TrainTimer extends CountDownTimer {

		private int mRedColor;
		private int mWhiteColor;

		public TrainTimer(long millisInFuture) {
			super(millisInFuture, Utilities.ONE_SECOND_MILLIS);
			final Resources res = getResources();
			mRedColor = res.getColor(R.color.marunouchi_red);
			mWhiteColor = res.getColor(R.color.color_white);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (mViewPager == null) {
				return;
			}
			int pageId = mViewPager.getCurrentItem();
			if (pageId != 2) {
				return;
			}
			final long[] formatedRemainingTime = getFormatedRemainingTime(millisUntilFinished);
			String remainingTimeText = buildRemainingTimeText(formatedRemainingTime);
			mRemainingTimeTextView.setText(remainingTimeText);
			if (millisUntilFinished < Utilities.ONE_MUNUTE_MILLIS) {
				mRemainingTimeTextView.setTextColor(mRedColor);
			} else {
				mRemainingTimeTextView.setTextColor(mWhiteColor);
			}
		}

		@Override
		public void onFinish() {
			cancel();
			ArrayList<TrainInfo> trainInfoList = MetroCoverApplication
					.getTrainInfoList();
			if (trainInfoList != null && trainInfoList.size() > 0) {
				trainInfoList.remove(0);
				MetroCoverApplication.setTrainInfoList(trainInfoList);
				drawingTrainInfoView(trainInfoList);
			}
		}
	}
}
