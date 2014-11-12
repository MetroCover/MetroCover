package metro.k.cover.tutorial;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.railways.Station;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class TutorialThird extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_third, null);
		setListView(view);

		return view;
	}

	private void setListView(final View view) {
		if (view == null) {
			return;
		}

		final ListView listView = (ListView) view
				.findViewById(R.id.tutorial_third_listview);
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
				PreferenceCommon.setStationName(getActivity(),
						station.getTitle());
				PreferenceCommon.setStationsRailwayName(getActivity(),
						station.getRailway());
				TutorialActivity.setNextPage();
			}
		});
	}
}
