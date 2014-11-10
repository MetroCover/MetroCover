package metro.k.cover.railways;

import metro.k.cover.MetroCoverApplication;
import android.app.IntentService;
import android.content.Intent;

/**
 * ロック画面に表示する情報を１時間おきに更新をするService
 * 
 * @author kohirose
 * 
 */
public class RailwaysInfoUpdateService extends IntentService {

	final static String TAG = "RailwaysInfoUpdateService";

	public RailwaysInfoUpdateService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		if (intent == null) {
			return;
		}

		MetroCoverApplication app = (MetroCoverApplication) getApplication();
		if (app != null) {
			app.createRailwaysInfoList();
		}
	}
}