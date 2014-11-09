package metro.k.cover.lock;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.view.ButtonWithFont;
import metro.k.cover.view.TextViewWithFont;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;

/**
 * パスワードセキュリティのダイアログ
 */
public class LockPasswordDialogActivity extends Activity implements
		OnClickListener {

	private EditText mEditText;
	private TextViewWithFont mOnceAgain;

	private String mOldPass;
	private final String HOMEE_SYSTEM_PASS = "###***&&&";

	private boolean isFirstTime; // 確認入力用
	private boolean isFromSetting;// セキュリティを外すとき用

	// パスワードの許容最小文字数
	private static final int MIN_PASS_COUNT = 4;

	// vibe
	private Vibrator mVibe;
	private static final int VIBE_TIME = 100;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		isFirstTime = true;
		isFromSetting = getIntent().getBooleanExtra(
				LockUtilities.KEY_PASSWORD_IS_FROM_SETTING, false);
		setupViews();
		setTitleView();
	}

	@Override
	public void onResume() {
		super.onResume();
		mOldPass = PreferenceCommon.getCurrentPassword(getApplicationContext());
	}

	private void setupViews() {
		final Resources res = getResources();
		final AssetManager am = getAssets();
		setContentView(R.layout.activity_password_security_dialog);
		getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		mEditText = (EditText) findViewById(R.id.security_pass_edittext);
		Utilities.setFontEditTextView(mEditText, am, res);
		
		mOnceAgain = (TextViewWithFont) findViewById(R.id.security_pass_title);
		mVibe = (Vibrator) getSystemService(VIBRATOR_SERVICE);

		ButtonWithFont cancelBtn = (ButtonWithFont) findViewById(R.id.security_pass_cancel_btn);
		ButtonWithFont confBtn = (ButtonWithFont) findViewById(R.id.security_pass_complete_btn);
		cancelBtn.setOnClickListener(this);
		confBtn.setOnClickListener(this);
	}

	private void setTitleView() {
		final Resources res = getResources();
		if (isFromSetting) {
			mOnceAgain.setText(res.getString(R.string.lock_input_old_password));
		} else {
			mOnceAgain.setText(res
					.getString(R.string.lock_input_password_title));
		}
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();

		// Cancel
		if (R.id.security_pass_cancel_btn == viewId) {
			finish();
			return;
		}

		// OK
		if (R.id.security_pass_complete_btn == viewId) {
			final String input = mEditText.getText().toString();
			// 設定画面から直接来た場合（現在のパスワードを入力させる）
			if (isFromSetting) {
				removePasswordSecurity(input);
				return;
			}

			// 新しいパスワードの設定（入力一回目）
			if (isFirstTime) {
				inputNewPassword(input);
				return;
			}

			// 新しいパスワードの設定（確認入力用）
			comfirmPass(input);
		}
	}

	private void showSubTitleMessage(final int stringId) {
		final Resources res = getResources();
		mEditText.setText("");
		mOnceAgain.setText(res.getString(stringId));
		mVibe.vibrate(VIBE_TIME);
	}

	private void comfirmPass(final String input) {
		if (input == null)
			return;

		final String firstPass = PreferenceCommon
				.getFirstPassword(getApplicationContext());

		// 文字数不足
		if (firstPass.length() == 0 || input.length() < MIN_PASS_COUNT) {
			showSubTitleMessage(R.string.lock_input_pass_minlength);
			return;
		}

		// １回目の入力値と異なる
		if (!input.equals(firstPass)) {
			showSubTitleMessage(R.string.lock_wrong_pass);
			return;
		}

		// 成功
		commitPassConfirm(input);
	}

	private void inputNewPassword(final String input) {
		// Invalid
		if (input == null)
			return;
		if (input.length() == 0)
			return;

		// 文字数不足
		if (input.length() < MIN_PASS_COUNT) {
			showSubTitleMessage(R.string.lock_input_pass_minlength);
			return;
		}

		// 成功
		commitPassFirst(input);
	}

	private void removePasswordSecurity(final String input) {
		if (input == null)
			return;

		// 未入力
		if (input.length() == 0)
			return;

		// パスワード不一致
		if (!mOldPass.equals(HOMEE_SYSTEM_PASS) && !mOldPass.equals(input)) {
			showSubTitleMessage(R.string.lock_wrong_pass);
			return;
		}

		// パスワード成功
		Intent intent = new Intent(this, LockSecurityChooseActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	// 初回入力時のパスワード保存
	private void commitPassFirst(final String input) {
		if (input == null) {
			return;
		}
		if (input.length() < MIN_PASS_COUNT) {
			return;
		}

		PreferenceCommon.setFirstPassword(getApplicationContext(), input);
		mOnceAgain.setText(getResources().getString(
				R.string.lock_input_pass_one_more));
		mEditText.setText("");
		isFirstTime = false;
	}

	// 確定のパスワード保存
	private void commitPassConfirm(final String input) {
		if (input == null) {
			return;
		}
		if (input.length() < MIN_PASS_COUNT) {
			return;
		}

		final Context c = getApplicationContext();
		PreferenceCommon.setSecurityType(c,
				getResources()
						.getInteger(R.integer.lock_security_type_password));// セキュリティ種別
		PreferenceCommon.setCurrentPassword(c, input);// 決定したパスワード

		Intent intent = new Intent(this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(intent);
		} catch (Exception e) {
			Utilities.showErrorCommonToast(c);
		}
	}
}
