package metro.k.cover.lock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import metro.k.cover.ImageCache;
import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.api.ApiRequestTrainInfo;
import metro.k.cover.circularprogressbar.CircularProgressBar;
import metro.k.cover.lock.LockPatternView.Cell;
import metro.k.cover.lock.LockPatternView.DisplayMode;
import metro.k.cover.railways.RailwaysInfo;
import metro.k.cover.traininfo.TrainInfoListener;
import metro.k.cover.view.ButtonWithFont;
import metro.k.cover.view.JazzyViewPager;
import metro.k.cover.view.JazzyViewPager.TransitionEffect;
import metro.k.cover.view.TextViewWithFont;
import metro.k.cover.wallpaper.WallpaperBitmapDB;
import metro.k.cover.wallpaper.WallpaperUtilities;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ロック画面のView
 */
public class LockLayout extends FrameLayout implements View.OnClickListener,
		View.OnLongClickListener {

	// Security Layout Resources
	private View mPassView;
	private View mPatternView;
	private int mSecurity;
	private String mPassword;
	private String mSecurityPattern;

	// master pass
	private final String HOMEE_SYSTEM_PASS = "###***&&&";

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
	private KeyView mKeyView;
	private OnLockPageChangeListener mOnLockPageChangeListener = new OnLockPageChangeListener();
	private TransitionEffect mEffect = TransitionEffect.RotateDown;

	// Clock
	private int mClockType = LockUtilities.CLOCK_TYPE_24;
	private LinearLayout mClockLinearLayout;
	private TextViewWithFont mClockTextView;
	private TextViewWithFont mDataTextView;
	private static Calendar mCalendar = Calendar.getInstance();
	private int mClockColorID;
	private int mClockSizeID;
	private float mClockTimeSize;
	private float mClockDateSize;

	// Battery
	private TextViewWithFont mBatteryView;

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
		int viewId = v.getId();

	}

	@Override
	public boolean onLongClick(View v) {
		return false;
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
		mViewPager.setOnPageChangeListener(mOnLockPageChangeListener);
		

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
		mClockSizeID = PreferenceCommon.getClockSize(context);

		final Resources res = getResources();
		if (mClockSizeID == res.getInteger(R.integer.lock_clock_size_small)) {
			mClockDateSize = res
					.getDimensionPixelSize(R.dimen.clock_size_date_small);
			mClockTimeSize = res
					.getDimensionPixelSize(R.dimen.clock_size_time_small);
		} else if (mClockSizeID == res
				.getInteger(R.integer.lock_clock_size_midium)) {
			mClockDateSize = res
					.getDimensionPixelSize(R.dimen.clock_size_date_midium);
			mClockTimeSize = res
					.getDimensionPixelSize(R.dimen.clock_size_time_midium);
		} else if (mClockSizeID == res
				.getInteger(R.integer.lock_clock_size_large)) {
			mClockDateSize = res
					.getDimensionPixelSize(R.dimen.clock_size_date_large);
			mClockTimeSize = res
					.getDimensionPixelSize(R.dimen.clock_size_time_large);
		}
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
		if (context == null)
			return;

		List<String>[] telInfo = null;
		try {
			telInfo = getMissedCall(context);
		} catch (Exception e) {
		}

		if (telInfo == null)
			return;

		final int size = telInfo[TEL_INFO_NUMBER].size();
		if (size == 0)
			return;

		setMissedCallView(telInfo[TEL_INFO_NUMBER]);
	}

	/**
	 * 不在着信表示
	 * 
	 * @param list
	 */
	private void setMissedCallView(final List<String> list) {
		if (list == null)
			return;
		final int size = list.size();
		if (size == 0)
			return;

		// final Context context = getContext().getApplicationContext();
		// final Resources res = getResources();
		// final View telView = LayoutInflater.from(context).inflate(
		// R.layout.telinfo, null);
		// mTelLayout = (LinearLayout)
		// telView.findViewById(R.id.telinfo_layout);
		// mTelLayout.setBackgroundResource(R.drawable.selector_btn);
		// final int sideMargin = (int) res
		// .getDimension(R.dimen.lockscreen_tel_margin);
		// final int bottomMargin = (int) res
		// .getDimension(R.dimen.lockscreen_tel_margin_bottom);
		// mTelLayout.setPadding(0, bottomMargin, 0, bottomMargin);
		// RelativeLayout.LayoutParams layoutParams = new
		// RelativeLayout.LayoutParams(
		// MP, WC);
		// layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		// layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
		// layoutParams.setMargins(sideMargin, 0, sideMargin, bottomMargin);
		// mTelLayout.setLayoutParams(layoutParams);
		//
		// // your tel-number
		// TextView number = (TextView)
		// telView.findViewById(R.id.telinfo_number);
		// if (size != 0) {
		// number.setText(size + res.getString(R.string.lock_matter));
		// }
		// mTelLayout.setOnClickListener(this);
		// this.addView(telView);
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
	private void addPasswordSecurityView() {
		final Context c = getContext().getApplicationContext();
		final AssetManager am = c.getAssets();
		final Resources res = c.getResources();
		mPassView = LayoutInflater.from(c).inflate(R.layout.input_password,
				null, false);
		final TextViewWithFont title = (TextViewWithFont) mPassView
				.findViewById(R.id.lock_pass_title);
		final EditText edittext = (EditText) mPassView
				.findViewById(R.id.lock_pass_edittext);
		final ButtonWithFont comp = (ButtonWithFont) mPassView
				.findViewById(R.id.lock_pass_comp_btn);
		final ButtonWithFont cancel = (ButtonWithFont) mPassView
				.findViewById(R.id.lock_pass_cancel_btn);

		Utilities.setFontEditTextView(edittext, am, res);

		this.addView(mPassView);
		edittext.requestFocus();
		final InputMethodManager imm = (InputMethodManager) c
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
		comp.setOnClickListener(getPasswordOKListener(edittext, title));
		cancel.setOnClickListener(getPasswordCancelListener());
	}

	/**
	 * 間違ったパスワードが入力されたとき
	 * 
	 * @param et
	 * @param tv
	 */
	private void setWrongPasswordView(final EditText et,
			final TextViewWithFont tv) {
		if (et == null || tv == null || mVib == null)
			return;
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
	private final View.OnClickListener getPasswordOKListener(final EditText et,
			final TextViewWithFont tv) {
		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String pass = et.getText().toString();
				if (pass.equals("")) {
					setWrongPasswordView(et, tv);
					return;
				}

				if (mPassword.equals(pass) || HOMEE_SYSTEM_PASS.equals(pass)) {
					LockUtilities.getInstance().unlock(getContext());
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
	private void addPatternSecurityView() {
		final Context c = getContext().getApplicationContext();
		final AssetManager am = c.getAssets();
		final Resources res = c.getResources();
		mPatternView = LayoutInflater.from(c).inflate(R.layout.input_pattern,
				null, false);
		final LockPatternView patternView = (LockPatternView) mPatternView
				.findViewById(R.id.lock_patternView);
		final TextViewWithFont titleView = (TextViewWithFont) mPatternView
				.findViewById(R.id.lock_pattern_title);
		final ButtonWithFont cancelBtn = (ButtonWithFont) mPatternView
				.findViewById(R.id.lock_pattern_cancel_btn);

		patternView.setOnPatternListener(getPatternListener(patternView,
				titleView));
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
			final LockPatternView patternView, final TextViewWithFont tv) {
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
					LockUtilities.getInstance().unlock(getContext());
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
				if (patternView == null)
					return;

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
	private void checkSecurityInfo() {
		checkAndRestart();
		// パスワード
		final Resources res = getResources();
		if (res.getInteger(R.integer.lock_security_type_password) == mSecurity) {
			addPasswordSecurityView();
			hideCalenderLayout();
			return;
		}
		// パターン
		if (res.getInteger(R.integer.lock_security_type_pattern) == mSecurity) {
			addPatternSecurityView();
			hideCalenderLayout();
			return;
		}
		// セリュリティなし
		LockUtilities.getInstance().unlock(getContext());
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
	public class LockPagerAdapter extends PagerAdapter implements TrainInfoListener{

		public static final String PAGE_LOCK = "page_lock";
		public static final String PAGE_PAGE_TRAIN_INFO_1 = "page_train_info_1";
		public static final String PAGE_PAGE_TRAIN_INFO_2 = "page_train_info_2";
		private int MASSAGE_TRAIN_INFO_LIST = 0;
		private Context mContext;
		private ArrayList<String> mList;
		private Object mPrimaryItem;
		private LayoutInflater mLayoutInflater;
		private int mTestNum1 = 0;
		private int mTestNum2 = 0;
		private int mLastPosition = 0;
		private Resources mResources = getResources();
		private AssetManager mAssetManager;
		private ApiRequestTrainInfo mApiRequestTrainInfo;
		private Handler mHandler;

		public LockPagerAdapter(Context context) {
			mContext = context;
			mList = new ArrayList<String>();
			mLayoutInflater = LayoutInflater.from(mContext);
			mResources = getResources();
			mAssetManager = context.getAssets();
			mApiRequestTrainInfo = new ApiRequestTrainInfo(context);
			mApiRequestTrainInfo.setListener(this);
			mHandler = new Handler() {
				public void handleMessage(Message message) {
					if (mViewPager == null) {
						return;
					}
					int pageId = mViewPager.getCurrentItem();
					int what = message.what;
					if (what == MASSAGE_TRAIN_INFO_LIST && pageId == 2) {
						ArrayList<TrainInfo> trainInfoList = (ArrayList<TrainInfo>) message.obj;
						drawingTrainInfoView(trainInfoList);
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
			String pageName = mList.get(position);
			if (pageName.equals(PAGE_LOCK)) {
				pageView = getCenterLayout();
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
				pageView = getleftLayout();
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
				pageView = getRightLayout();
			}
			setBackgrondLoadBitmap(pageView, position);
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
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater.inflate(R.layout.lock_train_info, null);
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
			mClockTextView = (TextViewWithFont) lockLayout
					.findViewById(R.id.lock_time_textview);
			mDataTextView = (TextViewWithFont) lockLayout
					.findViewById(R.id.lock_date_textview);
			mBatteryView = (TextViewWithFont) lockLayout
					.findViewById(R.id.lock_battery_textview);
			mKeyView = (KeyView) lockLayout.findViewById(R.id.unlock_keyview);
			mKeyView.setKeyViewListener(getKeyViewListener());
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
		private KeyViewListener getKeyViewListener() {
			KeyViewListener li = new KeyViewListener() {
				@Override
				public void onUnLock() {
					checkSecurityInfo();
				}
			};
			return li;
		}

		/**
		 * 背景設定
		 * 
		 * @param view
		 * @param position
		 */
		@SuppressLint("NewApi")
		private void setBackgrondLoadBitmap(final View view, final int position) {
			Bitmap bmp;
			WallpaperBitmapDB db;
			String keyCache = "";
			String keyDB = "";

			if (position == 0) {
				keyCache = ImageCache.KEY_WALLPAPER_LEFT_CACHE;
				keyDB = WallpaperBitmapDB.KEY_WALLPAPER_LEFT_DB;
			} else if (position == 1) {
				keyCache = ImageCache.KEY_WALLPAPER_CENTER_CACHE;
				keyDB = WallpaperBitmapDB.KEY_WALLPAPER_CENTER_DB;
			} else {
				keyCache = ImageCache.KEY_WALLPAPER_RIGHT_CACHE;
				keyDB = WallpaperBitmapDB.KEY_WALLPAPER_RIGHT_DB;
			}

			bmp = ImageCache.getImageBmp(keyCache);
			if (bmp == null) {
				db = new WallpaperBitmapDB(mContext);
				bmp = db.getBitmp(keyDB);
				if (bmp != null) {
					compatibleSetBackground(view, bmp);
				} else {
					bmp = WallpaperUtilities.getSystemWallpaper(mContext);
					if (bmp != null) {
						compatibleSetBackground(view, bmp);
					}
				}
			} else {
				compatibleSetBackground(view, bmp);
			}
		}

		/**
		 * 背景へセットする
		 * 
		 * @param layout
		 * @param bmp
		 */
		@SuppressLint("NewApi")
		private void compatibleSetBackground(final View layout, final Bitmap bmp) {
			if (layout == null || bmp == null) {
				return;
			}

			final Resources res = mContext.getResources();
			if (Build.VERSION.SDK_INT >= 16) {
				layout.setBackground(new BitmapDrawable(res, bmp));
			} else {
				layout.setBackgroundDrawable(new BitmapDrawable(res, bmp));
			}
		}

		public Object getPrimaryItem() {
			return mPrimaryItem;
		}

		private void countup(int position) {
			String pageName = mList.get(position);
			if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
				readRailwaysInfo();
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
				
				// 前回のデータ取得から１日以上たっていたら、リクエストするようにしたい
				new Thread(new Runnable() {
					@Override
					public void run() {
						mApiRequestTrainInfo.requestTrainInfo("駅名", "方向");
					}
				}).start();
			}
		}
		
		@Override
		public void completeCreateTimeTable(ArrayList<TrainInfo> timetable) {
			
			Message msg = mHandler.obtainMessage();
			msg.what = MASSAGE_TRAIN_INFO_LIST;
			msg.obj = timetable; //アプリケーションで持つようにする
			mHandler.sendMessage(msg);
		}

		@Override
		public void failedToCreateTimeTable() {
			// TODO Auto-generated method stub

		}
		
		private void drawingTrainInfoView(ArrayList<TrainInfo> trainInfoList) {
			RelativeLayout layout = (RelativeLayout) mLockPagerAdapter.getPrimaryItem();
			TextView tvTrainType1 = (TextView) layout.findViewById(R.id.lock_train_info_train_type_1);
			String trainTypeText = "";
//			if (trainTypeArray[0].equals(mContext.getString(R.string.train_type_local_response))) {
//				trainTypeText = mContext.getString(R.string.train_type_local);
//			} else if (trainTypeArray[0].equals(mContext.getString(R.string.train_type_express_response))) {
//				trainTypeText = mContext.getString(R.string.train_type_express);
//			} else if (trainTypeArray[0].equals(mContext.getString(R.string.train_type_rapid_response))) {
//				trainTypeText = mContext.getString(R.string.train_type_rapid);
//			} else if (trainTypeArray[0].equals(mContext.getString(R.string.train_type_limited_express_response))) {
//				trainTypeText = mContext.getString(R.string.train_type_limited_express);
//			}
			try{
				TrainInfo t= trainInfoList.get(0);
			tvTrainType1.setText(t.getTrainType());
			} catch (Exception e) {
				e.printStackTrace();
			}

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
			if (isInvalidAdapter(MetroCoverApplication.sRailwaysInfoAdapter)) {
				setEmptyRailwayInfoView(listLayout, empty, cpb, title, msg,
						reflesh_empty);
				return;
			}
			reflesh.setOnClickListener(getRefleshListner());
			lastupdate.setText(MetroCoverApplication.getLastUpdateTime());
			listview.setAdapter(MetroCoverApplication.sRailwaysInfoAdapter);
			empty.setVisibility(View.INVISIBLE);
			listLayout.setVisibility(View.VISIBLE);
		}

		/**
		 * 路線情報のAdapterのチェック
		 * 
		 * @param adapter
		 * @return
		 */
		private boolean isInvalidAdapter(ArrayAdapter<RailwaysInfo> adapter) {
			if (adapter == null) {
				return true;
			}
			if (adapter.isEmpty()) {
				return true;
			}
			return false;
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

			// 時間
			final String minuteStr = getTwoDigits(minute);
			final String col = res.getString(R.string.colon);
			boolean isAm = am_pm == 0;
			String timeStr = "";
			if (is24h) {
				timeStr = hour + col + minuteStr;
			} else {
				final String am = res.getString(R.string.clock_am);
				final String pm = res.getString(R.string.clock_pm);
				timeStr = hour + col + minuteStr + " " + (isAm ? am : pm);
			}
			mClockTextView.setText(timeStr);
			mClockTextView.setTextColor(mResources.getColor(mClockColorID));
			mClockTextView.setTextSize(mClockTimeSize);

			// 日にち
			boolean isJp = false;
			Locale locale = Locale.getDefault();
			if (locale.equals(Locale.JAPAN)) {
				isJp = true;
			}
			String dateStr = "";
			final String wod = LockUtilities.getInstance().getDatOfWeek(
					context, week_of_day);
			if (isJp) {
				final String y = res.getString(R.string.date_year);
				final String m = res.getString(R.string.date_month);
				final String d = res.getString(R.string.date_day);
				dateStr = year + y + month + m + day + d + wod;
			} else {
				dateStr = wod + " " + month + "/" + day + "/" + year;
			}
			mDataTextView.setText(dateStr);
			mDataTextView.setTextColor(mResources.getColor(mClockColorID));
			mDataTextView.setTextSize(mClockDateSize);
		}

		private String getTwoDigits(final int num) {
			if (num >= 10) {
				return String.valueOf(num);
			}
			return "0" + num;
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

			mBatteryView.setText(String.valueOf(battery) + "%");
			mBatteryView.setTextSize(mClockDateSize);
			mBatteryView.setTextColor(mResources.getColor(mClockColorID));
		}
	}

	/**
	 * Viewpagerのページ変更リスナー
	 * 
	 * @author kohirose
	 * 
	 */
	private class OnLockPageChangeListener extends
			ViewPager.SimpleOnPageChangeListener {
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
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}

		@Override
		public void onPageSelected(int position) {
		}
		
		
	}

}
