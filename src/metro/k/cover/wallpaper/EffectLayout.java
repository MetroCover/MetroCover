package metro.k.cover.wallpaper;

import metro.k.cover.R;
import metro.k.cover.view.TextViewWithFont;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * エフェクト選択リストのItemレイアウト
 * 
 * @author kohirose
 * 
 */
public class EffectLayout extends RelativeLayout {

	private TextViewWithFont mTitleView;
	private TextViewWithFont mSelectedView;

	public EffectLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mTitleView = (TextViewWithFont) findViewById(R.id.list_title);
		mSelectedView = (TextViewWithFont) findViewById(R.id.list_selected);
	}

	public void bindView(String effect, int visibility) {
		mTitleView.setText(effect);
		mSelectedView.setVisibility(visibility);
	}

}