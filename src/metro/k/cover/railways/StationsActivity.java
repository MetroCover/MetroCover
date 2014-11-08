package metro.k.cover.railways;

import java.util.ArrayList;
import java.util.List;

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
		final ArrayList<String> checkedIdList = RailwaysUtilities
				.getAllRailwaysCode();
		final ArrayList<String> checkedNameList = RailwaysUtilities
				.getAllRailwaysName(this);
		if (checkedIdList == null) {
			return;
		}
		final int size = checkedIdList.size();
		if (size == 0) {
			return;
		}

		final ArrayAdapter<Station> adapter = new StationsAdapter(this,
				R.layout.list_icon_title_radio_at);

		for (int i = 0; i < size; i++) {
			String code = checkedIdList.get(i);
			String railway = checkedNameList.get(i);
			List<String> stations = RailwaysUtilities
					.getStationList(this, code);
			ArrayList<Drawable> icons = RailwaysUtilities.getStationIconList(
					this, code);
			if (stations == null) {
				continue;
			}
			for (int j = 0; j < stations.size(); j++) {
				if (j == 0) {
					adapter.add(new Station(railway, railway, null));
				}
				adapter.add(new Station(railway, stations.get(j), icons.get(j)));
			}
		}

		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final Station station = adapter.getItem(position);
				PreferenceCommon.setStationName(getApplicationContext(),
						station.getTitle());
				PreferenceCommon.setStationsRailwayName(
						getApplicationContext(), station.getRailway());
				finish();
			}
		});
	}
}
