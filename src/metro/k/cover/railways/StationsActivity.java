package metro.k.cover.railways;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.TextViewWithFont;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

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
		setContentView(R.layout.tutorial_third);
		final TextViewWithFont clear = (TextViewWithFont) findViewById(R.id.tutorial_third_clear);
		final ImageView clearSep = (ImageView) findViewById(R.id.tutorial_third_clear_sep);
		final ListView listView = (ListView) findViewById(R.id.tutorial_third_listview);
		if (MetroCoverApplication.sStationAllListAdapter == null) {
			return;
		}
		if (flag) {
			clear.setVisibility(View.VISIBLE);
			clear.setOnClickListener(this);
			clearSep.setVisibility(View.VISIBLE);
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

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.tutorial_third_clear == viewId) {
			PreferenceCommon.setStationName(getApplicationContext(),
					getResources().getString(R.string.nothing));
			PreferenceCommon
					.setStationsRailwayName(getApplicationContext(), "");
			finish();
		}
	}
}
