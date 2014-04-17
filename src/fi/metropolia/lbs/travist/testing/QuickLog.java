package fi.metropolia.lbs.travist.testing;

import android.util.Log;

public class QuickLog {
	private QuickLog uniqueInstance = null;
	
	private QuickLog() {}
	
	QuickLog getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new QuickLog();
		}
		
		return uniqueInstance;
	}
	
	public static void logD(String logText) {
		Log.d("Testing", logText);
	}

	public static void logD(String logText, Object o) {
		Log.d("Testing", o.getClass().getSimpleName() + ": " + logText);
	}
}
