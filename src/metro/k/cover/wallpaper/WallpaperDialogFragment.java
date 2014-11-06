package metro.k.cover.wallpaper;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WallpaperDialogFragment extends DialogFragment {

	private String mTAG = "";
	private int mPosition;

	public static String KEY_SELECTED_PAGE = "key_selected_page";

	public WallpaperDialogFragment() {
		super();
	}

	public void showThemeDialogFragment(String tag, int position) {
		mTAG = tag;
		mPosition = position;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		try {
			Dialog dialog = new Dialog(getFragmentManager().findFragmentByTag(
					mTAG).getActivity());
			dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			dialog.getWindow().setFlags(
					WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
			dialog.setContentView(R.layout.wallpaper_dialog);
			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(Color.TRANSPARENT));

			final Resources res = getResources();
			final AssetManager am = getActivity().getAssets();

			// タイトル
			TextView titleView = (TextView) dialog
					.findViewById(R.id.wallpaper_dialog_titleview);
			Utilities.setFontTextView(titleView, am, res);

			// Homeeの壁紙
			RelativeLayout homee_layout = (RelativeLayout) dialog
					.findViewById(R.id.wallpaper_dialog_homee_layout);
			homee_layout.setOnClickListener(getHomeeListener(mPosition));
			TextView homee_titleView = (TextView) dialog
					.findViewById(R.id.wallpaper_dialog_homee_titleview);
			Utilities.setFontTextView(homee_titleView, am, res);

			// 暗黙投げる
			RelativeLayout gallery_layout = (RelativeLayout) dialog
					.findViewById(R.id.wallpaper_dialog_gallery_layout);
			gallery_layout.setOnClickListener(getGalleryListener(mPosition));
			TextView gallery_titleView = (TextView) dialog
					.findViewById(R.id.wallpaper_dialog_gallery_titleview);
			Utilities.setFontTextView(gallery_titleView, am, res);

			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Homeeの壁紙一覧へ
	 * 
	 * @return
	 */
	private View.OnClickListener getHomeeListener(final int page) {
		View.OnClickListener li = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				Intent intent = new Intent(getActivity(),
						WallpaperHomeeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(KEY_SELECTED_PAGE, page);
				startActivityForResult(intent,
						WallpaperUtilities.REQUEST_CODE_HOMEE_WALLPAPER);
			}
		};
		return li;
	}

	/**
	 * 暗黙的インテント
	 * 
	 * @return
	 */
	private View.OnClickListener getGalleryListener(final int page) {
		View.OnClickListener li = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				int requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_LEFT;
				if (page == WallpaperUtilities.PAGE_LEFT) {
					requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_LEFT;
				} else if (page == WallpaperUtilities.PAGE_CENTER) {
					requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_CENTER;
				} else {
					requestId = WallpaperUtilities.REQUEST_PICK_PICTURE_RIGHT;
				}

				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setType("image/*");
				try {
					getActivity().startActivityForResult(intent, requestId);
				} catch (Exception e) {
				}
			}
		};
		return li;
	}
}