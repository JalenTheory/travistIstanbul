package fi.metropolia.lbs.travist.foursquare_api;

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

public class Dlparse{
	//downloads and parses json
	public String url;
	private String t = "dlparse";
	public Dlparse(String u){
		url=u;
	}
	public String download(){
		String finaldata="test";
		String data="";
		BufferedReader in = null;
		Log.d("moi","url: "+url);
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
			Log.d(t,"parsekutsu");
			Log.d(t,"parsekutsu2");
			finaldata=parse(data);
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return finaldata;
	}
	public String parse(String data){
		
		JSONObject response;
		JSONObject venue;
		JSONObject location;
		JSONObject categoryinfo;
		
		JSONArray venues;
		JSONArray categories;
		
		String name;
		String address;
		String category;
		
		StringBuffer sb = new StringBuffer("");
		
		try {

			response = (new JSONObject(data)).getJSONObject("response");
			venues = response.optJSONArray("venues");
			Log.d("moi","length: "+venues.length());
			for(int i=0;i<3;i++){
				venue = venues.getJSONObject(i);
				name = venue.optString("name");
				Log.d("moi","name: "+name);
				location = venue.optJSONObject("location");
				address = location.optString("address");
				Log.d("moi","address: "+address);
				categories = venue.optJSONArray("categories");
				categoryinfo = categories.optJSONObject(0);
				category = categoryinfo.optString("name");
				Log.d("moi","category: "+name);
				int j = i+1;
				sb.append("Venue #"+j+"\nName: "+name+"\nAddress: "+address+"\nCategory: "+category+"\n\n");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data=sb.toString();
		return data;
	}
}
