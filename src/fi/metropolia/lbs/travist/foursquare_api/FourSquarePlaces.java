package fi.metropolia.lbs.travist.foursquare_api;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class FourSquarePlaces extends ListActivity{
    String UriPlaces;
    
    String jsonInput;
    ArrayAdapter<String> adapter;
    String[] places;
    String[] addresses;
    String client_id = "GWA2NRBNDFBENJIZIGFF2IFX5JTDTOUYUPLHCOCOTXMF34LU";
    String client_secret = "JSI4CFI3HSMK1FPCIE4DLEDBXL321CM1SGENAX4HLXYTSCHG";
    String version = "20131016";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//This bypasses the policy that doesn't allow users to run network operations in main thread
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	    StrictMode.setThreadPolicy(policy);
		

		getPlaces(getGPS());
		
		jsonInput = getPlaces(getGPS());
        
        JSONObject jb;
        JSONObject jb1;
        JSONArray venues;
        
        //Parse Json response from foursquare to only get name and address of each venue and place them in their own string arrays
		try {
			jb = new JSONObject(getPlaces(getGPS()));
			jb1 = jb.getJSONObject("response");
			venues = jb1.getJSONArray("venues");
			
			places = new String[venues.length()];
			addresses = new String[venues.length()];
			
			for (int i = 0; i < venues.length(); i++) {
				JSONObject item = venues.getJSONObject(i);
				places[i] = item.getString("name");
				try {
					JSONObject addr = item.getJSONObject("location");
					addresses[i] = addr.getString("address");
				}
				catch (JSONException e) {
					addresses[i] = " ";
				}
			}        		
		} 
		
		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
		
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, places);
		setListAdapter(adapter);
		
	}
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
      String item = (String) getListAdapter().getItem(position);
      Toast.makeText(this, item + " selected", Toast.LENGTH_SHORT).show();
      
      /*Add selected item to todo-list (name and address)
       * 
       * 
       */
      
    }
    

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public double[] getGPS() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
        List<String> providers = lm.getProviders(true);

        /* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
        Location l = null;
        
        for (int i=providers.size()-1; i>=0; i--) {
                l = lm.getLastKnownLocation(providers.get(i));
                if (l != null) break;
        }
        
        double[] gps = new double[2];
        if (l != null) {
                gps[0] = l.getLatitude();
                gps[1] = l.getLongitude();
                Log.d(Double.toString(gps[0]), Double.toString(gps[1]));
        }
        return gps;
	}
	
	public String getPlaces(double[] gps) {
		String Json = "";
		UriPlaces = "https://api.foursquare.com/v2/venues/search?ll=" + gps[0] + "," + gps[1] + "&client_secret=" + client_secret + "&client_id=" + client_id + "&v=" + version;
		HttpGet get = new HttpGet(UriPlaces);
        HttpClient client = new DefaultHttpClient();
        try {
            HttpResponse response = client.execute(get);
            HttpEntity e = response.getEntity();
            Json = EntityUtils.toString(e);
	    } 
        catch (ClientProtocolException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    } 
        catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	    }
        
        return Json;
        
	}

}
