package metro.k.cover.lock;

import java.util.ArrayList;

import metro.k.cover.R;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LockPagerAdapter extends PagerAdapter {
	public static final String PAGE_LOCK = "page_lock";
	public static final String PAGE_PAGE_TRAIN_INFO_1 = "page_train_info_1";
	public static final String PAGE_PAGE_TRAIN_INFO_2 = "page_train_info_2";
	private Context mContext;
	private ArrayList<String> mList;
	private Object mPrimaryItem;
	private LayoutInflater mLayoutInflater;
	private int mTestNum1 = 0;
	private int mTestNum2 = 0;
	private int mLastPosition = 0;

	public LockPagerAdapter(Context context) {
		mContext = context;
		mList = new ArrayList<String>();
		mLayoutInflater = LayoutInflater.from(mContext);
	}

	public void add(String pageName) {
		mList.add(pageName);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View pageView = null;
		String pageName = mList.get(position);
		if (pageName.equals(PAGE_LOCK)) {
			RelativeLayout lockLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_lock, null);
			pageView = lockLayout;
		} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_train_info, null);
			TextView tv = (TextView) trainInfoLayout
					.findViewById(R.id.page_train_info_test);
			tv.setText(String.valueOf(mTestNum1));
			pageView = trainInfoLayout;
		} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
			RelativeLayout trainInfoLayout = (RelativeLayout) mLayoutInflater
					.inflate(R.layout.page_train_info, null);
			TextView tv = (TextView) trainInfoLayout
					.findViewById(R.id.page_train_info_test);
			tv.setText(String.valueOf(mTestNum2));
			pageView = trainInfoLayout;
		}
		container.addView(pageView);
		return pageView;
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		if (mLastPosition == position) {
			return;
		}
		mLastPosition = position;
		mPrimaryItem = object;
		countup(position);
	}

	public Object getPrimaryItem() {
		return mPrimaryItem;
	}

	private void countup(int position) {
		String pageName = mList.get(position);
		int count = 0;
		if (pageName.equals(PAGE_PAGE_TRAIN_INFO_1)) {
			count = mTestNum1++;
		} else if (pageName.equals(PAGE_PAGE_TRAIN_INFO_2)) {
			count = mTestNum2++;
		} else {
			return;
		}
		RelativeLayout TrainInfoLayout = (RelativeLayout) getPrimaryItem();
		TextView textView = (TextView) TrainInfoLayout
				.findViewById(R.id.page_train_info_test);
		textView.setText(String.valueOf(count));
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object keyObject) {
		return view == keyObject;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

}