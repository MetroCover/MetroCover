package metro.k.cover;

import android.app.Application;
import android.content.Intent;

public class MetroCoverApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Intent intent = new Intent(this, LockService.class);
		startService(intent);
	}
}
