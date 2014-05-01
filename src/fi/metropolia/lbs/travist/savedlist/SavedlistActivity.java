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
Intent intent = new Intent();
				
				cursor.moveToPosition(position);
				String placeId = cursor.getString(cursor.getColumnIndex(PlaceTableClass.PLACE_ID));
				String placeName = cursor.getString(cursor.getColumnIndex(PlaceTableClass.PLACE_NAME));
				String lati = cursor.getString(cursor.getColumnIndex(PlaceTableClass.LATITUDE));
				String Long = cursor.getString(cursor.getColumnIndex(PlaceTableClass.LONGITUDE));
				String address = cursor.getString(cursor.getColumnIndex(PlaceTableClass.ADDRESS));
				String categoryId = cursor.getString(cursor.getColumnIndex(PlaceTableClass.CATEGORY_ID));
				String categoryName = cursor.getString(cursor.getColumnIndex(PlaceTableClass.CATEGORY_NAME));
				
				intent.setClass(view.getContext(), BrowseMenuActivity.class);
				intent.putExtra("placeId", placeId);
				intent.putExtra("placeName", placeName);
				intent.putExtra("lati", lati);
				intent.putExtra("longi", Long);
				intent.putExtra("address", address);
				intent.putExtra("categoryId", categoryId);
				intent.putExtra("categoryName", categoryName);
				intent.putExtra("iconURL", "list");
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
