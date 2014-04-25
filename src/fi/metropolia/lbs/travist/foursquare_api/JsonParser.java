package fi.metropolia.lbs.travist.foursquare_api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JsonParser {
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
