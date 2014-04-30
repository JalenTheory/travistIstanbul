package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.util.ArrayList;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.model.common.PreferencesFacade;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import fi.metropolia.lbs.travist.foursquare_api.AsyncFinished;
import fi.metropolia.lbs.travist.foursquare_api.Criteria;
import fi.metropolia.lbs.travist.foursquare_api.DownloadJSON;
import fi.metropolia.lbs.travist.foursquare_api.FourSquareQuery;
import fi.metropolia.lbs.travist.foursquare_api.Place;
import fi.metropolia.lbs.travist.offline_map.routes.Route;

/**
 * Handles the map view. To be used from an activity or a fragment.
 * 
 * 1. Get instance ( TravistMapViewAdapter.getInstance(); ) 2. Attach to
 * activity / fragment ( attachTo(this); ) 3. Declare map view for activity /
 * fragment and set ( set( MapView ..); )
 * 
 * @author Joni Turunen, Daniel Sanchez
 * 
 */
public class TravistMapViewAdapter implements AsyncFinished {

	private static TravistMapViewAdapter uniqueInstance = null;

	private MapView mapView = null;
	private Context context = null;
	private Fragment fragment = null;
	private Activity activity = null;
	private TileCache tileCache;
	private MapViewPosition mapViewPosition;
	private DanielMarker tempMarker;
	private LatLong tempLatLong;
	private LatLong from;
	private LatLong to;
	private ArrayList<DanielMarker> danielMarkers = new ArrayList<DanielMarker>();
	private MyLocationOverlay myLocationOverlay;
	private LayerManager layerManager;
	DownloadJSON dlJSON = new DownloadJSON(TravistMapViewAdapter.this);

	// TODO methods to work as a mediator for integration

	private boolean check = false;

	// singleton design pattern
	private TravistMapViewAdapter() {
	}

	public static TravistMapViewAdapter getInstance() {

		if (uniqueInstance == null) {
			uniqueInstance = new TravistMapViewAdapter();
		}

		return uniqueInstance;
	} // singleton ends here

	// connectors. References used by
	// needs to implement asyncFinished?
	protected void attachTo(Context newContext) {
		context = newContext;
	}

	protected void attachTo(Activity newActivity) {
		activity = newActivity;
	}

	@SuppressLint("NewApi")
	public void attachTo(Fragment newFragment) {
		fragment = newFragment;

		// TODO can these be reduced to just :Context?
		activity = fragment.getActivity();
		context = activity;
	}

	@SuppressLint("NewApi")
	protected void initMapView() {
		// TODO validate the correct connection of view container and mapview
		// that is mainly declared in the calling fragment.
		// if the user fails to set things up in the right order, container
		// might refer to another fragment that where mapview is intended to be
		// put in.
		// maybe use template method, command or sth to have always the right
		// order.
		// maybe use builder to set right things. it'll be a new instance, so
		// the
		// references would be nulls at first. This is possible to check at
		// building the
		// object.
		setupMapControls();
		setupMapViewPosition();
		addLocOverLayToMap();
		createTileCacheForMap();
		loadMap();
		setupFragmentMenuThingy();
	}
	
	protected void reInitMapView() {
		setupMapControls();
		setupMapViewPosition();
		addLocOverLayToMap();
		loadMap();
		setupFragmentMenuThingy();
	}

	protected void setupMapControls() {
		mapView.setClickable(true);
		// makes a nifty ruler
		mapView.getMapScaleBar().setVisible(true);
		mapView.setBuiltInZoomControls(true);

		// don't know.
		mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		mapView.getMapZoomControls().setZoomLevelMax((byte) 20);
	}

	protected void createTileCacheForMap() {
		tileCache = AndroidUtil.createTileCache(context, getClass()
				.getSimpleName(),
				mapView.getModel().displayModel.getTileSize(), 1f, mapView
						.getModel().frameBufferModel.getOverdrawFactor());
	}

