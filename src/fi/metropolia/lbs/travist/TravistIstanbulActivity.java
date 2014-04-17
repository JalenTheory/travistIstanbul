package fi.metropolia.lbs.travist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;
import fi.metropolia.lbs.travist.offline_map.AssetAdapter;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapFragment;
import fi.metropolia.lbs.travist.savedlist.SavedlistActivity;
import fi.metropolia.lbs.travist.todo.TodoActivity;

public class TravistIstanbulActivity extends Activity {
	public static final String TAG = "travist debug";

	private Button createButton(final Class<?> testCaseClass) {
		Button button = new Button(this);
		button.setText(testCaseClass.getSimpleName());
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				startActivity(new Intent(TravistIstanbulActivity.this,
						testCaseClass));
			}
		});
		return button;
	}

	public void openMap(View view) {
		Intent intent = new Intent(this, TestOfflineMapFragment.class);
		startActivity(intent);
	}

	/** Called when the activity is first created. */
	@SuppressLint("CommitPrefEdits")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		//Intent todoIntent = new Intent(this, SavedlistActivity.class);
		//startActivity(todoIntent);
		
        SharedPreferences shaPre = getSharedPreferences("MAP", MODE_PRIVATE);
        SharedPreferences.Editor editor = shaPre.edit();

        if(shaPre.getBoolean("dirStatus", false)) {
        	Log.d("LOG", "Files are in the app folder");
        } else {
        	new prepareMapFiles().execute();
        	editor.putBoolean("dirStatus", true);
    		editor.apply();
        	Log.d("LOG", "Files werent in app folder");
        }
		// Make buttons according to Activities of test cases
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_view_layout);
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.offline_gps.TravistLeppavaaraActivity.class));
	}
	
	public void insertPredefinedLocations() {
		ContentValues cv = new ContentValues();
		
		//Taksim
		cv.put(PlaceTableClass.PLACE_ID, "4cd103d96449a0936af0cdcf");
		cv.put(PlaceTableClass.PLACE_NAME, "Taksim");
		cv.put(PlaceTableClass.LATITUDE, "41.036441586758286");
		cv.put(PlaceTableClass.LONGITUDE, "28.983607292175293");
		cv.put(PlaceTableClass.ADDRESS, "Beyoğlu");
		cv.put(PlaceTableClass.IS_IN_SAVED, 1);
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv);
		
		cv.clear();
		cv.put(PlaceTableClass.PLACE_ID, "4e2463e7b0fbdf9ba7c6d470");
		cv.put(PlaceTableClass.PLACE_NAME, "Sultanahmet");
		cv.put(PlaceTableClass.LATITUDE, "41.00815272297204");
		cv.put(PlaceTableClass.LONGITUDE, "28.975353118830544");
		cv.put(PlaceTableClass.IS_IN_SAVED, 1);
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv);
		
		cv.clear();
		cv.put(PlaceTableClass.PLACE_ID, "4d84e9860a0d1456c7a307cc");
		cv.put(PlaceTableClass.PLACE_NAME, "Pierre Loti & Tarihi Kahve");
		cv.put(PlaceTableClass.LATITUDE, "41.05421070622379");
		cv.put(PlaceTableClass.LONGITUDE, "28.933804035186768");
		cv.put(PlaceTableClass.ADDRESS, "Eyüp Merkez Mah. Balmumcu Sok. No: 1");
		cv.put(PlaceTableClass.IS_IN_SAVED, 1);
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv);
		
		cv.clear();
		cv.put(PlaceTableClass.PLACE_ID, "4da0e0e79aa4721e1ce6ee19");
		cv.put(PlaceTableClass.PLACE_NAME, "Barlar Sokağı");
		cv.put(PlaceTableClass.LATITUDE, "40.987314");
		cv.put(PlaceTableClass.LONGITUDE, "29.02693");
		cv.put(PlaceTableClass.ADDRESS, "Eyüp Merkez Mah. Balmumcu Sok. No: 1");
		cv.put(PlaceTableClass.IS_IN_SAVED, 1);
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv);
	}
	
	private class prepareMapFiles extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			AssetAdapter ASS = new AssetAdapter(getBaseContext());
			ASS.assetsToDir();
			insertPredefinedLocations();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//Enable browse button also?
			
		}	
	}
}
