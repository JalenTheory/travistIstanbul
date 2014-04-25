package fi.metropolia.lbs.travist.foursquare_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.graphhopper.routing.Path;

import fi.metropolia.lbs.travist.offline_map.GHAsyncTask;
import android.os.AsyncTask;

public class DownloadJSON extends ParseJSON{
	private AsyncFinished asyncFin;
	
	public DownloadJSON(AsyncFinished asyncFin) {
		this.asyncFin = asyncFin;
	}
	
	public DownloadJSON(GHAsyncTask<Void, Void, Path> ghAsyncTask) {
		// TODO Auto-generated constructor stub
	}

	public void startDownload(String url) {
		new Download().execute(url);
	}
	
	private class Download extends AsyncTask<String, Void, String> {

		private String data;

		@Override
		protected String doInBackground(String... params) {
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(params[0].toString());
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();

				if (statusCode == 200) {
					HttpEntity getResponseEntity = response.getEntity();
					InputStream source = getResponseEntity.getContent();
					BufferedReader in = new BufferedReader(new InputStreamReader(source));

					StringBuilder sb = new StringBuilder();
					String line = null;

					while ((line = in.readLine()) != null) {
						sb.append(line + System.getProperty("line.separator"));
					}

					in.close();
					data = sb.toString();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			parse(result, asyncFin);
		}
	}
}