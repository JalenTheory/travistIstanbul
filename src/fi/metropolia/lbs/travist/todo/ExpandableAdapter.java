
package fi.metropolia.lbs.travist.todo;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import travist.pack.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import fi.metropolia.lbs.travist.browsemenu.BrowseMenuActivity;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;
 
public class ExpandableAdapter extends BaseExpandableListAdapter{
 
    private Context context;
    private HashMap<String, List<String>> contentMap;
    private String tag="expandableAdapter";  
    public List<String> todoList;
    public String asd;
    private String uid;
    private Cursor cursor;
    private ViewHolder holder;
    String[] projection = new String[]{
			PlaceTableClass.ID,
			PlaceTableClass.PLACE_ID,
			PlaceTableClass.PLACE_NAME,
			PlaceTableClass.LATITUDE,
			PlaceTableClass.LONGITUDE,
			PlaceTableClass.ADDRESS,
			PlaceTableClass.CATEGORY_ID,
			PlaceTableClass.CATEGORY_NAME};
    
    public ExpandableAdapter(Context context, Cursor cursor){
    	
        this.context = context;
        this.uid="1";
        this.cursor=cursor;
        holder = new ViewHolder();
        prepareLists();
    }
    
    public static class ViewHolder{
    	public String placeName;
    	public TextView group;
    	public TextView address;
    	public Button mapButton;
    	public Button saveButton;
    }
    
    private void prepareLists(){		
		todoList = new ArrayList<String>();
		contentMap = new HashMap<String, List<String>>();
		
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			holder.placeName = cursor.getString(cursor.getColumnIndex(PlaceTableClass.PLACE_NAME));
			todoList.add(holder.placeName);
			List<String> list = new ArrayList<String>();
			list.add("");
			contentMap.put(todoList.get(cursor.getPosition()), list);
		}
	}
    
    public List<String> getChildList(int pos){
    	List<String> list = (List<String>) contentMap.get(this.getGroup(pos));
    	return list;
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.contentMap.get(this.todoList.get(groupPosition))
                .get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
 
        final String childText = (String) getChild(groupPosition, childPosition);
 
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todo_matchmaking, null);
        }
 
        TextView txtListChild = (TextView) convertView.findViewById(R.id.todo_match_name); 
        txtListChild.setText(childText);
        
        /*txtListChild.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Dialog matchDialog = new Dialog(context);
				matchDialog.setContentView(R.layout.dialog_matchmaking);
				matchDialog.setTitle("Matchmaking");
				TextView matchName = (TextView) matchDialog.findViewById(R.id.dialog_matchmake_name);
				matchName.setText("Make");
				TextView matchNationality = (TextView) matchDialog.findViewById(R.id.dialog_matchmake_nationality);
				matchNationality.setText("Hiki�");
				
				LinearLayout dialogSend = (LinearLayout) matchDialog.findViewById(R.id.dialog_matchmake_send);
				dialogSend.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("Eetu", "haluu seuraa t�t� kautta");
						/* Send notification via email
						String email_address = "match_email";
						Intent email = new Intent(Intent.ACTION_SEND);
						email.setType("message/rfc822");
						email.putExtra(Intent.EXTRA_EMAIL, email_address);
						email.putExtra(Intent.EXTRA_SUBJECT, "Would you like to travel with me?");
						email.putExtra(Intent.EXTRA_TEXT, "Hi, Would you like to travel with me? Rape incoming");
						try {
							startActivity(Intent.createChooser(email,  "Send email"));
						}
						catch (android.content.ActivityNotFoundException e) {
							Toast.makeText(MainActivity.this,  "No email application found", Toast.LENGTH_SHORT);
						}*/
						
						/* Send notification via sms
						Intent sendSMS = new Intent(Intent.ACTION_VIEW);
						sendSMS.putExtra("sms_body", "Hello, wanna travel with me?");
						sendSMS.putExtra("address", "match_phone_number");
						sendSMS.setType("vnd.android-dir/mms-sms");
						startActivity(sendSMS);
						 */
						/*matchDialog.dismiss();
					}
					
				});
				
				LinearLayout dialogCancel = (LinearLayout) matchDialog.findViewById(R.id.dialog_matchmake_cancel);
				dialogCancel.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						matchDialog.dismiss();
						
					}
					
				});
				
				matchDialog.show();
				//Without setting custom width & height, the dialog looks retarded
				Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();
				int height = display.getHeight();
				int width = display.getWidth();
				matchDialog.getWindow().setLayout(width, (int) ((int)height * 0.5));
			}
		});*/
        return convertView;
    }
 
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.contentMap.get(this.todoList.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.todoList.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.todoList.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
    	
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todo_row, null);
        }
        holder.group = (TextView) convertView.findViewById(R.id.todo_listname);
        holder.group.setTypeface(null, Typeface.BOLD);
        String nameAddress = (String) getGroup(groupPosition);
        holder.group.setText(nameAddress);
        
        holder.address = (TextView) convertView.findViewById(R.id.todo_list_address);
        holder.address.setTypeface(null, Typeface.BOLD);
        String address = nameAddress.substring(nameAddress.lastIndexOf(",") + 1);
        holder.address.setText(address);
        
        holder.saveButton = (Button) convertView.findViewById(R.id.saveButton);
        holder.saveButton.setFocusable(false);
        
        LinearLayout todoShowOnMap = (LinearLayout) convertView.findViewById(R.id.todo_map);
        todoShowOnMap.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Open map and show todo-item on map
				Log.d("MAPPIA", "PAINETTU");
				Intent intent = new Intent();
				
				cursor.moveToPosition(groupPosition);
				String lati = cursor.getString(cursor.getColumnIndex(PlaceTableClass.LATITUDE));
				String Long = cursor.getString(cursor.getColumnIndex(PlaceTableClass.LONGITUDE));
				
				intent.setClass(context, BrowseMenuActivity.class);
				intent.putExtra("lati", lati);
				intent.putExtra("longi", Long);
				context.startActivity(intent);
				//TODO: put the json object into the intent
			}
		});
        
        holder.saveButton.setOnClickListener(new View.OnClickListener(){
        	//dialog gets prompted to verify users decision
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Add item to saved list?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						//add todo item to saved list and remove it from the todo list
						cursor.moveToPosition(groupPosition);
						int index = cursor.getColumnIndex(PlaceTableClass.PLACE_ID);
						String pid = cursor.getString(index);
						String url = "http://users.metropolia.fi/~eetupa/Turkki/setSaved.php?pid="+pid+"&uid="+uid;
						int index2 = cursor.getColumnIndex(PlaceTableClass.PLACE_NAME);
						String pname = cursor.getString(index2);
						Log.d(tag,"adapter url: "+url);
						UpSaved up = new UpSaved(url);
						up.upload();
						
						ContentValues cv = new ContentValues();
						cv.put(PlaceTableClass.IS_IN_SAVED, 1);
						cv.put(PlaceTableClass.IS_IN_TODO, 0);
						context.getContentResolver().update(LBSContentProvider.PLACES_URI, cv, PlaceTableClass.PLACE_NAME+" = '"+pname+"'", null);
						todoList.remove(groupPosition);
						notifyDataSetChanged();
       					notifyDataSetInvalidated();
						
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
					
				});
				AlertDialog dialog = builder.create();
				dialog.show();	
			}			
		});
        
        return convertView;
    }
 
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}