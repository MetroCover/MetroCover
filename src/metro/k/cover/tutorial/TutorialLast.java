package metro.k.cover.tutorial;

import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.view.TextViewWithFont;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class TutorialLast extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View view = inflater.inflate(R.layout.tutorial_last, null);

		TextViewWithFont textView = (TextViewWithFont) view
				.findViewById(R.id.tutorial_last_finish_btn);
		textView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (viewId == R.id.tutorial_last_finish_btn) {
			getActivity().finish();
			Intent intent = new Intent(getActivity(), SettingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			Utilities.startActivitySafely(intent, getActivity());
			return;
		}
	}
}
