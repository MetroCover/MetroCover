package metro.k.cover.lock;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import metro.k.cover.ImageCache;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.lock.LockPatternView.Cell;
import metro.k.cover.lock.LockPatternView.DisplayMode;
import metro.k.cover.wallpaper.WallpaperBitmapDB;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
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
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
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

	private KeyView mKeyView;
	OnLockPageChangeListener mOnLockPageChangeListener = new OnLockPageChangeListener();

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
			LockUtilities.getInstance().disableKeyguard(context);
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
			LockUtilities.getInstance().lock(c, true);
			LockUtilities.getInstance().disableKeyguard(c);
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

	/**
	 * Viewの準備
	 */
	private void initialize() {
		final Context context = getContext().getApplicationContext();

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
				return;
			}
		}
	};

	/**
	 * パスワードロックのView設定
	 */
	private void addPasswordSecurityView() {
		final Context c = getContext().getApplicationContext();
		final AssetManager am = c.getAssets();
		final Resources res = c.getResources();
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

		Utilities.setFontTextView(title, am, res);
		Utilities.setFontEditTextView(edittext, am, res);
		Utilities.setFontButtonView(comp, am, res);
		Utilities.setFontButtonView(cancel, am, res);

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
	private void setWrongPasswordView(final EditText et, final TextView tv) {
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
		final TextView titleView = (TextView) mPatternView
				.findViewById(R.id.lock_pattern_title);
		final Button cancelBtn = (Button) mPatternView
				.findViewById(R.id.lock_pattern_cancel_btn);
		
		Utilities.setFontTextView(titleView, am, res);
		Utilities.setFontButtonView(cancelBtn, am, res);

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
			return;
		}
		// パターン
		if (res.getInteger(R.integer.lock_security_type_pattern) == mSecurity) {
			addPatternSecurityView();
			return;
		}
		// セリュリティなし
		LockUtilities.getInstance().unlock(getContext());
	}

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

	/**
	 * パスワードおよびパターンロックのViewの破棄
	 * 
	 * @param v
	 */
	private void removePassView(View v) {
		this.removeView(v);
	}

	/**
	 * ロック画面Viewpagerのアダプター
	 * 
	 * @author kohirose
	 * 
	 */
	public class LockPagerAdapter extends PagerAdapter {
		public static final String PAGE_LOCK = "page_lock";
		public static final String PAGE_PAGE_TRAIN_INFO_1 = "page_train_info_1";
		public static final String PAGE_PAGE_TRAIN_INFO_2 = "page_train_info_2";
		private Context mContext;
		private ArrayList<String> mList;
		private Object mPrimaryItem;
		private LayoutInflater mLayoutInflater;
		private int mTestNum1 = 0;
		private int mTestNum2 = 0;
		private int mLastPosition = 0;

		public LockPagerAdapter(Context context) {
			mContext = context;
			mList = new ArrayList<String>();
			mLayoutInflater = LayoutInflater.from(mContext);
		}

		public void add(String pageName) {
			mList.add(pageName);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View pageView = null;
			String pageName = mList.get(position);
			if (pageName.equals(PAGE_LOCK)) {
				RelativeLayout lockLayout = (RelativeLayout) mLayoutInflater
						.inflate(R.layout.page_lock, null);
				mKeyView = (KeyView) lockLayout
						.findViewById(R.id.unlock_keyview);
				mKeyView.setKeyViewListener(new KeyViewListener() {
					@Override
					public void onUnLock() {
						checkSecurityInfo();
					}
				});
				pageView = lockLayout;
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
				RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
						.inflate(R.layout.page_train_info, null);
				TextView tv = (TextView) trainInfoLayout
						.findViewById(R.id.page_train_info_test);
				tv.setText(String.valueOf(mTestNum1));
				pageView = trainInfoLayout;
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
				RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
						.inflate(R.layout.page_train_info, null);
				TextView tv = (TextView) trainInfoLayout
						.findViewById(R.id.page_train_info_test);
				tv.setText(String.valueOf(mTestNum2));
				pageView = trainInfoLayout;
			}
			setBackgrondLoadBitmap(pageView, position);
			container.addView(pageView);
			return pageView;
		}

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
					bmp = getSystemWallpaper();
					if (bmp != null) {
						compatibleSetBackground(view, bmp);
					}
				}
			} else {
				compatibleSetBackground(view, bmp);
			}
		}

		@SuppressLint("ServiceCast")
		final Bitmap getSystemWallpaper() {
			if (mContext == null) {
				return null;
			}

			final WallpaperManager wm = (WallpaperManager) mContext
					.getSystemService(Context.WALLPAPER_SERVICE);
			if (wm == null) {
				return null;
			}

			final Drawable d = wm.getDrawable();
			if (d == null) {
				return null;
			}

			return ((BitmapDrawable) d).getBitmap();
		}

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

		public Object getPrimaryItem() {
			return mPrimaryItem;
		}

		private void countup(int position) {
			String pageName = mList.get(position);
			int count = 0;
			if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
				count = mTestNum1++;
			} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
				count = mTestNum2++;
			} else {
				return;
			}
			RelativeLayout TrainInfoLayout = (RelativeLayout) getPrimaryItem();
			TextView textView = (TextView) TrainInfoLayout
					.findViewById(R.id.page_train_info_test);
			textView.setText(String.valueOf(count));
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
