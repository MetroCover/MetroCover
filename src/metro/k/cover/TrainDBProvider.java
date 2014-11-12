package metro.k.cover;

import java.util.List;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

public class TrainDBProvider extends ContentProvider {

	public static final Uri CONTENT_URI = Uri.parse("content://metro.k.cover.tran_db_provider/departure_time");
	public static final String TABLE_NAME = "departure_time";

	public final String mimeTypeForOne = "vnd.android.cursor.item/vnd.metro.k." + TABLE_NAME;

	public final String mimeTypeForMany = "vnd.android.cursor.dir/vnd.metro.k." + TABLE_NAME;

	public static final class Columns {
		public static final String _ID = "_id";
		public static final String TABLE_INDEX = "table_index";
		public static final String STATION_CODE = "station_code";
		public static final String TIME = "time";
		public static final String HOUR = "hour";
		public static final String MINUTE = "minute";
		public static final String DAY_TYPE = "day_type";
		public static final String TRAIN_TYPE = "train_type";
		public static final String TERMINAL_STATION = "terminal_station";
	}

	public static final class DayType {
		public static final int WEEKDAYS = 0;
		public static final int SATURDAYS = 1;
		public static final int HOLIDAYS = 2;
	}
	private static final int TRAIN_ALL = 1;
	private static final int TRAIN_ID = 2;

	private static final UriMatcher sUrlMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUrlMatcher.addURI(CONTENT_URI.getAuthority(), TABLE_NAME, TRAIN_ALL);
		sUrlMatcher.addURI(CONTENT_URI.getAuthority(), TABLE_NAME + "/#", TRAIN_ID);
	}

	StationDbHelper mDbHelper;

	@Override
	public boolean onCreate() {
		mDbHelper = new StationDbHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		checkUri(uri);
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(uri.getPathSegments().get(0), projection, appendSelection(uri, selection), appendSelectionArgs(uri, selectionArgs),
				null, null, sortOrder);
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		checkUri(uri);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final long rowId = db.insertOrThrow(uri.getPathSegments().get(0), null, values);
		Uri returnUri = ContentUris.withAppendedId(uri, rowId);
		getContext().getContentResolver().notifyChange(returnUri, null);
		return returnUri;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		checkUri(uri);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final int count = db.update(uri.getPathSegments().get(0), values, appendSelection(uri, selection), appendSelectionArgs(uri, selectionArgs));
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		checkUri(uri);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		final int count = db.delete(uri.getPathSegments().get(0), appendSelection(uri, selection), appendSelectionArgs(uri, selectionArgs));
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public String getType(Uri uri) {
		final int code = sUrlMatcher.match(uri);
		if (code == TRAIN_ALL) {
			return mimeTypeForMany;
		} else if (code == TRAIN_ID) {
			return mimeTypeForOne;
		}
		throw new IllegalArgumentException("unknown uri : " + uri);
	}

	private void checkUri(Uri uri) {
		final int code = sUrlMatcher.match(uri);
		if (code == TRAIN_ALL) {
			return;
		} else if (code == TRAIN_ID) {
			return;
		}
		throw new IllegalArgumentException("unknown uri : " + uri);
	}

	private String appendSelection(Uri uri, String selection) {
		List<String> pathSegments = uri.getPathSegments();
		if (pathSegments.size() == 1) {
			return selection;
		}
		return BaseColumns._ID + " = ?" + (selection == null ? "" : " AND (" + selection + ")");
	}

	private String[] appendSelectionArgs(Uri uri, String[] selectionArgs) {
		List<String> pathSegments = uri.getPathSegments();
		if (pathSegments.size() == 1) {
			return selectionArgs;
		}
		if (selectionArgs == null || selectionArgs.length == 0) {
			return new String[] { pathSegments.get(1) };
		}
		String[] returnArgs = new String[selectionArgs.length + 1];
		returnArgs[0] = pathSegments.get(1);
		System.arraycopy(selectionArgs, 0, returnArgs, 1, selectionArgs.length);
		return returnArgs;
	}

	public class StationDbHelper extends SQLiteOpenHelper {

		private static final String DB_NAME = "train_db";
		private static final int DB_VERSION = 1;

		private static final String STRING_CREATE = "CREATE TABLE " + TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ Columns.TABLE_INDEX + " TEXT NOT NULL UNIQUE,"
				+ Columns.STATION_CODE + " TEXT NOT NULL,"
				+ Columns.TIME + " INTEGER NOT NULL,"
				+ Columns.HOUR + " INTEGER NOT NULL,"
				+ Columns.MINUTE + " INTEGER NOT NULL,"
				+ Columns.DAY_TYPE + " INTEGER NOT NULL,"
				+ Columns.TRAIN_TYPE + " INTEGER NOT NULL,"
				+ Columns.TERMINAL_STATION + " TEXT NOT NULL);";

		public StationDbHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.beginTransaction();
			try {
				db.execSQL(STRING_CREATE);
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int i, int i2) {
			makeOverTable(db);
		}

		@Override
		public void onDowngrade(SQLiteDatabase db, int i, int i2) {
			makeOverTable(db);
		}

		private void makeOverTable(SQLiteDatabase db) {
			db.beginTransaction();
			try {
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
				db.execSQL(STRING_CREATE);
			} finally {
				db.endTransaction();
			}

		}
	}
}
