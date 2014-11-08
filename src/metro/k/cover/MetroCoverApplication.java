package metro.k.cover;

import java.util.ArrayList;
import java.util.List;

import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.railways.Station;
import metro.k.cover.railways.StationsAdapter;

import android.app.Application;
import android.graphics.drawable.Drawable;
import android.widget.ArrayAdapter;

public class MetroCoverApplication extends Application {

	public static ArrayAdapter<Station> sStationAllListAdapter;

	@Override
	public void onCreate() {
		super.onCreate();
		createAllStationList();
	}

	/**
	 * アプリ起動時に全駅名リストを作成しておく
	 */
	private void createAllStationList() {
		new Thread("createAllStationList") {
			@Override
			public void run() {
				final ArrayList<String> checkedIdList = RailwaysUtilities
						.getAllRailwaysCode();
				final ArrayList<String> checkedNameList = RailwaysUtilities
						.getAllRailwaysName(getApplicationContext());
				if (checkedIdList == null) {
					return;
				}
				final int size = checkedIdList.size();
				if (size == 0) {
					return;
				}

				sStationAllListAdapter = new StationsAdapter(
						getApplicationContext(),
						R.layout.list_icon_title_radio_at);
				for (int i = 0; i < size; i++) {
					String code = checkedIdList.get(i);
					String railway = checkedNameList.get(i);
					List<String> stations = RailwaysUtilities.getStationList(
							getApplicationContext(), code);
					ArrayList<Drawable> icons = RailwaysUtilities
							.getStationIconList(getApplicationContext(), code);
					if (stations == null) {
						continue;
					}
					for (int j = 0; j < stations.size(); j++) {
						if (j == 0) {
							sStationAllListAdapter.add(new Station(railway,
									railway, null));
						}
						sStationAllListAdapter.add(new Station(railway,
								stations.get(j), icons.get(j)));
					}
				}
			}
		}.start();
	}
}
