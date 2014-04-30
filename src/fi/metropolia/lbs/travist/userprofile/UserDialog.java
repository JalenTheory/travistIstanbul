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
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class UserDialog extends DialogFragment {

	ProgressDialog pd;
	String result;
	View dialogview;
	Context context;
	UserDialog ud;
	String email;
	SigninListener slistener;
	
	public void onAttach(Activity act){
		super.onAttach(act);
		try{
			slistener = (SigninListener) act;
		}catch(ClassCastException e){
			
		}
	}
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		context = getActivity();
		ud = this;
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();
		dialogview = inflater.inflate(R.layout.dialog_login, null);
		builder.setView(dialogview);

		LinearLayout login_button = (LinearLayout) dialogview.findViewById(R.id.dialog_login_log_in);
		LinearLayout login_cancel = (LinearLayout) dialogview.findViewById(R.id.dialog_login_cancel);
		LinearLayout register_button = (LinearLayout) dialogview.findViewById(R.id.dialog_login_register);

		login_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		
				EditText emailt = (EditText) dialogview.findViewById(R.id.dialog_login_name);
				EditText pwt = (EditText) dialogview.findViewById(R.id.dialog_login_password);
				
				email = emailt.getText().toString();
				String pw = pwt.getText().toString();
				
				String url = "http://users.metropolia.fi/~eetupa/Turkki/checkUser.php" +
						"?email="+email+
						"&pw="+pw;
				Log.d("moi","url: "+url);
				Log.d("moi","email: "+email+"pw: "+pw);
				if(!email.equals("")||!pw.equals("")){
					new Dl().execute(url);
				}else{
					Toast.makeText(context, "Please insert at least an email address and a password", Toast.LENGTH_LONG).show();
				}
			}
		});
		
		login_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}			
		});
		register_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(), RegisterActivity.class);
				startActivity(i);
				dismiss();
			}
			
		});
		
		return builder.create();

	}

	
	class Dl extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(getActivity());
			pd.setMessage("Authenticating user");
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
			if(result.equals("failed\n")){
				Toast.makeText(
						context, "Authenticating failed.",
						Toast.LENGTH_LONG).show();
			}else if(result.equals("success\n")){
				Toast.makeText(context, "Welcome to Travist!", 
						Toast.LENGTH_LONG).show();
				ud.dismiss();
				//get username and set it to top
				SharedPreferences settings = getActivity().getSharedPreferences("user", 0);
			    SharedPreferences.Editor editor = settings.edit();
			    editor.putString("signed_in", email);
			    editor.commit();
			    //((TravistIstanbulActivity) getActivity()).setThisContentView();
			    slistener.setCView();
			}
			pd.cancel();
		}
	}
}