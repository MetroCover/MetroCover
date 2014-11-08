package metro.k.cover.railways;

import metro.k.cover.R;
import metro.k.cover.view.TextViewWithFont;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

public class StationLayout extends RelativeLayout {

	private TextViewWithFont mTitleView;
	private ImageView mIconView;
	private RadioButton mRadioButton;

	public StationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mTitleView = (TextViewWithFont) findViewById(R.id.list_icon_radio_title);
		mRadioButton = (RadioButton) findViewById(R.id.list_icon_radio_radio);
		mIconView = (ImageView) findViewById(R.id.list_icon_radio_icon);
	}

	public void bindView(Station station) {
		mTitleView.setText(station.getTitle());
		mIconView.setImageDrawable(station.getIcon());
	}

}