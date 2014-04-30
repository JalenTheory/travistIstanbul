package fi.metropolia.lbs.travist.offline_map;

import org.mapsforge.map.layer.renderer.TileRendererLayer;

public class LayerOnTapController implements MapState {
	private MapState mMapState;
	
	private MapState selectRouteState;
	private MapState normalState;
	private MapState gpsForOriginState;
	
	public final int NORMAL				= 0;
	public final int SELECT_ROUTE 		= 1;
	public final int GPS_FOR_ORIGIN 	= 2;

	public LayerOnTapController() {
		super();
		initStates();
	}

	private void initStates() {
		// initialize states to be used later
		selectRouteState 	= new SelectRouteState(this);
		normalState 		= new NormalState(this);
		gpsForOriginState	= new GpsForOriginState(this);	
		
		// set initial state		
		mMapState = normalState;
	}

	public void changeState(int state) {
		switch (state) {
		case 0:
			mMapState = normalState;
			break;
		case 1:
			mMapState = selectRouteState;
			break;
		case 2:
			mMapState = gpsForOriginState;
			break;
		default:
			mMapState = normalState;				
		}
	}

	@Override
	public void execute() {
		mMapState.execute();
	}
}
