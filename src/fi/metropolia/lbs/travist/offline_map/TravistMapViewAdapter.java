package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.Path;
import com.graphhopper.util.Constants;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
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
	private TableLayout tableLayout;
	private GraphHopperAPI hopper;
	String client_id = "GWA2NRBNDFBENJIZIGFF2IFX5JTDTOUYUPLHCOCOTXMF34LU";
	String client_secret = "JSI4CFI3HSMK1FPCIE4DLEDBXL321CM1SGENAX4HLXYTSCHG";
	String version = "20131016";
	public static final String TEST_CATEGORY = "category_numero";
	private Button todoButton;
	private Button saveButton;
	private String uid;
	private TextView bubbleView;

	// TODO methods to work as a mediator for integration

	private boolean check = false;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO Auto-generated method stub
		this.uid = "1";
		int i = 0;
		View rootView = inflater.inflate(R.layout.map_frag, container, false);
		mapView = (MapView) rootView.findViewById(R.id.mapView);
		mapView.setClickable(true);
		// makes a nifty ruler
		mapView.getMapScaleBar().setVisible(true);
		mapView.setBuiltInZoomControls(true);
		// don't know.
		mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		mapView.getMapZoomControls().setZoomLevelMax((byte) 20);
		Log.d("LOG", "FUCK" + i++);
		// initializes position and zoom level
		mapViewPosition = initializePosition(mapView.getModel().mapViewPosition);
		Log.d("LOG", "FUCK" + i++);
		tileCache = AndroidUtil.createTileCache(fragment.getActivity(),
				getClass().getSimpleName(),
				mapView.getModel().displayModel.getTileSize(), 1f,
				mapView.getModel().frameBufferModel.getOverdrawFactor());
		Log.d("LOG", "FUCK" + i++);
		loadMap();
		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			loadGraphStorage();
		}
		// testInitialZoom();
		/*
		 * tableLayout= (TableLayout) rootView.findViewById(R.id.tableMarker);
		 * tableLayout.setVisibility(View.INVISIBLE);
		 */

		todoButton = (Button) rootView.findViewById(R.id.todoButton);
		todoButton = (Button) rootView.findViewById(R.id.saveButton);

		return rootView;
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

		mapView.setClickable(true);
		// makes a nifty ruler
		mapView.getMapScaleBar().setVisible(true);
		mapView.setBuiltInZoomControls(true);

		// don't know.
		mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

		// initializes position and zoom level
		mapViewPosition = initializePosition(mapView.getModel().mapViewPosition);

		// add location tracking to the map
		myLocationOverlay = new MyLocationOverlay(context, mapViewPosition,
				null);
		myLocationOverlay.enableMyLocation(true);
		// myLocationOverlay.setSnapToLocationEnabled(true);

		tileCache = AndroidUtil.createTileCache(context, getClass()
				.getSimpleName(),
				mapView.getModel().displayModel.getTileSize(), 1f, mapView
						.getModel().frameBufferModel.getOverdrawFactor());

		loadMap();

		// on long click brings up contextual menu
		fragment.registerForContextMenu(mapView);

		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			if (isOnline()) {
				// loadPlaces();
			}
			// callPath(); // test routes
		}
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

		mvp.setZoomLevelMax((byte) 24);
		mvp.setZoomLevelMin((byte) 7);

		return mvp;
	}

	private void loadPlaces() {
		// TODO Do this in drawernavigation
		// This is for testing purposes / hardcoded criteria
		LatLong lng = myLocationOverlay.getPosition();

		Criteria crit = new Criteria();
		crit.setNear("istanbul");
		crit.setLimit("30");
		crit.setCategoryId(Criteria.ARTS_AND_ENTERTAIMENT);
		Log.d("LOG", "LatLong, " + lng);

		FourSquareQuery fq = new FourSquareQuery();
		String url = fq.createQuery(crit);
		Log.d("Main", "Main: " + url);
		downloadJson(url);
	}

	/**
	 * Loads places depending on its given type (Criteria)
	 * 
	 * @param criteria
	 */
	public void loadPlaces(Criteria criteria) {
		FourSquareQuery fq = new FourSquareQuery();
		String url = fq.createQuery(criteria);
		Log.d("Main", "Main: " + url);
		downloadJson(url);
	}

	private void downloadJson(String url) {
		// fragment needs to implement AsyncFinished
		DownloadJSON dlJSON = new DownloadJSON(TravistMapViewAdapter.this);
		dlJSON.startDownload(url);
	}

	private void loadMap() {
		logD("Loading Map");
		// File mapFile = new
		// File("/sdcard/graphhopper/maps/istanbul-gh/istanbul.map");
		File mapFile = new File(fragment.getActivity().getFilesDir(),
				"istanbul-gh/istanbul.map");
		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {
			/*
			 * @Override public boolean onTap(LatLong tapLatLong, Point layerXY,
			 * Point tapXY) { // TODO Auto-generated method stub if (check) {
			 * mapView.getLayerManager().getLayers().remove(tempMarker);
			 * DanielMarker marker = addMarker(tempMarker.getLatLong(),
			 * tempMarker.getPlace());
			 * mapView.getLayerManager().getLayers().add(marker);
			 * tableLayout.setVisibility(View.INVISIBLE); check = false; }
			 * 
			 * return super.onTap(tapLatLong, layerXY, tapXY); }
			 */

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
		  
		Drawable markerIcon;
//		 try {
//			URL url = new URL(place.getIconUrl());
//			android.graphics.Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
//			markerIcon = new BitmapDrawable(getResources(), bmp);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			 markerIcon = getResources().getDrawable(R.drawable.flag_green);
//			e.printStackTrace();
//		}
		 
		if (bubbleView == null) {
			makeBubbleView();
		}
		
		 if(place.getCategoryName().equals("Cafe"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.cafe);
		}
		else if(place.getCategoryName().equals("History Museum"))
		{
			 markerIcon = fragment.getResources().getDrawable(R.drawable.historymuseum);
		}
		else if(place.getCategoryName().equals("Museum"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.museum);
		}
		else if(place.getCategoryName().equals("Art Museum"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.artmuseum);
		}
		else if(place.getCategoryName().equals("Science Museum"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.sciencemuseum);
		}
		else if(place.getCategoryName().contains("Site"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.historicsite);
		}
		else if(place.getCategoryName().equals("Library"))
		{
			markerIcon = fragment.getResources().getDrawable(R.drawable.library);
		}
		else if(place.getCategoryName().contains("Event"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.eventspace);
		}
		else if(place.getCategoryName().contains("Residential"))
		{
				markerIcon = fragment.getResources().getDrawable(R.drawable.apartment);
		}
		else{
			markerIcon = fragment.getResources().getDrawable(R.drawable.flag_green);
	 	 }
		 
		//hello
		Bitmap bm = AndroidGraphicFactory.convertToBitmap(markerIcon);

		return new DanielMarker(latLong, bm, 0, -bm.getHeight(), place) {
			@Override
			public boolean onTap(LatLong geoPoint, Point viewPosition,
					Point tapPoint) {
				if (contains(viewPosition, tapPoint)) {
					if (!check) {
						check = true;

						Layers layers = mapView.getLayerManager().getLayers();
						Log.d("LOG", "Here's tapPoint and viewPosition: " + viewPosition + ", " + tapPoint);
						 
						setTextToBubbleView(place);
						Bitmap bubble = Utils.viewToBitmap(fragment.getActivity(), bubbleView);
						bubble.incrementRefCount();
						
						DanielMarker marker = new DanielMarker(latLong, bubble,
												0, -bubble.getHeight() / 2, place);
						
						layers.add(marker);
						tempMarker = marker;
						
//						todoButton.setOnClickListener(new View.OnClickListener() {
//						    @Override
//						    public void onClick(View v) {
//						        
//						    }
//						});
//						
//						saveButton.setOnClickListener(new View.OnClickListener() {
//						    @Override
//						    public void onClick(View v) {
//						    /*	AlertDialog.Builder builder = new AlertDialog.Builder(context);
//								builder.setMessage("Add item to saved list?")
//								.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//									@Override
//									public void onClick(DialogInterface dialog, int which) {*/
//								    	 
//										String url = "http://users.metropolia.fi/~eetupa/Turkki/setSaved.php?pid="+place.getPlaceId()+"&uid="+uid;
//										 
//										UpSaved up = new UpSaved(url);
//										up.upload();
//										
//										ContentValues cv = new ContentValues();
//										cv.put(PlaceTableClass.IS_IN_SAVED, 1);
//										cv.put(PlaceTableClass.IS_IN_TODO, 0);
//										//context.getContentResolver().update(LBSContentProvider.PLACES_URI, cv, PlaceTableClass.PLACE_NAME+" = '"+pname+"'", null);
//										/*todoList.remove(groupPosition);
//										notifyDataSetChanged();
//				       					notifyDataSetInvalidated();*/
//									}
//						});
				
						
						return true;
					}
				}
				return false;
			}
		};
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
		initMapView();
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
			myLocationOverlay.enableMyLocation(false);
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
		Layers layers = mapView.getLayerManager().getLayers();
		Log.d("LOG", "places: " + places.size());
		Log.d("LOG", "placez: " + danielMarkers.size());
		Log.d("LOG", "Item Amount: " + layers.size());

		if (danielMarkers.size() >= 10) {
			deletePoisFromMap();
		}

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

		for (int i = 0; i < danielMarkers.size(); i++) {
			layers.remove(danielMarkers.get(i));
		}
	}

	// From graphhopper examples
	private void calcPath(final double fromLat, final double fromLon,
			final double toLat, final double toLon) {

		logD("Calculating");
		new AsyncTask<Void, Void, GHResponse>() {
			float time;

			protected GHResponse doInBackground(Void... v) {
				StopWatch sw = new StopWatch().start();
				GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon)
						.setAlgorithm("dijkstrabi").setVehicle("car")
						.putHint("instructions", false)
						.putHint("douglas.minprecision", 1);
				GHResponse resp = hopper.route(req);
				time = sw.stop().getSeconds();
				return resp;
			}

			protected void onPostExecute(GHResponse resp) {
				if (!resp.hasErrors()) {
					logD("from:" + fromLat + "," + fromLon + " to:" + toLat
							+ "," + toLon + " found path with distance:"
							+ resp.getDistance() / 1000f + ", nodes:"
							+ resp.getPoints().getSize() + ", time:" + time
							+ " " + resp.getDebugInfo());
					logD("the route is " + (int) (resp.getDistance() / 100)
							/ 10f + "km long, time:" + resp.getMillis()
							/ 60000f + "min, debug:" + time);

					mapView.getLayerManager().getLayers()
							.add(createPolyline(resp));
				} else {
					logD("Error:" + resp.getErrors());
				}
			}
		}.execute();
	}

	// From graphhopper example
	private Polyline createPolyline(GHResponse response) {
		logD("Polyline");
		Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
		paintStroke.setStyle(Style.STROKE);
		paintStroke.setColor(Color.BLUE);
		paintStroke.setDashPathEffect(new float[] { 25, 15 });
		paintStroke.setStrokeWidth(8);

		Polyline line = new Polyline(
				(org.mapsforge.core.graphics.Paint) paintStroke,
				AndroidGraphicFactory.INSTANCE);
		List<LatLong> geoPoints = line.getLatLongs();
		PointList tmp = response.getPoints();
		for (int i = 0; i < response.getPoints().getSize(); i++) {
			geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));
		}
		return line;
	}

	// From graphhopper example
	private void loadGraphStorage() {
		logD("loading graph (" + Constants.VERSION + ") ... ");
		new GHAsyncTask<Void, Void, Path>() {
			protected Path saveDoInBackground(Void... v) throws Exception {
				GraphHopper tmpHopp = new GraphHopper().forMobile();
				tmpHopp.setCHShortcuts("fastest");
				// File temp = new File("/sdcard/graphhopper/maps/istanbul");
				File temp = new File(fragment.getActivity().getFilesDir(),
						"istanbul");
				tmpHopp.load(temp.getAbsolutePath());
				logD("found graph " + tmpHopp.getGraph().toString()
						+ ", nodes:" + tmpHopp.getGraph().getNodes());
				hopper = tmpHopp;
				return null;
			}

			protected void onPostExecute(Path o) {
				if (hasError()) {
					logD("An error happend while creating graph:"
							+ getErrorMessage());
				} else {
					logD("Finished loading graph. Touch to route.");
					// callPath(); //Calc distance between to locations

					// Do this in drawer navigation
					Criteria crit = new Criteria();
					crit.setNear("istanbul");
					crit.setLimit("30");
					crit.setCategoryId(Criteria.FOOD);

					FourSquareQuery fq = new FourSquareQuery();
					String url = fq.createQuery(crit);
					Log.d("Main", "Main: " + url);

					// DownloadJSON dlJSON = new
					// DownloadJSON(TravistMapFragment.this);
					// dlJSON.startDownload(url);
				}
			}
		}.execute();
	}

	// From mapsforge 0.4 examples
	static Bitmap viewToBitmap(Context c, View view) {
		view.measure(MeasureSpec.getSize(view.getMeasuredWidth()),
				MeasureSpec.getSize(view.getMeasuredHeight()));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Drawable drawable = new BitmapDrawable(c.getResources(),
				android.graphics.Bitmap.createBitmap(view.getDrawingCache()));
		view.setDrawingCacheEnabled(false);
		return AndroidGraphicFactory.convertToBitmap(drawable);
	}

}
