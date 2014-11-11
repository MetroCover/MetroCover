package metro.k.cover.railways;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.TextViewWithFont;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 駅リストのListViewのItemのレイアウト
 * 
 * @author kohirose
 * 
 */
public class StationLayout extends RelativeLayout {

	private TextViewWithFont mTitleView;
	private ImageView mIconView;

	public StationLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		mTitleView = (TextViewWithFont) findViewById(R.id.list_icon_radio_title);
		mIconView = (ImageView) findViewById(R.id.list_icon_radio_icon);
	}

	int selectedPosition = 0;

	public void bindView(final int position, Station station) {
		final String title = station.getTitle();
		final Drawable icon = station.getIcon();
		mTitleView.setText(title);
		cooseTextColor(title);
		mIconView.setImageDrawable(icon);
	}

	private void cooseTextColor(final String title) {
		final Resources res = getResources();
		if (Utilities.isInvalidStr(title)) {
			mTitleView.setTextColor(res.getColor(R.color.color_black));
			return;
		}

		if (title.equals(res.getString(R.string.railway_chiyoda))) {
			mTitleView.setTextColor(res.getColor(R.color.chiyoda_green));
			return;
		}

		if (title.equals(res.getString(R.string.railway_fukutoshin))) {
			mTitleView.setTextColor(res.getColor(R.color.fukutoshin_brown));
			return;
		}

		if (title.equals(res.getString(R.string.railway_hibiya))) {
			mTitleView.setTextColor(res.getColor(R.color.hibiya_silber));
			return;
		}

		if (title.equals(res.getString(R.string.railway_ginza))) {
			mTitleView.setTextColor(res.getColor(R.color.ginza_orange));
			return;
		}

		if (title.equals(res.getString(R.string.railway_marunouchi))) {
			mTitleView.setTextColor(res.getColor(R.color.marunouchi_red));
			return;
		}

		if (title.equals(res.getString(R.string.railway_tozai))) {
			mTitleView.setTextColor(res.getColor(R.color.tozai_sky));
			return;
		}

		if (title.equals(res.getString(R.string.railway_hanzomon))) {
			mTitleView.setTextColor(res.getColor(R.color.hanzomon_purple));
			return;
		}
		if (title.equals(res.getString(R.string.railway_namboku))) {
			mTitleView.setTextColor(res.getColor(R.color.namboku_emerald));
			return;
		}
		if (title.equals(res.getString(R.string.railway_yurakucho))) {
			mTitleView.setTextColor(res.getColor(R.color.yurakucho_gold));
			return;
		}

		mTitleView.setTextColor(res.getColor(R.color.color_black));
	}

}