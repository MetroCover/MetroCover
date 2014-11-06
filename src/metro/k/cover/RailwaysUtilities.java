package metro.k.cover;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;

public final class RailwaysUtilities {

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
}
