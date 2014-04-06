package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.util.List;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.overlay.Polyline;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
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
import com.graphhopper.util.Constants;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

public class TestOfflineMapActivity1 extends Activity {

	private MapView mapView;
	private TileCache tileCache;
	private GraphHopperAPI hopper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidGraphicFactory.createInstance(getApplication());
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		tileCache = AndroidUtil.createTileCache(this, getClass()
					.getSimpleName(),
					mapView.getModel().displayModel.getTileSize(), 1f, mapView
					.getModel().frameBufferModel.getOverdrawFactor());

		loadMap();
		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			Layers layers = mapView.getLayerManager().getLayers();
			Marker marker1 = addMarker(new LatLong(41.01384, 28.949659999999994));
			Marker marker2 = addMarker(new LatLong(41.0426483, 28.950041908777372));
			layers.add(marker1);
			layers.add(marker2);
			loadGraphStorage();
		}
	}

	private void loadMap() {
		logD("Loading Map");
		File mapFile = new File("/sdcard/Graphhopper/istanbul-gh/istanbul.map");
		logD(mapFile.getAbsolutePath().toString());
		mapView.getLayerManager().getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapView.getModel().mapViewPosition, true,
				AndroidGraphicFactory.INSTANCE) {
		};

		tileRendererLayer.setMapFile(mapFile);
		tileRendererLayer.setTextScale(1.5f);
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

		mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
						   tileRendererLayer.getMapDatabase().getMapFileInfo().boundingBox
						  .getCenterPoint(), (byte) 13));
		
		mapView.getLayerManager().getLayers().add(tileRendererLayer);
		setContentView(mapView);
	}

	private Marker addMarker(LatLong marker) {
		logD("Adding marker");
		Drawable markerIcon = getResources().getDrawable(R.drawable.dollar_sign);
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

					mapView.getLayerManager().getLayers().add(createPolyline(resp));
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
	
    private void loadGraphStorage()
    {
        logD("loading graph (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>()
        {
            protected Path saveDoInBackground( Void... v ) throws Exception
            {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.setCHShortcuts("fastest");
                File temp = new File("/sdcard/Graphhopper/istanbul");
                tmpHopp.load(temp.getAbsolutePath());
                logD("found graph " + tmpHopp.getGraph().toString() + ", nodes:" + tmpHopp.getGraph().getNodes()); 
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute( Path o )
            {
                if (hasError())
                {
                    logD("An error happend while creating graph:"  + getErrorMessage());
                } else
                {
                    logD("Finished loading graph. Touch to route.");
                    callPath();
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
}