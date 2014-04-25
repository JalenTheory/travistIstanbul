package fi.metropolia.lbs.travist.todo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class UpSaved {

	private String url;

	public UpSaved(String url) {
		this.url = url;
	}

	public void upload(){
		new Up().execute();
	}
	
	class Up extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {
			String data = "";
			BufferedReader in = null;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response;
			
			try {
				response = client.execute(request);
				
				in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

				data = in.readLine();
				in.close();
				Log.d("upsaved", "data: "+data);
				
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}
}
