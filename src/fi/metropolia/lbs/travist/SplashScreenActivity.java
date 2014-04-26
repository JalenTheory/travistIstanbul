package fi.metropolia.lbs.travist;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.client.ClientProtocolException;
import org.xml.sax.SAXException;

import fi.metropolia.lbs.travist.exchange.ExchangeFetchXML;
import fi.metropolia.lbs.travist.offline_map.AssetAdapter;
import fi.metropolia.lbs.travist.todo.TodoActivity;
import travist.pack.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SplashScreenActivity extends Activity {
	Intent travistIntent;
	ExchangeFetchXML fetchXML = new ExchangeFetchXML();
	String currency_rate[] = new String[9];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		getActionBar().hide();

		travistIntent = new Intent(this, TravistIstanbulActivity.class);
		
		SharedPreferences shaPre = getSharedPreferences("MAP", MODE_PRIVATE);
        SharedPreferences.Editor editor = shaPre.edit();
        
        try {
			currency_rate = fetchXML.getRates();
			//TODO catch vaa Exception e, pienempi koodi
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        File file = new File("currency_rate.txt");
        try {
			PrintWriter out = new PrintWriter(new FileWriter(file));
			
			for (String rate : currency_rate) {
				out.println(rate);
			}
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if(shaPre.getBoolean("dirStatus", false)) {
        	Log.d("LOG", "Files are in the app folder");
        	//Start a timertask that'll delay the start of TravistIstanbulActivity by 2 seconds (2000 milliseconds)
        	TimerTask startProgram = new TimerTask() {
    			@Override
    			public void run() {
    				startActivity(travistIntent);
    			}
    		};
    		Timer t = new Timer();
    		t.schedule(startProgram, 2000);
        } else {
        	new prepareMapFiles().execute();
        	editor.putBoolean("dirStatus", true);
    		editor.apply();
        	Log.d("LOG", "Files werent in app folder");
        }
		
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
