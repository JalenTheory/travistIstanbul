package fi.metropolia.lbs.travist.foursquare_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import android.os.AsyncTask;

import com.graphhopper.routing.Path;

import fi.metropolia.lbs.travist.offline_map.GHAsyncTask;

public class DownloadJSON extends ParseJSON {

	private AsyncFinished asyncFin;
	private ArrayList<Place> listOfPlaces = new ArrayList<Place>();
	private static AsyncTask<String, Void, String> asyncTask = null;

	public DownloadJSON(AsyncFinished asyncFin) {
		this.asyncFin = asyncFin;
		asyncTask = new Download();
	}

	public DownloadJSON(GHAsyncTask<Void, Void, Path> ghAsyncTask) {
		// TODO Auto-generated constructor stub
	}

	public void startDownload(String url) {
		Log.d("ASYNC", "START");
		Log.d("ASYNC", "status: " + asyncTask.getStatus());
		
		if (asyncTask.getStatus() == AsyncTask.Status.PENDING) {
			Log.d("ASYNC", "PENDING asynctask finished or pending");
			asyncTask = new Download();
			asyncTask.execute(url);
		} else if (asyncTask.getStatus() == AsyncTask.Status.RUNNING) {
			Log.d("ASYNC", "RUNNING, canceling");
			asyncTask.cancel(true);
			Log.d("ASYNC", "RUNNING status: " + asyncTask.getStatus());
		}

		Log.d("ASYNC", "BEFORE FINISHED: " + asyncTask.getStatus());
		if (asyncTask.getStatus() == AsyncTask.Status.FINISHED) {
			Log.d("ASYNC", "FINISHED status: " + asyncTask.getStatus());
			asyncTask = new Download();
			asyncTask.execute(url);
			Log.d("ASYNC", "FINISHED status: " + asyncTask.getStatus());
		}
	}

	private class Download extends AsyncTask<String, Void, String> {

		private String data;
		private Place place;
		private boolean running;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			running = true;
			listOfPlaces.clear();
		}

		@Override
		protected String doInBackground(String... params) {
			// Connect to the server and get the data
			while (running) {
				if (!running) {
					listOfPlaces.clear();
					break;
				}
				
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(params[0].toString());
				try {
					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					int statusCode = statusLine.getStatusCode();

					if (statusCode == 200) {
						HttpEntity getResponseEntity = response.getEntity();
						InputStream source = getResponseEntity.getContent();
						BufferedReader in = new BufferedReader(
								new InputStreamReader(source));

						StringBuilder sb = new StringBuilder();
						String line = null;

						while ((line = in.readLine()) != null) {
							sb.append(line+ System.getProperty("line.separator"));
						}

						in.close();
						data = sb.toString();
					}
					
					if (!running) {
						listOfPlaces.clear();
						break;
					}

					// Parse the data
					if (data != null) {
						JSONObject JSONresponse;
						JSONObject venue;
						JSONObject location;
						JSONObject categoryinfo;
						JSONArray venues;
						JSONArray categories;
						JSONObject iconUrl;

						JSONresponse = (new JSONObject(data))
								.getJSONObject("response");
						venues = JSONresponse.optJSONArray("venues");

						for (int i = 0; i < venues.length(); i++) {
							place = new Place();
							venue = venues.getJSONObject(i);

							location = venue.optJSONObject("location");
							categories = venue.optJSONArray("categories");
							categoryinfo = categories.optJSONObject(0);

							place.setPlaceName(venue.optString("name"));
							place.setCategoryName(categoryinfo
									.optString("name"));
							place.setAddress(location.optString("address"));
							place.setLatitude(location.optString("lat"));
							place.setLongitude(location.optString("lng"));

							// JSONObject jsonBook = new
							// JSONObject(venue.toString());
							JSONObject iconinfo = categoryinfo
									.getJSONObject("icon");
							place.setIconUrl(iconinfo.optString("prefix")
									+ "32" + iconinfo.optString("suffix"));

							listOfPlaces.add(place);
						}
					}
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!running) {
					listOfPlaces.clear();
					break;
				}
				return data;
			}
			return null;
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			running = false;
			Log.d("ASYNC", "ONCANCELLED");
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result != null) {
				asyncFin.downloadFinish(listOfPlaces);
				listOfPlaces.clear();
				Log.d("ASYNC", "END");
			}
		}
	}
}