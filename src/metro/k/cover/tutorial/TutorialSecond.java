package metro.k.cover.tutorial;

import java.util.ArrayList;
import java.util.List;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TutorialSecond extends Fragment implements OnClickListener {

	private List<IconTitleCheck> mList = new ArrayList<IconTitleCheck>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_second, null);

		// ListViewの設定
		setupArraylists();
		final ListView listview = (ListView) view
				.findViewById(R.id.tutorial_second_listview);
		final ArrayAdapter<IconTitleCheck> adapter = new IconTitleCheckAdapter(
				getActivity(), R.layout.list_icon_title_check_at);
		for (int i = 0; i < mList.size(); i++) {
			adapter.add(mList.get(i));
		}
		listview.setAdapter(adapter);

		final Resources res = getResources();
		final AssetManager am = getActivity().getAssets();

		// タイトル
		final TextView titleView = (TextView) view
				.findViewById(R.id.tutorial_second_title);
		titleView.setTypeface(Typeface.createFromAsset(am,
				res.getString(R.string.font_free_wing)));

		// 次へ
		final TextView next = (TextView) view
				.findViewById(R.id.tutorial_second_next_btn);
		next.setTypeface(Typeface.createFromAsset(am,
				res.getString(R.string.font_free_wing)));
		next.setOnClickListener(this);

		String str = "yurakucho,tozai,namboku";
		final ArrayList<String> list = Utilities.getSplitStr(str);
		for (int i = 0; i < list.size(); i++) {
			Log.d("kohirose", list.get(i));
		}

		return view;
	}

	private void setupArraylists() {
		final Resources res = getResources();

		// 千代田線
		IconTitleCheck item = new IconTitleCheck(
				res.getString(R.string.railway_chiyoda),
				res.getDrawable(R.drawable.ic_chiyoda), false);
		mList.add(item);

		// 副都心線
		item = new IconTitleCheck(res.getString(R.string.railway_fukutoshin),
				res.getDrawable(R.drawable.ic_fukutoshin), false);
		mList.add(item);

		// 銀座線
		item = new IconTitleCheck(res.getString(R.string.railway_ginza),
				res.getDrawable(R.drawable.ic_ginza), false);
		mList.add(item);

		// 半蔵門線
		item = new IconTitleCheck(res.getString(R.string.railway_hanzomon),
				res.getDrawable(R.drawable.ic_hanzomon), false);
		mList.add(item);

		// 日比谷線
		item = new IconTitleCheck(res.getString(R.string.railway_hibiya),
				res.getDrawable(R.drawable.ic_hibiya), false);
		mList.add(item);

		// 丸ノ内線
		item = new IconTitleCheck(res.getString(R.string.railway_marunouchi),
				res.getDrawable(R.drawable.ic_marunouchi), false);
		mList.add(item);

		// 南北線
		item = new IconTitleCheck(res.getString(R.string.railway_namboku),
				res.getDrawable(R.drawable.ic_namboku), false);
		mList.add(item);

		// 東西線
		item = new IconTitleCheck(res.getString(R.string.railway_tozai),
				res.getDrawable(R.drawable.ic_tozai), false);
		mList.add(item);

		// 有楽町線
		item = new IconTitleCheck(res.getString(R.string.railway_yurakucho),
				res.getDrawable(R.drawable.ic_yurakucho), false);
		mList.add(item);
	}

	private class IconTitleCheckAdapter extends ArrayAdapter<IconTitleCheck> {
		private ArrayList<IconTitleCheck> list = new ArrayList<IconTitleCheck>();
		private LayoutInflater inflater;
		private int layout;
		private AssetManager mAssetManager;

		public IconTitleCheckAdapter(Context context, int layoutAt) {
			super(context, layoutAt);
			this.inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			this.layout = layoutAt;
			mAssetManager = getActivity().getAssets();
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

			IconTitleCheck iconTitleCheck = list.get(position);

			holder.textView.setText(iconTitleCheck.getTitle());
			holder.textView.setTypeface(Typeface.createFromAsset(mAssetManager,
					getResources().getString(R.string.font_free_wing)));
			holder.imageView.setBackgroundDrawable(iconTitleCheck.getIcon());

			final int id = position;
			holder.checkbox
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							mList.get(id).isCheck = isChecked;
						}
					});
			holder.checkbox.setChecked(mList.get(id).getChecked());
			holder.layout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					holder.checkbox.setChecked(!holder.checkbox.isChecked());
				}
			});

			return view;
		}

		@Override
		public void add(IconTitleCheck object) {
			super.add(object);
			list.add(object);
		}
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

	/**
	 * App Name & App Icon & CheckBox
	 */
	private class IconTitleCheck {
		private String title;
		private Drawable icon;
		private boolean isCheck;

		public IconTitleCheck(String title, Drawable icon, boolean ischeck) {
			this.title = title;
			this.icon = icon;
			this.isCheck = ischeck;
		}

		public String getTitle() {
			return this.title;
		}

		public Drawable getIcon() {
			return this.icon;
		}

		public boolean getChecked() {
			return this.isCheck;
		}
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.tutorial_second_next_btn == viewId) {
			TutorialActivity.setNextPage();
			return;
		}
	}
}
