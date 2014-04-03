package fi.metropolia.lbs.travist.offline_map;

import java.io.File;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layer;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.slf4j.Marker;

import travist.pack.R;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.util.StopWatch;

import fi.metropolia.lbs.travist.TravistIstanbulActivity;

public class TestOfflineMapActivity<TestOfflineMapActivity> extends Activity {
	private MapView mapView;
	private String TAG;
    private LatLong start;
    private LatLong end;
 	private volatile boolean prepareInProgress = false;
	private volatile boolean shortestPathRunning = false;
	private GraphHopperAPI hopper;

	// used for POIs
	// ArrayItemizedOverlay itemizedOverlay; // works for mapsforge 0.3.x
	Drawable defaultMarker;

	// GraphHopper
	private Route route;
	private SimpleOnGestureListener gestureListener;
	private GestureDetector gestureDetector;
	private boolean debugging = false;

    protected boolean onMapTap( LatLong tapLatLong, Point layerXY, Point tapXY )
    {
        //if (!isReady())
          //  return false;

        if (shortestPathRunning)
        {
            // logUser("Calculation still in progress");
            return false;
        }
        Layers layers = mapView.getLayerManager().getLayers();

        if (start != null && end == null)
        {
            end = tapLatLong;
            shortestPathRunning = true;
            Marker marker = createMarker(tapLatLong, R.drawable.flag_red);
            if (marker != null)
            {
                layers.add((Layer) marker);
            }

            calcPath(start.latitude, start.longitude, end.latitude,
                    end.longitude);
        } else
        {
            start = tapLatLong;
            end = null;
            // remove all layers but the first one, which is the map
            while (layers.size() > 1)
            {
                layers.remove(1);
            }

            Marker marker = createMarker(start, R.drawable.flag_green);
            if (marker != null)
            {
                layers.add((Layer) marker);
            }
        }
        return true;
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
				if (!resp.hasErrors() && debugging) {
					Log.d("travist debug", "from:" + fromLat + "," + fromLon
							+ " to:" + toLat + "," + toLon
							+ " found path with distance:" + resp.getDistance()
							/ 1000f + ", nodes:" + resp.getPoints().getSize()
							+ ", time:" + time + " " + resp.getDebugInfo());
					Log.d("travist debug",
							"the route is " + (int) (resp.getDistance() / 100)
									/ 10f + "km long, time:" + resp.getMillis()
									/ 60000f + "min, debug:" + time);
					//getOverlayItems().add(route.createPolyline(resp));
					//mapView.redraw();
				} else {
					Log.d("travist debug", "Error:" + resp.getErrors());
				}
				shortestPathRunning = false;
			}
		}.execute();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (debugging) {
			initDebugging();
		}

		defaultMarker = getResources().getDrawable(R.drawable.ic_launcher);
		// Shows POIs
		//itemizedOverlay = new ArrayItemizedOverlay(defaultMarker);
		//mapView.getOverlays().add(itemizedOverlay);

		initMapview();

		setContentView(mapView);
		testPOI();
		initGraphHopper();
	}

	private void initMapview() {
		// set up mapview properties
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		// Using map from assets with a Toast informing the user
		// if it's not found.
		AssetAdapter mapFileAdapter = new AssetAdapter(this);
		File mapFile = mapFileAdapter
				.getFileFromAssetName("istanbul-gh/istanbul.map");
		if (!mapFile.exists()) {
			Toast.makeText(this, "FFffffuuuu - no file", Toast.LENGTH_LONG)
					.show();
		}
		FileOpenResult fileOpenResult = mapView.setMapFile(mapFile);
		if (!fileOpenResult.isSuccess()) {
			Toast.makeText(this, fileOpenResult.getErrorMessage(),
					Toast.LENGTH_LONG).show();

			// Doesn't really work. enough for the time being
			goBack();
		}

		if (debugging) {
			Log.d(TAG, getClass().getSimpleName()
					+ ": setting map file to map view");
		}
	}

	private void initDebugging() {
		// logger tag
		TAG = getResources().getString(R.string.debug_tag);
		Log.d(TAG, getClass().getSimpleName() + ": onCreate()");

		debugging = true;
	}

	private void initGraphHopper() {
		route = new Route(this, mapView, itemizedOverlay);
		// GraphHopper specific listeners
		gestureListener = new OfflineMapSimpleOnGestureListener();
		gestureDetector = new GestureDetector(gestureListener);
	}

	public void testPOI() {
		// Check coordinates on openStreetMap.org.
		// lat and lng need to be in the map area (istanbul)
		double lat = 41.0096334;
		double lng = 28.9651646;
		String title = "testing site";
		String description = "Something to do here. Not sure what";
		createPOI(lat, lng, title, description);
	}

	public void createPOI(double lat, double lng, String title,
			String description) {
		GeoPoint gp = new GeoPoint(lat, lng);
		OverlayItem item = new OverlayItem(gp, title, description);
		itemizedOverlay.addItem(item);
		mapView.refreshDrawableState();
	}

	private void goBack() {
		Log.d(TAG, getClass().getSimpleName() + ": goBack()");
		// Supposed to take the user back to the main screen
		// TODO use back stack or startactivityForResult
		startActivity(new Intent(this, TravistIstanbulActivity.class));
	}

	private class OfflineMapSimpleOnGestureListener extends
			SimpleOnGestureListener {
		// why does this fail? public boolean onDoubleTap(MotionEvent e) {};
		public boolean onSingleTapConfirmed(MotionEvent motionEvent) {

			float x = motionEvent.getX();
			float y = motionEvent.getY();
			Projection p = mapView.getProjection();
			GeoPoint tmpPoint = p.fromPixels((int) x, (int) y);

			Log.d("Travist debug", "Clicked map");

			if (start != null && end == null) {
				end = tmpPoint;
				shortestPathRunning = true;

				Marker marker = createMarker(tmpPoint, R.drawable.flag_red);
				if (markerItem != null) {
					routesOverlay.addItem(markerItem);
					mapView.refreshDrawableState();
				}

				calcPath(start.getLatitude(), start.getLongitude(),
						end.getLatitude(), end.getLongitude());
			} else {
				start = tmpPoint;
				end = null;
				routesOverlay.clear();
				// Marker marker = createMarker(start, R.drawable.flag_green);
				OverlayItem markerItem = new OverlayItem(tmpPoint, "start", "",
						getResources().getDrawable(R.drawable.flag_green));
				if (markerItem != null) {
					routesOverlay.addItem(markerItem);
					mapView.refreshDrawableState();
				}
			}
			return true;
		}

	}

	private Marker createMarker(LatLong p, int resource) {
		Drawable drawable = getResources().getDrawable(resource);
		Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
		return new Marker(p, bitmap, -bitmap.getHeight(),
				-bitmap.getWidth() / 2);
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
