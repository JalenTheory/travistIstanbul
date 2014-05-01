package fi.metropolia.lbs.travist.offline_map;

public class NormalState implements MapState{
	private MapState mStateContext;
	
	public NormalState(MapState stateContext) {
		super();
		mStateContext = stateContext;
	}

	@Override
	public void execute() {
		TravistMapViewAdapter.getInstance().logD("NormalState execute");
		TravistMapViewAdapter.getInstance().refreshPois();
	}
}
