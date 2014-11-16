package metro.k.cover.widget;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.R;
import metro.k.cover.railways.RailwaysInfo;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

/**
 * 遅延情報ウィジェットの表示Service
 * 
 * @author kohirose
 * 
 */
public class RailwaysInfoWidgetService extends RemoteViewsService {

	@Override
	public RemoteViewsFactory onGetViewFactory(Intent intent) {
		return new RailwaysInfoWidgetFactory();
	}

	private class RailwaysInfoWidgetFactory implements RemoteViewsFactory {

		public RemoteViews getViewAt(int position) {
			RemoteViews rv = new RemoteViews(getPackageName(),
					R.layout.widget_railways_info_at);
			if (MetroCoverApplication.getRailwaysInfoList() == null) {
				return rv;
			}

			RailwaysInfo info = MetroCoverApplication.getRailwaysInfoList()
					.getItem(position);
			Bitmap icon = ((BitmapDrawable) info.getIcon()).getBitmap();

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
			if (MetroCoverApplication.getRailwaysInfoList() != null) {
				count = MetroCoverApplication.getRailwaysInfoList().getCount();
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