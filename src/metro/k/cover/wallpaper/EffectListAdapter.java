package metro.k.cover.wallpaper;

import java.util.ArrayList;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.view.JazzyViewPager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * エフェクトリストのアダプター
 * 
 * @author kohirose
 * 
 */
public class EffectListAdapter extends ArrayAdapter<String> {

	private ArrayList<String> list = new ArrayList<String>();
	private int mCurrentEffectId = JazzyViewPager.EFFECT_ROTATEUP;

	private LayoutInflater inflater;

	// コンストラクタ
	public EffectListAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		this.inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCurrentEffectId = PreferenceCommon.getViewPagerEffectID(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final EffectLayout view;
		if (convertView == null) {
			view = (EffectLayout) inflater.inflate(R.layout.title_at, null);
		} else {
			view = (EffectLayout) convertView;
		}

		if (position == mCurrentEffectId) {
			view.bindView(getItem(position), View.VISIBLE);
		} else {
			view.bindView(getItem(position), View.INVISIBLE);
		}

		return view;
	}

	@Override
	public void add(String object) {
		super.add(object);
		list.add(object);
	}
}
