package metro.k.cover.wallpaper;

import java.util.ArrayList;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import metro.k.cover.view.JazzyViewPager;
import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * エフェクト設定画面
 * 
 * @author kohirose
 * 
 */
public class WallpaperEffectSelectActivity extends Activity implements
		OnItemClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupViews();
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupListView();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void setupViews() {
		setContentView(R.layout.activity_wallpaper_select_effect);

		final Resources res = getResources();
		final AssetManager am = getAssets();
		TextView titleView = (TextView) findViewById(R.id.wallpaper_effect_select_titleview);
		Utilities.setFontTextView(titleView, am, res);
	}

	private void setupListView() {
		final ListView listView = (ListView) findViewById(R.id.wallpaper_effect_select_listview);
		ArrayList<String> effects = new ArrayList<String>();
		for (int i = 0; i < JazzyViewPager.Effects.length; i++) {
			effects.add(JazzyViewPager.Effects[i]);
		}

		EffectListAdapter adapter = new EffectListAdapter(this, 0, effects);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent(this, WallpaperEffectSampleActivity.class);
		intent.putExtra(WallpaperUtilities.KEY_WALLPAPER_EFFECT_SELECT_ID,
				(int) id);
		Utilities.startActivitySafely(intent, this);
	}

}
