package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

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
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode.Callback;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;
import fi.metropolia.lbs.travist.foursquare_api.AsyncFinished;
import fi.metropolia.lbs.travist.foursquare_api.Criteria;
import fi.metropolia.lbs.travist.foursquare_api.DownloadJSON;
import fi.metropolia.lbs.travist.foursquare_api.FourSquareQuery;
import fi.metropolia.lbs.travist.foursquare_api.Place;
import fi.metropolia.lbs.travist.offline_map.routes.Route;
import fi.metropolia.lbs.travist.todo.UpSaved;

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
	int lastPoiLayer;
	int lastBubbleLayer;
	private MapView mapView = null;
	private Context context = null;
	private TravistMapViewAdapterFragment fragment = null;
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
	private LayerOnTapController layerOnTapController;
	private TextView bubbleView;
	private Criteria lastCriteria;
	private LatLong lastLayerTapLatLong;
	private View rootView;
	private Set<Integer> poiLayerSet;
	private Place tempPlace;
	public DanielMarker listMarker;
	
	public Place listPlace;

	// TODO methods to work as a mediator for integration

	private boolean check = false;


	private LatLong tempGeoPoint;

	// singleton design pattern
	private TravistMapViewAdapter() {
		poiLayerSet = new TreeSet<Integer>();
	}

	public static TravistMapViewAdapter getInstance() {

		if (uniqueInstance == null) {
			uniqueInstance = new TravistMapViewAdapter();
		}

		return uniqueInstance;
	} // singleton ends here
	
	public void destroy() {
		uniqueInstance = null;
	}

	// connectors. References used by
	// needs to implement asyncFinished?
	protected void attachTo(Context newContext) {
		context = newContext;
	}

	protected void attachTo(Activity newActivity) {
		activity = newActivity;
	}

	@SuppressLint("NewApi")
	public void attachTo(TravistMapViewAdapterFragment newFragment) {
		fragment = newFragment;

		// TODO can these be reduced to just :Context?
		activity = fragment.getActivity();
		context = activity;
	}
	
	public void set(View view) {
		rootView = view;
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

		Bundle bundle = fragment.getArguments();
		// Retarded way of showing the list-marker on the map
		if (bundle != null) {

			Layers layersz = mapView.getLayerManager().getLayers();
			
			String placeId = fragment.getArguments().getString("placeId");
			String placeName = fragment.getArguments().getString("placeName");
			String longitude = fragment.getArguments().getString("longi");
			String latitude = fragment.getArguments().getString("lati");
			String address = fragment.getArguments().getString("address");
			String categoryId = fragment.getArguments().getString("categoryId");
			String categoryName = fragment.getArguments().getString("categoryName");
			String iconURL = fragment.getArguments().getString("iconURL");
			
			listPlace = new Place();
			listPlace.setPlaceId(placeId);
			listPlace.setPlaceName(placeName);
			listPlace.setLongitude(longitude);
			listPlace.setLatitude(latitude);
			listPlace.setAddress(address);
			listPlace.setCategoryId(categoryId);
			listPlace.setCategoryName(categoryName);
			listPlace.setIconUrl(iconURL);
			
			listMarker = addMarker(new LatLong(Double.parseDouble(listPlace.getLatitude()), (Double.parseDouble(listPlace.getLongitude()))), listPlace);
			
			layersz.add(listMarker);			
			
		} 
		else {
			// Do nothing 'cos no list-markers to add
		}

		setupFragmentMenuThingy();
	}

	protected void reInitMapView() {
		setupMapControls();
		setupMapViewPosition();
		addLocOverLayToMap();
		loadMap();
		Bundle bundle = fragment.getArguments();
		// Retarded way of showing the list-marker on the map on reinit(Not
		// working?!)
		if (bundle != null) {
			Layers layersz = mapView.getLayerManager().getLayers();
			
			String placeId = fragment.getArguments().getString("placeId");
			String placeName = fragment.getArguments().getString("placeName");
			String longitude = fragment.getArguments().getString("longi");
			String latitude = fragment.getArguments().getString("lati");
			String address = fragment.getArguments().getString("address");
			String categoryId = fragment.getArguments().getString("categoryId");
			String categoryName = fragment.getArguments().getString("categoryName");
			String iconURL = fragment.getArguments().getString("iconURL");
			
			listPlace = new Place();
			listPlace.setPlaceId(placeId);
			listPlace.setPlaceName(placeName);
			listPlace.setLongitude(longitude);
			listPlace.setLatitude(latitude);
			listPlace.setAddress(address);
			listPlace.setCategoryId(categoryId);
			listPlace.setCategoryName(categoryName);
			listPlace.setIconUrl(iconURL);
			layersz.remove(listMarker);
			listMarker = addMarker(new LatLong(Double.parseDouble(listPlace.getLatitude()), (Double.parseDouble(listPlace.getLongitude()))), listPlace);
			layersz.add(listMarker);	
		} else {
			// Do nothing 'cos no list-markers to add
		}
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
		Drawable my_position_icon = activity.getResources().getDrawable(
				R.drawable.my_position_icon);
		Bitmap pos_bmap = AndroidGraphicFactory.convertToBitmap(my_position_icon);
		myLocationOverlay = new MyLocationOverlay(context, mapViewPosition,
				pos_bmap);
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
		lastCriteria = criteria;
		
		if (isOnline()) {
			Toast.makeText(context, "Loading attractions..", Toast.LENGTH_SHORT);
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
		// File mapFile = new
		// File("/sdcard/graphhopper/maps/istanbul-gh/istanbul.map");
		File mapFile = new File(fragment.getActivity().getFilesDir(),
				"istanbul-gh/istanbul.map");
		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		// performs the onTap depending on the state of the map
		layerOnTapController = new LayerOnTapController();
		
		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {

			@Override
			public boolean onTap(LatLong geoPoint, Point viewPosition,
					Point tapPoint) {
				
				logD("onTap clicked on TileRendererLayer");
				///etLayerMode(layerOnTapController.SELECT_ROUTE);
				lastLayerTapLatLong = geoPoint;
				layerOnTapController.execute();		
				
				return true;
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
	
	
	public LatLong getLastLayerTapPos() {
		return lastLayerTapLatLong;
	}
	
	// TileRendererLayer onTap controller. It'll change behaviour according to
	// changes
	public void setLayerMode(int state) {
		logD("set layer mode: " + state, this);
		layerOnTapController.changeState(state);
	}

	public void changeViewToSelectOrigin() {
		// some text view for testing
		logD("change view: select origin", this);
		Toast.makeText(context, "Please, select an origin point for your route", Toast.LENGTH_LONG);
		layerOnTapController.changeState(layerOnTapController.SELECT_ROUTE);
	}

	private DanielMarker addMarker(final LatLong latLong, final Place place) {
		logD("Adding marker");

		Drawable markerIcon;
		// try {
		// URL url = new URL(place.getIconUrl());
		// android.graphics.Bitmap bmp =
		// BitmapFactory.decodeStream(url.openConnection().getInputStream());
		// markerIcon = new BitmapDrawable(getResources(), bmp);
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// markerIcon = getResources().getDrawable(R.drawable.flag_green);
		// e.printStackTrace();
		// }

		if (bubbleView == null) {
			makeBubbleView();
		}

		if (place.getIconUrl().contains("entertainment")
				|| place.getIconUrl().contains("art")) {
			markerIcon = fragment.getResources().getDrawable(
					R.drawable.gimp_art);
			Log.i("raaam", "I am entertainment");
		} else if (place.getIconUrl().contains("food")
				|| place.getIconUrl().contains("drink")) {
			markerIcon = fragment.getResources().getDrawable(
					R.drawable.gimp_food);

		} else if (place.getIconUrl().contains("medical")) {
			markerIcon = fragment.getResources()
					.getDrawable(R.drawable.medical);
		} else if (place.getIconUrl().contains("nightlife")) {
			markerIcon = fragment.getResources().getDrawable(
					R.drawable.gimp_nightlife);
		} else if (place.getIconUrl().contains("shop")
				|| place.getIconUrl().contains("service")) {
			markerIcon = fragment.getResources().getDrawable(
					R.drawable.gimp_shopping);
		} else if (place.getCategoryName().contains("transport")
				|| place.getIconUrl().contains("travel")) {
			markerIcon = fragment.getResources().getDrawable(
					R.drawable.gimp_travel);
		} 
		else if (place.getIconUrl().contains("list")) {
			markerIcon = fragment.getResources().getDrawable((R.drawable.list_marker_icon));
		}
		else {
			Log.d("Hola hola", "Daniel");
			markerIcon = fragment.getResources().getDrawable(
					R.drawable.alpha_transparent);
		}

		// hello
		Bitmap bm = AndroidGraphicFactory.convertToBitmap(markerIcon);

		return new DanielMarker(latLong, bm, 0, -bm.getHeight(), place) {
			private LatLong tempPoiLatLong;
			
			@Override
			public boolean onTap(LatLong geoPoint, Point viewPosition,
					Point tapPoint) {
				if (contains(viewPosition, tapPoint)) {
					if (!check) {
						check = true;
						
						
						Log.d("LOG", "danielmarker: Here's tapPoint and viewPosition and place: "
								+ viewPosition + ", " + tapPoint + " " + place.getPlaceName());
						openBubble(place, geoPoint, latLong);
						
						// opens route and todolist add options
						activity.startActionMode((Callback) activity);
						
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
	public void openBubble(Place place, LatLong geoPoint, LatLong latLong) {
		Layers layers = mapView.getLayerManager().getLayers();
		
		tempPlace = place;
		logD("place openBubble : " + place.getPlaceName(), this);
		
		setTextToBubbleView(place);
		Bitmap bubble = Utils.viewToBitmap(
				fragment.getActivity(), bubbleView);
		bubble.incrementRefCount();

		DanielMarker bm = new DanielMarker(latLong, bubble, 0,
				-bubble.getHeight() / 2, place);
		
		layers.add(bm);
		
		int i = layers.indexOf(bm);
		logD("Addign to poilayerset " + i);
		poiLayerSet.add(layers.indexOf(bm));
		
		tempMarker = bm;
		tempGeoPoint = geoPoint;
		tempLatLong = latLong;

	}
	public void closeLastBubble() {
		if (lastBubbleLayer > 0)
			mapView.getLayerManager().getLayers().remove(lastBubbleLayer);		
	}
	public void removeLastMarkerLayer() {
		Layers layers = mapView.getLayerManager().getLayers();
		if (layers.indexOf(tempMarker) > 0 && tempMarker != null)
			layers.remove(layers.indexOf(tempMarker));
		
		//layers.add(addMarker(tempLatLong, tempPlace));
	}
	
	public void addToTodolist() {
		SharedPreferences settings = activity.getSharedPreferences("user", 0); 
		String email = settings.getString("signed_in", "");
		//add to todo to app db and server
		ContentValues poi2 = new ContentValues();
		poi2.put(PlaceTableClass.PLACE_ID, UUID.randomUUID().toString());		
		poi2.put(PlaceTableClass.PLACE_NAME, tempPlace.getPlaceName());
		poi2.put(PlaceTableClass.LATITUDE, tempPlace.getLatitude());
		poi2.put(PlaceTableClass.LONGITUDE, tempPlace.getLongitude());
		poi2.put(PlaceTableClass.ADDRESS, tempPlace.getAddress());
		poi2.put(PlaceTableClass.CATEGORY_ID, tempPlace.getCategoryId());
		poi2.put(PlaceTableClass.CATEGORY_NAME, tempPlace.getCategoryName());
		poi2.put(PlaceTableClass.IS_IN_TODO, "1");
		poi2.put(PlaceTableClass.IS_IN_SAVED, "0");
		poi2.put(PlaceTableClass.EMAIL, email);
		activity.getContentResolver().insert(LBSContentProvider.PLACES_URI, poi2);
		
		Cursor c =activity.getContentResolver().query(LBSContentProvider.PLACES_URI, 
				null, "PLACE_ID = " + tempPlace.getPlaceId(), null, null);
		logD("query : " + tempPlace.getPlaceId() + " -> " + c.getCount());
		
		class Dl extends AsyncTask<String, Void, String> {
			private String url;

			@Override
			protected void onPreExecute(){
				url = "http://users.metropolia.fi/~eetupa/Turkki/setTodo.php" +
						"?pid="/*place id here*/ +
						"&email="/*user email here (can be empty)*/;
			}
			protected String doInBackground(String... urls) {
				try {
					new UpSaved(url).upload();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(String result) {

			}
		}

	}

	private void setSavingButtonsVisible(Boolean visible) {
		// TODO set buttons visible
	}

	private void makeBubbleView() {
		// From mapsforge examples
		bubbleView = new TextView(activity);
		// LinearLayout.LayoutParams Params1 = new
		// LinearLayout.LayoutParams(15,50);
		// bubbleView.setLayoutParams(Params1);
		setBackground(bubbleView,
				fragment.getResources().getDrawable(R.drawable.infowin_marker));
		bubbleView.setGravity(Gravity.CENTER);
		bubbleView.setMaxEms(10);
		bubbleView.setTextSize(20);
		bubbleView.setMaxWidth(40);
	}

	private void setTextToBubbleView(Place place) {
		// bind foursquare data to bubbleview
		bubbleView.setText(place.getPlaceName() + "\n"
				+ place.getCategoryName() + "\n" + place.getAddress());
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

	public void routeFrom(LatLong latLong) {
		logD("route from..", this);
		from = latLong;
	}

	public void routeTo(LatLong latLong) {
		logD("route to..", this);
		if (from != null) {
			Route.getInstance().calcPath(from.latitude, from.longitude,
					latLong.latitude, latLong.longitude);
		}
	}
	
	public void refreshPois() {
		// deletePoisFromMap();
		loadPlaces(lastCriteria);
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

	public MyLocationOverlay getLocOverLay() {
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
			
			int poi_i= layers.indexOf(marker1);
			logD("Addign to poilayerset " + poi_i);
			poiLayerSet.add(layers.indexOf(marker1));

			danielMarkers.add(marker1);
		}
	}

	public void deletePoisFromMap() {
		Layers layers = mapView.getLayerManager().getLayers();
		
	
		Log.d("LOG", "deletePlacez size: " + danielMarkers.size());
		
		Layer tempLayer = layers.get(0);
		layers.clear();
		layers.add(tempLayer);

		danielMarkers.clear();
		
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

	public Fragment getFragment() {
		return this.fragment;
	}
}
