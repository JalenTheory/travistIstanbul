package fi.metropolia.lbs.travist.offline_map;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ArrayItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import travist.pack.R;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import fi.metropolia.lbs.travist.TravistIstanbulActivity;



public class TestOfflineMapActivity extends MapActivity {
	private final String TAG = "travist debug";
	
	private MapView mapView;
	
	// used for POIs
	ArrayItemizedOverlay itemizedOverlay; 
	Drawable defaultMarker;
	

	protected void onCreate( Bundle savedInstanceState) {
		Log.d(TAG, getClass().getSimpleName() + ": onCreate()");
		super.onCreate(savedInstanceState);
		
		defaultMarker = getResources()
				.getDrawable(R.drawable.ic_launcher);
		
		// Shows the offline map
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		
		// Shows POIs
		itemizedOverlay = new ArrayItemizedOverlay(defaultMarker);
		mapView.getOverlays().add(itemizedOverlay);
		
		// Using map from assets with a Toast informing the user
		// if it's not found.
		File mapFile = convertAssetToFile("istanbul.map");
		FileOpenResult fileOpenResult = mapView.setMapFile(mapFile);
		if (!fileOpenResult.isSuccess()) {
			Toast.makeText(this, 
					fileOpenResult.getErrorMessage(), Toast.LENGTH_LONG)
					.show();
			
			// Doesn't really work. enough for the time being
			goBack();
		}		
		
		Log.d(TAG, getClass().getSimpleName() + ": setting map file to map view");
		setContentView(mapView);
		
		testPOI();
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
	public void createPOI(double lat, double lng, String title, String description) {
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
	
	/**
	 * TODO... apache commons has IOUtils to use for file conversion
	 * 
	 * Handles a problem found with using AssetManager and 
	 * MapView (from mapsforge), that as MapView requires
	 * a File and AssetManager only provides a FileInputerStream,
	 * this will copy that file to the sd card for to use it
	 * as a File.
	 */
	private File convertAssetToFile(String fileName) {
		Log.d(TAG, getClass().getSimpleName() + ": convertAsset()");
		/*
		 import sun.misc.IOUtils;
		 new FileOutputStream("path/to/file").write(
		 IOUtils.readFully(inputStream, -1, false));		 
		 */
		AssetManager assetManager = this.getAssets();
		InputStream inputStream;
		File mapFile = null;
		try {
			inputStream = assetManager.open(fileName);
			mapFile = createFileFromInputStream(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d(TAG, getClass().getSimpleName() + ": Returning map file");
		return mapFile;	
	}
	
	private File createFileFromInputStream(InputStream inputStream) {
		Log.d(TAG, getClass().getSimpleName() + ": creating file..");
		File outputFile = null;
		try {
			// temp file
			File outputDir = this.getCacheDir();
			outputFile = File.createTempFile("istanbul", "map", outputDir);
			OutputStream outputStream = new FileOutputStream(outputFile);
			
			byte buffer[] = new byte[1024];
			int length = 0;
			
			while ( (length = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, length);
			}
			
			outputStream.close();
			inputStream.close();
			
			
		} catch (IOException e) {
			// TODO file not found Toast-msg
			Log.d(TAG, getClass().getSimpleName() + ": File creation failed");
			e.printStackTrace();
		}
		
		Log.d(TAG, getClass().getSimpleName() + ": File creation succeeded");
		return outputFile;
	}
	
}
