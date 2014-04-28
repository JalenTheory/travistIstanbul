package fi.metropolia.lbs.travist.routes;

import org.mapsforge.map.android.view.MapView;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;

@SuppressLint("NewApi")
public class TestTravistMapViewAdapterFragment extends Fragment{
	
	private TravistMapViewAdapter mTravistMapViewAdapter;
	private Route route;
	
	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mTravistMapViewAdapter = TravistMapViewAdapter.getInstance();
		mTravistMapViewAdapter.attachTo(this);

		setRetainInstance(true);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.routes_map_frag, container,
				false);
		MapView mapView = (MapView) rootView.findViewById(R.id.routes_mapview);
		mTravistMapViewAdapter.set(mapView);
		
		// get first instance. For performance, let's show mapView first before
		// initializing route
		route = Route.getInstance();

		return rootView;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		mTravistMapViewAdapter.logD("context menu called ", this);
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.route_frag_menu, menu);
	}
	
	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.route_menu_from:
			mTravistMapViewAdapter.routeFrom();
			return true;
		case R.id.route_menu_to:
			mTravistMapViewAdapter.routeTo();
			return true;
		case R.id.enable_gps:
			mTravistMapViewAdapter.enableGps();
		default:
			return super.onContextItemSelected(item);
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mTravistMapViewAdapter.getLocOverLay().enableMyLocation(true);
		mTravistMapViewAdapter.getLocOverLay().setSnapToLocationEnabled(true);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		mTravistMapViewAdapter.getLocOverLay().disableMyLocation();
	}
}
