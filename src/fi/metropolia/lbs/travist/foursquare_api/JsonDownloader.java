package fi.metropolia.lbs.travist.foursquare_api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.util.Log;

public class JsonDownloader {
	String url;
	String near = "Istanbul";
	String secret = "LFRZC3HOVDEBXI3LPFSD4JQW23ZCWDNIO3T4YCVCFYJ1TXM1";
	String id = "LTVFBAO5CGUDU5GFKTRGTNWII1HQBLLYRXX2J5E2ONUMJA20";
	String version = "20140314";
	String categoryid;
	String t = "debuggaa";

	public String download() {
		String finaldata = "test";
		String data = "";
		BufferedReader in = null;
		Log.d("moi", "url: " + url);
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
		try {
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));

			StringBuffer sb = new StringBuffer("");
			String l = "";
			String nl = System.getProperty("line.separator");
			while ((l = in.readLine()) != null) {
				sb.append(l + nl);
			}
			in.close();

			data = sb.toString();
			Log.d(t, "parsekutsu");
			Log.d(t, "parsekutsu2");
			finaldata = parse(data);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return finaldata;
	}
	
	private String parse(String data) {
		// TODO Auto-generated method stub
		return null;
	}

	class Dl extends AsyncTask<String, Void, String> {
		String returned;

		protected String doInBackground(String... urls) {
			Dlparse dp = new Dlparse(url);
			try {
				returned = dp.download();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			Log.d("moi", "returned: " + returned);
			//tv.setText(returned);
		}
	}
}
