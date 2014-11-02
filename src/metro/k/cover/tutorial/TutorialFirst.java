package metro.k.cover.tutorial;

import metro.k.cover.R;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorialFirst extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_first, null);
		final AssetManager am = getActivity().getAssets();
		final Resources res = getResources();

		TextView textView = (TextView) view
				.findViewById(R.id.tutorial_first_title);
		textView.setTypeface(Typeface.createFromAsset(am,
				res.getString(R.string.font_free_wing)));
		textView = (TextView) view.findViewById(R.id.tutorial_first_next_btn);
		textView.setTypeface(Typeface.createFromAsset(am,
				res.getString(R.string.font_free_wing)));
		textView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (viewId == R.id.tutorial_first_next_btn) {
			TutorialActivity.setNextPage();
			return;
		}
	}
}
