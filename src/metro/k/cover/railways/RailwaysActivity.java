package metro.k.cover.railways;

import java.util.ArrayList;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.ButtonWithFont;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class RailwaysActivity extends Activity implements OnClickListener {

	private RailwaysAdapter mRailwaysAdapter;

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
		setContentView(R.layout.activity_railways);
		ButtonWithFont cancel = (ButtonWithFont) findViewById(R.id.railways_cancel_btn);
		ButtonWithFont complete = (ButtonWithFont) findViewById(R.id.railways_complete_btn);
		cancel.setOnClickListener(this);
		complete.setOnClickListener(this);

		setupListView();
	}

	private void setupListView() {
		final ListView listView = (ListView) findViewById(R.id.railways_listview);
		mRailwaysAdapter = new RailwaysAdapter(this,
				R.layout.list_icon_title_check_at);
		final ArrayList<Railways> list = getCheckedList();
		if (list == null) {
			return;
		}

		final int size = list.size();
		if (size == 0) {
			return;
		}

		for (int i = 0; i < size; i++) {
			mRailwaysAdapter.add(list.get(i));
		}
		listView.setAdapter(mRailwaysAdapter);
	}

	/**
	 * 保存している路線リスト取得
	 * 
	 * @return
	 */
	private ArrayList<Railways> getCheckedList() {
		final ArrayList<Railways> list = RailwaysUtilities.getAllRailways(this);
		final String str = PreferenceCommon.getRailwaysNumber(this);
		final ArrayList<String> strs = Utilities.getSplitStr(str);
		if (strs == null) {
			return list;
		}

		final int checked_size = strs.size();
		if (checked_size == 0) {
			return list;
		}

		final ArrayList<Integer> ints = new ArrayList<Integer>();
		for (int i = 0; i < checked_size; i++) {
			try {
				ints.add(Integer.parseInt(strs.get(i)));
			} catch (Exception e) {
				continue;
			}
		}

		final int size = list.size();
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < checked_size; j++) {
				if (ints.get(j) == list.get(i).getRailwayNumber()) {
					list.get(i).setChecked(true);
				}
			}
		}
		return list;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();

		// キャンセル
		if (R.id.railways_cancel_btn == viewId) {
			finish();
			return;
		}

		// 完了
		if (R.id.railways_complete_btn == viewId) {
			if (mRailwaysAdapter != null) {
				PreferenceCommon.setRailwaysInfomation(this,
						mRailwaysAdapter.getCheckedItemIDList());
				PreferenceCommon.setRailwaysNumber(this,
						mRailwaysAdapter.getCheckedItemNumberList());
				PreferenceCommon.setRailwaysResponseName(this,
						mRailwaysAdapter.getCheckedResponseNmae());
				MetroCoverApplication
						.asyncCreateRailwaysInfoList(getApplicationContext());
			}
			finish();
			return;
		}
	}
}
