package fi.metropolia.lbs.travist.offline_map;

public class GpsForOriginState implements MapState {
	private MapState mStateContext;
	
	public GpsForOriginState(MapState stateContext) {
		super();		
		mStateContext = stateContext;
	}
	
	@Override
	public void execute() {
		TravistMapViewAdapter.getInstance();
		// get gps position and set it to from
		// set poi as to 
	}
}
