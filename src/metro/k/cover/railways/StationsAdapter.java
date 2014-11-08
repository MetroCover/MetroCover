package metro.k.cover.railways;

import java.util.ArrayList;

import metro.k.cover.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * 駅リストのアダプター
 * 
 * @author kohirose
 * 
 */
public class StationsAdapter extends ArrayAdapter<Station> {

	private ArrayList<Station> list = new ArrayList<Station>();
	private LayoutInflater inflater;
	private String mCheckedStation;

	public StationsAdapter(Context context, int layoutAt) {
		super(context, layoutAt);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final StationLayout view;

		if (convertView == null) {
			view = (StationLayout) inflater.inflate(
					R.layout.list_icon_title_radio_at, null);
		} else {
			view = (StationLayout) convertView;
		}

		final Station station = getItem(position);
		view.bindView(position, station);

		return view;
	}

	@Override
	public void add(Station object) {
		super.add(object);
		list.add(object);
	}

	public String getCheckedStation() {
		return mCheckedStation;
	}
}