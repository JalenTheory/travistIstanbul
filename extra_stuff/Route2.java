package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PathDashPathEffect.Style;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.maps.MapView;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.Path;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

public class Route2 {
	private static final int ROUTE_TYPE_CAR = 1;
	private static final int ROUTE_TYPE_WALK = 2;
	private static final int ROUTE_TYPE_BIKE = 3;
	private boolean prepareInProgress = false;
	private ArrayItemizedOverlay arrayItemizedOverlay;
	private GraphHopper hopper;
	private Context mContext;
	private MapView mapView;
	private int routeType;

	Route2(Context rContext, MapView rMapView, 
			ArrayItemizedOverlay rAIO) {
		
		mContext = rContext;
		mapView = rMapView;
		arrayItemizedOverlay = rAIO;
		routeType = ROUTE_TYPE_WALK; //default routeType
		initGraphhopper(routeType);

	}

	private void initGraphhopper(int routeType2) {
		hopper = new GraphHopper().forMobile();
		hopper.setInMemory(true);
		
		// use file thingie from other class
		hopper.setOSMFile(null);
		// file from input stream?
		hopper.setGraphHopperLocation("/sdcard");
		hopper.setCHShortcuts("fastest");
		hopper.load("");
		
	}

	public void testRoute() {

	}

	/** 
	 * TODO 
	 * class to be thrown back to Offline map. 
	 * Fragment manager handles the toast
	 * @param str
	 */
	//private void logUser(String str) {
	//	Toast.makeText(context, str, Toast.LENGTH_LONG).show();
	//}

	void loadGraphStorage() {
		// logUser("loading graph (" + Constants.VERSION + ") ... ");
		new GHAsyncTask<Void, Void, Path>() {
			protected Path saveDoInBackground(Void... v) throws Exception {
				GraphHopper tmpHopp = new GraphHopper().forMobile();
				tmpHopp.setCHShortcuts("fastest");
				
				File routeFile = (new AssetAdapter(mContext))
						.getFileFromAssetName("ist-gh/istanbul");
				
				tmpHopp.load("/sdcard/graphhopper/maps/istanbul-gh/istanbul");
				//log("found graph " + tmpHopp.getGraph().toString() + ", nodes:"
				//		+ tmpHopp.getGraph().getNodes());
				hopper = tmpHopp;
				return null;
			}

			protected void onPostExecute(Path o) {
				if (hasError()) {
					//logUser("An error happend while creating graph:"
						//	+ getErrorMessage());
				} else {
					//logUser("Finished loading graph. Touch to route.");
				}

				finishPrepare();
			}
		}.execute();
	}

	private void finishPrepare() {
		prepareInProgress = false;
	}

	public void calcPath(final double fromLat, final double fromLon,
			final double toLat, final double toLon) {

		Log.d("travist debut", "calculating path ...");
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
					Log.d("travist debug", "from:" + fromLat + "," + fromLon + " to:" + toLat
							+ "," + toLon + " found path with distance:"
							+ resp.getDistance() / 1000f + ", nodes:"
							+ resp.getPoints().getSize() + ", time:" + time
							+ " " + resp.getDebugInfo());
					Log.d("travist debug", "the route is " + (int) (resp.getDistance() / 100)
							/ 10f + "km long, time:" + resp.getMillis()
							/ 60000f + "min, debug:" + time);
					//getOverlayItems().add(createPolyline(resp));
					//mapView.redraw();
				} else {
					Log.d("travist debug", "Error:" + resp.getErrors());
				}
				//shortestPathRunning = false;
			}
		}.execute();
	}
	
	
    private Line createPolyline( GHResponse response )
    {
        Paint paintStroke = AndroidGraphicFactory.INSTANCE.createPaint();
        paintStroke.setStyle(Style.STROKE);
        paintStroke.setColor(Color.BLUE);
        paintStroke.setDashPathEffect(new float[]
        {
            25, 15
        });
        paintStroke.setStrokeWidth(8);

        // TODO: new mapsforge version wants an mapsforge-paint, not an android paint.
        // This doesn't seem to support transparceny
        //paintStroke.setAlpha(128);
        Polyline line = new Polyline((org.mapsforge.core.graphics.Paint) paintStroke, AndroidGraphicFactory.INSTANCE);
        List<LatLong> geoPoints = line.getLatLongs();
        PointList tmp = response.getPoints();
        for (int i = 0; i < response.getPoints().getSize(); i++)
        {
            geoPoints.add(new LatLong(tmp.getLatitude(i), tmp.getLongitude(i)));
        }

        return line;
    }
}
