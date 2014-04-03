package fi.metropolia.lbs.travist;

import travist.pack.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity;

public class TravistIstanbulActivity extends Activity {
	public static final String TAG = "travist debug";
	
	private Button createButton(final Class<?> testCaseClass) {
		Button button = new Button(this);
		button.setText(testCaseClass.getSimpleName());
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				startActivity(new Intent(TravistIstanbulActivity.this,
						testCaseClass));
			}
		});
		return button;
	}
	
    public void openMap(View view) {
    	Intent intent = new Intent(this, TestOfflineMapActivity.class);
    	startActivity(intent);
    }
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // Make buttons according to Activities of test cases
        LinearLayout linearLayout = (LinearLayout) findViewById(
        		R.id.main_view_layout);
        linearLayout.addView(createButton(
        		fi.metropolia.lbs.travist.foursquare_api.FoursquareActivity.class));
        
        linearLayout.addView(createButton(
        		fi.metropolia.lbs.travist.offline_map.TestOfflineMapActivity.class));
        
        
        
    }
    

}