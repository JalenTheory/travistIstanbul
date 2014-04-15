
package fi.metropolia.lbs.travist.todo;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import travist.pack.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
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
    	public Button mapButton;
    	public Button saveButton;
    }
    
    private void prepareLists(){		
		todoList = new ArrayList<String>();
		contentMap = new HashMap<String, List<String>>();
		
		for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			holder.placeName = cursor.getString(cursor.getColumnIndex(PlaceTableClass.PLACE_NAME));
			String category = cursor.getString(cursor.getColumnIndex(PlaceTableClass.CATEGORY_NAME));
			todoList.add(holder.placeName+", "+category);
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
            convertView = inflater.inflate(R.layout.todo_row, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.listItem); 
        txtListChild.setText(childText);   
        txtListChild.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(tag,"child touched");
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("HÖHÖÖ").setTitle("asd");
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		});
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
            convertView = inflater.inflate(R.layout.todolist_group, null);
        }
 
        holder.group = (TextView) convertView.findViewById(R.id.listHeader);
        holder.group.setTypeface(null, Typeface.BOLD);
        holder.group.setText((String) getGroup(groupPosition));
        /*
        holder.group.setOnLongClickListener(new View.OnLongClickListener() {			
			@Override
			public boolean onLongClick(View v) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage("Remove item from TODO-list?")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					
                   public void onClick(DialogInterface dialog, int id) {
                	   	ContentValues cv = new ContentValues();
       					cv.put(PlaceTableClass.IS_IN_TODO, 0);
       					context.getContentResolver().update(LBSContentProvider.PLACES_URI,
       							cv, PlaceTableClass.PLACE_NAME+" = '"+holder.placeName +"'", null);
       					todoList.remove(groupPosition);
       					notifyDataSetChanged();
       					notifyDataSetInvalidated();
       					cursor = context.getContentResolver().query(LBSContentProvider.PLACES_URI, projection, "IS_IN_TODO = '1'", null, null);
       					//((Activity) context).finish();
       					//Intent intent = new Intent(context, TodoActivity.class);
       					//context.startActivity(intent);
                   }
               })
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            	   
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.cancel();
                   }
               });
				AlertDialog dialog = builder.create();
				dialog.show();
				return false;
			}
		});*/
        
        holder.mapButton = (Button) convertView.findViewById(R.id.mapButton);
        holder.mapButton.setFocusable(false);
        holder.saveButton = (Button) convertView.findViewById(R.id.saveButton);
        holder.saveButton.setFocusable(false);
        
        holder.mapButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, TodoActivity.class);
				//TODO: put the json object into the intent
				context.startActivity(intent);
			}			
		});
        
        holder.saveButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				
				cursor.moveToPosition(groupPosition);
				int index = cursor.getColumnIndex(PlaceTableClass.PLACE_ID);
				String pid = cursor.getString(index);
				String url = "http://users.metropolia.fi/~eetupa/Turkki/setSaved.php?pid="+pid+"&uid="+uid;
				Log.d(tag,"adapter url: "+url);
				UpSaved up = new UpSaved(url);
				up.upload();
				
				ContentValues cv = new ContentValues();
				cv.put(PlaceTableClass.IS_IN_SAVED, 1);
				context.getContentResolver().update(LBSContentProvider.PLACES_URI, cv, PlaceTableClass.PLACE_NAME+" = '"+holder.placeName +"'", null);
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