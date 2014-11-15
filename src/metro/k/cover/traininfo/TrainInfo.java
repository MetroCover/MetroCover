package metro.k.cover.traininfo;

public class TrainInfo {

	private int mHour;
	private int mMinute;
	private String mDestinationStation = "";
	private String mTrainType = "";
	private String mDirection = "";

	public TrainInfo(int minute, int hour, String destinationStation, String trainType, String direction) {
		mHour = hour;
		mMinute = minute;
		mDestinationStation = destinationStation;
		mTrainType = trainType;
		mDirection = direction;
	}

	public int getHour() {
		return mHour;
	}

	public int getMinute() {
		return mMinute;
	}

	public String getDestinationStation() {
		return mDestinationStation;
	}

	public String getTrainType() {
		return mTrainType;
	}

	public String getDirectino() {
		return mDirection;
	}
}
