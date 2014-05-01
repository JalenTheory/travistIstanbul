package fi.metropolia.lbs.travist.offline_map;

public class SelectRouteState implements MapState {
	private LayerOnTapController mStateContext;
	
	public SelectRouteState(LayerOnTapController stateContext) {
		super();
		mStateContext = stateContext;
	}

	@Override
	public void execute() {
		TravistMapViewAdapter tmva = TravistMapViewAdapter.getInstance();
		tmva.routeFrom(tmva.getLastLayerTapPos());
		tmva.routeTo();
		
		tmva.hideButtons();
		
		mStateContext.changeState(mStateContext.NORMAL);
		// routeFromHere
		// set poi as to
	}

}
