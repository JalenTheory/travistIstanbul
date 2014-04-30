package fi.metropolia.lbs.travist.poi;


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
