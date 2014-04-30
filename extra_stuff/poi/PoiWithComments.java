package fi.metropolia.lbs.travist.poi;

public class PoiWithComments extends PoiDecorator {
	public PoiWithComments(Poi newPoi) {
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
