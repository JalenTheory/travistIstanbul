package fi.metropolia.lbs.travist.todo;

import travist.pack.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MatchDialog extends DialogFragment {
	
	private String email;
	private String name;
	private String country;
	private String gsm;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		
		email = getArguments().getString("email");
		name = getArguments().getString("name");
		country = getArguments().getString("country");
		gsm = getArguments().getString("gsm");
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage("Contact "+name);
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_matchmaking, null);
		builder.setView(v);
		
		TextView matchName = (TextView) v.findViewById(R.id.dialog_matchmake_name);
		matchName.setText(name);
		TextView matchNationality = (TextView) v.findViewById(R.id.dialog_matchmake_nationality);
		matchNationality.setText(country);
		
		/*
		 * SEND-NAPEILLE:
		 */
		
		LinearLayout send_email = (LinearLayout) v.findViewById(R.id.dialog_matchmake_send_email);
		send_email.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String[] email_address = new String[]{email};
				Intent email = new Intent(Intent.ACTION_SEND); 
				email.setType("message/rfc822");
				email.putExtra(Intent.EXTRA_EMAIL, email_address);
				email.putExtra(Intent.EXTRA_SUBJECT, "Would you like to travel with me?");
				email.putExtra(Intent.EXTRA_TEXT, "");
				try {
					startActivity(Intent.createChooser(email,  "Send email"));
				}
				catch (android.content.ActivityNotFoundException e) {
					Toast t = Toast.makeText(builder.getContext(),  "No email", Toast.LENGTH_SHORT);
					t.show();
				}
			}
		});
		
		LinearLayout send_sms = (LinearLayout) v.findViewById(R.id.dialog_matchmake_send_sms);
		send_sms.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent sendSMS = new Intent(Intent.ACTION_VIEW);
				sendSMS.putExtra("sms_body", "Hello, wanna travel with me?");
				sendSMS.putExtra("address", gsm);
				sendSMS.setType("vnd.android-dir/mms-sms");
				startActivity(sendSMS);
			}
		});
		
		LinearLayout cancel = (LinearLayout) v.findViewById(R.id.dialog_matchmake_cancel);
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
			
		});
		return builder.create();

	}

}
