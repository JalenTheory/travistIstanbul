package fi.metropolia.lbs.travist.register;

import travist.pack.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class RegisterActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		
		LinearLayout register_button = (LinearLayout) findViewById (R.id.register_register);
		LinearLayout cancel_button = (LinearLayout) findViewById (R.id.register_cancel);
		register_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
		cancel_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
			
		});
	}

}
