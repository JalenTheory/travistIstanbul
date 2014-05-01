package fi.metropolia.lbs.travist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.metropolia.lbs.travist.browsemenu.BrowseMenuActivity;
import fi.metropolia.lbs.travist.emergency.EmergencyActivity;
import fi.metropolia.lbs.travist.exchange.ExchangeActivity;
import fi.metropolia.lbs.travist.exchange.ExchangeFetchXML;
import fi.metropolia.lbs.travist.offline_map.AssetAdapter;
import fi.metropolia.lbs.travist.offline_map.TravistMapViewAdapterFragment;
import fi.metropolia.lbs.travist.savedlist.SavedlistActivity;
import fi.metropolia.lbs.travist.todo.TodoActivity;
import fi.metropolia.lbs.travist.userprofile.RegisterActivity;
import fi.metropolia.lbs.travist.userprofile.SigninListener;
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
	
	ExchangeFetchXML XMLOperations = new ExchangeFetchXML();
	
	Thread t;
	
	CheckInternetConnectivity checkInternet = new CheckInternetConnectivity();
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
		Intent intent = new Intent(this, TravistMapViewAdapterFragment.class);
		startActivity(intent);
	}
	
	/** Called when the activity is first created. */
	@SuppressLint("CommitPrefEdits")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		//Use layout below to enable demo-version

		//setContentView(R.layout.main_menu_locked);
		
		getActionBar().hide();
		
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		/*if (checkInternet.isInternetAvailable() || !checkInternet.isInternetAvailable()) {
			Log.d("Haetaan tiedot xml:st� ja tallennetaan tiedostoon", "Jihuu");
			try {
				XMLOperations.saveXMLToFile(XMLOperations.getRates(), getBaseContext());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		
		if (checkInternet.isInternetAvailable()) {
			Log.d("Haetaan tiedot xml:st� ja tallennetaan tiedostoon", "Jihuu");
			
		}
		*/
		
		//check if a user is logged in, if yes set the unlocked menu
		
		//setCView sets the layout for this activity
		setCView();
		
		/*
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
        */
        // TODO clean up
        /*
		// Make buttons according to Activities of test cases
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_view_layout);
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.emergency.EmergencyActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.exchange.ExchangeActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.todo.TodoActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.savedlist.SavedlistActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.routes.TestRoutesActivity.class));
        
		//Remove comment tags to enable demo-version*/
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
	    	String name = tmp.getName(this, email);
	    	if(name.equals("Your")){
	    		tv.setText(name+" Travist");
	    	}else{
	    		tv.setText(name+"'s Travist");
	    	}
	    }
	    todoIntent = new Intent(this, TodoActivity.class);
		savedIntent = new Intent(this, SavedlistActivity.class);
		emergencyIntent = new Intent(this, EmergencyActivity.class);
		exchangeIntent = new Intent(this, ExchangeActivity.class);
		//This should open the maps activity(?)
		browseIntent = new Intent(this, BrowseMenuActivity.class);
		registerIntent = new Intent(this, RegisterActivity.class);
        
        todoButton = (LinearLayout) findViewById (R.id.main_todo);
        savedButton = (LinearLayout) findViewById (R.id.main_saved);
        emergencyButton = (LinearLayout) findViewById (R.id.main_emergency);
        exchangeButton = (LinearLayout) findViewById (R.id.main_exchange);
        browseButton = (LinearLayout) findViewById (R.id.main_browse);
        login = (ImageView) findViewById (R.id.main_login);
        logoff = (ImageView) findViewById (R.id.logoff);
        
        final Animation animScale = AnimationUtils.loadAnimation(this, R.layout.anim_button);

        todoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActivityStartAsync(TodoActivity.class).execute();
				v.startAnimation(animScale);
				todoButton.setAlpha(1f);
			}
        });
        
        todoButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				todoButton.setAlpha(0.30f);
				return false;
			}
		});
        savedButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActivityStartAsync(SavedlistActivity.class).execute();
				v.startAnimation(animScale);
				savedButton.setAlpha(1f);
			}
        });
        savedButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				savedButton.setAlpha(0.30f);
				return false;
			}
		});
        emergencyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActivityStartAsync(EmergencyActivity.class).execute();
				v.startAnimation(animScale);
				emergencyButton.setAlpha(1f);
			}
        });
        emergencyButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				emergencyButton.setAlpha(0.30f);
				return false;
			}
		});
        exchangeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActivityStartAsync(ExchangeActivity.class).execute();
				v.startAnimation(animScale);
				exchangeButton.setAlpha(1f);
			}
        });
        exchangeButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				exchangeButton.setAlpha(0.30f);
				return false;
			}
		});
        browseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				v.startAnimation(animScale);
				browseButton.setAlpha(1f);
				Intent i = new Intent(TravistIstanbulActivity.this, BrowseMenuActivity.class);
				startActivity(i);
			}
        });
        /*
        browseButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				browseButton.setAlpha(0.30f);
				return false;
			}
		});
        */
        /* UNCOMMENT TO ENABLE LOGIN/LOGOFF 
	    if(login!=null){
	        login.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(new CheckInternetConnectivity().isInternetAvailable(TodoActivity.this)){
						UserDialog ud = new UserDialog();
						ud.show(getFragmentManager(), "mainact");
					}else{
						Toast.makeText(TodoActivity.this, "No connection.", Toast.LENGTH_LONG).show();
					}
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
        /*
		if (savedInstanceState == null) {
			AndroidGraphicFactory.createInstance(getApplication());			
		}*/
	}
	
	class ActivityStartAsync extends AsyncTask<String, String, String> {
		
		Class<?> classs;
		
		public ActivityStartAsync (Class<?> classs){
	        this.classs = classs;
	    }
			
		@Override
		protected String doInBackground(String... params) {
			Intent i = new Intent(TravistIstanbulActivity.this, classs);
			startActivity(i);
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {

		}
		
		@Override
		protected void onPreExecute() {
			 t = new Thread(new Runnable() {

			        @Override
			        public void run() {
			            try {
			                t.sleep(200);
			                runOnUiThread(new Runnable() {
			                    public void run() {
			                        setContentView(R.layout.splash_screen);
			                    }
			                });

			            } catch (InterruptedException e) {
			                // TODO Auto-generated catch block
			                e.printStackTrace();
			            }

			        }
			    });
			    t.start();
			//setContentView(R.layout.splash_screen);
		}
		
		@Override
		protected void onProgressUpdate(String... text) {
		}
	}
	/*
	class prepareMapFiles extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			AssetAdapter ASS = new AssetAdapter(getBaseContext());
			ASS.assetsToDir();
			Log.d("TULEEKO", "TULEEEE");
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//Enable map-related buttons here soon as dl is done or use a splash activity to load maps to internal storage
			//This is done only once per installation

		}
	}*/
}
