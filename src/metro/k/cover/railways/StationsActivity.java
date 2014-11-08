package metro.k.cover.railways;

import java.util.ArrayList;
import java.util.List;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class StationsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		setContentView(R.layout.tutorial_third);
		final ListView listView = (ListView) findViewById(R.id.tutorial_third_listview);
		if (MetroCoverApplication.sStationAllListAdapter == null) {
			return;
		}
		listView.setAdapter(MetroCoverApplication.sStationAllListAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Station station = MetroCoverApplication.sStationAllListAdapter
						.getItem(position);
				PreferenceCommon.setStationName(getApplicationContext(),
						station.getTitle());
				PreferenceCommon.setStationsRailwayName(
						getApplicationContext(), station.getRailway());
				finish();
			}
		});
	}
}
