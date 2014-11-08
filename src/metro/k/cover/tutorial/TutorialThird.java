package metro.k.cover.tutorial;

import java.util.ArrayList;
import java.util.List;

import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.railways.Station;
import metro.k.cover.railways.StationsAdapter;
import metro.k.cover.view.TextViewWithFont;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TutorialThird extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_third, null);

		// Finish
		final TextViewWithFont finishView = (TextViewWithFont) view
				.findViewById(R.id.tutorial_third_finish_btn);
		finishView.setOnClickListener(this);

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
					adapter.add(new Station(railway, null));
				}
				adapter.add(new Station(stations.get(j), icons.get(j)));
			}
		}
		listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		listView.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.tutorial_third_finish_btn == viewId) {
			Intent intent = new Intent(getActivity(), SettingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Utilities.startActivitySafely(intent, getActivity());
			getActivity().finish();
			return;
		}
	}
}
