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

public class DlMatches {

	public String url;
	private String tag = "dlmatches";
	private JSONObject[] returnArray;

	public DlMatches(String u) {
		Log.d(tag, "url: " + u);
		url = u;
	}

	public JSONObject[] downloadMatches() {

		String data = "";
		BufferedReader in = null;
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

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// PARSING JSON
		JSONObject match;
		JSONArray matches;

		try {
			matches = (new JSONArray(data));
			returnArray = new JSONObject[matches.length()];
			for (int i = 0; i < matches.length(); i++) {
				match = matches.optJSONObject(i);
				returnArray[i] = match;
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnArray;
	}

}