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
		tmva.logD("routing.. ", this);
		tmva.routeFrom(tmva.getLastLayerTapPos());
		tmva.routeTo();
		
		mStateContext.changeState(mStateContext.NORMAL);
		// routeFromHere
		// set poi as to
	}

}
