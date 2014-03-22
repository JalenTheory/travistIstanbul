package fi.metropolia.lbs.travist.foursquare_api;

import travist.pack.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FoursquareActivity extends Activity {
	
	TextView tv;
	LinearLayout ll;
	String url;
	Button b1, b2, b3;
	String near = "Istanbul";
	String secret = "LFRZC3HOVDEBXI3LPFSD4JQW23ZCWDNIO3T4YCVCFYJ1TXM1";
	String id = "LTVFBAO5CGUDU5GFKTRGTNWII1HQBLLYRXX2J5E2ONUMJA20";
	String version = "20140314";
	int limit = 3;
	String categoryid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv = (TextView) findViewById(R.id.tv);
		b1 = (Button) findViewById(R.id.button1);
		b2 = (Button) findViewById(R.id.button2);
		
		//makeUrl();
		Log.d("asd",""+url);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void button1(View v){
		Log.d("moi","b1: "+v.getId());
		getStuff("b1");
	}
	
	public void button2(View v){
		Log.d("moi","b2: "+v.getId());
		categoryid="4bf58dd8d48988d119941735";
		getStuff("b2");
	}
	
	public void button3(View v){
		Log.d("moi","b3: "+v.getId());
		getStuff("b3");
	}
	
	public void getStuff(String s){
		
		if(s=="b1"){
			url="https://api.foursquare.com/v2/venues/search" +
				"?near=" +near+
				"&client_secret=" +secret+
				"&client_id=" +id+
				"&v=" +version+
				"&limit=" +limit;
		}
		if(s=="b2"){
			url="https://api.foursquare.com/v2/venues/search" +
				"?categoryId=" +categoryid+
				"&client_secret=" +secret+
				"&client_id=" +id+
				"&near=" +near+
				"&limit=" +limit+
				"&v=" +version;
		}
		new Dl().execute();
	}
	
	 class Dl extends AsyncTask<String, Void, String>{
	        String returned;
	 
	        protected String doInBackground(String... urls){
	            Dlparse dp = new Dlparse(url);
	            try{
	                returned = dp.download();
	            } catch(Exception e){
	                e.printStackTrace();
	            }
	            return null;
	        }
	 
	        @Override
	        protected void onPostExecute(String result){
	        	Log.d("moi","returned: "+returned);
	            tv.setText(returned);
	        }
	 }
}
