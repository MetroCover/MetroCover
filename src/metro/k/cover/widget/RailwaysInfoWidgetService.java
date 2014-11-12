package metro.k.cover.widget;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.R;
import metro.k.cover.railways.RailwaysInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

public class RailwaysInfoWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new RailwaysInfoWidgetFactory();
	}

	private class RailwaysInfoWidgetFactory implements RemoteViewsFactory {

		public RemoteViews getViewAt(int position) {
			RailwaysInfo info = MetroCoverApplication.sRailwaysInfoAdapter
					.getItem(position);
			Bitmap icon = ((BitmapDrawable) info.getIcon()).getBitmap();
			RemoteViews rv = new RemoteViews(getPackageName(),
					R.layout.widget_railways_info_at);
			rv.setTextViewText(R.id.widget_railways_info_name,
					info.getRailway());
			rv.setTextViewText(R.id.widget_railways_info_msg, info.getMessage());
			rv.setImageViewBitmap(R.id.widget_railways_info_icon, icon);

			return rv;
		}

		public long getItemId(int position) {
			return position;
		}

		public int getCount() {
			int count = 0;
			if (MetroCoverApplication.sRailwaysInfoAdapter != null) {
				count = MetroCoverApplication.sRailwaysInfoAdapter.getCount();
			}
			return count;
		}

		public RemoteViews getLoadingView() {
			return null;
		}

		public int getViewTypeCount() {
			return 1;
		}

		public boolean hasStableIds() {
			return true;
		}

		@Override
		public void onCreate() {

		}

		@Override
		public void onDataSetChanged() {
		}

		@Override
		public void onDestroy() {

		}
	}

}