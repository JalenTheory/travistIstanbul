package fi.metropolia.lbs.travist.offline_map.routes;

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
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.TextView;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.Path;
import com.graphhopper.util.Constants;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import fi.metropolia.lbs.travist.foursquare_api.Criteria;
import fi.metropolia.lbs.travist.foursquare_api.DownloadJSON;
import fi.metropolia.lbs.travist.foursquare_api.FourSquareQuery;
import fi.metropolia.lbs.travist.foursquare_api.Place;
import fi.metropolia.lbs.travist.offline_map.DanielMarker;
import fi.metropolia.lbs.travist.offline_map.GHAsyncTask;

@SuppressLint("NewApi")
public class TestRoutesFragment extends Fragment implements
		fi.metropolia.lbs.travist.foursquare_api.AsyncFinished {

	private MapView mapView;
	private TileCache tileCache;
	private GraphHopperAPI hopperApi;
	private MapViewPosition mapViewPosition;
	private DanielMarker tempMarker;
	private boolean check = false;
	private LatLong tempLatLong;
	private LatLong from;
	private LatLong to;
	private LayerManager layerManager;
	private MyLocationOverlay myLocationOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		logD("context menu called ", this);
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getActivity().getMenuInflater();
		inflater.inflate(R.menu.route_frag_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.route_menu_from:
			// TODO use TravistMapViewAdapter
			routeFrom();
			return true;
		case R.id.route_menu_to:
			// TODO use TravistMapViewAdapter
			routeTo();
			return true;
		case R.id.enable_gps:
			// TODO use TravistMapViewAdapter ?
			enableGps();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	public void routeFrom() {
		logD("route from..", this);
		from = tempLatLong;
	}

	public void routeTo() {
		logD("route to..", this);
		if (from != null) {
			calcPath(from.latitude, from.longitude, tempLatLong.latitude,
					tempLatLong.longitude);
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

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.routes_map_frag, container,
				false);

		mapView = (MapView) rootView.findViewById(R.id.routes_mapview);
		// TODO set mapview to adapter
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
		myLocationOverlay = new MyLocationOverlay(getActivity(),
				mapViewPosition, null);

		tileCache = AndroidUtil.createTileCache(getActivity(), getClass()
				.getSimpleName(),
				mapView.getModel().displayModel.getTileSize(), 1f, mapView
						.getModel().frameBufferModel.getOverdrawFactor());

		loadMap();

		// on long click brings up contextual menu
		registerForContextMenu(mapView);

		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			mapView.getLayerManager().getLayers().add(myLocationOverlay);
			loadGraphStorage();
		}
		// testInitialZoom();
		/*
		 * // on long click brings up contextual menu
		 * registerForContextMenu(mapView);
		 */
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(41.0426483, 28.950041908777372),
				(byte) 7);
	}

	public TestRoutesFragment() {
		super();
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

	// From graphhopper example
	private void loadMap() {
		logD("Loading Map");
		// File mapFile = new
		// File("/sdcard/graphhopper/maps/istanbul-gh/istanbul.map");

		File mapFile = new File(TravistMapViewAdapter.getInstance()
				.getContext().getFilesDir(), "istanbul-gh/istanbul.map");

		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {
			// room for code.
			// this had unfunctional onTap listeners

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
		Drawable markerIcon = getResources().getDrawable(R.drawable.flag_green);
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
						TextView bubbleView = new TextView(getActivity());
						setBackground(bubbleView,
								getResources().getDrawable(R.drawable.bubble));
						bubbleView.setGravity(Gravity.CENTER);
						bubbleView.setMaxEms(50);
						bubbleView.setTextSize(30);
						bubbleView.setText(place.getCategoryName());
						Bitmap bubble = viewToBitmap(getActivity(), bubbleView);
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
				getActivity().openContextMenu(mapView);
				return true;
			}
		};
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
				GHResponse resp = hopperApi.route(req);
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
				File temp = new File(TravistMapViewAdapter.getInstance()
						.getContext().getFilesDir(), "istanbul-gh");
				tmpHopp.load(temp.getAbsolutePath());
				logD("found graph " + tmpHopp.getGraph().toString()
						+ ", nodes:" + tmpHopp.getGraph().getNodes());
				hopperApi = tmpHopp;
				return null;
			}

			protected void onPostExecute(Path o) {
				if (hasError()) {
					logD("An error happend while creating graph:"
							+ getErrorMessage());
				} else {
					logD("Finished loading graph. Touch to route.");
					callPath(); // Calc distance between to locations

					// Do this in drawernavigation
					Criteria crit = new Criteria();
					crit.setNear("istanbul");
					crit.setLimit("30");
					// crit.setlatlong?
					crit.setCategoryId(Criteria.ARTS_AND_ENTERTAIMENT);

					FourSquareQuery fq = new FourSquareQuery();
					String url = fq.createQuery(crit);
					Log.d("Main", "Main: " + url);

					DownloadJSON dlJSON = new DownloadJSON(
							TestRoutesFragment.this);
					dlJSON.startDownload(url);
				}
			}
		}.execute();
	}

	private void callPath() {
		calcPath(41.01384, 28.949659999999994, 41.0426483, 28.950041908777372);
	}

	private void logD(String logText) {
		Log.d("Testing", logText);
	}

	private void logD(String logText, Object o) {
		Log.d("Testing", o.getClass().getSimpleName() + ": " + logText);
	}

	// Called when category is downloaded and parsed into arraylist
	@Override
	public void downloadFinish(ArrayList<Place> places) {
		for (int i = 0; i < places.size(); i++) {
			Layers layers = mapView.getLayerManager().getLayers();

			Marker marker1 = addMarker(
					new LatLong(
							Double.parseDouble(places.get(i).getLatitude()),
							Double.parseDouble(places.get(i).getLongitude())),
					places.get(i));
			layers.add(marker1);
		}
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

	// From mapsforge 0.4 examples
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable background) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			view.setBackground(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}
}
