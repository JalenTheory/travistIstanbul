package fi.metropolia.lbs.travist.todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DlPlaces {
	
	private String url;
	private String tag = "dlmysql";
	private JSONObject[] returnArray;
	
	public DlPlaces(String u){
		Log.d(tag,"url: "+u);
		url=u;
	}
	public JSONObject[] downloadPlaces(){

		String data="";
		BufferedReader in = null;
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		
		try {
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			
			StringBuffer sb = new StringBuffer("");
			String l = "";
			String nl = System.getProperty("line.separator");
			while((l=in.readLine())!=null){
				sb.append(l+nl);
			}
			in.close();
				
			data = sb.toString();
			Log.d(tag,"data: "+data);
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//PARSING JSON
		JSONObject place;
		JSONArray places;
		
		try {
			places = (new JSONArray(data));
			returnArray = new JSONObject[places.length()];
			Log.d(tag,"length: "+places.length());
			for(int i=0;i<places.length();i++){
				place = places.optJSONObject(i);
				Log.d(tag,"place.name: "+place.optString("PLACE_NAME"));
				returnArray[i]=place;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnArray;
	}
	
}