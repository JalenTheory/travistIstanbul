package fi.metropolia.lbs.travist.todo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import travist.pack.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.todo_activity);
		url = "http://users.metropolia.fi/~eetupa/Turkki/getTodosByUid.php?uid="
				+ userId;
		download = "places";
		new Dl(download).execute();
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
		adapter = new ExpandableAdapter(this,
				places,
				userId);
				//todoList, contentMap);
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
		/*Button showOnMap = (Button) expLv.findViewById(R.id.showOnMap);
		if(showOnMap==null){
			Log.d(tag,"button=null");
		}
		showOnMap.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				Log.d(tag,"buttonpressed");
			}
			
		});*/
	}
/*
	private void prepareLists(){		
		todoList = new ArrayList<String>();
		contentMap = new HashMap<String, List<String>>();
		
		for(int i=0; i<places.length;i++){
			todoList.add(places[i].optString("PLACE_NAME")+", "+places[i].optString("CATEGORY_NAME"));
			List<String> list = new ArrayList<String>();
			list.add("");
			contentMap.put(todoList.get(i), list);
		}
	}*/
	
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
