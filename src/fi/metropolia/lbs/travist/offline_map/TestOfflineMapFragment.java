package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

@SuppressLint("NewApi")
public class TestOfflineMapFragment extends Fragment implements
		fi.metropolia.lbs.travist.foursquare_api.AsyncFinished {

	private MapView mapView;	
	private TableLayout tableLayout; 
	private Button butt1;
	private Button butt2;
	private int categoriesSwitch = 0;
	private TileCache tileCache;
	private GraphHopperAPI hopper;
	private MapViewPosition mapViewPosition;
	private DanielMarker tempMarker;
	private boolean check = false;
	 String client_id = "GWA2NRBNDFBENJIZIGFF2IFX5JTDTOUYUPLHCOCOTXMF34LU";
	String client_secret = "JSI4CFI3HSMK1FPCIE4DLEDBXL321CM1SGENAX4HLXYTSCHG";
	String version = "20131016";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View rootView = inflater.inflate(R.layout.map_frag, container, false);

		mapView = (MapView) rootView.findViewById(R.id.mapView);
		mapView.setClickable(true);
		// makes a nifty ruler
		mapView.getMapScaleBar().setVisible(true);
		mapView.setBuiltInZoomControls(true);

		// don't know.
		mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

		// initializes position and zoom level
		mapViewPosition = initializePosition(mapView.getModel().mapViewPosition);

		tileCache = AndroidUtil.createTileCache(getActivity(), getClass()
					.getSimpleName(),
					mapView.getModel().displayModel.getTileSize(), 1f, 
					mapView.getModel().frameBufferModel.getOverdrawFactor());

		loadMap();
		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			loadGraphStorage();
		}
		// testInitialZoom();
		
		tableLayout= (TableLayout) rootView.findViewById(R.id.tableMarker);
		tableLayout.setVisibility(View.INVISIBLE);
		
		return rootView;
	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(41.0426483, 28.950041908777372),
				(byte) 7);
	}

	public TestOfflineMapFragment() {
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

	//From graphhopper example
	private void loadMap() {
		logD("Loading Map");
		//File mapFile = new File("/sdcard/graphhopper/maps/istanbul-gh/istanbul.map");
		File mapFile = new File(getActivity().getFilesDir(), "istanbul-gh/istanbul.map");
		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {

			@Override
			public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
				// TODO Auto-generated method stub
				if (check) {
					mapView.getLayerManager().getLayers().remove(tempMarker);
					DanielMarker marker = addMarker(tempMarker.getLatLong(),tempMarker.getPlace());
					mapView.getLayerManager().getLayers().add(marker);
					tableLayout.setVisibility(View.INVISIBLE);
					check = false;
				}

				return super.onTap(tapLatLong, layerXY, tapXY);
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
		 
	 	
		 if(place.getCategoryName().equals("Café"))
		{
				markerIcon = getResources().getDrawable(R.drawable.cafe);
		}
		else if(place.getCategoryName().equals("History Museum"))
		{
			 markerIcon = getResources().getDrawable(R.drawable.historymuseum);
		}
		else if(place.getCategoryName().equals("Museum"))
		{
				markerIcon = getResources().getDrawable(R.drawable.museum);
		}
		else if(place.getCategoryName().equals("Art Museum"))
		{
				markerIcon = getResources().getDrawable(R.drawable.artmuseum);
		}
		else if(place.getCategoryName().equals("Science Museum"))
		{
				markerIcon = getResources().getDrawable(R.drawable.sciencemuseum);
		}
		else if(place.getCategoryName().contains("Site"))
		{
				markerIcon = getResources().getDrawable(R.drawable.historicsite);
		}
		else if(place.getCategoryName().equals("Library"))
		{
			markerIcon = getResources().getDrawable(R.drawable.library);
		}
		else if(place.getCategoryName().contains("Event"))
		{
				markerIcon = getResources().getDrawable(R.drawable.eventspace);
		}
		else if(place.getCategoryName().contains("Residential"))
		{
				markerIcon = getResources().getDrawable(R.drawable.apartment);
		}
		else{
			markerIcon = getResources().getDrawable(R.drawable.flag_green);
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
						Log.w("Tapp", "The Marker was touched with onTap: " + this.getLatLong().toString());

						// From mapsforge examples
						TextView bubbleView = new TextView(getActivity());
//						LinearLayout.LayoutParams Params1 = new LinearLayout.LayoutParams(15,50);
//						bubbleView.setLayoutParams(Params1); 
						setBackground(bubbleView,getResources().getDrawable(R.drawable.infowin_marker));
						bubbleView.setGravity(Gravity.CENTER);
						bubbleView.setMaxEms(10);
						bubbleView.setTextSize(20);
						bubbleView.setMaxWidth(40);
						 
						  
						//bind foursquare data to bubbleview
						bubbleView.setText(place.getPlaceName()+"\n"+place.getCategoryName()+"\n"+place.getAddress());
						
						Bitmap bubble = viewToBitmap(getActivity(), bubbleView);
						bubble.incrementRefCount();
						
						butt1 = (Button)tableLayout.findViewById(R.id.todoButton);
						butt2 = (Button)tableLayout.findViewById(R.id.savelistButton);
						
						tableLayout.setLeft(bubbleView.getLeft());
						tableLayout.setTop(bubbleView.getTop());
							
						tableLayout.setVisibility(View.VISIBLE);
						
							
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
		};
	}

	//From graphhopper examples
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

	//From graphhopper example
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

	//From graphhopper example
	private void loadGraphStorage() {
		logD("loading graph (" + Constants.VERSION + ") ... ");
		new GHAsyncTask<Void, Void, Path>() {
			protected Path saveDoInBackground(Void... v) throws Exception {
				GraphHopper tmpHopp = new GraphHopper().forMobile();
				tmpHopp.setCHShortcuts("fastest");
				//File temp = new File("/sdcard/graphhopper/maps/istanbul");
				File temp = new File(getActivity().getFilesDir(), "istanbul");
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
		
					//Do this in drawer navigation
					Criteria crit = new Criteria();
					crit.setNear("istanbul");
					crit.setLimit("30");
					crit.setCategoryId(Criteria.MUSEUMS);

					FourSquareQuery fq = new FourSquareQuery();
					String url = fq.createQuery(crit);
					Log.d("Main", "Main: " + url);

					DownloadJSON dlJSON = new DownloadJSON(TestOfflineMapFragment.this);
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

	//Called when category is downloaded and parsed into arraylist
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

	//From mapsforge 0.4 examples
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

	//From mapsforge 0.4 examples
	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable background) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			view.setBackground(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}
}