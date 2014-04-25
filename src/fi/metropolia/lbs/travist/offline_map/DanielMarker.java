package fi.metropolia.lbs.travist.offline_map;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.layer.overlay.Marker;

import fi.metropolia.lbs.travist.foursquare_api.Place;


public class DanielMarker extends Marker{

	private Place place;
	
	public DanielMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset) {
		super(latLong, bitmap, horizontalOffset, verticalOffset);
		// TODO Auto-generated constructor stub
	}
	
	public DanielMarker(LatLong latLong, Bitmap bitmap, int horizontalOffset, int verticalOffset, Place place) {
		super(latLong, bitmap, horizontalOffset, verticalOffset);
		this.place = place;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
}
