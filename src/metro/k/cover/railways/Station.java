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
	private String name_for_api;

	public Station(String railway, String title, Drawable icon,
			String nameForAPI) {
		this.title = title;
		this.icon = icon;
		this.railway = railway;
		this.name_for_api = nameForAPI;
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

	public String getNameForAPI() {
		return name_for_api;
	}

}