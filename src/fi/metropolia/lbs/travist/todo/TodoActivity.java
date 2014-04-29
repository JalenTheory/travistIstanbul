package fi.metropolia.lbs.travist.todo;

import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import travist.pack.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.metropolia.lbs.travist.TravistIstanbulActivity;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;

public class TodoActivity extends Activity {

	private String tag = "Todo";
	private String url;
	private JSONObject places[];
	private JSONObject matches[];
	//useremail should be taken from database
	private String userEmail = "RAM@RAM.FI";
	private String download = "";
	
    private ExpandableListAdapter adapter;
    private ExpandableListView expLv;
    private List<String> todoList;
    private List<String> childList;
    private HashMap<String, List<String>> contentMap;
    private int groupPosition;
    private LinearLayout listll;
    private TextView listllChild;
    private Cursor cursor;
    ProgressDialog pd;
 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo);
		
		ContentValues cv = new ContentValues();
		cv.put(PlaceTableClass.PLACE_ID, "123");
		cv.put(PlaceTableClass.PLACE_NAME, "Manly Spa");
		cv.put(PlaceTableClass.LATITUDE, "10");
		cv.put(PlaceTableClass.LONGITUDE, "10");
		cv.put(PlaceTableClass.ADDRESS, "Sparoad");
		cv.put(PlaceTableClass.CATEGORY_ID, "111");
		cv.put(PlaceTableClass.CATEGORY_NAME, "Spa");
		cv.put(PlaceTableClass.IS_IN_TODO, "1");
		cv.put(PlaceTableClass.IS_IN_SAVED, "0");
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv);
		
		ContentValues cv1 = new ContentValues();
		cv1.put(PlaceTableClass.PLACE_ID, "456");
		cv1.put(PlaceTableClass.PLACE_NAME, "Creamed");
		cv1.put(PlaceTableClass.LATITUDE, "10");
		cv1.put(PlaceTableClass.LONGITUDE, "11");
		cv1.put(PlaceTableClass.ADDRESS, "Iceroad");
		cv1.put(PlaceTableClass.CATEGORY_ID, "111");
		cv1.put(PlaceTableClass.CATEGORY_NAME, "Ice cream");
		cv1.put(PlaceTableClass.IS_IN_TODO, "1");
		cv1.put(PlaceTableClass.IS_IN_SAVED, "0");
		this.getContentResolver().insert(LBSContentProvider.PLACES_URI, cv1);

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

		@Override
		protected void onPreExecute(){
			pd = new ProgressDialog(TodoActivity.this);
			pd.setMessage("Getting matches");
			pd.show();
		}
		protected String doInBackground(String... urls) {

			DlMatches dlmatches = new DlMatches(url);
			try {
				matches = dlmatches.downloadMatches();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			pd.cancel();
			showMatches();
		}
	}

	private void createList() {
		//creates the expandablelistview and onexpandlisteners for groups in the exp.listview

		expLv = (ExpandableListView) findViewById(R.id.todo_list);
		adapter = new ExpandableAdapter(this, cursor);
		expLv.setAdapter(adapter);
		
		expLv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					final int pos, long id) {
				AlertDialog.Builder builder = new AlertDialog.Builder(TodoActivity.this);
				builder.setMessage("Remove item from todolist?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						todoList = (List<String>) ((ExpandableAdapter) adapter).todoList;
						
						ContentValues cv = new ContentValues();
       					cv.put(PlaceTableClass.IS_IN_TODO, 0);
       					Log.d("moi","asd: "+todoList.get(pos).toString());
       					TodoActivity.this.getContentResolver().update(LBSContentProvider.PLACES_URI,
       							cv, PlaceTableClass.PLACE_NAME+" = '"+todoList.get(pos).toString()+"'", null);
       					
       					todoList.remove(pos);
       					((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
       					((BaseExpandableListAdapter) adapter).notifyDataSetInvalidated();
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
					
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return false;
			}
		});
		
		expLv.setOnChildClickListener(new OnChildClickListener(){

			@Override
			public boolean onChildClick(ExpandableListView exlv, View v,
					int gpos, int cpos, long id) {
				Log.d("moi","childtext: "+childList.get(cpos).toString());
				if(!childList.get(cpos).toString().equals("No matches")){
					MatchDialog md = new MatchDialog();
					Bundle bundle = new Bundle();
					bundle.putString("email", matches[cpos].optString("EMAIL"));
					bundle.putString("name", matches[cpos].optString("NAME"));
					bundle.putString("country", matches[cpos].optString("COUNTRY"));
					bundle.putString("gsm", matches[cpos].optString("GSM"));
					md.setArguments(bundle);
					md.show(getFragmentManager(), "todo");
				}
				return false;
			}
			
		});
		
		expLv.setOnGroupExpandListener(new OnGroupExpandListener(){
			@Override
			public void onGroupExpand(int gpos) {
				childList = (List<String>) ((ExpandableAdapter) adapter).getChildList(gpos);
				
				if(childList.get(0).equals("")){
					groupPosition = gpos;
					download="matches";				
					cursor.moveToPosition(groupPosition);
					int index = cursor.getColumnIndex(PlaceTableClass.PLACE_ID);
					String pid = cursor.getString(index);
					url = "http://users.metropolia.fi/~eetupa/Turkki/getUsersByPid.php?pid="+pid;
					Log.d(tag,"onexpand url: "+url);
					new Dl().execute();
				}
			}
		});
	}

	private void showMatches(){
		childList.removeAll(childList);
		for(int i=0;i<matches.length;i++){
			if(!matches[i].optString("EMAIL").equals(String.valueOf(userEmail))){
				if(!matches[i].optString("EMAIL").equals("null")){
					Log.d("moi","uid: "+matches[i].optString("UID"));
					childList.add(matches[i].optString("NAME")+", "+matches[i].optString("COUNTRY"));
				}else{
					childList.add("No matches");
				}
			}
		}
		((BaseExpandableListAdapter) adapter).notifyDataSetChanged();
		((BaseExpandableListAdapter) adapter).notifyDataSetInvalidated();
	}
	
	public void onBackPressed() {  
	    //do whatever you want the 'Back' button to do  
	    //as an example the 'Back' button is set to start a new Activity named 'NewActivity'  
	    startActivity(new Intent(this, TravistIstanbulActivity.class));  

	    return;  
	}
}
