package metro.k.cover;

import metro.k.cover.lock.ChooseLockPattern;
import metro.k.cover.lock.LockService;
import metro.k.cover.lock.SelectSecurityActivity;
import metro.k.cover.wallpaper.WallpaperDetailActivity;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	// MetroCoverのオンオフ
	private boolean isMetroCoverEnable = false;
	private CheckBox mMetroCoverCheckBox = null;

	// 現在のセキュリティタイプ
	private int mCurrentSecurityType;

	// パターンロックのバイブレーション
	private boolean isPatternLockVibe = false;
	private CheckBox mPatternLockVibeCheckBox = null;

	// パターンロックの軌跡
	private boolean isPatternLockTrack = false;
	private CheckBox mPatternLockTrackCheckBox = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		initPrefValue();
		setupPrefViews();
	}

	@Override
	protected void onPause() {
		super.onPause();
		setPrefValue();
	}

	private void setupViews() {
		setContentView(R.layout.activity_setting);

		final AssetManager am = getAssets();
		final Resources res = getResources();

		// Title
		TextView main_titleview = (TextView) findViewById(R.id.setting_main_titleview);
		Utilities.setFontTextView(main_titleview, am, res);

		// MetroCover
		RelativeLayout metrocover_layout = (RelativeLayout) findViewById(R.id.setting_metrocover_layout);
		metrocover_layout.setOnClickListener(this);
		TextView metrocover_titleview = (TextView) findViewById(R.id.setting_metrocover_titleview);
		Utilities.setFontTextView(metrocover_titleview, am, res);
		mMetroCoverCheckBox = (CheckBox) findViewById(R.id.setting_metrocover_checkbox);
		mMetroCoverCheckBox.setOnCheckedChangeListener(this);

		// System-Lock
		RelativeLayout systemlock_layout = (RelativeLayout) findViewById(R.id.setting_systemlock_layout);
		systemlock_layout.setOnClickListener(this);
		TextView systemlock_titleview = (TextView) findViewById(R.id.setting_systemlock_titleview);
		Utilities.setFontTextView(systemlock_titleview, am, res);

		// MetroCover Security
		RelativeLayout security_layout = (RelativeLayout) findViewById(R.id.setting_metrocover_security_layout);
		security_layout.setOnClickListener(this);
		TextView security_titleview = (TextView) findViewById(R.id.setting_metrocover_security_titleview);
		Utilities.setFontTextView(security_titleview, am, res);

		// PatternLockVibe
		RelativeLayout pattern_vibe_layout = (RelativeLayout) findViewById(R.id.setting_pattern_vibe_layout);
		pattern_vibe_layout.setOnClickListener(this);
		TextView pattern_vibe_titleview = (TextView) findViewById(R.id.setting_pattern_vibe_titleview);
		Utilities.setFontTextView(pattern_vibe_titleview, am, res);
		mPatternLockVibeCheckBox = (CheckBox) findViewById(R.id.setting_pattern_vibe_checkbox);
		mPatternLockVibeCheckBox.setOnCheckedChangeListener(this);

		// PatternLockTrack
		RelativeLayout pattern_track_layout = (RelativeLayout) findViewById(R.id.setting_pattern_track_layout);
		pattern_track_layout.setOnClickListener(this);
		TextView pattern_track_titleview = (TextView) findViewById(R.id.setting_pattern_track_titleview);
		Utilities.setFontTextView(pattern_track_titleview, am, res);
		mPatternLockTrackCheckBox = (CheckBox) findViewById(R.id.setting_pattern_track_checkbox);
		mPatternLockTrackCheckBox.setOnCheckedChangeListener(this);

		// Wallpaper
		RelativeLayout wallpapers_layout = (RelativeLayout) findViewById(R.id.setting_wallpapers_layout);
		wallpapers_layout.setOnClickListener(this);
		TextView wallpapers_titleview = (TextView) findViewById(R.id.setting_wallpapers_titleview);
		Utilities.setFontTextView(wallpapers_titleview, am, res);
	}

	/**
	 * Prefのデータの設定値からViewを操作
	 */
	private void setupPrefViews() {

		// MetroCoverのオンオフ
		mMetroCoverCheckBox.setChecked(isMetroCoverEnable);

		// 現在のセキュリティタイプ
		final AssetManager am = getAssets();
		final Resources res = getResources();
		TextView current_securityview = (TextView) findViewById(R.id.setting_metrocover_security_currentview);
		if (mCurrentSecurityType == res
				.getInteger(R.integer.lock_security_type_password)) {
			current_securityview.setText(res.getString(R.string.lock_password));
		} else if (mCurrentSecurityType == res
				.getInteger(R.integer.lock_security_type_pattern)) {
			current_securityview.setText(res.getString(R.string.lock_patern));
		} else {
			current_securityview.setText(res.getString(R.string.lock_nothing));
		}
		Utilities.setFontTextView(current_securityview, am, res);

		// パターンロックのバイブレーション
		mPatternLockVibeCheckBox.setChecked(isPatternLockVibe);

		// パターンロックの軌跡
		mPatternLockTrackCheckBox.setChecked(isPatternLockTrack);
	}

	/**
	 * 保存している設定値を取得
	 */
	private void initPrefValue() {
		isMetroCoverEnable = PreferenceCommon
				.getMetroCover(getApplicationContext());
		mCurrentSecurityType = PreferenceCommon
				.getSecurityType(getApplicationContext());
		isPatternLockVibe = PreferenceCommon
				.getLockPatternVib(getApplicationContext());
		isPatternLockTrack = PreferenceCommon
				.getLockPatternTrack(getApplicationContext());
	}

	/**
	 * 現在入力されている設定値を保存
	 */
	private void setPrefValue() {
		PreferenceCommon.setMetroCover(getApplicationContext(),
				isMetroCoverEnable);
		PreferenceCommon.setLockPatternVib(getApplicationContext(),
				isPatternLockVibe);
		PreferenceCommon.setLockPatternTrack(getApplicationContext(),
				isPatternLockTrack);

		if (isMetroCoverEnable) {
			Utilities.disableKeyguard(getApplicationContext());
			Utilities.disableKeyguardWindow(this);
		} else {
			Utilities.enableKeyguard(getApplicationContext());
			Utilities.enableKeyguardWindow(this);
		}
	}

	/**
	 * ロック画面の表示Serviceを開始/停止する
	 */
	private void manageLockService(boolean toStart) {
		Intent intent = new Intent(this, LockService.class);
		if (toStart) {
			startService(intent);
		} else {
			stopService(intent);
		}
	}

	/**
	 * システムのロック画面設定へ遷移
	 */
	private void startSystemLockSettings() {
		Intent intent = new Intent();
		intent.setClassName("com.android.settings",
				"com.android.settings.ChooseLockGeneric");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
			return;
		} catch (Exception e) {
		}

		intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Utilities.startActivitySafely(intent, this);
	}

	/**
	 * セキュリティ設定画面へ遷移。現在設定中の場合は一度外してもらってから選択画面へ遷移させるようにする。
	 */
	private void startSelectSecurityActivity() {
		final Resources res = getResources();
		Intent intent;

		// none
		if (res.getInteger(R.integer.lock_security_type_none) == mCurrentSecurityType) {
			intent = new Intent(this, SelectSecurityActivity.class);
			Utilities.startActivitySafely(intent, this);
			return;
		}

		// pass
		// if (res.getInteger(R.integer.lock_security_type_password) ==
		// mCurrentSecurityType) {
		// intent = new Intent(this, PasswordSecurityDialogActivity.class);
		// intent.putExtra(LockUtil.IS_FROM_SETTING, true);
		// startActivity(intent);
		// return;
		// }

		// pattern
		if (res.getInteger(R.integer.lock_security_type_pattern) == mCurrentSecurityType) {
			intent = new Intent(this, ChooseLockPattern.class);
			intent.putExtra(Utilities.KEY_PATTERN_IS_FROM_SETTING, true);
			Utilities.startActivitySafely(intent, this);
			return;
		}
	}

	/**
	 * 壁紙設定画面へ遷移
	 */
	private void startWallpaperDetailActivity() {
		Intent intent = new Intent(this, WallpaperDetailActivity.class);
		Utilities.startActivitySafely(intent, this);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();

		// MetroCover
		if (R.id.setting_metrocover_layout == viewId) {
			mMetroCoverCheckBox.setChecked(!mMetroCoverCheckBox.isChecked());
			return;
		}

		// System-Lock
		if (R.id.setting_systemlock_layout == viewId) {
			startSystemLockSettings();
			return;
		}

		// Security-type
		if (R.id.setting_metrocover_security_layout == viewId) {
			startSelectSecurityActivity();
			return;
		}

		// Vibe
		if (R.id.setting_pattern_vibe_layout == viewId) {
			mPatternLockVibeCheckBox.setChecked(!mPatternLockVibeCheckBox
					.isChecked());
			return;
		}

		// Track
		if (R.id.setting_pattern_track_layout == viewId) {
			mPatternLockTrackCheckBox.setChecked(!mPatternLockTrackCheckBox
					.isChecked());
			return;
		}

		// Wallpapers
		if (R.id.setting_wallpapers_layout == viewId) {
			startWallpaperDetailActivity();
			return;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		final int viewId = buttonView.getId();

		// MetroCover
		if (R.id.setting_metrocover_checkbox == viewId) {
			isMetroCoverEnable = isChecked;
			manageLockService(isChecked);
			return;
		}

		// Vibe
		if (R.id.setting_pattern_vibe_checkbox == viewId) {
			isPatternLockVibe = isChecked;
			return;
		}

		// Track
		if (R.id.setting_pattern_track_checkbox == viewId) {
			isPatternLockTrack = isChecked;
			return;
		}
	}

}