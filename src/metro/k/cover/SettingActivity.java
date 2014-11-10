package metro.k.cover;

import java.io.File;

import metro.k.cover.lock.LockClockTextColorSelectActivity;
import metro.k.cover.lock.LockPasswordDialogActivity;
import metro.k.cover.lock.LockPatternChooseActivity;
import metro.k.cover.lock.LockSecurityChooseActivity;
import metro.k.cover.lock.LockService;
import metro.k.cover.lock.LockUtilities;
import metro.k.cover.railways.RailwaysActivity;
import metro.k.cover.railways.StationsActivity;
import metro.k.cover.view.TextViewWithFont;
import metro.k.cover.wallpaper.WallpaperDetailActivity;
import metro.k.cover.wallpaper.WallpaperEffectSelectActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

	// 現在のエフェクトタイプ
	private String mCurrentEffectType;

	// 現在の時計表記
	private int mClockSelected = LockUtilities.CLOCK_TYPE_24;
	private TextViewWithFont mCurrentClockTypeView;
	private int mCurrentClockType;

	// 現在の時計表記サイズ
	private int mClockSizeSelected = 1;
	private TextViewWithFont mCurrentClockSizeView;
	private int mCurrentClockSize;

	// 現在の時計の色
	private int mClockColorID;
	private String mClockColorStr;
	private TextViewWithFont mCurrentClockColorView;

	// パターンロックのバイブレーション
	private boolean isPatternLockVibe = false;
	private CheckBox mPatternLockVibeCheckBox = null;

	// パターンロックの軌跡
	private boolean isPatternLockTrack = false;
	private CheckBox mPatternLockTrackCheckBox = null;

	// 登録駅名
	private String mCurrentStationsRailwayName;
	private String mCurrentStationName;
	private TextViewWithFont mCurrentStationView;

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

		// MetroCover
		RelativeLayout metrocover_layout = (RelativeLayout) findViewById(R.id.setting_metrocover_layout);
		metrocover_layout.setOnClickListener(this);
		mMetroCoverCheckBox = (CheckBox) findViewById(R.id.setting_metrocover_checkbox);
		mMetroCoverCheckBox.setOnCheckedChangeListener(this);

		// System-Lock
		RelativeLayout systemlock_layout = (RelativeLayout) findViewById(R.id.setting_systemlock_layout);
		systemlock_layout.setOnClickListener(this);

		// MetroCover Security
		RelativeLayout security_layout = (RelativeLayout) findViewById(R.id.setting_metrocover_security_layout);
		security_layout.setOnClickListener(this);

		// Clock Type
		RelativeLayout clocktype_layout = (RelativeLayout) findViewById(R.id.setting_clocktype_layout);
		clocktype_layout.setOnClickListener(this);

		// PatternLockVibe
		RelativeLayout pattern_vibe_layout = (RelativeLayout) findViewById(R.id.setting_pattern_vibe_layout);
		pattern_vibe_layout.setOnClickListener(this);
		mPatternLockVibeCheckBox = (CheckBox) findViewById(R.id.setting_pattern_vibe_checkbox);
		mPatternLockVibeCheckBox.setOnCheckedChangeListener(this);

		// PatternLockTrack
		RelativeLayout pattern_track_layout = (RelativeLayout) findViewById(R.id.setting_pattern_track_layout);
		pattern_track_layout.setOnClickListener(this);
		mPatternLockTrackCheckBox = (CheckBox) findViewById(R.id.setting_pattern_track_checkbox);
		mPatternLockTrackCheckBox.setOnCheckedChangeListener(this);

		// Wallpaper
		RelativeLayout wallpapers_layout = (RelativeLayout) findViewById(R.id.setting_wallpapers_layout);
		wallpapers_layout.setOnClickListener(this);

		// Railways
		RelativeLayout railways_layout = (RelativeLayout) findViewById(R.id.setting_railways_layout);
		railways_layout.setOnClickListener(this);

		// Railways
		RelativeLayout station_layout = (RelativeLayout) findViewById(R.id.setting_station_layout);
		station_layout.setOnClickListener(this);

		// License
		RelativeLayout license_layout = (RelativeLayout) findViewById(R.id.setting_license_layout);
		license_layout.setOnClickListener(this);

		// Effects
		RelativeLayout effect_layout = (RelativeLayout) findViewById(R.id.setting_wallpaper_effect_layout);
		effect_layout.setOnClickListener(this);

		// Clock color
		RelativeLayout clockcol_layout = (RelativeLayout) findViewById(R.id.setting_clock_color_layout);
		clockcol_layout.setOnClickListener(this);

		// Clock color
		RelativeLayout clocksize_layout = (RelativeLayout) findViewById(R.id.setting_clock_size_layout);
		clocksize_layout.setOnClickListener(this);
	}

	/**
	 * Prefのデータの設定値からViewを操作
	 */
	private void setupPrefViews() {

		// MetroCoverのオンオフ
		mMetroCoverCheckBox.setChecked(isMetroCoverEnable);

		// 現在のセキュリティタイプ
		final Resources res = getResources();
		TextViewWithFont current_securityview = (TextViewWithFont) findViewById(R.id.setting_metrocover_security_currentview);
		if (mCurrentSecurityType == res
				.getInteger(R.integer.lock_security_type_password)) {
			current_securityview.setText(res.getString(R.string.lock_password));
		} else if (mCurrentSecurityType == res
				.getInteger(R.integer.lock_security_type_pattern)) {
			current_securityview.setText(res.getString(R.string.lock_patern));
		} else {
			current_securityview.setText(res.getString(R.string.lock_nothing));
		}

		// パターンロックのバイブレーション
		mPatternLockVibeCheckBox.setChecked(isPatternLockVibe);

		// パターンロックの軌跡
		mPatternLockTrackCheckBox.setChecked(isPatternLockTrack);

		// Effect
		TextViewWithFont current_effect = (TextViewWithFont) findViewById(R.id.setting_wallpapers_effect_currentview);
		current_effect.setText(mCurrentEffectType);

		// 現在の時計表記
		mCurrentClockTypeView = (TextViewWithFont) findViewById(R.id.setting_clocktype_currentview);
		if (mCurrentClockType == LockUtilities.CLOCK_TYPE_12) {
			mCurrentClockTypeView.setText(res.getString(R.string.clock_12));
		} else {
			mCurrentClockTypeView.setText(res.getString(R.string.clock_24));
		}

		// 現在の時計表記サイズ　
		mCurrentClockSizeView = (TextViewWithFont) findViewById(R.id.setting_clock_size_currentview);
		if (mCurrentClockSize == res
				.getInteger(R.integer.lock_clock_size_small)) {
			mCurrentClockSizeView.setText(res
					.getString(R.string.clock_size_small));
		} else if (mCurrentClockSize == res
				.getInteger(R.integer.lock_clock_size_midium)) {
			mCurrentClockSizeView.setText(res
					.getString(R.string.clock_size_midium));
		} else if (mCurrentClockSize == res
				.getInteger(R.integer.lock_clock_size_large)) {
			mCurrentClockSizeView.setText(res
					.getString(R.string.clock_size_large));
		}

		// 時計色
		mCurrentClockColorView = (TextViewWithFont) findViewById(R.id.setting_clock_color_currentview);
		mCurrentClockColorView.setText(mClockColorStr);
		if (mClockColorID == R.color.color_white) {
			mCurrentClockColorView.setTextColor(res
					.getColor(R.color.color_black));
		} else {
			mCurrentClockColorView.setTextColor(res.getColor(mClockColorID));
		}

		// 駅名
		mCurrentStationView = (TextViewWithFont) findViewById(R.id.setting_station_currentview);
		String name = "";
		if (Utilities.isInvalidStr(mCurrentStationsRailwayName)) {
			name = mCurrentStationName;
		} else {
			name = mCurrentStationsRailwayName + File.separator
					+ mCurrentStationName + res.getString(R.string.station);
		}
		mCurrentStationView.setText(name);
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
		mCurrentEffectType = String.valueOf(PreferenceCommon
				.getViewPagerEffect(getApplicationContext()));
		mCurrentClockType = PreferenceCommon
				.getClockType(getApplicationContext());
		mClockColorID = PreferenceCommon.getClockColor(getApplicationContext());
		mClockColorStr = PreferenceCommon
				.getClockColorStr(getApplicationContext());
		mCurrentStationName = PreferenceCommon
				.getStationName(getApplicationContext());
		mCurrentStationsRailwayName = PreferenceCommon
				.getStationsRailwayName(getApplicationContext());
		mClockSizeSelected = mCurrentClockSize = PreferenceCommon
				.getClockSize(getApplicationContext());
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
			LockUtilities.getInstance()
					.disableKeyguard(getApplicationContext());
		} else {
			LockUtilities.getInstance().enableKeyguard(getApplicationContext());
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
			intent = new Intent(this, LockSecurityChooseActivity.class);
			Utilities.startActivitySafely(intent, this);
			return;
		}

		// pass
		if (res.getInteger(R.integer.lock_security_type_password) == mCurrentSecurityType) {
			intent = new Intent(this, LockPasswordDialogActivity.class);
			intent.putExtra(LockUtilities.KEY_PASSWORD_IS_FROM_SETTING, true);
			startActivity(intent);
			return;
		}

		// pattern
		if (res.getInteger(R.integer.lock_security_type_pattern) == mCurrentSecurityType) {
			intent = new Intent(this, LockPatternChooseActivity.class);
			intent.putExtra(LockUtilities.KEY_PATTERN_IS_FROM_SETTING, true);
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

	/**
	 * エフェクト設定画面へ遷移
	 */
	private void startWallpaperEffectSelectActivity() {
		Intent intent = new Intent(this, WallpaperEffectSelectActivity.class);
		Utilities.startActivitySafely(intent, this);
	}

	/**
	 * 路線設定画面へ遷移
	 */
	private void startRailwaysActivity() {
		Intent intent = new Intent(this, RailwaysActivity.class);
		Utilities.startActivitySafely(intent, this);
	}

	/**
	 * 時計色設定画面へ遷移
	 */
	private void startColorSelectActivity() {
		Intent intent = new Intent(this, LockClockTextColorSelectActivity.class);
		Utilities.startActivitySafely(intent, this);
	}

	/**
	 * 駅設定画面へ遷移
	 */
	private void startStationActivity() {
		Intent intent = new Intent(this, StationsActivity.class);
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

		// Station
		if (R.id.setting_station_layout == viewId) {
			startStationActivity();
			return;
		}

		// Vibe
		if (R.id.setting_pattern_vibe_layout == viewId) {
			mPatternLockVibeCheckBox.setChecked(!mPatternLockVibeCheckBox
					.isChecked());
			return;
		}

		// Clock-Type
		if (R.id.setting_clocktype_layout == viewId) {
			buildClockTypeDialog();
			return;
		}

		// Clock-Size
		if (R.id.setting_clock_size_layout == viewId) {
			buildClockSizeDialog();
			return;
		}

		// Clock-Color
		if (R.id.setting_clock_color_layout == viewId) {
			startColorSelectActivity();
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

		// Effects
		if (R.id.setting_wallpaper_effect_layout == viewId) {
			startWallpaperEffectSelectActivity();
			return;
		}

		// Railways
		if (R.id.setting_railways_layout == viewId) {
			startRailwaysActivity();
			return;
		}

		// License
		if (R.id.setting_license_layout == viewId) {
			buildInfoDialog().show();
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

	/**
	 * 時計表記タイプ選択ダイアログ
	 */
	private void buildClockTypeDialog() {
		final Resources res = getResources();
		final String item_list[] = new String[] {
				res.getString(R.string.clock_12),
				res.getString(R.string.clock_24) };

		new AlertDialog.Builder(SettingActivity.this)
				.setIcon(R.drawable.ic_clock)
				.setTitle(res.getString(R.string.clock_type))
				.setSingleChoiceItems(item_list, mCurrentClockType,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mClockSelected = whichButton;
							}
						})
				.setPositiveButton(res.getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mCurrentClockTypeView
										.setText(item_list[mClockSelected]);
								PreferenceCommon
										.setClockType(getApplicationContext(),
												mClockSelected);
								mCurrentClockType = mClockSelected;
								dialog.cancel();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						}).show();
	}

	/**
	 * 時計表記サイズ　選択ダイアログ
	 */
	private void buildClockSizeDialog() {
		final Resources res = getResources();
		final String item_list[] = new String[] {
				res.getString(R.string.clock_size_small),
				res.getString(R.string.clock_size_midium),
				res.getString(R.string.clock_size_large) };

		new AlertDialog.Builder(SettingActivity.this)
				.setIcon(R.drawable.ic_clock)
				.setTitle(res.getString(R.string.clock_size))
				.setSingleChoiceItems(item_list, mCurrentClockSize,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mClockSizeSelected = whichButton;
							}
						})
				.setPositiveButton(res.getString(R.string.ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								mCurrentClockSizeView
										.setText(item_list[mClockSizeSelected]);
								PreferenceCommon.setClockSize(
										getApplicationContext(),
										mClockSizeSelected);
								mCurrentClockSize = mClockSizeSelected;
								dialog.cancel();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								dialog.cancel();
							}
						}).show();
	}

	/**
	 * ライセンス表記
	 * 
	 * @param title
	 * @param message
	 * @return
	 */
	private AlertDialog.Builder buildInfoDialog() {
		final Resources res = getResources();
		final LayoutInflater inflater = LayoutInflater
				.from(SettingActivity.this);
		final View layout = inflater.inflate(R.layout.dialog_license,
				(ViewGroup) findViewById(R.id.dialog_layout_root), false);
		TextView msg = (TextView) layout.findViewById(R.id.dialog_msg);
		msg.setText(res.getString(R.string.license_msg));

		final String close = getResources().getString(R.string.close);
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(res.getString(R.string.license));
		builder.setView(layout);
		builder.setNegativeButton(close, dialogCancelListener);
		return builder;
	}

	/**
	 * ライセンス表記ダイアログの「閉じる」ボタン
	 */
	private DialogInterface.OnClickListener dialogCancelListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.cancel();
		}
	};
}
