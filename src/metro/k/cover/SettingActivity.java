package metro.k.cover;

import metro.k.cover.lock.LockSettingActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class SettingActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		ImageView iv = (ImageView)findViewById(R.id.logo);
		iv.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		final int viewId = v.getId();
		if(R.id.logo == viewId) {
			Intent intent = new Intent(this, LockSettingActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return;
		}
	}

}
