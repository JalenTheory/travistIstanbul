package fi.metropolia.lbs.travist.savedlist;

import travist.pack.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;
import fi.metropolia.lbs.travist.savedlist.ListAdapter.ViewHolder;

public class SavedlistActivity extends Activity{

	private Cursor cursor;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.savedlist_activity);
		
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
	
		ListView lv = (ListView) findViewById(R.id.savedlistview);
		ListAdapter adapter = new ListAdapter(this, cursor, 0);
		lv.setAdapter(adapter);
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

				Intent newintent = new Intent(SavedlistActivity.this, SavedlistActivity.class);
				SavedlistActivity.this.startActivity(newintent);
			}
		});
	}
	
}