	protected void addLocOverLayToMap() {
		// add location tracking to the map
		myLocationOverlay = new MyLocationOverlay(context, mapViewPosition,
				null);
		myLocationOverlay.enableMyLocation(true);
		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			layerManager = mapView.getLayerManager();
			mapView.getLayerManager().getLayers().add(myLocationOverlay);
		}
	}

	protected void setupMapViewPosition() {
		// initializes position and zoom level
		mapViewPosition = initializePosition(mapView.getModel().mapViewPosition);
	}

	protected void setupFragmentMenuThingy() {
		// on long click brings up contextual menu
		fragment.registerForContextMenu(mapView);
	}

	// Center of drawn map at first
	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(41.0426483, 28.950041908777372),
				(byte) 7);
	}

	protected MapPosition getInitialPosition(LatLong initPos) {
		return new MapPosition(initPos, (byte) 7);
	}

	protected MapViewPosition initializePosition(MapViewPosition mvp) {
		LatLong center = mvp.getCenter();

		if (center.equals(new LatLong(0, 0))) {
			mvp.setMapPosition(this.getInitialPosition());
		}

		// This seemed to just define min and max levels
		// not sure how much it makes a difference to anything
		mvp.setZoomLevelMax((byte) 24);
		mvp.setZoomLevelMin((byte) 7);

		return mvp;
	}

	/**
	 * Loads places depending on its given type (Criteria)
	 * 
	 * @param criteria
	 */
	public void loadPlaces(Criteria criteria) {
		if (isOnline()) {
			FourSquareQuery fq = new FourSquareQuery();
			String url = fq.createQuery(criteria);
			Log.d("Main", "Main: " + url);
			downloadJson(url);
		}
	}

	private void downloadJson(String url) {
		// fragment needs to implement AsyncFinished
		dlJSON.startDownload(url);
	}

	// From graphhopper example
	protected void loadMap() {
		logD("Loading Map");

		// TODO next refactoring iteration validate if map is in mem already
		File mapFileDir = new File(context.getFilesDir(), "istanbul-gh");
		File mapFile = new File(mapFileDir, "istanbul.map");
		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {
			// room for code.
			// this had unfunctional onTap listeners
			//
			// I think the onTap listeners should be implemented on the
			// markers
			// - Joni
			@Override
			public boolean onLongPress(LatLong tapLatLong, Point thisXY,
					Point tapXY) {
				logD("long press clicked " + tapLatLong.toString(), this);

				// save coordinates and open menu for to choose from or to
				tempLatLong = tapLatLong;
				activity.openContextMenu(mapView);
				return true;
			}

		};

		tileRendererLayer.setMapFile(mapFile);
		tileRendererLayer.setTextScale(1.5f);
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

		// last value is the initial zoom level
		// 7 = shows almost whole city
		// 20 = shows one building
		// 16 = shows about 500m x 500m
		mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
				tileRendererLayer.getMapDatabase().getMapFileInfo().boundingBox
						.getCenterPoint(), (byte) 12));

		mapView.getLayerManager().getLayers().add(tileRendererLayer);
	}

	private DanielMarker addMarker(final LatLong latLong, final Place place) {
		logD("Adding marker");
		Drawable markerIcon = activity.getResources().getDrawable(
				R.drawable.flag_green);
		Bitmap bm = AndroidGraphicFactory.convertToBitmap(markerIcon);

		return new DanielMarker(latLong, bm, 0, -bm.getHeight(), place) {
			@Override
			public boolean onTap(LatLong geoPoint, Point viewPosition,
					Point tapPoint) {
				if (contains(viewPosition, tapPoint)) {
					if (!check) {
						check = true;

						Layers layers = mapView.getLayerManager().getLayers();
						Log.d("LOG", "Here's tapPoint and viewPosition: "
								+ viewPosition + ", " + tapPoint);
						Log.w("Tapp", "The Marker was touched with onTap: "
								+ this.getLatLong().toString());

						// From mapsforge examples
						TextView bubbleView = new TextView(activity);
						setBackground(bubbleView, activity.getResources()
								.getDrawable(R.drawable.bubble));
						// TODO refactor hardcoded properties into
						// res/values.xml etc.
						bubbleView.setGravity(Gravity.CENTER);
						bubbleView.setMaxEms(50);
						bubbleView.setTextSize(30);
						bubbleView.setText(place.getCategoryName());
						Bitmap bubble = Utils
								.viewToBitmap(activity, bubbleView);
						bubble.incrementRefCount();
						DanielMarker marker = new DanielMarker(latLong, bubble,
								0, -bubble.getHeight() / 2, place);
						layers.add(marker);
						tempMarker = marker;
						// DSA
						return true;
					}
				}
				return false;
			}

			@Override
			public boolean onLongPress(LatLong tapLatLong, Point thisXY,
					Point tapXY) {
				logD("long press clicked " + tapLatLong.toString(), this);

				// save coordinates and open menu for to choose from or to
				tempLatLong = tapLatLong;
				activity.openContextMenu(mapView);
				return true;
			}

			// TODO test if this was causing trouble
			/*
			 * @Override public boolean onLongPress(LatLong tapLatLong, Point
			 * thisXY, Point tapXY) { logD("long press clicked " +
			 * tapLatLong.toString(), this);
			 * 
			 * // save coordinates and open menu for to choose from or to
			 * tempLatLong = tapLatLong; activity.openContextMenu(mapView);
			 * return true; }
			 */
		};
	}

	private boolean isOnline() {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			return cm.getActiveNetworkInfo().isConnectedOrConnecting();
		} catch (Exception e) {
			return false;
		}
	}

	// From mapsforge 0.4 examples
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable background) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			view.setBackground(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}

	private void callPath() {
		Route.getInstance().calcPath(41.01384, 28.949659999999994, 41.0426483,
				28.950041908777372);
	}

	public void set(MapView newMapView) {
		mapView = newMapView;
	}

	public void routeFrom() {
		logD("route from..", this);
		from = tempLatLong;
	}

	public void routeTo() {
		logD("route to..", this);
		if (from != null) {
			Route.getInstance().calcPath(from.latitude, from.longitude,
					tempLatLong.latitude, tempLatLong.longitude);
		}
	}

	public void enableGps() {
		logD("gps pressed..", this);
		if (!myLocationOverlay.isMyLocationEnabled()) {
			myLocationOverlay.enableMyLocation(true);
			myLocationOverlay.setSnapToLocationEnabled(true);
		} else {
			myLocationOverlay.enableMyLocation(true);
			myLocationOverlay.setSnapToLocationEnabled(false);
		}
	}

	public Context getContext() {
		return context != null ? context : null;
	}

	public MapView getMapView() {
		return mapView;
	}

	protected MyLocationOverlay getLocOverLay() {
		return myLocationOverlay;
	}

	// for quick and dirty logging
	protected void logD(String logText) {
		Log.d("Testing", logText);
	}

	public void logD(String logText, Object o) {
		Log.d("Testing", o.getClass().getSimpleName() + ": " + logText);
	}

	@Override
	public void downloadFinish(ArrayList<Place> places) {
		deletePoisFromMap();
		Layers layers = mapView.getLayerManager().getLayers();
		Log.d("LOG", "places: " + places.size());
		Log.d("LOG", "placez: " + danielMarkers.size());
		Log.d("LOG", "Item Amount: " + layers.size());

		for (int i = 0; i < places.size(); i++) {

			DanielMarker marker1 = addMarker(
					new LatLong(
							Double.parseDouble(places.get(i).getLatitude()),
							Double.parseDouble(places.get(i).getLongitude())),
					places.get(i));
			layers.add(marker1);

			danielMarkers.add(marker1);
		}
	}

	public void deletePoisFromMap() {
		Layers layers = mapView.getLayerManager().getLayers();
		Log.d("LOG", "deletePlacez size: " + danielMarkers.size());
		if (danielMarkers.size() > 15) {
			for (int i = 0; i < danielMarkers.size(); i++) {
				layers.remove(danielMarkers.get(i));
			}
			danielMarkers.clear();
		}
	}

	// MapsForge examples
	protected void destroyLayers() {
		for (Layer layer : layerManager.getLayers()) {
			layerManager.getLayers().remove(layer);
			layer.onDestroy();
		}
	}

	protected void destroyMapViewPositions() {
		mapViewPosition.destroy();
	}

	protected void destroyMapViews() {
		mapView.destroy();
	}

	protected void destroyTileCaches() {
		this.tileCache.destroy();
	}
}
