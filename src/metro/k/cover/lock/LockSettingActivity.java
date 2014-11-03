package metro.k.cover.lock;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LockSettingActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private CheckBox mLockVibeCheckBox = null;
	private CheckBox mLockTrackCheckBox = null;

	private TextView mCurrentSecurityTextView = null;
	private TextView mCurrentPatternVibeTextView = null;
	private TextView mCurrentPatternTrackTextView = null;

	private boolean isPatternVibe = false;
	private boolean isPatternTrack = false;

	private int mCurrentSecurity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
		initPref();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	private void setupViews() {
		setContentView(R.layout.activity_lock_setting);

		final AssetManager am = getAssets();
		final Resources res = getResources();

		// タイトル
		TextView titleView = (TextView) findViewById(R.id.lock_setting_titleview);
		Utilities.setFontTextView(titleView, am, res);

		// システムのロック画面設定
		LinearLayout layout_system = (LinearLayout) findViewById(R.id.lock_setting_system_security);
		layout_system.setOnClickListener(this);
		TextView systemLockTitleView = (TextView) findViewById(R.id.lock_setting_system_security_title);
		TextView systemLockMsgView = (TextView) findViewById(R.id.lock_setting_system_security_msg);
		Utilities.setFontTextView(systemLockTitleView, am, res);
		Utilities.setFontTextView(systemLockMsgView, am, res);

		// セキュリティ
		LinearLayout layout_security = (LinearLayout) findViewById(R.id.lock_setting_homee_security);
		layout_security.setOnClickListener(this);
		mCurrentSecurityTextView = (TextView) findViewById(R.id.lock_setting_current_homee_security);
		Utilities.setFontTextView(mCurrentSecurityTextView, am, res);

		// 軌跡
		RelativeLayout layout_track = (RelativeLayout) findViewById(R.id.lock_setting_pattern_track);
		layout_track.setOnClickListener(this);
		mLockTrackCheckBox = (CheckBox) findViewById(R.id.lock_setting_pattern_track_checkbox);
		mLockTrackCheckBox.setOnCheckedChangeListener(this);
		mCurrentPatternTrackTextView = (TextView) findViewById(R.id.lock_setting_pattern_current_track);
		Utilities.setFontTextView(mCurrentPatternTrackTextView, am, res);

		// バイブ
		RelativeLayout layout_vibe = (RelativeLayout) findViewById(R.id.lock_setting_pattern_vibe);
		layout_vibe.setOnClickListener(this);
		mLockVibeCheckBox = (CheckBox) findViewById(R.id.lock_setting_pattern_vibe_checkbox);
		mLockVibeCheckBox.setOnCheckedChangeListener(this);
		mCurrentPatternVibeTextView = (TextView) findViewById(R.id.lock_setting_pattern_current_vibe);
		Utilities.setFontTextView(mCurrentPatternVibeTextView, am, res);
	}

	private void startSystemSecurity() {
		// Step1:
		Intent intent = new Intent();
		intent.setClassName("com.android.settings",
				"com.android.settings.ChooseLockGeneric");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
			return;
		} catch (Exception e) {
		}

		// Step2:
		intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			startActivity(intent);
			return;
		} catch (Exception e) {
		}

		Toast.makeText(getApplicationContext(),
				getResources().getString(R.string.common_err),
				Toast.LENGTH_SHORT).show();
		return;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();

		if (R.id.lock_setting_system_security == viewId) {
			startSystemSecurity();
			return;
		}

		if (R.id.lock_setting_homee_security == viewId) {
			startSelectSecurityActivity(mCurrentSecurity);
			return;
		}

		if (R.id.lock_setting_pattern_vibe == viewId) {
			mLockVibeCheckBox.setChecked(mLockVibeCheckBox.isChecked() ? false
					: true);
			return;
		}

		if (R.id.lock_setting_pattern_track == viewId) {
			mLockTrackCheckBox
					.setChecked(mLockTrackCheckBox.isChecked() ? false : true);
			return;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		final int boxId = buttonView.getId();
		final Resources res = getResources();

		if (R.id.lock_setting_pattern_vibe_checkbox == boxId) {
			mCurrentPatternVibeTextView
					.setText(mLockVibeCheckBox.isChecked() ? res
							.getString(R.string.enable) : res
							.getString(R.string.unable));
			return;
		}

		if (R.id.lock_setting_pattern_track_checkbox == boxId) {
			mCurrentPatternTrackTextView
					.setText(mLockTrackCheckBox.isChecked() ? res
							.getString(R.string.enable) : res
							.getString(R.string.unable));
			return;
		}
	}

	private void initPref() {
		final Context c = getApplicationContext();
		isPatternVibe = PreferenceCommon.getLockPatternVib(c);
		isPatternTrack = PreferenceCommon.getLockPatternTrack(c);
		mCurrentSecurity = PreferenceCommon.getSecurityType(c);
		init();
	}

	private void init() {
		initCurrentSecurity(mCurrentSecurity);
		initCurrentViblate();
		initCurrentPatternTrack();
	}

	private void initCurrentSecurity(int currentSecurity) {
		final Resources res = getResources();

		// None
		if (currentSecurity == res
				.getInteger(R.integer.lock_security_type_none)) {
			mCurrentSecurityTextView.setText(res
					.getString(R.string.lock_nothing));

		}

		// Password
		if (currentSecurity == res
				.getInteger(R.integer.lock_security_type_password)) {
			mCurrentSecurityTextView.setText(res
					.getString(R.string.lock_password));
		}

		// Pattern
		if (currentSecurity == res
				.getInteger(R.integer.lock_security_type_pattern)) {
			mCurrentSecurityTextView.setText(res
					.getString(R.string.lock_patern));
		}

		Utilities.setFontTextView(mCurrentSecurityTextView, getAssets(),
				getResources());
	}

	private void initCurrentViblate() {
		final Resources res = getResources();
		if (mCurrentSecurity == res
				.getInteger(R.integer.lock_security_type_pattern)) {
			mCurrentPatternVibeTextView.setText(isPatternVibe ? res
					.getString(R.string.enable) : res
					.getString(R.string.unable));
			mLockVibeCheckBox.setChecked(isPatternVibe ? true : false);
		}
	}

	private void initCurrentPatternTrack() {
		final Resources res = getResources();
		if (mCurrentSecurity == res
				.getInteger(R.integer.lock_security_type_pattern)) {
			mCurrentPatternTrackTextView.setText(isPatternTrack ? res
					.getString(R.string.enable) : res
					.getString(R.string.unable));
			mLockTrackCheckBox.setChecked(isPatternTrack ? true : false);
		}
	}

	private void startSelectSecurityActivity(int currentSecurity) {
		Intent intent;
		final Resources res = getResources();
		// none
		if (res.getInteger(R.integer.lock_security_type_none) == currentSecurity) {
			intent = new Intent(this, SelectSecurityActivity.class);
			startActivity(intent);
			return;
		}

		// pass
		if (res.getInteger(R.integer.lock_security_type_password) == currentSecurity) {
			// intent = new Intent(this, PasswordSecurityDialogActivity.class);
			// intent.putExtra(LockUtil.IS_FROM_SETTING, true);
			// startActivity(intent);
			// return;
		}

		// pattern
		if (res.getInteger(R.integer.lock_security_type_pattern) == currentSecurity) {
			intent = new Intent(this, ChooseLockPattern.class);
			intent.putExtra(Utilities.KEY_PATTERN_IS_FROM_SETTING, true);
			startActivity(intent);
			return;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		setCheckBoxPreference();
	}

	private void setCheckBoxPreference() {
		final Context c = getApplicationContext();
		PreferenceCommon.setLockPatternVib(c, mLockVibeCheckBox.isChecked());
		PreferenceCommon.setLockPatternTrack(c, mLockTrackCheckBox.isChecked());
	}
}
