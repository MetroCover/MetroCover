package metro.k.cover.tutorial;

import metro.k.cover.R;
import metro.k.cover.view.TextViewWithFont;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TutorialFirst extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_first, null);

		TextViewWithFont textView = (TextViewWithFont) view
				.findViewById(R.id.tutorial_first_next_btn);
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
