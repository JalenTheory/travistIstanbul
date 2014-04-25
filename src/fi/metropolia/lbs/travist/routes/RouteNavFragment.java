package fi.metropolia.lbs.travist.routes;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// TODO support api for fragments..
@SuppressLint("NewApi")
public class RouteNavFragment extends Fragment {
	private View mNavView;
	
	@SuppressLint("NewApi")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNavView = getActivity().findViewById(R.id.route_nav);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.routes_nav_fragment, container, false);
	}
	
	public void calculateRoute() {
		// in case route fragment needs extra functions for 
		// nav fragment. 
	}
	
	private void logD(String logText) {
		Log.d("Testing", logText);
	}

	private void logD(String logText, Object o) {
		Log.d("Testing", o.getClass().getSimpleName() + ": " + logText);
	}
	
}
