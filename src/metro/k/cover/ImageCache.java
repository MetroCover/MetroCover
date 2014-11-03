package metro.k.cover;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;

/**
 * 画像系のキャッシュ
 * 
 * @author kohirose
 * 
 */
public class ImageCache {

	public static final String KEY_WALLPAPER_LEFT_CACHE = "key_wallpaper_left_cache";
	public static final String KEY_WALLPAPER_CENTER_CACHE = "key_wallpaper_center_cache";
	public static final String KEY_WALLPAPER_RIGHT_CACHE = "key_wallpaper_right_cache";

	/***************************************************************
	 * Drawable
	 ***************************************************************/

	private static HashMap<String, Drawable> mCacheDrawable = new HashMap<String, Drawable>();

	public static Drawable getImage(String key) {
		if (mCacheDrawable.containsKey(key)) {
			return mCacheDrawable.get(key);
		}
		return null;
	}

	public static void setImage(String key, Drawable image) {
		mCacheDrawable.put(key, image);
	}

	public static void clearCache() {
		mCacheDrawable = null;
		mCacheDrawable = new HashMap<String, Drawable>();
	}

	public static void clearCache(String key) {
		Drawable d = mCacheDrawable.get(key);
		if (d != null) {
			setImage(key, null);
		}
	}

	/***************************************************************
	 * Bitmap
	 ***************************************************************/

	private static HashMap<String, Bitmap> mCacheBitmap = new HashMap<String, Bitmap>();

	public static Bitmap getImageBmp(String key) {
		if (mCacheBitmap.containsKey(key)) {
			return mCacheBitmap.get(key);
		}
		return null;
	}

	public static void setImageBmp(String key, Bitmap image) {
		mCacheBitmap.put(key, image);
	}

	public static void clearCacheBmp() {
		mCacheBitmap = null;
		mCacheBitmap = new HashMap<String, Bitmap>();
	}

	public static void clearCacheBmp(String key) {
		Bitmap bmp = mCacheBitmap.get(key);
		if (bmp != null) {
			setImageBmp(key, null);
		}
	}

	/***************************************************************
	 * LayerDrawable
	 ***************************************************************/
	private static HashMap<String, LayerDrawable> mIconBgCache = new HashMap<String, LayerDrawable>();

	public static LayerDrawable getImageLayerDrawable(String key) {
		if (mIconBgCache.containsKey(key)) {
			return mIconBgCache.get(key);
		}
		return null;
	}

	public static void setImageLayerDrawable(String key, LayerDrawable image) {
		mIconBgCache.put(key, image);
	}

	public static void clearCacheLayerDrawable() {
		mIconBgCache = null;
		mIconBgCache = new HashMap<String, LayerDrawable>();
	}
}