package metro.k.cover.widget;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.R;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.widget.RemoteViews;

public class RailwaysInfoWidgetProvider extends AppWidgetProvider {

	private static final String ACTION_CLICK = "metro.k.cover.widget.ACTION_CLICK";

	@Override
	public void onUpdate(Context context, AppWidgetManager manager,
			int[] appWidgetIds) {
		super.onUpdate(context, manager, appWidgetIds);

		for (int appWidgetId : appWidgetIds) {
			update(context, manager, appWidgetId);
		}
	}

	private void update(final Context context, AppWidgetManager manager,
			final int appWidgetId) {
		Intent remoteViewsFactoryIntent = new Intent(context,
				RailwaysInfoWidgetService.class);
		RemoteViews rv = new RemoteViews(context.getPackageName(),
				R.layout.widget_railways_info);
		rv.setRemoteAdapter(R.id.widget_railways_info_listview,
				remoteViewsFactoryIntent);
		rv.setTextViewText(R.id.widget_railways_info_lastupdate,
				MetroCoverApplication.getLastUpdateTime());

		setOnButtonClickPendingIntent(context, rv, appWidgetId);

		manager.updateAppWidget(appWidgetId, rv);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
		if (context == null || intent == null) {
			return;
		}

		if (ACTION_CLICK.equals(intent.getAction())) {
			final int appWidgetId = intent.getIntExtra(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
				return;
			}
			reflesh(context, appWidgetId);
		}
	}

	private void reflesh(final Context context, final int appWidgetId) {
		if (context == null) {
			return;
		}

		final Handler h = new Handler();
		new Thread("widgetReflesh") {
			@Override
			public void run() {
				MetroCoverApplication.syncCreateRailwaysInfoList(context);
				h.post(getRefeshTask(context, appWidgetId));
			}
		}.start();
	}

	private Runnable getRefeshTask(final Context context, final int appWidgetId) {
		Runnable task = new Runnable() {
			@Override
			public void run() {
				update(context, AppWidgetManager.getInstance(context),
						appWidgetId);
			}
		};
		return task;
	}

	private void setOnButtonClickPendingIntent(Context ctx, RemoteViews rv,
			int appWidgetId) {
		Intent btnClickIntent = new Intent(ACTION_CLICK);
		btnClickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);

		PendingIntent btnClickPendingIntent = PendingIntent.getBroadcast(ctx,
				appWidgetId, btnClickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		rv.setOnClickPendingIntent(R.id.widget_railways_info_update_btn,
				btnClickPendingIntent);
	}
}