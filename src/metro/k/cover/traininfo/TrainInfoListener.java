package metro.k.cover.traininfo;

import java.util.ArrayList;
import java.util.EventListener;

public interface TrainInfoListener extends EventListener {
	public void completeCreateTimeTable(ArrayList<TrainInfo> timetable);
	public void failedToCreateTimeTable();
}