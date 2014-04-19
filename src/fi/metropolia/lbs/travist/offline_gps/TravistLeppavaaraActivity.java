package fi.metropolia.lbs.travist.offline_gps;

import java.io.File;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.core.model.Point;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.layer.MyLocationOverlay;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.LayerManager;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.widget.TextView;

public class TravistLeppavaaraActivity extends Activity {
	private MapView mapView;
	private TileCache tileCache;
	private MapViewPosition mapViewPosition;
	private MyLocationOverlay myLocationOverlay;
	private LayerManager layerManager;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidGraphicFactory.createInstance(getApplication());
		mapView = new MapView(this);
		mapView.setClickable(true);

		// makes a nifty ruler
		mapView.getMapScaleBar().setVisible(true);
		mapView.setBuiltInZoomControls(true);

		// don't know.
		mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
		mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

		// initializes position and zoom level
		mapViewPosition = initializePosition(mapView.getModel().mapViewPosition);

		Drawable drawable = getResources()
				.getDrawable(R.drawable.currency_dollar_icon);
		Bitmap bitmap = AndroidGraphicFactory.convertToBitmap(drawable);
		myLocationOverlay = new MyLocationOverlay(
				TravistLeppavaaraActivity.this, mapViewPosition, bitmap);
		myLocationOverlay.enableMyLocation(true);
		myLocationOverlay.setSnapToLocationEnabled(true);

		layerManager = mapView.getLayerManager();

		tileCache = AndroidUtil.createTileCache(this, getClass()
				.getSimpleName(),
				mapView.getModel().displayModel.getTileSize(), 1f, mapView
						.getModel().frameBufferModel.getOverdrawFactor());

		loadMap();
		if (!layerManager.getLayers().isEmpty()) {
			layerManager.getLayers().add(myLocationOverlay);
			Log.d("Test", "Test:" + myLocationOverlay.isMyLocationEnabled());
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		myLocationOverlay.enableMyLocation(true);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		myLocationOverlay.setSnapToLocationEnabled(false);
		myLocationOverlay.disableMyLocation();
	}

	protected MapPosition getInitialPosition() {
		return new MapPosition(new LatLong(60.218056, 24.810833), (byte) 5);
	}

	protected MapViewPosition initializePosition(MapViewPosition mvp) {
		LatLong center = mvp.getCenter();

		if (center.equals(new LatLong(0, 0))) {
			mvp.setMapPosition(this.getInitialPosition());
		}

		mvp.setZoomLevelMax((byte) 24);
		mvp.setZoomLevelMin((byte) 7);

		return mvp;
	}

	private void loadMap() {
		logD("Loading Map");
		File mapFile = new File(
				"/sdcard/graphhopper/maps/leppavaara-gh/map.map");
		layerManager.getLayers().clear();

		TileRendererLayer tileRendererLayer = new TileRendererLayer(tileCache,
				mapViewPosition, true, AndroidGraphicFactory.INSTANCE) {

			@Override
			public boolean onTap(LatLong tapLatLong, Point layerXY, Point tapXY) {
				Marker marker1 = addMarker(tapLatLong);
				layerManager.getLayers().add(marker1);
				return super.onTap(tapLatLong, layerXY, tapXY);
			}
		};

		tileRendererLayer.setMapFile(mapFile);
		tileRendererLayer.setTextScale(1.5f);
		tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

		mapView.getModel().mapViewPosition.setMapPosition(new MapPosition(
				tileRendererLayer.getMapDatabase().getMapFileInfo().boundingBox
						.getCenterPoint(), (byte) 15));

		layerManager.getLayers().add(tileRendererLayer);
		setContentView(mapView);
	}

	private Marker addMarker(final LatLong latLong) {
		logD("Adding marker");
		Drawable markerIcon = getResources()
				.getDrawable(R.drawable.currency_dollar_icon);
		Bitmap bm = AndroidGraphicFactory.convertToBitmap(markerIcon);

		return new Marker(latLong, bm, 0, -bm.getHeight() / 2) {
			@Override
			public boolean onTap(LatLong geoPoint, Point viewPosition,
					Point tapPoint) {
				if (contains(viewPosition, tapPoint)) {
					layerManager.getLayers().remove(this);
					Log.w("Tapp", "The Marker was touched with onTap: "
							+ this.getLatLong().toString());

					// ASD
					TextView bubbleView = new TextView(
							TravistLeppavaaraActivity.this);
					setBackground(bubbleView,
							getResources().getDrawable(R.drawable.flag_green));
					bubbleView.setGravity(Gravity.CENTER);
					bubbleView.setMaxEms(50);
					bubbleView.setTextSize(50);
					bubbleView.setText("asd");
					Bitmap bubble = viewToBitmap(
							TravistLeppavaaraActivity.this, bubbleView);
					bubble.incrementRefCount();
					layerManager.getLayers().add(
							new Marker(latLong, bubble, 0,
									-bubble.getHeight() / 2));
					// DSA

					return true;
				}
				return false;
			}
		};
	}

	private void logD(String logText) {
		Log.d("Testing", logText);
	}

	static Bitmap viewToBitmap(Context c, View view) {
		view.measure(MeasureSpec.getSize(view.getMeasuredWidth()),
				MeasureSpec.getSize(view.getMeasuredHeight()));
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.setDrawingCacheEnabled(true);
		Drawable drawable = new BitmapDrawable(c.getResources(),
				android.graphics.Bitmap.createBitmap(view.getDrawingCache()));
		view.setDrawingCacheEnabled(false);
		return AndroidGraphicFactory.convertToBitmap(drawable);
	}

	@SuppressLint("NewApi")
	public static void setBackground(View view, Drawable background) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			view.setBackground(background);
		} else {
			view.setBackgroundDrawable(background);
		}
	}

}
