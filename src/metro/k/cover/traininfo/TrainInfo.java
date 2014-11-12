package metro.k.cover.traininfo;

public class TrainInfo {

	private int mHour;
	private int mMinute;
	private String mDestinationStation = "";
	private String mTrainType = "";

	public TrainInfo(int minute, int hour, String destinationStation, String trainType) {
		mHour = hour;
		mMinute = minute;
		mDestinationStation = destinationStation;
		mTrainType = trainType;
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
}
