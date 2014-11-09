package metro.k.cover.lock;

import metro.k.cover.R;
import metro.k.cover.railways.RailwaysInfo;
import metro.k.cover.view.TextViewWithFont;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LockRailwaysLayout extends LinearLayout {

	private TextViewWithFont mNameView;
	private TextViewWithFont mInfoView;
	private ImageView mIconView;

	public LockRailwaysLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mNameView = (TextViewWithFont) findViewById(R.id.lock_railways_info_name);
		mInfoView = (TextViewWithFont) findViewById(R.id.lock_railways_info_msg);
		mIconView = (ImageView) findViewById(R.id.lock_railways_info_icon);
	}

	public void bindView(RailwaysInfo info) {
		mNameView.setText(info.getRailway());
		mInfoView.setText(info.getMessage());
		mIconView.setImageDrawable(info.getIcon());
	}

}