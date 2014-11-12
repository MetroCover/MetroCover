package metro.k.cover.wallpaper;

import metro.k.cover.R;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class WallpaperDialogFragment extends DialogFragment {

	private String mTAG = "";
	private int mPosition;
	private boolean isHomeeWallpaper = false;
	private boolean isPlusHomeWallpaper = false;
	private boolean isBuzzHomeWallpaper = false;

	public static String KEY_SELECTED_PAGE = "key_selected_page";

	public WallpaperDialogFragment() {
		super();
	}

	public void showThemeDialogFragment(String tag, int position,
			boolean homee, boolean plushome, boolean buzzhome) {
		mTAG = tag;
		mPosition = position;
		isHomeeWallpaper = homee;
		isPlusHomeWallpaper = plushome;
		isBuzzHomeWallpaper = buzzhome;
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

			// Homeeの壁紙
			RelativeLayout homee_layout = (RelativeLayout) dialog
					.findViewById(R.id.wallpaper_dialog_homee_layout);
			ImageView homee_sep = (ImageView) dialog
					.findViewById(R.id.wallpaper_dialog_homee_sep);
			if (isHomeeWallpaper) {
				homee_layout.setVisibility(View.VISIBLE);
				homee_sep.setVisibility(View.VISIBLE);
				homee_layout.setOnClickListener(getOtherHomeWallpaperListener(
						mPosition, WallpaperUtilities.HOMEE_APP_ID));
			} else {
				homee_layout.setVisibility(View.GONE);
				homee_sep.setVisibility(View.GONE);
			}

			// [+]HOMEの壁紙
			RelativeLayout plushome_layout = (RelativeLayout) dialog
					.findViewById(R.id.wallpaper_dialog_plushome_layout);
			ImageView plushome_sep = (ImageView) dialog
					.findViewById(R.id.wallpaper_dialog_plushome_sep);
			if (isPlusHomeWallpaper) {
				plushome_layout.setVisibility(View.VISIBLE);
				plushome_sep.setVisibility(View.VISIBLE);
				plushome_layout
						.setOnClickListener(getOtherHomeWallpaperListener(
								mPosition, WallpaperUtilities.PLUSHOME_APP_ID));
			} else {
				plushome_layout.setVisibility(View.GONE);
				plushome_sep.setVisibility(View.GONE);
			}

			// buzzHOMEの壁紙
			RelativeLayout buzzhome_layout = (RelativeLayout) dialog
					.findViewById(R.id.wallpaper_dialog_buzzhome_layout);
			ImageView buzzhome_sep = (ImageView) dialog
					.findViewById(R.id.wallpaper_dialog_buzzhome_sep);
			if (isBuzzHomeWallpaper) {
				buzzhome_layout.setVisibility(View.VISIBLE);
				buzzhome_sep.setVisibility(View.VISIBLE);
				buzzhome_layout
						.setOnClickListener(getOtherHomeWallpaperListener(
								mPosition, WallpaperUtilities.BUZZHOME_APP_ID));
			} else {
				buzzhome_layout.setVisibility(View.GONE);
				buzzhome_sep.setVisibility(View.GONE);
			}

			// 暗黙投げる
			RelativeLayout gallery_layout = (RelativeLayout) dialog
					.findViewById(R.id.wallpaper_dialog_gallery_layout);
			gallery_layout.setOnClickListener(getGalleryListener(mPosition));

			dialog.setCanceledOnTouchOutside(true);
			return dialog;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 他のホームアプリの壁紙一覧へ
	 * 
	 * @return
	 */
	private View.OnClickListener getOtherHomeWallpaperListener(final int page,
			final int HomeID) {
		View.OnClickListener li = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				Intent intent = new Intent(getActivity(),
						WallpaperOtherHomeActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra(WallpaperUtilities.KEY_OTHER_HOMEAPP_WALLPAPER,
						HomeID);
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