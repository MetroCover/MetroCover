package metro.k.cover.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LockReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			final Context c = context.getApplicationContext();
			Intent service = new Intent(c, LockService.class);
			service.setAction(intent.getAction());
			c.startService(service);
		} catch (RuntimeException e) {
		}
	}
}
