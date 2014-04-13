package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.util.List;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.Dimension;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.util.LatLongUtils;
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
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.Path;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.util.Constants;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

public class TestOfflineMapActivity extends Activity {

	protected MapView mapView;
	protected TileCache tileCache;
	protected GraphHopperAPI hopper;
	protected MapViewPosition mapViewPosition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidGraphicFactory.createInstance(getApplication());
		mapView = new MapView(this);
		mapView.setClickable(true);
		// makes a nifty ruler
		mapView.getMapScaleBar().setVisible(true);
		mapView.setBuiltInZoomControls(true);

		// don't know.
		mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

		// initializes position and zoom level
		mapViewPosition = initializePosition(mapView.getModel().mapViewPosition);
		
		
		tileCache = AndroidUtil.createTileCache(this, getClass()
				.getSimpleName(),
				mapView.getModel().displayModel.getTileSize(), 1f, mapView
						.getModel().frameBufferModel.getOverdrawFactor());

		loadMap();
		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			Layers layers = mapView.getLayerManager().getLayers();
			Marker marker1 = addMarker(new LatLong(41.01384, 28.949659999999994));
			Marker marker2 = addMarker(new LatLong(41.0426483,
					28.950041908777372));
			layers.add(marker1);
			layers.add(marker2);
			loadGraphStorage();
		}
		//testInitialZoom();
	}

	protected MapPosition getInitialPosition() {
		// BasicMapViewer sample has something about this
		// mapsforge.map.reader.*
		// MapDatabase, MapFileInfo...
		return new MapPosition(new LatLong(41.0426483, 28.950041908777372),
				(byte) 7);
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

	private void loadMap() {
		logD("Loading Map");
		File mapFile = new File(
				"/sdcard/graphhopper/maps/istanbul-gh/istanbul.map");
		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {
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
						.getCenterPoint(), (byte) 16));

		mapView.getLayerManager().getLayers().add(tileRendererLayer);
		setContentView(mapView);
	}

	private Marker addMarker(LatLong marker) {
		logD("Adding marker");
		Drawable markerIcon = getResources()
				.getDrawable(R.drawable.dollar_sign);
		Bitmap bm = AndroidGraphicFactory.convertToBitmap(markerIcon);
		return new Marker(marker, bm, -bm.getHeight(), -bm.getWidth());
	}

	private void calcPath(final double fromLat, final double fromLon,
			final double toLat, final double toLon) {

		logD("Calculating");
		new AsyncTask<Void, Void, GHResponse>() {
			float time;

			protected GHResponse doInBackground(Void... v) {
				StopWatch sw = new StopWatch().start();
				GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon)
						.setAlgorithm("dijkstrabi")
						.setVehicle("foot")
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

	private void loadGraphStorage() {
		logD("loading graph (" + Constants.VERSION + ") ... ");
		new GHAsyncTask<Void, Void, Path>() {
			protected Path saveDoInBackground(Void... v) throws Exception {
				GraphHopper tmpHopp = new GraphHopper().forMobile();
				tmpHopp.setEncodingManager(new EncodingManager("foot"));
				tmpHopp.setCHShortcuts("fastest");
				File temp = new File("/sdcard/graphhopper/maps/istanbul");
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
					callPath();
				}
			}
		}.execute();
	}

	private void callPath() {
		calcPath(41.01384, 28.949659999999994, 41.0426483, 28.950041908777372);
	}

	protected void logD(String logText) {
		Log.d("Testing", logText);
	}
	protected void logD(String logText, Object o) {
		Log.d("Testing", o.getClass().getSimpleName() + ": " + logText);
	}
}