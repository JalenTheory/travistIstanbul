package fi.metropolia.lbs.travist.userprofile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import travist.pack.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import fi.metropolia.lbs.travist.TravistIstanbulActivity;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.UserTableClass;

public class RegisterActivity extends Activity {
	
	RadioButton yes;
	RadioButton no;
	ProgressDialog pd;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		//radiobuttons
		
		yes = (RadioButton) findViewById(R.id.matchmake_radio_yes);
		no = (RadioButton) findViewById(R.id.matchmake_radio_no);
		
		yes.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				yes.setChecked(true);
				no.setChecked(false);
			}		
		});
		
		no.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				no.setChecked(true);
				yes.setChecked(false);
			}		
		});
		
		LinearLayout register_button = (LinearLayout) findViewById (R.id.register_register);
		LinearLayout cancel_button = (LinearLayout) findViewById (R.id.register_cancel);
		
		register_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//PUSH USER INFO TO SERVER
				
				EditText etname = (EditText) findViewById(R.id.register_name);
				EditText etemail = (EditText) findViewById(R.id.register_email);
				EditText etcountry = (EditText) findViewById(R.id.register_country);
				EditText etpw = (EditText) findViewById(R.id.register_password);
				EditText etgsm = (EditText) findViewById(R.id.register_GSM);
				
				String name = etname.getText().toString();
				String email = etemail.getText().toString();
				String country = etcountry.getText().toString();
				String pw = etpw.getText().toString();
				String gsm = etgsm.getText().toString();
				
				int matchmaking = -1;
				if(yes.isChecked()){
					matchmaking = 1;
				}else{
					matchmaking = 0;
				}
				
				ContentValues cv1 = new ContentValues();
				cv1.put(UserTableClass.NAME, name);
				cv1.put(UserTableClass.COUNTRY, country);
				cv1.put(UserTableClass.EMAIL, email);
				cv1.put(UserTableClass.GSM, gsm);
				RegisterActivity.this.getContentResolver().insert(LBSContentProvider.USERS_URI, cv1);
				
				String url = "http://users.metropolia.fi/~eetupa/Turkki/setUser.php" +
						"?name="+name+
						"&country="+country+
						"&email="+email+
						"&gsm="+gsm+
						"&match="+matchmaking+
						"&pw="+pw;
				Log.d("moi","url: "+url);

				new Dl().execute(url);
				SharedPreferences settings = getSharedPreferences("user", 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("signed_in", email);
			    editor.commit();
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}

	class Dl extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(RegisterActivity.this);
			pd.setMessage("Registering new user");
			pd.show();
		}
		protected String doInBackground(String... urls) {
			String data = "";
			BufferedReader in = null;
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(urls[0].toString());

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
			return data;
		}
		@Override
		protected void onPostExecute(String result) {
			pd.cancel();
			Log.d("moi","result: "+result+"'");
			if(result.contains("failed")){
				Toast.makeText(RegisterActivity.this, "Registering failed.", Toast.LENGTH_LONG).show();
			}else if(result.contains("input")){
				Toast.makeText(RegisterActivity.this, "Check your input", Toast.LENGTH_LONG).show();
			}else if (result.contains("success")){
				Toast.makeText(RegisterActivity.this, "Success! Welcome to Travist!", Toast.LENGTH_LONG).show();
				Intent i = new Intent(RegisterActivity.this, TravistIstanbulActivity.class);
				startActivity(i);
				//finish();
			}
		}
	}
	
}
