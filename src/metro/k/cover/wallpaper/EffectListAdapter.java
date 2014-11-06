package metro.k.cover.wallpaper;

import java.util.ArrayList;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.JazzyViewPager;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EffectListAdapter extends ArrayAdapter<String> {

	static class ViewHolder {
		TextView labelText;
		TextView selectedView;
	}

	private Resources mResources;
	private AssetManager mAssetManager;
	private int mCurrentEffectId = JazzyViewPager.EFFECT_ROTATEUP;

	private LayoutInflater inflater;

	// コンストラクタ
	public EffectListAdapter(Context context, int textViewResourceId,
			ArrayList<String> labelList) {
		super(context, textViewResourceId, labelList);
		mResources = context.getResources();
		mAssetManager = context.getAssets();
		mCurrentEffectId = PreferenceCommon.getViewPagerEffectID(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		View view = convertView;

		if (view == null) {
			inflater = (LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.title_at, null);
			TextView label = (TextView) view.findViewById(R.id.list_title);
			TextView selected = (TextView) view
					.findViewById(R.id.list_selected);
			holder = new ViewHolder();
			holder.labelText = label;
			holder.selectedView = selected;
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		String str = getItem(position);

		if (!TextUtils.isEmpty(str)) {
			holder.labelText.setText(str);
			Utilities.setFontTextView(holder.labelText, mAssetManager,
					mResources);
		}

		if (position == mCurrentEffectId) {
			holder.selectedView.setVisibility(View.VISIBLE);
			Utilities.setFontTextView(holder.selectedView, mAssetManager,
					mResources);
		} else {
			holder.selectedView.setVisibility(View.INVISIBLE);
		}

		return view;
	}
}
