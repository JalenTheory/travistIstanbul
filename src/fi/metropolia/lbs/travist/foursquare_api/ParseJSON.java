package fi.metropolia.lbs.travist.foursquare_api;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

abstract class ParseJSON{
	
	private AsyncFinished asyncFin;
	private ArrayList<Place> listOfPlaces = new ArrayList<Place>();
	
	public void parse(String data, AsyncFinished asyncFin) {
		new StartParsing().execute(data);
		this.asyncFin = asyncFin;
	}
	
	private class StartParsing extends AsyncTask<String, Void, String>{
		private String data;
		private Place place;
		
		@Override
		protected String doInBackground(String... params) {
			data = params[0].toString();
			JSONObject response;
			JSONObject venue;
			JSONObject location;
			JSONObject categoryinfo;
			JSONArray venues;
			JSONArray categories;

			try {
				response = (new JSONObject(data)).getJSONObject("response");
				venues = response.optJSONArray("venues");
				
				for (int i = 0; i < venues.length(); i++) {
					place = new Place();
					venue = venues.getJSONObject(i);
		
					location = venue.optJSONObject("location");
					categories = venue.optJSONArray("categories");
					categoryinfo = categories.optJSONObject(0);
					
					place.setPlaceName(venue.optString("name"));
					place.setCategoryName(categoryinfo.optString("name"));
					place.setLatitude(location.optString("lat"));
					place.setLongitude(location.optString("lng"));
					listOfPlaces.add(place);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			asyncFin.downloadFinish(listOfPlaces);
		}
	}
}