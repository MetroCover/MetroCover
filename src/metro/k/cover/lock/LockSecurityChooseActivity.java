package metro.k.cover.lock;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * セキュリティ設定画面
 */
public class LockSecurityChooseActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void setupViews() {
		setContentView(R.layout.activity_select_scurity);

		final Resources res = getResources();
		final AssetManager am = getAssets();

		TextView titleView = (TextView) findViewById(R.id.select_security_titleview);
		Utilities.setFontTextView(titleView, am, res);

		TextView passwordtitleView = (TextView) findViewById(R.id.select_security_password_titleview);
		TextView nonetitleView = (TextView) findViewById(R.id.select_security_none_titleview);
		TextView patterntitleView = (TextView) findViewById(R.id.select_security_pattern_titleview);
		Utilities.setFontTextView(passwordtitleView, am, res);
		Utilities.setFontTextView(nonetitleView, am, res);
		Utilities.setFontTextView(patterntitleView, am, res);

		TextView passwordLevelView = (TextView) findViewById(R.id.select_security_password_level);
		TextView noneLevelView = (TextView) findViewById(R.id.select_security_none_level);
		TextView patternLevelView = (TextView) findViewById(R.id.select_security_pattern_level);
		Utilities.setFontTextView(passwordLevelView, am, res);
		Utilities.setFontTextView(noneLevelView, am, res);
		Utilities.setFontTextView(patternLevelView, am, res);

		RelativeLayout passLayout = (RelativeLayout) findViewById(R.id.select_security_password_layout);
		RelativeLayout noneLayout = (RelativeLayout) findViewById(R.id.select_security_none_layout);
		RelativeLayout paternLayout = (RelativeLayout) findViewById(R.id.select_security_pattern_layout);

		passLayout.setOnClickListener(this);
		noneLayout.setOnClickListener(this);
		paternLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		Intent intent = null;

		// Password
		if (R.id.select_security_password_layout == viewId) {
			intent = new Intent(LockSecurityChooseActivity.this,
					LockPasswordDialogActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return;
		}

		// None
		if (R.id.select_security_none_layout == viewId) {
			// run on ui-thread
			PreferenceCommon.setSecurityType(getApplicationContext(),
					getResources()
							.getInteger(R.integer.lock_security_type_none));
			intent = new Intent(LockSecurityChooseActivity.this,
					SettingActivity.class);
			startActivitySafely(intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			return;
		}

		// Pattern
		if (R.id.select_security_pattern_layout == viewId) {
			intent = new Intent(LockSecurityChooseActivity.this,
					LockPatternChooseActivity.class);
			startActivitySafely(intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			return;
		}
	}

	private void startActivitySafely(final Intent intent, final int flag) {
		if (intent == null)
			return;

		try {
			intent.setFlags(flag);
			startActivity(intent);
			return;
		} catch (Exception e) {
		}

		final Context c = getApplicationContext();
		Toast.makeText(c, c.getResources().getString(R.string.common_err),
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * セキュリティチェックには戻さず設定画面に戻す
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent(this, SettingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
