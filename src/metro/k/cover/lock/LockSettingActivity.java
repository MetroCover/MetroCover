package metro.k.cover.lock;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class LockSettingActivity extends Activity implements OnClickListener,
		OnCheckedChangeListener {

	private RelativeLayout mLockVibeLayout = null;
	private RelativeLayout mLockTrackLayout = null;
	private LinearLayout mSystemSecurityLayout = null;

	private CheckBox mLockVibeCheckBox = null;
	private CheckBox mLockTrackCheckBox = null;

	private TextView mCurrentLockHomeeSecurityTextView = null;
	private TextView mCurrentPatternVibeTextView = null;
	private TextView mCurrentPatternTrackTextView = null;

	private ImageView mSepLockVibe = null;
	private ImageView mSepLockTrack = null;

	private boolean isPatternVibe = false;
	private boolean isPatternTrack = false;

	private int mCurrentSecurity;
	private static final int LOCK_SECURITY_NONE = 0;
	private static final int LOCK_SECURITY_PASS = 1;
	private static final int LOCK_SECURITY_PATT = 2;

	private static final int VISIBLE = View.VISIBLE;
	private static final int GONE = View.GONE;

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

		mSystemSecurityLayout = (LinearLayout) findViewById(R.id.lock_setting_system_security);
		LinearLayout layout_homee_security = (LinearLayout) findViewById(R.id.lock_setting_homee_security);
		mLockVibeLayout = (RelativeLayout) findViewById(R.id.lock_setting_pattern_vibe);
		mLockTrackLayout = (RelativeLayout) findViewById(R.id.lock_setting_pattern_track);

		mCurrentLockHomeeSecurityTextView = (TextView) findViewById(R.id.lock_setting_current_homee_security);
		mCurrentPatternVibeTextView = (TextView) findViewById(R.id.lock_setting_pattern_current_vibe);
		mCurrentPatternTrackTextView = (TextView) findViewById(R.id.lock_setting_pattern_current_track);

		mLockVibeCheckBox = (CheckBox) findViewById(R.id.lock_setting_pattern_vibe_checkbox);
		mLockTrackCheckBox = (CheckBox) findViewById(R.id.lock_setting_pattern_track_checkbox);

		mSepLockVibe = (ImageView) findViewById(R.id.lock_setting_pattern_vibe_sep);
		mSepLockTrack = (ImageView) findViewById(R.id.lock_setting_pattern_track_sep);

		mLockTrackLayout.setOnClickListener(this);
		mLockTrackCheckBox.setOnCheckedChangeListener(this);
		mSystemSecurityLayout.setOnClickListener(this);
		layout_homee_security.setOnClickListener(this);
		mLockVibeLayout.setOnClickListener(this);
		mLockVibeCheckBox.setOnCheckedChangeListener(this);
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
		if (currentSecurity == LOCK_SECURITY_NONE) {
			mCurrentLockHomeeSecurityTextView.setText(res
					.getString(R.string.lock_nothing));
		}

		// Password
		if (currentSecurity == LOCK_SECURITY_PASS) {
			mCurrentLockHomeeSecurityTextView.setText(res
					.getString(R.string.lock_password));
		}

		// Pattern
		if (currentSecurity == LOCK_SECURITY_PATT) {
			mCurrentLockHomeeSecurityTextView.setText(res
					.getString(R.string.lock_patern));
		}
	}

	private void initCurrentViblate() {
		if (mCurrentSecurity == LOCK_SECURITY_PATT) {
			final Resources res = getResources();
			mCurrentPatternVibeTextView.setText(isPatternVibe ? res
					.getString(R.string.enable) : res
					.getString(R.string.unable));
			mLockVibeCheckBox.setChecked(isPatternVibe ? true : false);
			mLockVibeLayout.setVisibility(VISIBLE);
			mSepLockVibe.setVisibility(VISIBLE);
		} else {
			mLockVibeLayout.setVisibility(GONE);
			mSepLockVibe.setVisibility(GONE);
		}
	}

	private void initCurrentPatternTrack() {
		if (mCurrentSecurity == LOCK_SECURITY_PATT) {
			final Resources res = getResources();
			mCurrentPatternTrackTextView.setText(isPatternTrack ? res
					.getString(R.string.enable) : res
					.getString(R.string.unable));
			mLockTrackCheckBox.setChecked(isPatternTrack ? true : false);
			mLockTrackLayout.setVisibility(VISIBLE);
			mSepLockTrack.setVisibility(VISIBLE);
		} else {
			mLockTrackLayout.setVisibility(GONE);
			mSepLockTrack.setVisibility(GONE);
		}
	}

	private void startSelectSecurityActivity(int currentSecurity) {
		Intent intent;

		// none
		if (LOCK_SECURITY_NONE == currentSecurity) {
			intent = new Intent(this, SelectSecurityActivity.class);
			startActivity(intent);
			return;
		}

		// pass
		if (LOCK_SECURITY_PASS == currentSecurity) {
			// intent = new Intent(this, PasswordSecurityDialogActivity.class);
			// intent.putExtra(LockUtil.IS_FROM_SETTING, true);
			// startActivity(intent);
			// return;
		}

		// pattern
		if (LOCK_SECURITY_PATT == currentSecurity) {
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
