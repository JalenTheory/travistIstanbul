package fi.metropolia.lbs.travist.poi;

public class PoiWithRatings extends PoiDecorator {
	public PoiWithRatings(Poi newPoi) {
		super(newPoi);
		// TODO Ratings..
	}
	
	@Override
	public void saveToTodoList() {
		// TODO Auto-generated method stub
		super.saveToTodoList();
	}

	@Override
	public void drawOnMap() {
		// TODO Auto-generated method stub
		super.drawOnMap();
	}
}
