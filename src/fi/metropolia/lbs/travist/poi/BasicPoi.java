package fi.metropolia.lbs.travist.poi;

import org.mapsforge.core.model.LatLong;

public class BasicPoi implements Poi {
	private String title;
	private String category;
	private LatLong coordinates;
	// LatLong -> Marker
	// extends Marker?
	
	public BasicPoi(String newTitle, String newCategory, LatLong newCoordinates) {
		title = newTitle;
		category = newCategory;
		coordinates = newCoordinates;
	}
	
	@Override
	public void saveToTodoList() {
		// visitor
	}

	@Override
	public void drawOnMap() {
		// TODO reference to the correspondent component to draw into
		// either MapView or OverLay
	}

}
