package fi.metropolia.lbs.travist.poi;

import java.util.ArrayList;
import java.util.List;

import org.mapsforge.core.model.LatLong;

import fi.metropolia.lbs.travist.foursquare_api.JsonDownloader;
import android.os.AsyncTask;
import android.util.Log;

abstract class PoiDecorator implements Poi {
	private Poi rootPoi;
	public PoiDecorator( Poi newPoi) {
		rootPoi = newPoi;
	}
	
	public void saveToTodoList() {
		rootPoi.saveToTodoList();
	}
	public void drawOnMap() {
		rootPoi.drawOnMap();
	}
}
