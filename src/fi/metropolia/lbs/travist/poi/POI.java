package fi.metropolia.lbs.travist.poi;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.model.LatLong;

import fi.metropolia.lbs.travist.foursquare_api.JsonDownloader;
import android.os.AsyncTask;
import android.util.Log;

public abstract class POI {
	private String title;
	private String category;
	private LatLong coordinates;
	
	public POI(String newTitle, String newCategory, LatLong newCoordinates) {
		title = newTitle;
		category = newCategory;
		coordinates = newCoordinates;
	}
	
	public abstract void saveToTodoList();
	public abstract void drawOnMap();
}
