package fi.metropolia.lbs.travist;

import java.util.Timer;
import java.util.TimerTask;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import fi.metropolia.lbs.travist.browsemenu.BrowseMenu;
import fi.metropolia.lbs.travist.emergency.EmergencyActivity;
import fi.metropolia.lbs.travist.exchange.ExchangeActivity;
import fi.metropolia.lbs.travist.exchange.ExchangeFetchXML;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapFragment;
import fi.metropolia.lbs.travist.register.RegisterActivity;
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
		
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		
		/*if (checkInternet.isInternetAvailable() || !checkInternet.isInternetAvailable()) {
			Log.d("Haetaan tiedot xml:stä ja tallennetaan tiedostoon", "Jihuu");
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
			}
			
		}
		
		
        /*SharedPreferences shaPre = getSharedPreferences("MAP", MODE_PRIVATE);
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
        /*LinearLayout linearLayout = (LinearLayout) findViewById(R.id.main_view_layout);
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.emergency.EmergencyActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.exchange.ExchangeActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.todo.TodoActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.savedlist.SavedlistActivity.class));
		
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity.class));
		linearLayout.addView(createButton(fi.metropolia.lbs.travist.routes.TestRoutesActivity.class));
        
		//Remove comment tags to enable demo-version*/
        final Context context = this;
		
		todoIntent = new Intent(this, TodoActivity.class);
		savedIntent = new Intent(this, SavedlistActivity.class);
		emergencyIntent = new Intent(this, EmergencyActivity.class);
		exchangeIntent = new Intent(this, ExchangeActivity.class);
		//This should open the maps activity(?)
		browseIntent = new Intent(this, BrowseMenu.class);
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
				new ActivityStartAsync(BrowseMenu.class).execute();
				v.startAnimation(animScale);
				browseButton.setAlpha(1f);
			}
        });
        browseButton.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				browseButton.setAlpha(0.30f);
				return false;
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
	
	/*private class prepareMapFiles extends AsyncTask<String, Void, String> {

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
