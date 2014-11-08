package metro.k.cover.railways;

import android.graphics.drawable.Drawable;

/**
 * 駅
 * 
 * @author kohirose
 * 
 */
public class Station {

	private String railway;
	private String title;
	private Drawable icon;

	public Station(String railway, String title, Drawable icon) {
		this.title = title;
		this.icon = icon;
		this.railway = railway;
	}

	public String getTitle() {
		return this.title;
	}

	public Drawable getIcon() {
		return this.icon;
	}

	public String getRailway() {
		return railway;
	}

}