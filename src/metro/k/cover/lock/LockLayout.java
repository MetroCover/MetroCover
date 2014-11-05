package metro.k.cover.lock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.lock.LockPatternView.Cell;
import metro.k.cover.lock.LockPatternView.DisplayMode;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * ロック画面のView
 */
public class LockLayout extends FrameLayout implements View.OnClickListener,
		View.OnLongClickListener {

	private static final int WC = RelativeLayout.LayoutParams.WRAP_CONTENT;
	private static final int MP = RelativeLayout.LayoutParams.MATCH_PARENT;

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

	// mainview
	private RelativeLayout mLockLayout;

	// vibe
	private Vibrator mVib;
	private static final int VIBELATE_TIME = 100;
	private boolean isEnableLock;

	public LockLayout(Context context) {
		super(context);
	}

	public LockLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LockLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		final Context c = getContext().getApplicationContext();
		regist(c);
		initialize();
		getTelInfo(c);
		initPref(c);
	}

	private void regist(final Context c) {
		if (c == null)
			return;
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(AudioManager.RINGER_MODE_CHANGED_ACTION);
		c.registerReceiver(mBroadcastReceiver, filter);
	}
	
	// initialize SharedPreference
	private void initPref(final Context context) {
		mSecurity = PreferenceCommon.getSecurityType(context);
		mPassword = PreferenceCommon.getCurrentPassword(context);
		mSecurityPattern = PreferenceCommon.getCurrentPattern(context);
		isEnableLock = PreferenceCommon.getMetroCover(context);

		if (isEnableLock) {
			LockUtil.getInstance().disableKeyguard(context);
		}
	}

	/**
	 * Control Statusbar
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
			LockUtil.getInstance().lock(c, true);
			LockUtil.getInstance().disableKeyguard(c);
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		disableTelephonyReceiver(getContext().getApplicationContext());
	}

	private void disableTelephonyReceiver(Context context) {
		context.unregisterReceiver(mBroadcastReceiver);

		final TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
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

	/********************************************************
	 * TEL
	 *********************************************************/

	/**
	 * Listener
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
				if (LockUtil.getInstance().isShowing()) {
					LockUtil.getInstance().unlock(getContext());
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
	 * tel-info
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

	// 不在着信表示
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

	// 不在着信取得
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

	/********************************************************
	 * その他
	 *********************************************************/
	/**
	 * init
	 */
	private void initialize() {
		final Context context = getContext().getApplicationContext();

		// メンバ変数の初期化
		mLockLayout = (RelativeLayout) findViewById(R.id.lock_foregroundLayout);

		// vibe
		mVib = (Vibrator) getContext().getApplicationContext()
				.getSystemService(Context.VIBRATOR_SERVICE);

		// 電話情報の受信開始
		final TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(mPhoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Viewpager
		LockPagerAdapter mLockPagerAdapter = new LockPagerAdapter(context);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_1);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_LOCK);
		mLockPagerAdapter.add(LockPagerAdapter.PAGE_PAGE_TRAIN_INFO_2);
		ViewPager pager = (ViewPager) findViewById(R.id.lock_layout_viewpager);
		pager.setAdapter(mLockPagerAdapter);
		pager.setCurrentItem(1);
		pager.setOnPageChangeListener(mOnLockPageChangeListener);
	}

	OnLockPageChangeListener mOnLockPageChangeListener = new OnLockPageChangeListener();

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

	// その他のBroadcastReceiver（ユーザー動作）
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
				return;
			}
		}
	};

	/********************************************************
	 * セキュリティ
	 *********************************************************/

	// パスワードロックのView設定
	private void addPasswordSecurityView() {
		final Context c = getContext().getApplicationContext();
		mLockLayout.setVisibility(View.GONE);
		mPassView = LayoutInflater.from(c).inflate(R.layout.input_password,
				null, false);
		final TextView title = (TextView) mPassView
				.findViewById(R.id.lock_pass_title);
		final EditText edittext = (EditText) mPassView
				.findViewById(R.id.lock_pass_edittext);
		final Button comp = (Button) mPassView
				.findViewById(R.id.lock_pass_comp_btn);
		final Button cancel = (Button) mPassView
				.findViewById(R.id.lock_pass_cancel_btn);

		this.addView(mPassView);
		edittext.requestFocus();
		final InputMethodManager imm = (InputMethodManager) c
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(edittext, InputMethodManager.SHOW_IMPLICIT);
		comp.setOnClickListener(getPasswordOKListener(edittext, title));
		cancel.setOnClickListener(getPasswordCancelListener());
	}

	// 間違ったパスワードが入力されたとき
	private void setWrongPasswordView(final EditText et, final TextView tv) {
		if (et == null || tv == null || mVib == null)
			return;
		mVib.vibrate(VIBELATE_TIME);
		et.setText("");
		tv.setText("間違ってるよ");
	}

	// パスワードロックのOKボタン押下
	private final View.OnClickListener getPasswordOKListener(final EditText et,
			final TextView tv) {
		final View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final String pass = et.getText().toString();
				if (pass.equals("")) {
					setWrongPasswordView(et, tv);
					return;
				}

				if (mPassword.equals(pass) || HOMEE_SYSTEM_PASS.equals(pass)) {
					LockUtil.getInstance().unlock(getContext());
				} else {
					setWrongPasswordView(et, tv);
				}
			}
		};
		return listener;
	}

	// パスワードロックのキャンセルボタン押下
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

	// パスワードロックのView設定
	private void addPatternSecurityView() {
		mLockLayout.setVisibility(View.GONE);
		mPatternView = LayoutInflater
				.from(getContext().getApplicationContext()).inflate(
						R.layout.input_pattern, null, false);
		final LockPatternView patternView = (LockPatternView) mPatternView
				.findViewById(R.id.lock_patternView);
		final TextView titleView = (TextView) mPatternView
				.findViewById(R.id.lock_pattern_title);
		final Button cancelBtn = (Button) mPatternView
				.findViewById(R.id.lock_pattern_cancel_btn);

		patternView.setOnPatternListener(getPatternListener(patternView,
				titleView));
		cancelBtn.setOnClickListener(getPatternCancelListener(patternView));
		this.addView(mPatternView);
	}

	// パターンロックのパターンリスナー
	private final LockPatternView.OnPatternListener getPatternListener(
			final LockPatternView patternView, final TextView tv) {
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
						|| LockUtil.getInstance().getMasterPattern(
								getContext().getApplicationContext(),
								onlyNumStr)) {
					tv.setVisibility(View.INVISIBLE);
					LockUtil.getInstance().unlock(getContext());
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

	// パターンロックのキャンセルボタン押下
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

	// セキュリティ判定
	private void checkSecurityInfo() {
		checkAndRestart();
		// パスワード
		final Resources res = getResources();
		if (res.getInteger(R.integer.lock_security_type_password) == mSecurity) {
			addPasswordSecurityView();
			return;
		}
		// パターン
		if (res.getInteger(R.integer.lock_security_type_pattern) == mSecurity) {
			addPatternSecurityView();
			return;
		}
		// セリュリティなし
		mLockLayout.setVisibility(View.VISIBLE);
		LockUtil.getInstance().unlock(getContext());
	}

	private void checkAndRestart() {
		final Context c = getContext().getApplicationContext();
		final boolean isRunnning = LockService.isServiceRunning(c);
		if (isRunnning)
			return;

		try {
			c.startService(new Intent(c, LockService.class));
			LockUtil.getInstance().disableKeyguard(c);
		} catch (Exception e) {
		}
	}

	private void removePassView(View v) {
		this.removeView(v);
		mLockLayout.setVisibility(View.VISIBLE);
	}
}
