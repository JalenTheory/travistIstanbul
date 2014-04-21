package fi.metropolia.lbs.travist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import fi.metropolia.lbs.travist.offline_map.AssetAdapter;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapFragment;

public class TravistIstanbulActivity extends Activity {
	public static final String TAG = "travist debug";
//hello
	private Button createButton(final Class<?> testCaseClass) {
		Button button = new Button(this);
		button.setText(testCaseClass.getSimpleName());
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				startActivity(new Intent(TravistIstanbulActivity.this, testCaseClass));
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
		
		//Intent todoIntent = new Intent(this, ExchangeActivity.class);
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
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.emergency.EmergencyActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.exchange.ExchangeActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.todo.TodoActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.savedlist.SavedlistActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.routes.TestRoutesActivity.class));
	}
	
	private class prepareMapFiles extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			AssetAdapter ASS = new AssetAdapter(getBaseContext());
			ASS.assetsToDir();
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//Enable map-related buttons here soon as dl is done or use a splash activity to load maps to internal storage
			//This is done only once per installation
			
		}	
	}
}
