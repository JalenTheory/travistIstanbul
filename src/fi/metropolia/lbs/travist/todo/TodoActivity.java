package fi.metropolia.lbs.travist.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import travist.pack.R;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;

public class TodoActivity extends Activity {

	private String tag = "Todo";
	private String url;
	private JSONObject places[];
	private JSONObject matches[];
	//userid and placeid's should be taken from database
	private int userId = 1;
	private int pid1 = 123;
	private int pid2 = 456;
	private int pid3 = 789;
	private String download = "";
	
    private ExpandableListAdapter adapter;
    private ExpandableListView expLv;
    private List<String> todoList;
    private List<String> checkList;
    private HashMap<String, List<String>> contentMap;
    private int groupPosition;
    private LinearLayout listll;
    private TextView listllChild;
    private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_activity);
		url = "http://users.metropolia.fi/~eetupa/Turkki/getTodosByUid.php?uid="
				+ userId;
		download = "places";
		
		ContentValues cv = new ContentValues();
		cv.put(PlaceTableClass.PLACE_ID, "111");
		cv.put(PlaceTableClass.PLACE_NAME, "Manly Spa");
		cv.put(PlaceTableClass.LATITUDE, "10");
		cv.put(PlaceTableClass.LONGITUDE, "10");
		cv.put(PlaceTableClass.ADDRESS, "Sparoad");
		cv.put(PlaceTableClass.CATEGORY_ID, "111");
		cv.put(PlaceTableClass.CATEGORY_NAME, "Spa");
		cv.put(PlaceTableClass.IS_IN_TODO, "1");
		cv.put(PlaceTableClass.IS_IN_SAVED, "0");
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv);

		String[] projection = new String[]{
				PlaceTableClass.ID,
				PlaceTableClass.PLACE_ID,
				PlaceTableClass.PLACE_NAME,
				PlaceTableClass.LATITUDE,
				PlaceTableClass.LONGITUDE,
				PlaceTableClass.ADDRESS,
				PlaceTableClass.CATEGORY_ID,
				PlaceTableClass.CATEGORY_NAME};
		cursor = this.getContentResolver().query(LBSContentProvider.PLACES_URI, projection, "IS_IN_TODO = '1'", null, null);
		
		createList();
	}
	
	class Dl extends AsyncTask<String, Void, String> {
		String download;

		public Dl(String d) {
			this.download = d;
		}

		protected String doInBackground(String... urls) {

			if (download == "places") {
				DlPlaces dlplaces = new DlPlaces(url);
				try {
					places = dlplaces.downloadPlaces();
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (download == "matches") {
				DlMatches dlmatches = new DlMatches(url);
				try {
					matches = dlmatches.downloadMatches();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (download == "places") {
				createList();
			} else if (download == "matches") {
				showMatches();
			}
		}
	}

	private void createList() {
		//creates the expandablelistview and listeners for groups in the exp.listview
		//prepareLists();
		
		expLv = (ExpandableListView) findViewById(R.id.expandableListView);
		//adapter = new ExpandableAdapter(this, todoList, contentMap);
		adapter = new ExpandableAdapter(this, cursor);
		expLv.setAdapter(adapter);
		
		expLv.setOnGroupExpandListener(new OnGroupExpandListener(){
			@Override
			public void onGroupExpand(int gpos) {
				checkList = (List<String>) ((ExpandableAdapter) adapter).getChildList(gpos);
				if(checkList.get(0).equals("")){
					groupPosition = gpos;
					download="matches";				
					int pid=0;
					/*	TODO:
					 * 	The pid should come from the app DB:
					 * 	get the group id by group position from places[].
					 */
					if(groupPosition == 0){
						pid = pid1;
					}else if(groupPosition == 1){
						pid = pid3;
					}
					url = "http://users.metropolia.fi/~eetupa/Turkki/getUsersByPid.php?pid="+pid;
					new Dl(download).execute();
				}
			}
		});
	}

	private void prepareLists(){		
		todoList = new ArrayList<String>();
		contentMap = new HashMap<String, List<String>>();
		/* getting todos from the server
		for(int i=0; i<places.length;i++){
			todoList.add(places[i].optString("PLACE_NAME")+", "+places[i].optString("CATEGORY_NAME"));
			List<String> list = new ArrayList<String>();
			list.add("");
			contentMap.put(todoList.get(i), list);
		}*/
		
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String place = cursor.getString(cursor.getColumnIndex(PlaceTableClass.PLACE_NAME));
			String category = cursor.getString(cursor.getColumnIndex(PlaceTableClass.CATEGORY_NAME));
			todoList.add(place+", "+category);
			List<String> list = new ArrayList<String>();
			list.add("");
			contentMap.put(todoList.get(cursor.getPosition()), list);
		}
	}
	
	private void showMatches(){
		checkList.removeAll(checkList);
		for(int i=0;i<matches.length;i++){
			if(!matches[i].optString("UID").equals(String.valueOf(userId))){	
				checkList.add(matches[i].optString("NAME")+", "+matches[i].optString("COUNTRY"));			
				((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
				((BaseExpandableListAdapter) adapter).notifyDataSetInvalidated();
			}
		}
	}
}
