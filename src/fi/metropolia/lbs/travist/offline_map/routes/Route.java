package fi.metropolia.lbs.travist.offline_map.routes;

import java.io.File;
import java.util.List;

import org.mapsforge.core.graphics.Color;
import org.mapsforge.core.graphics.Paint;
import org.mapsforge.core.graphics.Style;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.overlay.Polyline;

import android.os.AsyncTask;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperAPI;
import com.graphhopper.routing.Path;
import com.graphhopper.util.Constants;
import com.graphhopper.util.PointList;
import com.graphhopper.util.StopWatch;

import fi.metropolia.lbs.travist.offline_map.GHAsyncTask;
import fi.metropolia.lbs.travist.offline_map.TravistMapViewAdapter;

/**
 * Calculates routes. Get instance and use calcPath(). MapView has to be instanciated and attached (check TravistMapViewAdapter).
 * 
 * @author Joni Turunen, Daniel Sanchez
 *
 */
public class Route {
	private GraphHopperAPI hopperApi;
	private MapView mapView;
	private int layerIndex = -1;
	

	private static Route uniqueInstance = null;

	// TODO get time estimation and distance for counted route

	// singleton design pattern / only one route
	private Route() {
		TravistMapViewAdapter tmvadapter = TravistMapViewAdapter.getInstance();
		
		if ((mapView = tmvadapter.getMapView()) != null) {
			loadGraphStorage();
		} else {
			 Toast.makeText(tmvadapter.getContext(), "Couldn't load graph storage for the route", Toast.LENGTH_SHORT).show();
		}
	}

	private void setContext(TravistMapViewAdapter tmvadapter) {
		// TODO Auto-generated method stub
		
	}

	public static Route getInstance() {

		if (uniqueInstance == null) {
			uniqueInstance = new Route();
		}

		return uniqueInstance;
	} // singleton ends here

	// From graphhopper examples
	public void calcPath(final double fromLat, final double fromLon,
			final double toLat, final double toLon) {

		 //Toast.makeText(TravistMapViewAdapter.getInstance().getContext(), "Calculating route...", Toast.LENGTH_SHORT).show();
		new AsyncTask<Void, Void, GHResponse>() {
			float time;

			protected GHResponse doInBackground(Void... v) {
				StopWatch sw = new StopWatch().start();
				GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon)
						.setAlgorithm("dijkstrabi").setVehicle("foot")
						.putHint("instructions", false)
						.putHint("douglas.minprecision", 1);
				GHResponse resp = hopperApi.route(req);
				time = sw.stop().getSeconds();
				return resp;
			}

			protected void onPostExecute(GHResponse resp) {
				if (!resp.hasErrors()) {
					(TravistMapViewAdapter.getInstance()).logD(
							"from:" + fromLat + "," + fromLon + " to:" + toLat
									+ "," + toLon
									+ " found path with distance:"
									+ resp.getDistance() / 1000f + ", nodes:"
									+ resp.getPoints().getSize() + ", time:"
									+ time + " " + resp.getDebugInfo(), this);
					(TravistMapViewAdapter.getInstance()).logD("the route is "
							+ (int) (resp.getDistance() / 100) / 10f
							+ "km long, time:" + resp.getMillis() / 60000f
							+ "min, debug:" + time, this);
					
					
					
					Toast.makeText(TravistMapViewAdapter.getInstance().getContext(), "Route is "
							+ String.format("%.2f", (resp.getDistance() / 1000f)) 
							+" km", Toast.LENGTH_LONG).show();

					addToLayers(createPolyline(resp));
					
				} else {
					(TravistMapViewAdapter.getInstance()).logD(
							"Error:" + resp.getErrors(), this);
				}
			}
		}.execute();
	}
	
	/**
	 * Add route polyline (layers) to MapView layers. Keeps track
	 * of routes layer index, to swap it - showing only one route
	 * at a time.
	 * 
	 * @param routePolyline
	 */
	private void addToLayers(Polyline routePolyline) {
		Layers layers = mapView.getLayerManager().getLayers();
		if (layerIndex < 0) {			
			// add layer with route and save layer index
			layers.add(routePolyline);
			layerIndex = layers.indexOf(routePolyline);
		} else {
			// clear previous route layer and add new one
			layers.remove(layerIndex);
			layers.add(layerIndex, routePolyline);
		}
		
		TravistMapViewAdapter.getInstance().logD("route layer index: " + layerIndex, this);
	} 
	

	// From graphhopper example
	private Polyline createPolyline(GHResponse response) {
		(TravistMapViewAdapter.getInstance()).logD("Polyline", this);
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
		if (!mapView.getLayerManager().getLayers().isEmpty()) {
			// TODO validate layers
		}
		(TravistMapViewAdapter.getInstance()).logD("loading graph ("
				+ Constants.VERSION + ") ... ", this);
		new GHAsyncTask<Void, Void, Path>() {
			protected Path saveDoInBackground(Void... v) throws Exception {
				GraphHopper tmpHopp = new GraphHopper().forMobile();
				tmpHopp.setCHShortcuts("fastest");
				File tempDir = new File(
						TravistMapViewAdapter.getInstance().
						getContext().getFilesDir(), "istanbul-gh");
				tmpHopp.load(tempDir.getAbsolutePath());
				(TravistMapViewAdapter.getInstance()).logD("found graph "
						+ tmpHopp.getGraph().toString() + ", nodes:"
						+ tmpHopp.getGraph().getNodes(), this);
				hopperApi = tmpHopp;
				return null;
			}

			protected void onPostExecute(Path o) {
				if (hasError()) {
					(TravistMapViewAdapter.getInstance()).logD(
							"An error happend while creating graph:"
									+ getErrorMessage(), this);
				} else {
					(TravistMapViewAdapter.getInstance()).logD(
							"Finished loading graph. Touch to route.", this);

				}
			}
		}.execute();
	}
}
