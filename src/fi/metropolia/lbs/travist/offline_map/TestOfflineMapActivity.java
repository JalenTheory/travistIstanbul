package fi.metropolia.lbs.travist.offline_map;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

public class TestOfflineMapActivity extends Activity {

	private static final int CONTENT_VIEW_ID = 10101010;

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout frame = new FrameLayout(this);
		frame.setId(CONTENT_VIEW_ID);
		setContentView(frame, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		if (savedInstanceState == null) {
	        AndroidGraphicFactory.createInstance(getApplication());
			Fragment newFragment = new TestOfflineMapFragment();
			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.add(CONTENT_VIEW_ID, newFragment).commit();
		}
	}
}
