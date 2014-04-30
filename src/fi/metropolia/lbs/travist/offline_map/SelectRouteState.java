package fi.metropolia.lbs.travist.offline_map;

public class SelectRouteState implements MapState {
	private LayerOnTapController mStateContext;
	
	public SelectRouteState(LayerOnTapController stateContext) {
		super();
		mStateContext = stateContext;
	}

	@Override
	public void execute() {
		TravistMapViewAdapter.getInstance().changeViewToSelectOrigin();
		
		mStateContext.changeState(mStateContext.NORMAL);
		// routeFromHere
		// set poi as to
	}

}
