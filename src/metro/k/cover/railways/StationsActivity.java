package metro.k.cover.railways;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

/**
 * 駅選択画面
 * 
 * @author kohirose
 * 
 */
public class StationsActivity extends Activity implements OnClickListener {

	private boolean flag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (!Utilities.isInvalidStr(getIntent().getStringExtra(
				RailwaysUtilities.KEY_CURRENT_STATION))) {
			// 設定中の場合
			flag = true;
		}
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
		setContentView(R.layout.activity_stations);
		final ImageView clear = (ImageView) findViewById(R.id.stations_clear_image);
		final ListView listView = (ListView) findViewById(R.id.stations_listview);
		if (MetroCoverApplication.sStationAllListAdapter == null) {
			return;
		}
		if (flag) {
			clear.setVisibility(View.VISIBLE);
			clear.setOnClickListener(this);
		}
		listView.setAdapter(MetroCoverApplication.sStationAllListAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Station station = MetroCoverApplication.sStationAllListAdapter
						.getItem(position);
				final String name = station.getTitle();
				final String railway = station.getRailway();
				if (name.equals(railway)) {
					return;
				}

				saveStationData(name, railway, station.getNameForAPI());
				startSelectDirection(railway, name);
			}
		});
	}

	private void saveStationData(final String name, final String railway,
			final String nameForAPI) {
		new Thread("saveStationData") {
			@Override
			public void run() {
				PreferenceCommon.setStationName(getApplicationContext(), name);
				PreferenceCommon.setStationsRailwayName(
						getApplicationContext(), railway);
				PreferenceCommon.setStationNameForAPI(getApplicationContext(),
						nameForAPI);
			}
		}.start();
	}

	private void startSelectDirection(final String railway, final String station) {
		if (Utilities.isInvalidStr(railway) || Utilities.isInvalidStr(railway)) {
			return;
		}
		Intent intent = new Intent(this, StationsDirectionActivity.class);
		intent.putExtra(RailwaysUtilities.KEY_SELECTED_STATIONS_RAILWAY_NAME,
				railway);
		intent.putExtra(RailwaysUtilities.KEY_SELECTED_STATION_NAME, station);
		Utilities.startActivitySafely(intent, this);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.stations_clear_image == viewId) {
			PreferenceCommon.setStationName(getApplicationContext(),
					getResources().getString(R.string.nothing));
			PreferenceCommon
					.setStationsRailwayName(getApplicationContext(), "");
			PreferenceCommon.setStationNameForAPI(getApplicationContext(), "");
			PreferenceCommon.setTrainDirection(getApplicationContext(), "");
			finish();
		}
	}
}
