package metro.k.cover.widget;

import metro.k.cover.MetroCoverApplication;
import metro.k.cover.PreferenceCommon;
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

	private Handler mLoadHandler;

	@Override
	public void onUpdate(Context context, AppWidgetManager manager,
			int[] appWidgetIds) {
		super.onUpdate(context, manager, appWidgetIds);
		for (int appWidgetId : appWidgetIds) {
			update(context, manager, appWidgetId);
		}
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

	@Override
	public void onEnabled(Context context) {
		super.onEnabled(context);
		PreferenceCommon.setIsEnabledRailwaysInfoWidget(context, true);
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		removeLoadHandler();
		PreferenceCommon.setIsEnabledRailwaysInfoWidget(context, false);
	}

	/**
	 * 更新用Handlerの設定
	 */
	private void createLoadHandler() {
		if (mLoadHandler != null) {
			return;
		}
		mLoadHandler = new Handler();
	}

	/**
	 * 更新用Handlerの削除
	 */
	private void removeLoadHandler() {
		if (mLoadHandler == null) {
			return;
		}
		mLoadHandler = null;
	}

	/**
	 * 更新処理
	 * 
	 * @param context
	 * @param manager
	 * @param appWidgetId
	 */
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

		manager.updateAppWidget(appWidgetId, null);
		manager.updateAppWidget(appWidgetId, rv);
		manager.notifyAppWidgetViewDataChanged(appWidgetId,
				R.id.widget_railways_info_listview);
	}

	/**
	 * 手動更新処理
	 * 
	 * @param context
	 * @param appWidgetId
	 */
	private void reflesh(final Context context, final int appWidgetId) {
		if (context == null) {
			return;
		}

		createLoadHandler();
		new Thread("widgetReflesh") {
			@Override
			public void run() {
				MetroCoverApplication.syncCreateRailwaysInfoList(context);
				if (mLoadHandler != null) {
					mLoadHandler.post(getRefeshTask(context, appWidgetId));
				}
			}
		}.start();
	}

	/**
	 * 手動更新時のタスク
	 * 
	 * @param context
	 * @param appWidgetId
	 * @return
	 */
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

	/**
	 * 更新ボタンにリスナー付与
	 * 
	 * @param ctx
	 * @param rv
	 * @param appWidgetId
	 */
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