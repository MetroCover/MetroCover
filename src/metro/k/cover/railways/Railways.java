package metro.k.cover.railways;

import android.graphics.drawable.Drawable;

/**
 * 路線
 * 
 * @author kohirose
 * 
 */
public class Railways {

	// 路線ナンバー
	public static final int RAILWAY_NUM_CHIYODA = 0;
	public static final int RAILWAY_NUM_FUKUTOSHIN = 1;
	public static final int RAILWAY_NUM_GINZA = 2;
	public static final int RAILWAY_NUM_HANZOMON = 3;
	public static final int RAILWAY_NUM_HIBIYA = 4;
	public static final int RAILWAY_NUM_MARUNOUCHI = 5;
	public static final int RAILWAY_NUM_NAMBOKU = 6;
	public static final int RAILWAY_NUM_TOZAI = 7;
	public static final int RAILWAY_NUM_YURAKUCHO = 8;

	// 路線コード
	public static final String RAILWAY_CODE_CHIYODA = "C";
	public static final String RAILWAY_CODE_FUKUTOSHIN = "F";
	public static final String RAILWAY_CODE_GINZA = "G";
	public static final String RAILWAY_CODE_HANZOMON = "Z";
	public static final String RAILWAY_CODE_HIBIYA = "H";
	public static final String RAILWAY_CODE_MARUNOUCHI = "M";
	public static final String RAILWAY_CODE_NAMBOKU = "N";
	public static final String RAILWAY_CODE_TOZAI = "T";
	public static final String RAILWAY_CODE_YURAKUCHO = "Y";
	
	private int railwayNum;
	private String code;
	private String title;
	private Drawable icon;
	private boolean isCheck;

	public Railways(int num, String stringID, String title, Drawable icon,
			boolean ischeck) {
		this.railwayNum = num;
		this.code = stringID;
		this.title = title;
		this.icon = icon;
		this.isCheck = ischeck;
	}

	public int getRailwayNumber() {
		return railwayNum;
	}

	public String getCode() {
		return this.code;
	}

	public String getTitle() {
		return this.title;
	}

	public Drawable getIcon() {
		return this.icon;
	}

	public void setChecked(final boolean isChecked) {
		this.isCheck = isChecked;
	}

	public boolean getChecked() {
		return this.isCheck;
	}
}