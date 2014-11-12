package metro.k.cover.traininfo;

import java.util.ArrayList;
import java.util.EventListener;

import metro.k.cover.lock.TrainInfo;

public interface TrainInfoListener extends EventListener {
	public void completeCreateTimeTable(ArrayList<TrainInfo> timetable);
	public void failedToCreateTimeTable();
}