package metro.k.cover.tutorial;

import java.util.ArrayList;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.SettingActivity;
import metro.k.cover.Utilities;
import metro.k.cover.railways.Railways;
import metro.k.cover.railways.RailwaysAdapter;
import metro.k.cover.railways.RailwaysUtilities;
import metro.k.cover.railways.Station;
import metro.k.cover.view.ButtonWithFont;
import metro.k.cover.view.JazzyViewPager;
import metro.k.cover.view.TextViewWithFont;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class TutorialActivity extends FragmentActivity implements
		OnPageChangeListener {

	private JazzyViewPager mViewPager;
	private TutorialPagerAdapter mAdapter;

	private boolean isTutorialOpend = false;

	// 方角
	private TextViewWithFont mSelectedStation;
	private String[] mDirections;
	private ButtonWithFont mDirectionButton_1;
	private ButtonWithFont mDirectionButton_2;
	private static String mSelectedStationTitle;
	private static String mSelectedStationRailway;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isTutorialOpend = PreferenceCommon
				.getTutorialOpend(getApplicationContext());
		 if (isTutorialOpend) {
		 startSettingActivity();
		 } else {
		setupViews();
			PreferenceCommon.setTutorialOpend(getApplicationContext(), true);
		}
	}

	/**
	 * 設定画面に遷移
	 */
	private void startSettingActivity() {
		Intent intent = new Intent(this, SettingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		try {
			startActivity(intent);
			finish();
		} catch (Exception e) {
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!isTutorialOpend && mAdapter != null) {
			PreferenceCommon.setRailwaysInfomation(getApplicationContext(),
					mAdapter.getCheckedStr());
			PreferenceCommon.setRailwaysNumber(getApplicationContext(),
					mAdapter.getCheckedNumber());
			PreferenceCommon.setRailwaysNameForAPI(getApplicationContext(),
					mAdapter.getCheckedResponseNmae());
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mViewPager == null) {
			return true;
		}

		// 2ページ目以降でのバックキーは前ページに戻す
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& mViewPager.getCurrentItem() != 0) {
			setPrevPage();
			return true;
		}

		// 1ページ目のバックキーを無効にする
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& mViewPager.getCurrentItem() == 0) {
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void setupViews() {
		setContentView(R.layout.activity_tutorial);
		mViewPager = (JazzyViewPager) findViewById(R.id.tutorial_viewpager);
		mViewPager.setTransitionEffect(PreferenceCommon
				.getViewPagerEffect(this));
		mViewPager.setPageMargin(30);
		mAdapter = new TutorialPagerAdapter(this);
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
	}

	/**
	 * 次のページへ
	 */
	private void setNextPage() {
		if (mViewPager == null) {
			return;
		}
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
	}

	/**
	 * 前のページへ
	 */
	private void setPrevPage() {
		if (mViewPager == null) {
			return;
		}
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
	}

	/**
	 * ViewPagerのアダプター
	 * 
	 * @author kohirose
	 * 
	 */
	private class TutorialPagerAdapter extends PagerAdapter implements
			OnClickListener {

		// 枚数
		private final int MAX_PAGE = 5;

		// Context
		private Context mContext;

		// View
		private LayoutInflater mLayoutInflater;

		// チェックした路線
		private ArrayList<Railways> railwaysList = new ArrayList<Railways>();

		public TutorialPagerAdapter(Context context) {
			super();
			mContext = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return MAX_PAGE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == (RelativeLayout) object;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			View view = null;
			switch (position) {
			case 0:
				view = getRootLayout(R.layout.tutorial_first,
						R.id.tutorial_first_root);
				addFirstLayoutFunction(view);
				break;
			case 1:
				view = getRootLayout(R.layout.tutorial_second,
						R.id.tutorial_second_root);
				addSecondLayoutFunction(view);
				break;
			case 2:
				view = getRootLayout(R.layout.tutorial_third,
						R.id.tutorial_third_root);
				addThirdLayoutFunction(view);
				break;
			case 3:
				view = getRootLayout(R.layout.tutorial_fourth,
						R.id.tutorial_fourth_root);
				addFourthLayoutFunction(view);
				break;
			case 4:
				view = getRootLayout(R.layout.tutorial_last,
						R.id.tutorial_last_root);
				addTutorialLastFunction(view);
			default:
				view = getRootLayout(R.layout.tutorial_last,
						R.id.tutorial_last_root);
				addTutorialLastFunction(view);
				break;
			}

			container.addView(view);
			mViewPager.setObjectForPosition(view, position);
			return view;
		}

		/**
		 * 各ページのルートを取得
		 * 
		 * @param layoutId
		 * @param rootId
		 * @return
		 */
		private View getRootLayout(final int layoutId, final int rootId) {
			if (mLayoutInflater == null) {
				return null;
			}

			return mLayoutInflater.inflate(layoutId,
					(RelativeLayout) findViewById(rootId));
		}

		/**
		 * 1ページ目の設定（ウェルカムページ）
		 * 
		 * @param rootView
		 */
		private void addFirstLayoutFunction(final View rootView) {
			if (rootView == null) {
				return;
			}
			TextViewWithFont next = (TextViewWithFont) rootView
					.findViewById(R.id.tutorial_first_next_btn);
			next.setOnClickListener(this);
		}

		/**
		 * 2ページ目の設定（路線選択）
		 * 
		 * @param rootView
		 */
		private void addSecondLayoutFunction(final View rootView) {
			if (rootView == null) {
				return;
			}

			railwaysList = RailwaysUtilities.getAllRailways(mContext);
			if (railwaysList != null) {
				final int size = railwaysList.size();
				if (size > 0) {
					ListView listView = (ListView) rootView
							.findViewById(R.id.tutorial_second_listview);
					ArrayAdapter<Railways> adapter = new RailwaysAdapter(
							mContext, R.layout.list_icon_title_check_at);
					for (int i = 0; i < size; i++) {
						adapter.add(railwaysList.get(i));
					}
					listView.setAdapter(adapter);
				}
			}
			TextViewWithFont next = (TextViewWithFont) rootView
					.findViewById(R.id.tutorial_second_next_btn);
			next.setOnClickListener(this);
		}

		/**
		 * 3ページ目の設定（駅選択）
		 * 
		 * @param rootView
		 */
		private void addThirdLayoutFunction(final View rootView) {
			if (rootView == null) {
				return;
			}

			final ListView listView = (ListView) rootView
					.findViewById(R.id.tutorial_third_listview);
			if (MetroCoverApplication.sStationAllListAdapter == null) {
				return;
			}

			listView.setAdapter(MetroCoverApplication.sStationAllListAdapter);
			listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					final Station station = MetroCoverApplication.sStationAllListAdapter
							.getItem(position);
					mSelectedStationTitle = station.getTitle();
					mSelectedStationRailway = station.getRailway();
					PreferenceCommon.setStationName(mContext,
							mSelectedStationTitle);
					PreferenceCommon.setStationsRailwayName(mContext,
							mSelectedStationRailway);
					PreferenceCommon.setStationNameForAPI(mContext,
							station.getNameForAPI());
					setNextPage();
				}
			});
		}

		/**
		 * 4ページ目の設定（方角設定）
		 * 
		 * @param rootView
		 */
		private void addFourthLayoutFunction(final View rootView) {
			if (rootView == null) {
				return;
			}

			mSelectedStation = (TextViewWithFont) rootView
					.findViewById(R.id.tutorial_fourth_selected_staticon);
			mDirectionButton_1 = (ButtonWithFont) rootView
					.findViewById(R.id.tutorial_fourth_direction_1);
			mDirectionButton_2 = (ButtonWithFont) rootView
					.findViewById(R.id.tutorial_fourth_direction_2);
			mDirectionButton_1.setOnClickListener(this);
			mDirectionButton_2.setOnClickListener(this);
		}

		/**
		 * 最終ページ設定
		 * 
		 * @param rootView
		 */
		private void addTutorialLastFunction(final View rootView) {
			if (rootView == null) {
				return;
			}

			TextViewWithFont finish = (TextViewWithFont) rootView
					.findViewById(R.id.tutorial_last_finish_btn);
			finish.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			final int viewId = v.getId();

			// 次へ
			if (R.id.tutorial_first_next_btn == viewId
					|| R.id.tutorial_second_next_btn == viewId) {
				setNextPage();
				return;
			}

			// 終わり
			if (R.id.tutorial_last_finish_btn == viewId) {
				startSettingActivity();
				return;
			}

			// 方角
			if (R.id.tutorial_fourth_direction_1 == viewId) {
				if (mDirections == null) {
					return;
				}
				if (mDirections.length != RailwaysUtilities.SIZE_DIRECTION_DATA_ARRAY) {
					return;
				}
				PreferenceCommon.setTrainDirection(mContext, mDirections[0]);
				setNextPage();
			}

			if (R.id.tutorial_fourth_direction_2 == viewId) {
				if (mDirections == null) {
					return;
				}
				if (mDirections.length != RailwaysUtilities.SIZE_DIRECTION_DATA_ARRAY) {
					return;
				}
				PreferenceCommon.setTrainDirection(mContext, mDirections[1]);
				setNextPage();
			}
		}

		/**
		 * チェックボックスにチェックされている路線のIDを取得する
		 * 
		 * @return
		 */
		public String getCheckedStr() {
			if (railwaysList == null) {
				return "";
			}

			final int size = railwaysList.size();
			if (size == 0) {
				return "";
			}

			String str = "";
			for (int i = 0; i < size; i++) {
				if (railwaysList.get(i).getChecked()) {
					str += railwaysList.get(i).getCode() + ",";
				}
			}

			if (!Utilities.isInvalidStr(str)) {
				final int len = str.length();
				str = str.substring(0, len - 1);
			}
			return str;
		}

		/**
		 * チェックボックスにチェックされている路線番号を取得する
		 * 
		 * @return
		 */
		public String getCheckedNumber() {
			if (railwaysList == null) {
				return "";
			}

			final int size = railwaysList.size();
			if (size == 0) {
				return "";
			}

			String str = "";
			for (int i = 0; i < size; i++) {
				if (railwaysList.get(i).getChecked()) {
					str += railwaysList.get(i).getRailwayNumber() + ",";
				}
			}

			if (!Utilities.isInvalidStr(str)) {
				final int len = str.length();
				str = str.substring(0, len - 1);
			}
			return str;
		}

		/**
		 * チェックボックスにチェックされている路線のレスポンスネームを取得する
		 * 
		 * @return
		 */
		public String getCheckedResponseNmae() {
			if (railwaysList == null) {
				return "";
			}

			final int size = railwaysList.size();
			if (size == 0) {
				return "";
			}

			String str = "";
			for (int i = 0; i < size; i++) {
				if (railwaysList.get(i).getChecked()) {
					str += railwaysList.get(i).getResponseName() + ",";
				}
			}

			if (!Utilities.isInvalidStr(str)) {
				final int len = str.length();
				str = str.substring(0, len - 1);
			}
			return str;
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == JazzyViewPager.SCROLL_STATE_SETTLING) {
			final int page = mViewPager.getCurrentItem();
			if (page == 3) {
				// ３ページ目で選択された駅から方角をViewにセットする
				if (!Utilities.isInvalidStr(mSelectedStationTitle)
						&& mSelectedStation != null) {
					mSelectedStation.setText(mSelectedStationTitle);
				}

				if (Utilities.isInvalidStr(mSelectedStationRailway)) {
					return;
				}
				mDirections = RailwaysUtilities.getDirection(this,
						mSelectedStationRailway);
				if (mDirections == null) {
					return;
				}
				if (mDirections.length != RailwaysUtilities.SIZE_DIRECTION_DATA_ARRAY) {
					return;
				}
				if (mDirectionButton_1 == null || mDirectionButton_2 == null) {
					return;
				}
				mDirectionButton_1.setText(mDirections[2]);
				mDirectionButton_2.setText(mDirections[3]);
			}
		}

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub

	}

}
