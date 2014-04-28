package fi.metropolia.lbs.travist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import fi.metropolia.lbs.travist.emergency.EmergencyActivity;
import fi.metropolia.lbs.travist.exchange.ExchangeActivity;
import fi.metropolia.lbs.travist.offline_map.AssetAdapter;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapFragment;
import fi.metropolia.lbs.travist.register.RegisterActivity;
import fi.metropolia.lbs.travist.routes.TestRoutesActivity;
import fi.metropolia.lbs.travist.savedlist.SavedlistActivity;
import fi.metropolia.lbs.travist.todo.TodoActivity;

public class TravistIstanbulActivity extends Activity {
	public static final String TAG = "travist debug";
	
	LinearLayout todoButton;
	Intent todoIntent;
	LinearLayout savedButton;
	Intent savedIntent;
	LinearLayout emergencyButton;
	Intent emergencyIntent;
	LinearLayout exchangeButton;
	Intent exchangeIntent;
	LinearLayout browseButton;
	Intent browseIntent;
	Intent registerIntent;
	ImageView login;
	ImageView logoff;
	
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
		//setContentView(R.layout.main);
		//Use layout below to enable demo-version
		setContentView(R.layout.main_menu_locked);
		
		
		
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
        /*
		// Make buttons according to Activities of test cases
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_view_layout);
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.emergency.EmergencyActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.exchange.ExchangeActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.todo.TodoActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.savedlist.SavedlistActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.routes.TestRoutesActivity.class));
        */
		//Remove comment tags to enable demo-version
        final Context context = this;
		
		todoIntent = new Intent(this, TodoActivity.class);
		savedIntent = new Intent(this, SavedlistActivity.class);
		emergencyIntent = new Intent(this, EmergencyActivity.class);
		exchangeIntent = new Intent(this, ExchangeActivity.class);
		//This should open the maps activity(?)
		browseIntent = new Intent(this, TestRoutesActivity.class);
		registerIntent = new Intent(this, RegisterActivity.class);
        
        todoButton = (LinearLayout) findViewById (R.id.main_todo);
        savedButton = (LinearLayout) findViewById (R.id.main_saved);
        emergencyButton = (LinearLayout) findViewById (R.id.main_emergency);
        exchangeButton = (LinearLayout) findViewById (R.id.main_exchange);
        browseButton = (LinearLayout) findViewById (R.id.main_browse);
        login = (ImageView) findViewById (R.id.main_login);
        logoff = (ImageView) findViewById (R.id.logoff);

        todoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(todoIntent);
			}
        });
        savedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(savedIntent);
			}
        });
        emergencyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(emergencyIntent);
			}
        });
        exchangeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(exchangeIntent);
			}
        });
        browseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(browseIntent);
			}
        });
        login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.dialog_login);
				LinearLayout login_button = (LinearLayout) dialog.findViewById(R.id.dialog_login_log_in);
				LinearLayout login_cancel = (LinearLayout) dialog.findViewById(R.id.dialog_login_cancel);
				LinearLayout register_button = (LinearLayout) dialog.findViewById(R.id.dialog_login_register);

				login_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				login_cancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}			
				});
				register_button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(registerIntent);
						dialog.dismiss();
					}
					
				});
				dialog.setTitle("Login");
				dialog.show();
			}
        });
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
