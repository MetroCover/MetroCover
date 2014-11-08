package metro.k.cover.railways;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import metro.k.cover.R;
import metro.k.cover.Utilities;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

public final class RailwaysUtilities {

	public static ArrayList<String> getAllRailwaysCode() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(Railways.RAILWAY_CODE_CHIYODA);
		list.add(Railways.RAILWAY_CODE_FUKUTOSHIN);
		list.add(Railways.RAILWAY_CODE_GINZA);
		list.add(Railways.RAILWAY_CODE_HANZOMON);
		list.add(Railways.RAILWAY_CODE_HIBIYA);
//		list.add(Railways.RAILWAY_CODE_MARUNOUCHI);
		list.add(Railways.RAILWAY_CODE_NAMBOKU);
//		list.add(Railways.RAILWAY_CODE_TOZAI);
//		list.add(Railways.RAILWAY_CODE_YURAKUCHO);
		return list;
	}
	
	public static ArrayList<String> getAllRailwaysName(final Context context) {
		ArrayList<String> list = new ArrayList<String>();
		final Resources res = context.getResources();
		list.add(res.getString(R.string.railway_chiyoda));
		list.add(res.getString(R.string.railway_fukutoshin));
		list.add(res.getString(R.string.railway_ginza));
		list.add(res.getString(R.string.railway_hanzomon));
		list.add(res.getString(R.string.railway_hibiya));
//		list.add(res.getString(R.string.railway_marunouchi));
		list.add(res.getString(R.string.railway_namboku));
//		list.add(res.getString(R.string.railway_tozai));
//		list.add(res.getString(R.string.railway_yurakucho));
		return list;
	}

	/**
	 * 全路線取得
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<Railways> getAllRailways(final Context context) {
		if (context == null) {
			return null;
		}

		final Resources res = context.getResources();
		final ArrayList<Railways> list = new ArrayList<Railways>();
		// 千代田線
		Railways item = new Railways(Railways.RAILWAY_NUM_CHIYODA,
				Railways.RAILWAY_CODE_CHIYODA,
				res.getString(R.string.railway_chiyoda),
				res.getDrawable(R.drawable.ic_chiyoda), false);
		list.add(item);

		// 副都心線
		item = new Railways(Railways.RAILWAY_NUM_FUKUTOSHIN,
				Railways.RAILWAY_CODE_FUKUTOSHIN,
				res.getString(R.string.railway_fukutoshin),
				res.getDrawable(R.drawable.ic_fukutoshin), false);
		list.add(item);

		// 銀座線
		item = new Railways(Railways.RAILWAY_NUM_GINZA,
				Railways.RAILWAY_CODE_GINZA,
				res.getString(R.string.railway_ginza),
				res.getDrawable(R.drawable.ic_ginza), false);
		list.add(item);

		// 半蔵門線
		item = new Railways(Railways.RAILWAY_NUM_HANZOMON,
				Railways.RAILWAY_CODE_HANZOMON,
				res.getString(R.string.railway_hanzomon),
				res.getDrawable(R.drawable.ic_hanzomon), false);
		list.add(item);

		// 日比谷線
		item = new Railways(Railways.RAILWAY_NUM_HIBIYA,
				Railways.RAILWAY_CODE_GINZA,
				res.getString(R.string.railway_hibiya),
				res.getDrawable(R.drawable.ic_hibiya), false);
		list.add(item);

		// 丸ノ内線
		item = new Railways(Railways.RAILWAY_NUM_MARUNOUCHI,
				Railways.RAILWAY_CODE_MARUNOUCHI,
				res.getString(R.string.railway_marunouchi),
				res.getDrawable(R.drawable.ic_marunouchi), false);
		list.add(item);

		// 南北線
		item = new Railways(Railways.RAILWAY_NUM_NAMBOKU,
				Railways.RAILWAY_CODE_NAMBOKU,
				res.getString(R.string.railway_namboku),
				res.getDrawable(R.drawable.ic_namboku), false);
		list.add(item);

		// 東西線
		item = new Railways(Railways.RAILWAY_NUM_TOZAI,
				Railways.RAILWAY_CODE_TOZAI,
				res.getString(R.string.railway_tozai),
				res.getDrawable(R.drawable.ic_tozai), false);
		list.add(item);

		// 有楽町線
		item = new Railways(Railways.RAILWAY_NUM_YURAKUCHO,
				Railways.RAILWAY_CODE_YURAKUCHO,
				res.getString(R.string.railway_yurakucho),
				res.getDrawable(R.drawable.ic_yurakucho), false);
		list.add(item);
		return list;
	}

	public static Railways getRailwaysFromNumber(final Context context,
			final int num, final boolean checked) {
		final Resources res = context.getResources();
		Railways railways = null;
		switch (num) {
		case Railways.RAILWAY_NUM_CHIYODA:
			railways = new Railways(Railways.RAILWAY_NUM_CHIYODA,
					Railways.RAILWAY_CODE_CHIYODA,
					res.getString(R.string.railway_chiyoda),
					res.getDrawable(R.drawable.ic_chiyoda), checked);
		case Railways.RAILWAY_NUM_FUKUTOSHIN:
			railways = new Railways(Railways.RAILWAY_NUM_FUKUTOSHIN,
					Railways.RAILWAY_CODE_FUKUTOSHIN,
					res.getString(R.string.railway_fukutoshin),
					res.getDrawable(R.drawable.ic_fukutoshin), checked);
		case Railways.RAILWAY_NUM_GINZA:
			railways = new Railways(Railways.RAILWAY_NUM_GINZA,
					Railways.RAILWAY_CODE_GINZA,
					res.getString(R.string.railway_ginza),
					res.getDrawable(R.drawable.ic_ginza), checked);
		case Railways.RAILWAY_NUM_HANZOMON:
			railways = new Railways(Railways.RAILWAY_NUM_HANZOMON,
					Railways.RAILWAY_CODE_HANZOMON,
					res.getString(R.string.railway_hanzomon),
					res.getDrawable(R.drawable.ic_hanzomon), checked);
		case Railways.RAILWAY_NUM_HIBIYA:
			railways = new Railways(Railways.RAILWAY_NUM_HIBIYA,
					Railways.RAILWAY_CODE_GINZA,
					res.getString(R.string.railway_hibiya),
					res.getDrawable(R.drawable.ic_hibiya), checked);
		case Railways.RAILWAY_NUM_MARUNOUCHI:
			railways = new Railways(Railways.RAILWAY_NUM_MARUNOUCHI,
					Railways.RAILWAY_CODE_MARUNOUCHI,
					res.getString(R.string.railway_marunouchi),
					res.getDrawable(R.drawable.ic_marunouchi), checked);
		case Railways.RAILWAY_NUM_NAMBOKU:
			railways = new Railways(Railways.RAILWAY_NUM_NAMBOKU,
					Railways.RAILWAY_CODE_NAMBOKU,
					res.getString(R.string.railway_namboku),
					res.getDrawable(R.drawable.ic_namboku), checked);
		case Railways.RAILWAY_NUM_TOZAI:
			railways = new Railways(Railways.RAILWAY_NUM_TOZAI,
					Railways.RAILWAY_CODE_TOZAI,
					res.getString(R.string.railway_tozai),
					res.getDrawable(R.drawable.ic_tozai), checked);
		case Railways.RAILWAY_NUM_YURAKUCHO:
			railways = new Railways(Railways.RAILWAY_NUM_YURAKUCHO,
					Railways.RAILWAY_CODE_YURAKUCHO,
					res.getString(R.string.railway_yurakucho),
					res.getDrawable(R.drawable.ic_yurakucho), checked);
		default:
			break;
		}

		return railways;
	}

	/**
	 * 指定の路線から駅名リストを取得する
	 * 
	 * @param railway
	 * @return
	 */
	public static List<String> getStationList(final Context context,
			final String railwayCode) {
		if (context == null || Utilities.isInvalidStr(railwayCode)) {
			return null;
		}

		final Resources res = context.getResources();
		String[] array_str = null;
		if (railwayCode.equals(Railways.RAILWAY_CODE_CHIYODA)) {
			array_str = res.getStringArray(R.array.chiyoda_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_FUKUTOSHIN)) {
			array_str = res.getStringArray(R.array.fukutoshin_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_GINZA)) {
			array_str = res.getStringArray(R.array.ginza_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_HANZOMON)) {
			array_str = res.getStringArray(R.array.hanzomon_railway_stations);
			return Arrays.asList(array_str);
		}

		return null;
	}

	@SuppressLint("Recycle")
	public static ArrayList<Drawable> getStationIconList(final Context context,
			final String railwayCode) {
		if (context == null || Utilities.isInvalidStr(railwayCode)) {
			return null;
		}

		final Resources res = context.getResources();
		TypedArray images = null;
		if (railwayCode.equals(Railways.RAILWAY_CODE_CHIYODA)) {
			images = res.obtainTypedArray(R.array.chiyoda_railway_icons);
			return convertTypedArray(images);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_FUKUTOSHIN)) {
			images = res.obtainTypedArray(R.array.fukutoshin_railway_icons);
			return convertTypedArray(images);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_GINZA)) {
			images = res.obtainTypedArray(R.array.ginza_railway_icons);
			return convertTypedArray(images);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_HANZOMON)) {
			images = res.obtainTypedArray(R.array.hanzomon_railway_icons);
			return convertTypedArray(images);
		}
		
		if (railwayCode.equals(Railways.RAILWAY_CODE_HIBIYA)) {
			images = res.obtainTypedArray(R.array.hibiya_railway_icons);
			return convertTypedArray(images);
		}
		
		if (railwayCode.equals(Railways.RAILWAY_CODE_NAMBOKU)) {
			images = res.obtainTypedArray(R.array.namboku_railway_icons);
			return convertTypedArray(images);
		}

		return null;
	}

	private static ArrayList<Drawable> convertTypedArray(TypedArray array) {
		if (array == null) {
			return null;
		}

		final ArrayList<Drawable> list = new ArrayList<Drawable>();
		final int size = array.length();
		for (int i = 0; i < size; i++) {
			list.add(array.getDrawable(i));
		}
		return list;
	}
}
