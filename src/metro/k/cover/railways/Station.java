package metro.k.cover.railways;

import android.graphics.drawable.Drawable;

/**
 * 駅
 * 
 * @author kohirose
 * 
 */
public class Station {

	private String title;
	private Drawable icon;

	public Station(String title, Drawable icon) {
		this.title = title;
		this.icon = icon;
	}

	public String getTitle() {
		return this.title;
	}

	public Drawable getIcon() {
		return this.icon;
	}

}