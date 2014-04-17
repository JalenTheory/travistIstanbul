package fi.metropolia.lbs.travist.routes;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import fi.metropolia.lbs.travist.testing.QuickLog;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class TestRoutesActivity extends Activity {
	private final Fragment mRoutesFragment = new TestRoutesFragment();

	@SuppressLint("NewApi")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.routes_activity);

		if (savedInstanceState == null) {
			FragmentTransaction fragmentTransaction = getFragmentManager()
					.beginTransaction();
			Log.d("Testing", "nav fragment");
			fragmentTransaction.add(R.id.route_nav, new RouteNavFragment());
			Log.d("Testing", "map fragment");
			fragmentTransaction.add(R.id.test_route_mapview, mRoutesFragment);
			fragmentTransaction.commit();

			AndroidGraphicFactory.createInstance(getApplication());
		}

	}

}
