package fi.metropolia.lbs.travist.offline_map;

import android.widget.Toast;

public class SelectRouteState implements MapState {
	private LayerOnTapController mStateContext;
	
	public SelectRouteState(LayerOnTapController stateContext) {
		super();
		mStateContext = stateContext;
	}

	@Override
	public void execute() {		
		TravistMapViewAdapter tmva = TravistMapViewAdapter.getInstance();
		Toast.makeText(tmva.getContext(), "Select point of origin", Toast.LENGTH_LONG);
		tmva.logD("routing.. ", this);
		tmva.routeFrom(tmva.getLastLayerTapPos());
		tmva.routeTo();
		
		mStateContext.changeState(mStateContext.NORMAL);
		// routeFromHere
		// set poi as to
	}

}
