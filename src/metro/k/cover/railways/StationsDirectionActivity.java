package metro.k.cover.railways;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.view.ButtonWithFont;
import metro.k.cover.view.TextViewWithFont;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 電車の進路方向選択画面　　
 * 
 * @author kohirose
 * 
 */
public class StationsDirectionActivity extends Activity implements
		OnClickListener {

	private String mSelectedStation;
	private String mSelectedStationsRailway;

	private String[] mDirections;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSelectedStation = getIntent().getStringExtra(
				RailwaysUtilities.KEY_SELECTED_STATION_NAME);
		mSelectedStationsRailway = getIntent().getStringExtra(
				RailwaysUtilities.KEY_SELECTED_STATIONS_RAILWAY_NAME);
		setupViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void setupViews() {
		setContentView(R.layout.tutorial_fourth);
		final TextViewWithFont stationView = (TextViewWithFont) findViewById(R.id.tutorial_fourth_selected_staticon);
		stationView
				.setText(!Utilities.isInvalidStr(mSelectedStation) ? mSelectedStation
						: getResources().getString(R.string.nothing));
		setupButtons();
	}

	private void setupButtons() {
		final ButtonWithFont btn_1 = (ButtonWithFont) findViewById(R.id.tutorial_fourth_direction_1);
		final ButtonWithFont btn_2 = (ButtonWithFont) findViewById(R.id.tutorial_fourth_direction_2);

		mDirections = RailwaysUtilities.getDirection(this,
				mSelectedStationsRailway);
		if (mDirections == null) {
			return;
		}
		if (mDirections.length != RailwaysUtilities.SIZE_DIRECTION_DATA_ARRAY) {
			return;
		}
		btn_1.setOnClickListener(this);
		btn_2.setOnClickListener(this);
		btn_1.setText(mDirections[2]);
		btn_2.setText(mDirections[3]);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.tutorial_fourth_direction_1 == viewId) {
			if (mDirections == null) {
				return;
			}
			if (mDirections.length != RailwaysUtilities.SIZE_DIRECTION_DATA_ARRAY) {
				return;
			}
			PreferenceCommon.setTrainDirection(this, mDirections[0]);
			returnSettingActivity();
			return;
		}

		if (R.id.tutorial_fourth_direction_2 == viewId) {
			if (mDirections == null) {
				return;
			}
			if (mDirections.length != RailwaysUtilities.SIZE_DIRECTION_DATA_ARRAY) {
				return;
			}
			PreferenceCommon.setTrainDirection(this, mDirections[1]);
			returnSettingActivity();
			return;
		}
	}

	/**
	 * 設定完了したら設定画面に戻る
	 */
	private void returnSettingActivity() {
		Intent intent = new Intent(this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Utilities.startActivitySafely(intent, this);
	}
}
