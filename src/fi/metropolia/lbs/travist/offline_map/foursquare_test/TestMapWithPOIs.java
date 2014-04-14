package fi.metropolia.lbs.travist.offline_map.foursquare_test;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.layer.overlay.Marker;

import travist.pack.R;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity;

public class TestMapWithPOIs extends TestOfflineMapActivity{
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	}
	
	
	private Marker addMarker(LatLong marker, String drawable) {
		logD("Adding marker");
		Drawable markerIcon = getResources().
				getDrawable(getResources().getIdentifier(drawable, null, null));
		Bitmap bm = AndroidGraphicFactory.convertToBitmap(markerIcon);
		return new Marker(marker, bm, -bm.getHeight(), -bm.getWidth());
	}
}
