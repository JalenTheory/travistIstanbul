package fi.metropolia.lbs.travist.todo;

import travist.pack.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

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
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
		
		/* EMAIL:
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
			Toast t = Toast.makeText(TodoActivity.this,  "No email", Toast.LENGTH_SHORT);
			t.show();
		}*/
		
		/* SMS
		Intent sendSMS = new Intent(Intent.ACTION_VIEW);
		sendSMS.putExtra("sms_body", "Hello, wanna travel with me?");
		sendSMS.putExtra("address", gsm);
		sendSMS.setType("vnd.android-dir/mms-sms");
		startActivity(sendSMS);
		 */
		return builder.create();

	}

}
