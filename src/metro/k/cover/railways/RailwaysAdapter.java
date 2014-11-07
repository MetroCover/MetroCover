package metro.k.cover.railways;

import java.util.ArrayList;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 路線リストのアダプター
 * 
 * @author kohirose
 * 
 */
public class RailwaysAdapter extends ArrayAdapter<Railways> {

	private Context mContext;
	private ArrayList<Railways> list = new ArrayList<Railways>();
	private LayoutInflater inflater;
	private int layout;
	private AssetManager mAssetManager;

	public RailwaysAdapter(Context context, int layoutAt) {
		super(context, layoutAt);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.layout = layoutAt;
		mContext = context;
		mAssetManager = context.getAssets();
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(layout, null, false);
			RelativeLayout relativeLayout = (RelativeLayout) view
					.findViewById(R.id.list_icon_check_retativelayout);
			TextView titleView = (TextView) view
					.findViewById(R.id.list_icon_check_title);
			ImageView iconView = (ImageView) view
					.findViewById(R.id.list_icon_check_icon);
			CheckBox check = (CheckBox) view
					.findViewById(R.id.list_icon_check_check);

			holder = new ViewHolder();
			holder.layout = relativeLayout;
			holder.textView = titleView;
			holder.imageView = iconView;
			holder.checkbox = check;
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		Railways railway = list.get(position);

		holder.textView.setText(railway.getTitle());
		holder.textView.setTypeface(Typeface.createFromAsset(mAssetManager,
				mContext.getResources().getString(R.string.font_free_wing)));
		Utilities.setBackground(holder.imageView, railway.getIcon());

		final int id = position;
		holder.checkbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						list.get(id).setChecked(isChecked);
					}
				});
		holder.checkbox.setChecked(list.get(id).getChecked());
		holder.layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.checkbox.setChecked(!holder.checkbox.isChecked());
			}
		});

		return view;
	}

	@Override
	public void add(Railways object) {
		super.add(object);
		list.add(object);
	}

	public ArrayList<Railways> getList() {
		return list;
	}

	/**
	 * チェックがついている路線リスト
	 * 
	 * @return
	 */
	public ArrayList<Railways> getCheckedItemList() {
		final ArrayList<Railways> checkedList = new ArrayList<Railways>();
		if (list == null) {
			return checkedList;
		}
		final int size = list.size();
		if (size == 0) {
			return checkedList;
		}

		for (int i = 0; i < size; i++) {
			if (list.get(i).getChecked()) {
				checkedList.add(list.get(i));
			}
		}

		return checkedList;
	}

	/**
	 * チェックがついている路線のAPI-IDをつなげたもの
	 * 
	 * @return
	 */
	public String getCheckedItemIDList() {
		if (list == null) {
			return "";
		}

		final int size = list.size();
		if (size == 0) {
			return "";
		}

		String str = "";
		for (int i = 0; i < size; i++) {
			if (list.get(i).getChecked()) {
				str += list.get(i).getCode() + ",";
			}
		}

		if (!Utilities.isInvalidStr(str)) {
			final int len = str.length();
			str = str.substring(0, len - 1);
		}
		return str;
	}

	/**
	 * チェックがついている路線の番号をつなげたもの
	 * 
	 * @return
	 */
	public String getCheckedItemNumberList() {
		if (list == null) {
			return "";
		}

		final int size = list.size();
		if (size == 0) {
			return "";
		}

		String str = "";
		for (int i = 0; i < size; i++) {
			if (list.get(i).getChecked()) {
				str += list.get(i).getRailwayNumber() + ",";
			}
		}

		if (!Utilities.isInvalidStr(str)) {
			final int len = str.length();
			str = str.substring(0, len - 1);
		}
		return str;
	}

	/**
	 * ViewHolder
	 */
	static class ViewHolder {
		RelativeLayout layout;
		TextView textView;
		ImageView imageView;
		CheckBox checkbox;
	}
}