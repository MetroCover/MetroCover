package metro.k.cover.tutorial;

import java.util.ArrayList;
import java.util.List;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.railways.Railways;
import metro.k.cover.railways.RailwaysAdapter;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.view.TextViewWithFont;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TutorialSecond extends Fragment implements OnClickListener {

	private static List<Railways> mList = new ArrayList<Railways>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_second, null);

		// ListViewの設定
		mList = RailwaysUtilities.getAllRailways(getActivity());
		if (mList != null) {
			final int size = mList.size();
			if (size > 0) {
				final ListView listview = (ListView) view
						.findViewById(R.id.tutorial_second_listview);
				final ArrayAdapter<Railways> adapter = new RailwaysAdapter(
						getActivity(), R.layout.list_icon_title_check_at);
				for (int i = 0; i < size; i++) {
					adapter.add(mList.get(i));
				}
				listview.setAdapter(adapter);
			}
		}

		// 次へ
		final TextViewWithFont next = (TextViewWithFont) view
				.findViewById(R.id.tutorial_second_next_btn);
		next.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.tutorial_second_next_btn == viewId) {
			TutorialActivity.setNextPage();
			return;
		}
	}

	/**
	 * チェックボックスにチェックされている路線のIDを取得する
	 * 
	 * @return
	 */
	public static String getCheckedStr() {
		if (mList == null) {
			return "";
		}

		final int size = mList.size();
		if (size == 0) {
			return "";
		}

		String str = "";
		for (int i = 0; i < size; i++) {
			if (mList.get(i).getChecked()) {
				str += mList.get(i).getCode() + ",";
			}
		}

		if (!Utilities.isInvalidStr(str)) {
			final int len = str.length();
			str = str.substring(0, len - 1);
		}
		return str;
	}

	/**
	 * チェックボックスにチェックされている路線のIDを取得する
	 * 
	 * @return
	 */
	public static ArrayList<String> getCheckedList() {
		if (mList == null) {
			return null;
		}

		final int size = mList.size();
		if (size == 0) {
			return null;
		}

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			if (mList.get(i).getChecked()) {
				list.add(mList.get(i).getCode());
			}
		}
		return list;
	}

	/**
	 * チェックボックスにチェックされている路線番号を取得する
	 * 
	 * @return
	 */
	public static String getCheckedNumber() {
		if (mList == null) {
			return "";
		}

		final int size = mList.size();
		if (size == 0) {
			return "";
		}

		String str = "";
		for (int i = 0; i < size; i++) {
			if (mList.get(i).getChecked()) {
				str += mList.get(i).getRailwayNumber() + ",";
			}
		}

		if (!Utilities.isInvalidStr(str)) {
			final int len = str.length();
			str = str.substring(0, len - 1);
		}
		return str;
	}

	/**
	 * チェックボックスにチェックされている路線の名前を取得する
	 * 
	 * @return
	 */
	public static ArrayList<String> getCheckedNameList() {
		if (mList == null) {
			return null;
		}

		final int size = mList.size();
		if (size == 0) {
			return null;
		}

		ArrayList<String> list = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			if (mList.get(i).getChecked()) {
				list.add(mList.get(i).getTitle());
			}
		}
		return list;
	}

	/**
	 * チェックボックスにチェックされている路線のレスポンスネームを取得する
	 * 
	 * @return
	 */
	public static String getCheckedResponseNmae() {
		if (mList == null) {
			return "";
		}

		final int size = mList.size();
		if (size == 0) {
			return "";
		}

		String str = "";
		for (int i = 0; i < size; i++) {
			if (mList.get(i).getChecked()) {
				str += mList.get(i).getResponseName() + ",";
			}
		}

		if (!Utilities.isInvalidStr(str)) {
			final int len = str.length();
			str = str.substring(0, len - 1);
		}
		return str;
	}
}
