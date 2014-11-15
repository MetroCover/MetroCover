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

	// 設定画面から駅選択へ行く際に渡すintentのKey
	public static final String KEY_CURRENT_STATION = "key_cuurent_station";

	// 駅選択から方面選択へいく際に渡すintnetのKey
	public static final String KEY_SELECTED_STATION_NAME = "key_selected_station_name";
	public static final String KEY_SELECTED_STATIONS_RAILWAY_NAME = "key_selected_stations_railway_name";
	public static final String KEY_SELECTED_STATION_NAME_API = "key_selected_stations_railway_name_API";

	// 方角データの配列サイズ＝４（0:方角API1,1:方角API2,2:方角名1,3:方角名2）
	public static final int SIZE_DIRECTION_DATA_ARRAY = 4;

	// 方角
	public static final int DIRECTION_1 = 0;
	public static final int DIRECTION_2 = 1;
	public static final int DIRECTION_NAME_1 = 2;
	public static final int DIRECTION_NAME_2 = 3;

	/**
	 * APIから返ってきたレスポンス名から路線名へ変換する
	 * 
	 * @param context
	 * @param apiResponse
	 * @return
	 */
	public static String getRailwaysName(final Context context,
			final String apiResponse) {
		if (Utilities.isInvalidStr(apiResponse)) {
			return "";
		}
		final Resources res = context.getResources();

		// 千代田線
		if (res.getString(R.string.railway_chiyoda_response)
				.equals(apiResponse)) {
			return res.getString(R.string.railway_chiyoda);
		}
		// 副都心線
		if (res.getString(R.string.railway_ginza_response).equals(apiResponse)) {
			return res.getString(R.string.railway_fukutoshin);
		}
		// 銀座線
		if (res.getString(R.string.railway_fukutoshin_response).equals(
				apiResponse)) {
			return res.getString(R.string.railway_fukutoshin);
		}
		// 日比谷線
		if (res.getString(R.string.railway_hibiya_response).equals(apiResponse)) {
			return res.getString(R.string.railway_hibiya);
		}
		// 丸ノ内線
		if (res.getString(R.string.railway_marunouchi_response).equals(
				apiResponse)) {
			return res.getString(R.string.railway_marunouchi);
		}
		// 南北線
		if (res.getString(R.string.railway_namboku_response)
				.equals(apiResponse)) {
			return res.getString(R.string.railway_namboku);
		}
		// 東西線
		if (res.getString(R.string.railway_tozai_response).equals(apiResponse)) {
			return res.getString(R.string.railway_tozai);
		}
		// 有楽町線
		if (res.getString(R.string.railway_yurakucho_response).equals(
				apiResponse)) {
			return res.getString(R.string.railway_yurakucho);
		}
		// 半蔵門線
		if (res.getString(R.string.railway_hanzomon_response).equals(
				apiResponse)) {
			return res.getString(R.string.railway_hanzomon);
		}
		return "";
	}

	/**
	 * // APIから返ってきたレスポンス名からアイコンを取得する
	 * 
	 * @param context
	 * @param apiResponse
	 * @return
	 */
	public static Drawable getRailwayIcon(final Context context,
			final String apiResponse) {
		if (context == null || Utilities.isInvalidStr(apiResponse)) {
			return null;
		}

		final Resources res = context.getResources();

		// 千代田線
		if (res.getString(R.string.railway_chiyoda_response)
				.equals(apiResponse)) {
			return res.getDrawable(R.drawable.ic_chiyoda);
		}
		// 副都心線
		if (res.getString(R.string.railway_fukutoshin_response).equals(
				apiResponse)) {
			return res.getDrawable(R.drawable.ic_fukutoshin);
		}
		// 銀座線
		if (res.getString(R.string.railway_ginza_response).equals(apiResponse)) {
			return res.getDrawable(R.drawable.ic_ginza);
		}
		// 日比谷線
		if (res.getString(R.string.railway_hibiya_response).equals(apiResponse)) {
			return res.getDrawable(R.drawable.ic_hibiya);
		}
		// 丸ノ内線
		if (res.getString(R.string.railway_marunouchi_response).equals(
				apiResponse)) {
			return res.getDrawable(R.drawable.ic_marunouchi);
		}
		// 南北線
		if (res.getString(R.string.railway_namboku_response)
				.equals(apiResponse)) {
			return res.getDrawable(R.drawable.ic_namboku);
		}
		// 東西線
		if (res.getString(R.string.railway_tozai_response).equals(apiResponse)) {
			return res.getDrawable(R.drawable.ic_tozai);
		}
		// 有楽町線
		if (res.getString(R.string.railway_yurakucho_response).equals(
				apiResponse)) {
			return res.getDrawable(R.drawable.ic_yurakucho);
		}
		// 半蔵門線
		if (res.getString(R.string.railway_hanzomon_response).equals(
				apiResponse)) {
			return res.getDrawable(R.drawable.ic_hanzomon);
		}
		return null;
	}

	/**
	 * 全路線コードを取得する
	 * 
	 * @return
	 */
	public static ArrayList<String> getAllRailwaysCode() {
		ArrayList<String> list = new ArrayList<String>();
		list.add(Railways.RAILWAY_CODE_CHIYODA);
		list.add(Railways.RAILWAY_CODE_FUKUTOSHIN);
		list.add(Railways.RAILWAY_CODE_GINZA);
		list.add(Railways.RAILWAY_CODE_HANZOMON);
		list.add(Railways.RAILWAY_CODE_HIBIYA);
		list.add(Railways.RAILWAY_CODE_MARUNOUCHI);
		list.add(Railways.RAILWAY_CODE_MARUNOUCHI_M);
		list.add(Railways.RAILWAY_CODE_NAMBOKU);
		list.add(Railways.RAILWAY_CODE_TOZAI);
		list.add(Railways.RAILWAY_CODE_YURAKUCHO);
		return list;
	}

	/**
	 * 全路線名を取得する
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<String> getAllRailwaysName(final Context context) {
		ArrayList<String> list = new ArrayList<String>();
		final Resources res = context.getResources();
		list.add(res.getString(R.string.railway_chiyoda));
		list.add(res.getString(R.string.railway_fukutoshin));
		list.add(res.getString(R.string.railway_ginza));
		list.add(res.getString(R.string.railway_hanzomon));
		list.add(res.getString(R.string.railway_hibiya));
		list.add(res.getString(R.string.railway_marunouchi));
		list.add(res.getString(R.string.railway_marunouchi_m));
		list.add(res.getString(R.string.railway_namboku));
		list.add(res.getString(R.string.railway_tozai));
		list.add(res.getString(R.string.railway_yurakucho));
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
				res.getString(R.string.railway_chiyoda_response),
				res.getString(R.string.railway_chiyoda),
				res.getDrawable(R.drawable.ic_chiyoda), false);
		list.add(item);

		// 副都心線
		item = new Railways(Railways.RAILWAY_NUM_FUKUTOSHIN,
				Railways.RAILWAY_CODE_FUKUTOSHIN,
				res.getString(R.string.railway_fukutoshin_response),
				res.getString(R.string.railway_fukutoshin),
				res.getDrawable(R.drawable.ic_fukutoshin), false);
		list.add(item);

		// 銀座線
		item = new Railways(Railways.RAILWAY_NUM_GINZA,
				Railways.RAILWAY_CODE_GINZA,
				res.getString(R.string.railway_ginza_response),
				res.getString(R.string.railway_ginza),
				res.getDrawable(R.drawable.ic_ginza), false);
		list.add(item);

		// 半蔵門線
		item = new Railways(Railways.RAILWAY_NUM_HANZOMON,
				Railways.RAILWAY_CODE_HANZOMON,
				res.getString(R.string.railway_hanzomon_response),
				res.getString(R.string.railway_hanzomon),
				res.getDrawable(R.drawable.ic_hanzomon), false);
		list.add(item);

		// 日比谷線
		item = new Railways(Railways.RAILWAY_NUM_HIBIYA,
				Railways.RAILWAY_CODE_HIBIYA,
				res.getString(R.string.railway_hibiya_response),
				res.getString(R.string.railway_hibiya),
				res.getDrawable(R.drawable.ic_hibiya), false);
		list.add(item);

		// 丸ノ内線
		item = new Railways(Railways.RAILWAY_NUM_MARUNOUCHI,
				Railways.RAILWAY_CODE_MARUNOUCHI,
				res.getString(R.string.railway_marunouchi_response),
				res.getString(R.string.railway_marunouchi),
				res.getDrawable(R.drawable.ic_marunouchi), false);
		list.add(item);

		// 丸ノ内線支線
		item = new Railways(Railways.RAILWAY_NUM_MARUNOUCHI_M,
				Railways.RAILWAY_CODE_MARUNOUCHI_M,
				res.getString(R.string.railway_marunouchi_branch_response),
				res.getString(R.string.railway_marunouchi_m),
				res.getDrawable(R.drawable.station_mm_3), false);
		list.add(item);

		// 南北線
		item = new Railways(Railways.RAILWAY_NUM_NAMBOKU,
				Railways.RAILWAY_CODE_NAMBOKU,
				res.getString(R.string.railway_namboku_response),
				res.getString(R.string.railway_namboku),
				res.getDrawable(R.drawable.ic_namboku), false);
		list.add(item);

		// 東西線
		item = new Railways(Railways.RAILWAY_NUM_TOZAI,
				Railways.RAILWAY_CODE_TOZAI,
				res.getString(R.string.railway_tozai_response),
				res.getString(R.string.railway_tozai),
				res.getDrawable(R.drawable.ic_tozai), false);
		list.add(item);

		// 有楽町線
		item = new Railways(Railways.RAILWAY_NUM_YURAKUCHO,
				Railways.RAILWAY_CODE_YURAKUCHO,
				res.getString(R.string.railway_yurakucho_response),
				res.getString(R.string.railway_yurakucho),
				res.getDrawable(R.drawable.ic_yurakucho), false);
		list.add(item);

		return list;
	}

	/**
	 * 路線番号から路線クラスを取得する
	 * 
	 * @param context
	 * @param num
	 * @param checked
	 * @return
	 */
	public static Railways getRailwaysFromNumber(final Context context,
			final int num, final boolean checked) {
		final Resources res = context.getResources();
		Railways railways = null;
		switch (num) {
		case Railways.RAILWAY_NUM_CHIYODA:
			railways = new Railways(Railways.RAILWAY_NUM_CHIYODA,
					Railways.RAILWAY_CODE_CHIYODA,
					res.getString(R.string.railway_chiyoda_response),
					res.getString(R.string.railway_chiyoda),
					res.getDrawable(R.drawable.ic_chiyoda), checked);
		case Railways.RAILWAY_NUM_FUKUTOSHIN:
			railways = new Railways(Railways.RAILWAY_NUM_FUKUTOSHIN,
					Railways.RAILWAY_CODE_FUKUTOSHIN,
					res.getString(R.string.railway_fukutoshin_response),
					res.getString(R.string.railway_fukutoshin),
					res.getDrawable(R.drawable.ic_fukutoshin), checked);
		case Railways.RAILWAY_NUM_GINZA:
			railways = new Railways(Railways.RAILWAY_NUM_GINZA,
					Railways.RAILWAY_CODE_GINZA,
					res.getString(R.string.railway_ginza_response),
					res.getString(R.string.railway_ginza),
					res.getDrawable(R.drawable.ic_ginza), checked);
		case Railways.RAILWAY_NUM_HANZOMON:
			railways = new Railways(Railways.RAILWAY_NUM_HANZOMON,
					Railways.RAILWAY_CODE_HANZOMON,
					res.getString(R.string.railway_hanzomon_response),
					res.getString(R.string.railway_hanzomon),
					res.getDrawable(R.drawable.ic_hanzomon), checked);
		case Railways.RAILWAY_NUM_HIBIYA:
			railways = new Railways(Railways.RAILWAY_NUM_HIBIYA,
					Railways.RAILWAY_CODE_HIBIYA,
					res.getString(R.string.railway_hibiya_response),
					res.getString(R.string.railway_hibiya),
					res.getDrawable(R.drawable.ic_hibiya), checked);
		case Railways.RAILWAY_NUM_MARUNOUCHI:
			railways = new Railways(Railways.RAILWAY_NUM_MARUNOUCHI,
					Railways.RAILWAY_CODE_MARUNOUCHI,
					res.getString(R.string.railway_marunouchi_response),
					res.getString(R.string.railway_marunouchi),
					res.getDrawable(R.drawable.ic_marunouchi), checked);
		case Railways.RAILWAY_NUM_MARUNOUCHI_M:
			railways = new Railways(Railways.RAILWAY_NUM_MARUNOUCHI_M,
					Railways.RAILWAY_CODE_MARUNOUCHI_M,
					res.getString(R.string.railway_marunouchi_branch_response),
					res.getString(R.string.railway_marunouchi_m),
					res.getDrawable(R.drawable.station_mm_3), checked);
		case Railways.RAILWAY_NUM_NAMBOKU:
			railways = new Railways(Railways.RAILWAY_NUM_NAMBOKU,
					Railways.RAILWAY_CODE_NAMBOKU,
					res.getString(R.string.railway_namboku_response),
					res.getString(R.string.railway_namboku),
					res.getDrawable(R.drawable.ic_namboku), checked);
		case Railways.RAILWAY_NUM_TOZAI:
			railways = new Railways(Railways.RAILWAY_NUM_TOZAI,
					Railways.RAILWAY_CODE_TOZAI,
					res.getString(R.string.railway_tozai_response),
					res.getString(R.string.railway_tozai),
					res.getDrawable(R.drawable.ic_tozai), checked);
		case Railways.RAILWAY_NUM_YURAKUCHO:
			railways = new Railways(Railways.RAILWAY_NUM_YURAKUCHO,
					Railways.RAILWAY_CODE_YURAKUCHO,
					res.getString(R.string.railway_yurakucho_response),
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

		if (railwayCode.equals(Railways.RAILWAY_CODE_HIBIYA)) {
			array_str = res.getStringArray(R.array.hibiya_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_NAMBOKU)) {
			array_str = res.getStringArray(R.array.namboku_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_TOZAI)) {
			array_str = res.getStringArray(R.array.tozai_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_YURAKUCHO)) {
			array_str = res.getStringArray(R.array.yurakucho_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_MARUNOUCHI)) {
			array_str = res.getStringArray(R.array.marunouchi_railway_stations);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_MARUNOUCHI_M)) {
			array_str = res
					.getStringArray(R.array.marunouchi_branch_railway_stations);
			return Arrays.asList(array_str);
		}

		return null;
	}

	/**
	 * 指定の路線から駅名リスト（APIリクエスト用の名前）を取得する
	 * 
	 * @param railway
	 * @return
	 */
	public static List<String> getStationListForAPI(final Context context,
			final String railwayCode) {
		if (context == null || Utilities.isInvalidStr(railwayCode)) {
			return null;
		}

		final Resources res = context.getResources();
		String[] array_str = null;
		if (railwayCode.equals(Railways.RAILWAY_CODE_CHIYODA)) {
			array_str = res
					.getStringArray(R.array.chiyoda_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_FUKUTOSHIN)) {
			array_str = res
					.getStringArray(R.array.fukutoshin_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_GINZA)) {
			array_str = res.getStringArray(R.array.ginza_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_HANZOMON)) {
			array_str = res
					.getStringArray(R.array.hanzomon_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_HIBIYA)) {
			array_str = res.getStringArray(R.array.hibiya_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_NAMBOKU)) {
			array_str = res
					.getStringArray(R.array.namboku_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_TOZAI)) {
			array_str = res.getStringArray(R.array.tozai_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_YURAKUCHO)) {
			array_str = res
					.getStringArray(R.array.yurakucho_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_MARUNOUCHI)) {
			array_str = res
					.getStringArray(R.array.marunouchi_railway_stations_api);
			return Arrays.asList(array_str);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_MARUNOUCHI_M)) {
			array_str = res
					.getStringArray(R.array.marunouchi_branch_railway_stations_api);
			return Arrays.asList(array_str);
		}

		return null;
	}

	/**
	 * 駅のアイコンリストを取得する
	 * 
	 * @param context
	 * @param railwayCode
	 * @return
	 */
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

		if (railwayCode.equals(Railways.RAILWAY_CODE_TOZAI)) {
			images = res.obtainTypedArray(R.array.tozai_railway_icons);
			return convertTypedArray(images);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_YURAKUCHO)) {
			images = res.obtainTypedArray(R.array.yurakucho_railway_icons);
			return convertTypedArray(images);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_MARUNOUCHI)) {
			images = res.obtainTypedArray(R.array.marunouchi_railway_icons);
			return convertTypedArray(images);
		}

		if (railwayCode.equals(Railways.RAILWAY_CODE_MARUNOUCHI_M)) {
			images = res
					.obtainTypedArray(R.array.marunouchi_branch_railway_icons);
			return convertTypedArray(images);
		}

		return null;
	}

	/**
	 * array.xmlからアイコン取得
	 * 
	 * @param array
	 * @return
	 */
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

	/**
	 * 路線から２方向を取得
	 * 
	 * @param context
	 * @param railway
	 * @return
	 */
	public static String[] getDirection(final Context context,
			final String railway) {
		if (context == null || Utilities.isInvalidStr(railway)) {
			return null;
		}

		final Resources res = context.getResources();
		final String[] directions = new String[4];
		if (railway.equals(res.getString(R.string.railway_chiyoda))) {
			directions[0] = res.getString(R.string.direction_chiyoda_1);
			directions[1] = res.getString(R.string.direction_chiyoda_2);
			directions[2] = res.getString(R.string.station_ayase);
			directions[3] = res.getString(R.string.station_yoyogiuehara);
		} else if (railway.equals(res.getString(R.string.railway_fukutoshin))) {
			directions[0] = res.getString(R.string.direction_fukutoshin_1);
			directions[1] = res.getString(R.string.direction_fukutoshin_2);
			directions[2] = res.getString(R.string.station_wakoshi);
			directions[3] = res.getString(R.string.station_shibuya);
		} else if (railway.equals(res.getString(R.string.railway_ginza))) {
			directions[0] = res.getString(R.string.direction_ginza_1);
			directions[1] = res.getString(R.string.direction_ginza_2);
			directions[2] = res.getString(R.string.station_asakusa);
			directions[3] = res.getString(R.string.station_shibuya);
		} else if (railway.equals(res.getString(R.string.railway_hanzomon))) {
			directions[0] = res.getString(R.string.direction_hanzomon_1);
			directions[1] = res.getString(R.string.direction_hanzomon_2);
			directions[2] = res.getString(R.string.station_shibuya);
			directions[3] = res.getString(R.string.station_oshiage);
		} else if (railway.equals(res.getString(R.string.railway_hibiya))) {
			directions[0] = res.getString(R.string.direction_hibiya_1);
			directions[1] = res.getString(R.string.direction_hibiya_2);
			directions[2] = res.getString(R.string.station_kitasenju);
			directions[3] = res.getString(R.string.station_nakameguro);
		} else if (railway.equals(res.getString(R.string.railway_marunouchi))) {
			directions[0] = res.getString(R.string.direction_marunouchi_1);
			directions[1] = res.getString(R.string.direction_marunouchi_2);
			directions[2] = res.getString(R.string.station_ikebukuro);
			directions[3] = res.getString(R.string.station_ogikubo);
		} else if (railway.equals(res.getString(R.string.railway_marunouchi_m))) {
			directions[0] = res
					.getString(R.string.direction_marunouchi_branch_1);
			directions[1] = res
					.getString(R.string.direction_marunouchi_branch_2);
			directions[2] = res.getString(R.string.station_nakanosakaue);
			directions[3] = res.getString(R.string.station_hounanncho);
		} else if (railway.equals(res.getString(R.string.railway_namboku))) {
			directions[0] = res.getString(R.string.direction_namboku_1);
			directions[1] = res.getString(R.string.direction_namboku_2);
			directions[2] = res.getString(R.string.station_meguro);
			directions[3] = res.getString(R.string.station_akabaneiwabuchi);
		} else if (railway.equals(res.getString(R.string.railway_tozai))) {
			directions[0] = res.getString(R.string.direction_tozai_1);
			directions[1] = res.getString(R.string.direction_tozai_2);
			directions[2] = res.getString(R.string.station_nakano);
			directions[3] = res.getString(R.string.station_nishifunabashi);
		} else if (railway.equals(res.getString(R.string.railway_yurakucho))) {
			directions[0] = res.getString(R.string.direction_yurakucho_1);
			directions[1] = res.getString(R.string.direction_yurakucho_2);
			directions[2] = res.getString(R.string.station_wakoshi);
			directions[3] = res.getString(R.string.station_shinkiba);
		}

		return directions;
	}
}
