package metro.k.cover.lock;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * セキュリティ設定画面
 */
public class SelectSecurityActivity extends Activity implements OnClickListener {

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
		LinearLayout passLayout = (LinearLayout) findViewById(R.id.security_pass_layout);
		LinearLayout noneLayout = (LinearLayout) findViewById(R.id.security_none_layout);
		LinearLayout paternLayout = (LinearLayout) findViewById(R.id.security_patern_layout);

		passLayout.setOnClickListener(this);
		noneLayout.setOnClickListener(this);
		paternLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		Intent intent = null;

		// Password
		// if (R.id.security_pass_layout == viewId) {
		// intent = new Intent(SelectSecurityActivity.this,
		// PasswordSecurityDialogActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// return;
		// }

		// None
		if (R.id.security_none_layout == viewId) {
			// run on ui-thread
			PreferenceCommon.setSecurityType(getApplicationContext(),
					getResources()
							.getInteger(R.integer.lock_security_type_none));
			intent = new Intent(SelectSecurityActivity.this,
					SettingActivity.class);
			startActivitySafely(intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			return;
		}

		// Pattern
		if (R.id.security_patern_layout == viewId) {
			intent = new Intent(SelectSecurityActivity.this,
					ChooseLockPattern.class);
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
