package metro.k.cover.railways;

import android.graphics.drawable.Drawable;

/**
 * 路線運行情報
 * 
 * @author kohirose
 * 
 */
public class RailwaysInfo {

	private Drawable icon;
	private String railway;
	private String message;

	public RailwaysInfo() {
	}

	public void setRailway(final String railway) {
		this.railway = railway;
	}

	public void setMessage(final String msg) {
		this.message = msg;
	}

	public void setIcon(final Drawable d) {
		this.icon = d;
	}

	public String getRailway() {
		return this.railway;
	}

	public String getMessage() {
		return this.message;
	}

	public Drawable getIcon() {
		return icon;
	}
}