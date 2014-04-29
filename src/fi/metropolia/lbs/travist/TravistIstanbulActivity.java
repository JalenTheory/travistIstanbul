package fi.metropolia.lbs.travist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;
import fi.metropolia.lbs.travist.emergency.EmergencyActivity;
import fi.metropolia.lbs.travist.exchange.ExchangeActivity;
import fi.metropolia.lbs.travist.offline_map.AssetAdapter;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapFragment;
import fi.metropolia.lbs.travist.offline_map.routes.TestRoutesActivity;
import fi.metropolia.lbs.travist.savedlist.SavedlistActivity;
import fi.metropolia.lbs.travist.todo.TodoActivity;
import fi.metropolia.lbs.travist.userprofile.RegisterActivity;
import fi.metropolia.lbs.travist.userprofile.SigninListener;
import fi.metropolia.lbs.travist.userprofile.UserDialog;
import fi.metropolia.lbs.travist.userprofile.UserHelper;

public class TravistIstanbulActivity extends Activity implements SigninListener{
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
		
		//check if a user is logged in, if yes set the unlocked menu
		
		//setThisContentView();
		//signedIn();
		setCView();
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

	@Override
	public void signedIn() {
		SharedPreferences settings = getSharedPreferences("user", 0);
		String email = settings.getString("signed_in", "none");
			
	    if(email.equals("none")){
	    	setContentView(R.layout.main_menu_locked);
	    }else{
	    	setContentView(R.layout.main_menu_unlocked);
	    	TextView tv = (TextView) findViewById(R.id.welcome_username);
	    	tv.setText(email+"'s Travist");
	    }
		
	}

	@Override
	public void setCView() {
		SharedPreferences settings = getSharedPreferences("user", 0);
		String email = settings.getString("signed_in", "none");
			
	    if(email.equals("none")){
	    	setContentView(R.layout.main_menu_locked);
	    }else{
	    	setContentView(R.layout.main_menu_unlocked);
	    	UserHelper tmp = UserHelper.getInstance( );
	    	TextView tv = (TextView) findViewById(R.id.welcome_username);
	    	tv.setText(tmp.getName(this, email)+"'s Travist");
	    }
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
        /* UNCOMMENT TO ENABLE LOGIN/LOGOFF
        if(login!=null){
        login.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				UserDialog ud = new UserDialog();
				ud.show(getFragmentManager(), "mainact");
			}
        	});
        }
		
        if(logoff!=null){
        	logoff.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					Log.d("moi","logoff");
					Toast.makeText(TravistIstanbulActivity.this, "Logged out.", Toast.LENGTH_LONG).show();
					SharedPreferences settings = getSharedPreferences("user", 0);
				    SharedPreferences.Editor editor = settings.edit();
				    editor.remove("signed_in");
				    editor.commit();
				    setCView();
				}     		
        	});
        }*/
	}
}
