package metro.k.cover.lock;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * ロック画面からカメラアイコンが押された際に起動するカメラの踏み台
 * 
 * @author kohirose
 * 
 */
public class LockCameraActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		launchCameraApp();
		finish();
	}

	private void launchCameraApp() {
		Intent intent;
		final PackageManager pm = getApplicationContext().getPackageManager();

		// 0
		intent = pm.getLaunchIntentForPackage("com.sec.android.app.camera");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 1:
		intent = pm.getLaunchIntentForPackage("jp.kyocera.camera");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 2:
		intent = pm.getLaunchIntentForPackage("com.android.camera");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 3: SHARP
		intent = new Intent();
		intent.setClassName("jp.co.sharp.android.camera",
				"jp.co.sharp.android.camera.stillimagecamera.Camera");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 4:
		intent = new Intent();
		intent.setClassName("com.google.android.gallery3d",
				"com.android.camera.CameraActivity");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 5: SONY
		intent = new Intent();
		intent.setClassName("com.sonyericsson.android.camera",
				"com.sonyericsson.android.camera.CameraActivity");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 6: Nexus5
		intent = new Intent();
		intent.setClassName("com.google.android.GoogleCamera",
				"com.android.camera.CameraLauncher");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 7_1: Panasonic
		intent = pm
				.getLaunchIntentForPackage("com.panasonic.mobile.camera.sagittarius");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 7_2: Panasonic
		intent = pm
				.getLaunchIntentForPackage("com.panasonic.mobile.camera.virgo");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 8: LG
		intent = new Intent();
		intent.setClassName("com.lge.camera", "com.lge.camera.CameraApp");
		if (startActivityForSafely(intent)) {
			return;
		}

		// 9:
		intent = new Intent();
		intent.setClassName("com.android.gallery3d",
				"com.android.camera.CameraLauncher");
		if (startActivityForSafely(intent)) {
			return;
		}

		// other cameraApp
		intent = new Intent(Intent.ACTION_MAIN);
		intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		if (startActivityForSafely(intent)) {
			return;
		}

		// No Camera App
		Toast.makeText(getApplicationContext(), "No Such Apps",
				Toast.LENGTH_SHORT).show();
	}

	private boolean startActivityForSafely(Intent intent) {
		try {
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// error free
			startActivity(intent);
		} catch (Throwable e) {
			return false;
		}
		return true;
	}
}
