package metro.k.cover.lock;

import java.util.ArrayList;

import metro.k.cover.R;
import metro.k.cover.railways.RailwaysInfo;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * ロック画面に出す遅延情報リストのアダプター
 * 
 * @author kohirose
 * 
 */
public class LockRailwaysInfoAdapter extends ArrayAdapter<RailwaysInfo> {

	private ArrayList<RailwaysInfo> list = new ArrayList<RailwaysInfo>();
	private LayoutInflater inflater;

	public LockRailwaysInfoAdapter(Context context, int layoutAt) {
		super(context, layoutAt);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final LockRailwaysLayout view;

		if (convertView == null) {
			view = (LockRailwaysLayout) inflater.inflate(
					R.layout.lock_railways_info_at, null);
		} else {
			view = (LockRailwaysLayout) convertView;
		}

		final RailwaysInfo info = getItem(position);
		view.bindView(info);

		return view;
	}

	@Override
	public void add(RailwaysInfo object) {
		super.add(object);
		list.add(object);
	}
}