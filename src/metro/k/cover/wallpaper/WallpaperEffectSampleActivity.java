package metro.k.cover.wallpaper;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.ButtonWithFont;
import metro.k.cover.view.JazzyOutlineContainer;
import metro.k.cover.view.JazzyViewPager;
import metro.k.cover.view.JazzyViewPager.TransitionEffect;
import metro.k.cover.view.TextViewWithFont;
import metro.k.cover.view.ViewUtilities;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

/**
 * エフェクト設定画面
 * 
 * @author kohirose
 * 
 */
public class WallpaperEffectSampleActivity extends Activity implements
		OnClickListener {

	private int mEffectId = JazzyViewPager.EFFECT_ROTATEDOWN;
	private JazzyViewPager mJazzyViewPager;
	private TransitionEffect mEffect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEffectId = getIntent().getIntExtra(
				WallpaperUtilities.KEY_WALLPAPER_EFFECT_SELECT_ID,
				JazzyViewPager.EFFECT_ROTATEDOWN);
		mEffect = ViewUtilities.getTransitionEffectFromNumber(mEffectId);
		setupViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void setupViews() {
		setContentView(R.layout.activity_wallpaper_effect);

		ButtonWithFont cancel = (ButtonWithFont) findViewById(R.id.wallpaper_effect_cancel_btn);
		ButtonWithFont complete = (ButtonWithFont) findViewById(R.id.wallpaper_effect_comp_btn);
		cancel.setOnClickListener(this);
		complete.setOnClickListener(this);

		mJazzyViewPager = (JazzyViewPager) findViewById(R.id.wallpaper_effect_viewpager);
		mJazzyViewPager.setTransitionEffect(mEffect);
		mJazzyViewPager.setAdapter(new MainAdapter());
		mJazzyViewPager.setPageMargin(30);

		RelativeLayout root = (RelativeLayout) findViewById(R.id.wallpaper_effect_root);
		Utilities.setSeasonsBackground(this, root);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if (R.id.wallpaper_effect_cancel_btn == viewId) {
			finish();
			return;
		}
		if (R.id.wallpaper_effect_comp_btn == viewId) {
			PreferenceCommon.setViewPagerEffect(this, mEffectId);
			PreferenceCommon.setViewPagerEffectID(this, mEffectId);
			finish();
			return;
		}
	}

	private class MainAdapter extends PagerAdapter {
		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final View view = LayoutInflater.from(getApplicationContext())
					.inflate(R.layout.wallpaper_sample, null);
			final TextViewWithFont effect_name = (TextViewWithFont) view
					.findViewById(R.id.wallpaper_sample_textview);
			effect_name.setText(String.valueOf(mEffect));
			container.addView(view);
			mJazzyViewPager.setObjectForPosition(view, position);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object obj) {
			container.removeView(mJazzyViewPager.findViewFromObject(position));
		}

		@Override
		public int getCount() {
			return WallpaperUtilities.MAX_PAGE;
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			if (view instanceof JazzyOutlineContainer) {
				return ((JazzyOutlineContainer) view).getChildAt(0) == obj;
			} else {
				return view == obj;
			}
		}
	}
}
