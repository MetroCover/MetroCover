package metro.k.cover.lock;

import metro.k.cover.PreferenceCommon;
import metro.k.cover.R;
import metro.k.cover.view.TextViewWithFont;
import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

/**
 * ロック画面のテキストカラー設定画面
 * 
 * @author kohirose
 * 
 */
public class LockClockTextColorSelectActivity extends Activity implements
		OnClickListener {

	public static final String KEY_COLOR_SELECT = "key_color_select";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		setContentView(R.layout.activity_clock_color_select);

		TextViewWithFont textView = (TextViewWithFont) findViewById(PreferenceCommon
				.getClockColorForSelected(this));
		textView.setVisibility(View.VISIBLE);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.clock_text_color_white_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_black_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_metro_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_chiyoda_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_fukutoshin_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_ginza_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_hanzomon_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_hibiya_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_marunouchi_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_namboku_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_tozai_layout);
		layout.setOnClickListener(this);
		layout = (RelativeLayout) findViewById(R.id.clock_text_color_yurakucho_layout);
		layout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();

		final Resources res = getResources();

		String colorStr = res.getString(R.string.color_white);
		int colorId = R.color.color_white;
		int selectId = R.id.clock_text_color_white_selected;

		switch (viewId) {
		case R.id.clock_text_color_black_layout:
			colorStr = res.getString(R.string.color_black);
			colorId = R.color.color_black;
			selectId = R.id.clock_text_color_black_selected;
			break;
		case R.id.clock_text_color_metro_layout:
			colorStr = res.getString(R.string.color_metro);
			colorId = R.color.metro_main_color;
			selectId = R.id.clock_text_color_metro_selected;
			break;
		case R.id.clock_text_color_chiyoda_layout:
			colorStr = res.getString(R.string.railway_chiyoda_color);
			colorId = R.color.chiyoda_green;
			selectId = R.id.clock_text_color_chiyoda_selected;
			break;
		case R.id.clock_text_color_fukutoshin_layout:
			colorStr = res.getString(R.string.railway_fukutoshin_color);
			colorId = R.color.fukutoshin_brown;
			selectId = R.id.clock_text_color_fukutoshin_selected;
			break;
		case R.id.clock_text_color_ginza_layout:
			colorStr = res.getString(R.string.railway_ginza_color);
			colorId = R.color.ginza_orange;
			selectId = R.id.clock_text_color_ginza_selected;
			break;
		case R.id.clock_text_color_hanzomon_layout:
			colorStr = res.getString(R.string.railway_hanzomon_color);
			colorId = R.color.hanzomon_purple;
			selectId = R.id.clock_text_color_hanzomon_selected;
			break;
		case R.id.clock_text_color_hibiya_layout:
			colorStr = res.getString(R.string.railway_hibiya_color);
			colorId = R.color.hibiya_silber;
			selectId = R.id.clock_text_color_hibiya_selected;
			break;
		case R.id.clock_text_color_marunouchi_layout:
			colorStr = res.getString(R.string.railway_marunouchi_color);
			colorId = R.color.marunouchi_red;
			selectId = R.id.clock_text_color_marunouchi_selected;
			break;
		case R.id.clock_text_color_namboku_layout:
			colorStr = res.getString(R.string.railway_namboku_color);
			colorId = R.color.namboku_emerald;
			selectId = R.id.clock_text_color_namboku_selected;
			break;
		case R.id.clock_text_color_tozai_layout:
			colorStr = res.getString(R.string.railway_tozai_color);
			colorId = R.color.tozai_sky;
			selectId = R.id.clock_text_color_tozai_selected;
			break;
		case R.id.clock_text_color_yurakucho_layout:
			colorStr = res.getString(R.string.railway_yurakucho_color);
			colorId = R.color.yurakucho_gold;
			selectId = R.id.clock_text_color_yurakucho_selected;
			break;
		default:
			break;
		}

		PreferenceCommon.setClockColorForSelected(this, selectId);
		PreferenceCommon.setClockColor(this, colorId);
		PreferenceCommon.setClockColorStr(this, colorStr);
		finish();
	}
}
