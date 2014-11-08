package metro.k.cover.tutorial;

import java.util.ArrayList;
import java.util.List;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.railways.Station;
import metro.k.cover.railways.StationsAdapter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
		final ArrayList<String> checkedIdList = RailwaysUtilities
				.getAllRailwaysCode();
		final ArrayList<String> checkedNameList = RailwaysUtilities
				.getAllRailwaysName(getActivity());
		if (checkedIdList == null) {
			return;
		}
		final int size = checkedIdList.size();
		if (size == 0) {
			return;
		}

		final ArrayAdapter<Station> adapter = new StationsAdapter(
				getActivity(), R.layout.list_icon_title_radio_at);

		for (int i = 0; i < size; i++) {
			String code = checkedIdList.get(i);
			String railway = checkedNameList.get(i);
			List<String> stations = RailwaysUtilities.getStationList(
					getActivity(), code);
			ArrayList<Drawable> icons = RailwaysUtilities.getStationIconList(
					getActivity(), code);
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
				PreferenceCommon.setStationName(getActivity(),
						station.getTitle());
				PreferenceCommon.setStationsRailwayName(getActivity(),
						station.getRailway());
				TutorialActivity.setNextPage();

			}
		});
	}
}
