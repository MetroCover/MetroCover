package metro.k.cover.wallpaper;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * DB操作
 * 
 * @author kohirose
 * 
 */
public class WallpaperBitmapDB extends SQLiteOpenHelper {

	// DBのKey
	public static final String KEY_WALLPAPER_LEFT_DB = "key_wallpaper_left_db";
	public static final String KEY_WALLPAPER_CENTER_DB = "key_wallpaper_center_db";
	public static final String KEY_WALLPAPER_RIGHT_DB = "key_wallpaper_right_db";

	private static final String NAME = "bitmap.db";
	private static final int VERSION = 1;

	private static final String TABLE = "bitmap";
	private static final String _ID = "_id";
	private static final String URL = "url";
	private static final String BITMAP = "bitmap";

	private static final String[] COLUMNS = { _ID, URL, BITMAP };

	public WallpaperBitmapDB(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + URL
				+ " TEXT NOT NULL," + BITMAP + " BLOB NOT NULL" + ");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE);
		onCreate(db);
	}

	private synchronized void setCacheAsBlob(String key, byte[] bytes) {
		boolean exist = (getCacheAsBlob(key) == null) ? false : true;
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(URL, key);
		values.put(BITMAP, bytes);
		if (exist) {
			db.update(TABLE, values, URL + "=" + "'" + key + "'", null);
		} else {
			db.insert(TABLE, null, values);
		}
		db.close();
	}

	private synchronized byte[] getCacheAsBlob(String key) {
		byte[] bytes = null;
		final SQLiteDatabase db = getReadableDatabase();
		final Cursor cursol = db.query(TABLE, COLUMNS, URL + "=" + "'" + key
				+ "'", null, null, null, null);
		if (cursol.getCount() == 1) {
			try {
				if (cursol.moveToFirst()) {
					bytes = cursol.getBlob(2);
				}
			} catch (Exception e) {
			}
		}
		cursol.close();
		db.close();
		return bytes;
	}

	public Bitmap getBitmp(String url) {
		Bitmap bitmap = null;
		byte[] bytes = getCacheAsBlob(url);
		if (bytes != null) {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPurgeable = true;
			bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
					options);
		}
		return bitmap;
	}

	public void setBitmap(String url, Bitmap bitmap) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		if (bitmap.compress(Bitmap.CompressFormat.PNG, 100,
				byteArrayOutputStream)) {
			byte[] bytes = byteArrayOutputStream.toByteArray();
			setCacheAsBlob(url, bytes);
		}
	}

	public void delteBitmap(final String key) {
		SQLiteDatabase db = getWritableDatabase();
		try {
			db.delete(TABLE, "url = '" + key + "'", null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.close();
		}
	}

}