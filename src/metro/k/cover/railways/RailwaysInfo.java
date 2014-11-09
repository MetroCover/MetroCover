package metro.k.cover.railways;

/**
 * 路線運行情報
 * 
 * @author kohirose
 * 
 */
public class RailwaysInfo {

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

	public String getRailway() {
		return this.railway;
	}

	public String getMessage() {
		return this.message;
	}
}