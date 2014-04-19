package fi.metropolia.lbs.travist.emergency;

import travist.pack.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class EmergencyActivity extends Activity {
	LinearLayout ambulance;
	LinearLayout fire;
	LinearLayout police;
	LinearLayout coastguard;
	LinearLayout forest_fire;
	LinearLayout missing_child;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.emergency);
		
		ambulance = (LinearLayout) findViewById (R.id.emergency_ambulance);
		fire = (LinearLayout) findViewById (R.id.emergency_fire);
		police = (LinearLayout) findViewById (R.id.emergency_police);
		coastguard = (LinearLayout) findViewById (R.id.emergency_coastguard);
		forest_fire = (LinearLayout) findViewById (R.id.emergency_forest_fire);
		missing_child = (LinearLayout) findViewById (R.id.emergency_missing_child);
		
		ambulance.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "112"));
				startActivity(intent);
			}
		});
		
		fire.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "110"));
				startActivity(intent);
			}
		});
		
		police.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "155"));
				startActivity(intent);
			}
		});
		
		coastguard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "158"));
				startActivity(intent);
			}
		});
		
		forest_fire.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "177"));
				startActivity(intent);
			}
		});
		
		missing_child.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "183"));
				startActivity(intent);
			}
		});
	}

}
