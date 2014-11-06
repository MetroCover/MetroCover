package metro.k.cover.lock;

import java.util.List;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.lock.LockPatternView.Cell;
import metro.k.cover.lock.LockPatternView.DisplayMode;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LockPatternChooseActivity extends Activity implements
		LockPatternView.OnPatternListener {

	// pass
	private String mCurrentPattern; // もともと設定していた場合のパターン

	// SettingActivityからきたかどうか
	private boolean isFromSetting;

	// パターンの状態
	private static final int PATTERN_IDLE_FIRST = 0;
	private static final int PATTERN_DRAWING_FIRST = 1;
	private static final int PATTERN_MISS_FIRST = 2;
	private static final int PATTERN_SUCCESS_FISRST = 3;
	private static final int PATTERN_IDLE_SECOND = 4;
	private static final int PATTERN_DRAWING_SECOND = 5;
	private static final int PATTERN_MISS_SECOND = 6;
	private static final int PATTERN_SUCCESS_SECOND = 7;

	// １回目か２回目か
	private boolean isSecond;

	// タイトル
	private TextView mTitle;

	// キャンセルボタン
	private Button mCancelButton;

	// 完了ボタン
	private Button mCompButton;

	// １回目と２回目のパターン
	private String mPatternFirst;
	private String mPatternSecond;

	// View
	private LockPatternView mPatternView;

	// 現在の状態
	private int mState;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		isFromSetting = getIntent().getBooleanExtra(
				LockUtilities.KEY_PATTERN_IS_FROM_SETTING, false);
		setupViews();
	}

	@Override
	public void onResume() {
		super.onResume();
		mCurrentPattern = PreferenceCommon
				.getCurrentPattern(getApplicationContext());
	}

	/**
	 * レイアウト
	 */
	private void setupViews() {
		requestWindowFeature(1);
		setContentView(R.layout.activity_lock_pattern);
		mTitle = ((TextView) findViewById(R.id.pattern_title));
		mCancelButton = ((Button) findViewById(R.id.lock_pattern_cancel_btn));
		mCompButton = ((Button) findViewById(R.id.lock_pattern_comp_btn));
		mPatternView = ((LockPatternView) findViewById(R.id.patternView));
		mPatternView.setOnPatternListener(this);
		changeViewFromState(mState);
	}

	/**
	 * 状態によってViewを変える
	 * 
	 * @param state
	 */
	private void changeViewFromState(int state) {
		mState = state;
		switch (mState) {
		case PATTERN_IDLE_FIRST:
			setIdleStateViewFirst();
			return;
		case PATTERN_DRAWING_FIRST:
			setDrawingStateViewFirst();
			return;
		case PATTERN_MISS_FIRST:
			mPatternView.setDisplayMode(DisplayMode.Wrong);
			setMissStateViewFirst();
			return;
		case PATTERN_SUCCESS_FISRST:
			setSuccessState();
			return;
		case PATTERN_IDLE_SECOND:
			setIdleStateViewSecond();
			return;
		case PATTERN_DRAWING_SECOND:
			setDrawingStateSecond();
			return;
		case PATTERN_MISS_SECOND:
			mPatternView.setDisplayMode(DisplayMode.Wrong);
			setMissStateViewSecond();
			return;
		case PATTERN_SUCCESS_SECOND:
			setSuccessConfirmState();
		default:
			return;
		}
	}

	/**
	 * パターン入力状態（１回目）
	 */
	private void setDrawingStateViewFirst() {
		final Resources res = getResources();
		final AssetManager am = getAssets();
		mTitle.setText(res.getString(R.string.lock_pattern_title_drawing));
		Utilities.setFontTextView(mTitle, am, res);
		mCancelButton.setText(res.getString(R.string.cancel));
		mCancelButton.setEnabled(false);
		if (isFromSetting) {
			mCompButton.setText(res.getString(R.string.lock_pattern_confirm));
		} else {
			mCompButton.setText(res.getString(R.string.lock_pattern_next));
		}
		mCompButton.setEnabled(false);

		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターン入力状態（２回目）
	 */
	private void setDrawingStateSecond() {
		final Resources res = getResources();
		final AssetManager am = getAssets();
		mTitle.setText(res.getString(R.string.lock_pattern_title_drawing));
		mCancelButton.setText(res.getString(R.string.cancel));
		mCancelButton.setEnabled(false);
		mCompButton.setText(res.getString(R.string.lock_pattern_confirm));
		mCompButton.setEnabled(false);

		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターン失敗状態（１回目）
	 */
	private void setMissStateViewFirst() {
		final Resources res = getResources();
		if (isFromSetting) {
			mTitle.setText(res
					.getString(R.string.lock_pattern_title_miss_first_from_setting));
			mCompButton.setText(res.getString(R.string.lock_pattern_confirm));
		} else {
			mTitle.setText(res
					.getString(R.string.lock_pattern_title_miss_first));
			mCompButton.setText(res.getString(R.string.lock_pattern_next));
		}
		mCancelButton.setText(res.getString(R.string.lock_pattern_back));
		mCancelButton.setEnabled(true);
		mCancelButton
				.setOnClickListener(changeStateListener(PATTERN_IDLE_FIRST));
		mCompButton.setEnabled(false);

		final AssetManager am = getAssets();
		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターン失敗状態（２回目）
	 */
	private void setMissStateViewSecond() {
		final Resources res = getResources();
		mTitle.setText(res
				.getString(R.string.lock_pattern_title_miss_first_from_setting));
		mCancelButton.setText(res.getString(R.string.cancel));
		mCancelButton.setEnabled(true);
		mCancelButton.setOnClickListener(mCancelListener);
		mCompButton.setText(res.getString(R.string.lock_pattern_back));
		mCompButton.setEnabled(true);
		mCompButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mPatternView.clearPattern();
				mPatternView.enableInput();
			}
		});

		final AssetManager am = getAssets();
		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターンアイドル状態（１回目）
	 */
	private void setIdleStateViewFirst() {
		final Resources res = getResources();
		mPatternView.clearPattern();
		mPatternView.enableInput();
		if (isFromSetting) {
			mTitle.setText(res
					.getString(R.string.lock_pattern_title_idle_from_setting));
			mCompButton.setText(res.getString(R.string.lock_pattern_confirm));
		} else {
			mTitle.setText(res
					.getString(R.string.lock_pattern_title_idle_first));
			mCompButton.setText(res.getString(R.string.lock_pattern_next));
		}
		mCancelButton.setText(res.getString(R.string.cancel));
		mCancelButton.setEnabled(true);
		mCancelButton.setOnClickListener(mCancelListener);
		mCompButton.setEnabled(false);

		final AssetManager am = getAssets();
		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターンアイドル状態（２回目）
	 */
	private void setIdleStateViewSecond() {
		final Resources res = getResources();
		mPatternView.clearPattern();
		mPatternView.enableInput();
		mTitle.setText(res.getString(R.string.lock_pattern_title_idle_second));
		mCancelButton.setText(res.getString(R.string.cancel));
		mCancelButton.setEnabled(true);
		mCancelButton.setOnClickListener(mCancelListener);
		mCompButton.setText(res.getString(R.string.lock_pattern_next));
		mCompButton.setEnabled(false);

		final AssetManager am = getAssets();
		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターン成功状態（１回目）
	 */
	private void setSuccessState() {
		final Resources res = getResources();
		mPatternView.disableInput();
		mTitle.setText(res.getString(R.string.lock_pattern_title_success_first));
		mCancelButton.setText(res.getString(R.string.lock_pattern_back));
		mCancelButton.setEnabled(true);
		mCancelButton
				.setOnClickListener(changeStateListener(PATTERN_IDLE_FIRST));
		mCompButton.setText(res.getString(R.string.lock_pattern_next));
		mCompButton.setEnabled(true);
		mCompButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeViewFromState(PATTERN_IDLE_SECOND);
				isSecond = true;
			}
		});

		final AssetManager am = getAssets();
		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	/**
	 * パターン成功状態（２回目）
	 */
	private void setSuccessConfirmState() {
		final Resources res = getResources();
		mPatternView.disableInput();
		mTitle.setText(res
				.getString(R.string.lock_pattern_title_success_second));
		mCancelButton.setText(res.getString(R.string.cancel));
		mCompButton.setText(res.getString(R.string.lock_pattern_confirm));
		mCancelButton.setEnabled(true);
		mCancelButton.setOnClickListener(mCancelListener);
		mCompButton.setEnabled(true);
		mCompButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				complete(mPatternSecond.toString());
			}
		});

		final AssetManager am = getAssets();
		Utilities.setFontTextView(mTitle, am, res);
		Utilities.setFontButtonView(mCancelButton, am, res);
		Utilities.setFontButtonView(mCompButton, am, res);
	}

	private View.OnClickListener changeStateListener(final int toState) {
		View.OnClickListener li = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				changeViewFromState(toState);
			}
		};
		return li;
	}

	private View.OnClickListener mCancelListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	private void complete(final String input) {
		new Thread("LockPattern_Preference") {
			@Override
			public void run() {
				commitPattern(input);
			}
		}.start();

		setResult(-1);
		Intent intent = new Intent(this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	// 設定したパターンを保存
	private void commitPattern(final String input) {
		final Context c = getApplicationContext();
		final Resources res = getResources();
		// セキュリティ種別
		PreferenceCommon.setSecurityType(c,
				res.getInteger(R.integer.lock_security_type_pattern));
		// 設定値
		PreferenceCommon.setCurrentPattern(c, input);
	}

	@Override
	public void onPatternStart() {
		if (!isSecond) {
			changeViewFromState(PATTERN_DRAWING_FIRST);
			return;
		}
		changeViewFromState(PATTERN_DRAWING_SECOND);
	}

	@Override
	public void onPatternCleared() {

	}

	@Override
	public void onPatternCellAdded(List<Cell> pattern) {

	}

	@Override
	public void onPatternDetected(List<Cell> pattern) {
		if (!isSecond) {
			inputFirstPattern(pattern);
		} else {
			confirmPattern(pattern);
		}
	}

	// パターン入力（１回目）
	private void inputFirstPattern(final List<Cell> pattern) {
		// Invalid
		if (pattern == null)
			return;
		// 入力不足
		final int size = pattern.size();
		if (size < LockUtilities.PATTERN_MINIMUM_LENGTH) {
			changeViewFromState(PATTERN_MISS_FIRST);
			return;
		}

		final String input = pattern.toString();
		if (!isFromSetting) {
			mPatternFirst = input;
			changeViewFromState(PATTERN_SUCCESS_FISRST);
			return;
		}

		String compareMasterStr = "";
		for (int i = 0; i < size; i++) {
			compareMasterStr += pattern.get(i).toMailString();
		}
		if (mCurrentPattern.equals(input)
				|| LockUtilities.getMasterPattern(getApplicationContext(),
						compareMasterStr)) {
			Intent intent = new Intent(this, LockSecurityChooseActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		} else {
			changeViewFromState(PATTERN_MISS_FIRST);
		}
	}

	// パターン入力（確定）
	private void confirmPattern(final List<Cell> pattern) {
		if (pattern == null)
			return;
		if (!isSecond)
			return;

		// 入力不足
		if (pattern.size() < LockUtilities.PATTERN_MINIMUM_LENGTH) {
			changeViewFromState(PATTERN_MISS_SECOND);
			return;
		}

		mPatternSecond = pattern.toString();
		// １回目の入力と２回目の入力が異なるとき
		if (!mPatternSecond.equals(mPatternFirst)) {
			changeViewFromState(PATTERN_MISS_SECOND);
			return;
		}

		// 成功
		changeViewFromState(PATTERN_SUCCESS_SECOND);
	}

	/**
	 * 画面回転時に状態を保存しておく
	 */

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		final State state;
		if (!isSecond) {
			state = new State(mState, mPatternFirst);
		} else {
			state = new State(mState, mPatternSecond);
		}
		outState.putInt(LockUtilities.CONFIGURATION_STATE_STATE, state.state);
		outState.putString(LockUtilities.CONFIGURATION_STATE_PATTERN,
				state.pattern);
	}

	/**
	 * 保存したものをとってくる
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);

		final int state = savedInstanceState
				.getInt(LockUtilities.CONFIGURATION_STATE_STATE);
		final String pattern = savedInstanceState
				.getString(LockUtilities.CONFIGURATION_STATE_PATTERN);

		mState = state;
		if (!isSecond) {
			mPatternFirst = pattern;
		} else {
			mPatternSecond = pattern;
		}
	}

	/**
	 * 状態クラス
	 */
	public static final class State {
		public int state;
		public String pattern;

		public State(int state, String pattern) {
			this.state = state;
			this.pattern = pattern;
		}
	}
}