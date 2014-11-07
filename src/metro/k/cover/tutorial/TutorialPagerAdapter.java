package metro.k.cover.tutorial;

import metro.k.cover.view.JazzyViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

	// チュートリアルの枚数
	private static final int MAX_PAGE = 3;

	public TutorialPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		((JazzyViewPager) TutorialActivity.getViewPager())
				.setObjectForPosition(obj, position);
		return obj;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		if (object != null) {
			return ((Fragment) object).getView() == view;
		} else {
			return false;
		}
	}

	@Override
	public Fragment getItem(int page) {
		switch (page) {
		case 0:
			return new TutorialFirst();
		case 1:
			return new TutorialSecond();
		case 2:
			return new TutorialThird();
		default:
			return new TutorialFirst();
		}
	}

	@Override
	public int getCount() {
		return MAX_PAGE;
	}
}
