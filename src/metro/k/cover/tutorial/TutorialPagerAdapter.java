package metro.k.cover.tutorial;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TutorialPagerAdapter extends FragmentStatePagerAdapter {

	// チュートリアルの枚数
	private static final int MAX_PAGE = 3;

	public TutorialPagerAdapter(FragmentManager fm) {
		super(fm);
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
