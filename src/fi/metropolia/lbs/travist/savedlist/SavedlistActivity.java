package fi.metropolia.lbs.travist.savedlist;

import travist.pack.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import fi.metropolia.lbs.travist.TravistIstanbulActivity;
import fi.metropolia.lbs.travist.browsemenu.BrowseMenuActivity;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;
import fi.metropolia.lbs.travist.savedlist.ListAdapter.ViewHolder;


public class SavedlistActivity extends Activity{

	private Cursor cursor;
	private ListAdapter adapter;
	private String[] projection;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.saved);
		
		getActionBar().hide();
		
		String[] projection = new String[]{
				PlaceTableClass.ID,
				PlaceTableClass.PLACE_ID,
				PlaceTableClass.PLACE_NAME,
				PlaceTableClass.LATITUDE,
				PlaceTableClass.LONGITUDE,
				PlaceTableClass.ADDRESS,
				PlaceTableClass.CATEGORY_ID,
				PlaceTableClass.CATEGORY_NAME};
		
		cursor = this.getContentResolver().query(LBSContentProvider.PLACES_URI, projection, "IS_IN_SAVED = '1'", null, null);
	
		ListView lv = (ListView) findViewById(R.id.saved_list);
		adapter = new ListAdapter(this, cursor, 0);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				//Open saved-item on map
				//Open map and show todo-item on map
				Log.d("MAPPIA", "PAINETTU");
				Intent intent = new Intent();
				
				cursor.moveToPosition(position);
				String lati = cursor.getString(cursor.getColumnIndex(PlaceTableClass.LATITUDE));
				String Long = cursor.getString(cursor.getColumnIndex(PlaceTableClass.LONGITUDE));
				
				intent.setClass(view.getContext(), BrowseMenuActivity.class);
				intent.putExtra("lati", lati);
				intent.putExtra("longi", Long);
				view.getContext().startActivity(intent);
			}
		});
	
	}
	
	public void onBackPressed() {  
	    //do whatever you want the 'Back' button to do  
	    //as an example the 'Back' button is set to start a new Activity named 'NewActivity'  
	    startActivity(new Intent(this, TravistIstanbulActivity.class));  

	    return;  
	}
	
}
