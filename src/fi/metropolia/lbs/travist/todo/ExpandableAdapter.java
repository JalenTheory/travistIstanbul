
package fi.metropolia.lbs.travist.todo;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import travist.pack.R;
import android.content.ContentValues;
import android.content.Context;
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
    private List<String> todoList;
    private String uid;
    private Cursor cursor;
    private ViewHolder holder;
    
    public ExpandableAdapter(
    		Context context, 
    		//List<String> todoList, 
    		//HashMap<String, List<String>> contentMap
    		Cursor cursor
    		){
        this.context = context;
        this.uid=String.valueOf(uid);
       //this.contentMap = contentMap;
       //this.todoList = todoList;
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
            convertView = inflater.inflate(R.layout.todolist_item, null);
        }
 
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.listItem);
 
        txtListChild.setText(childText);
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
    	
        //String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todolist_group, null);
        }
 
        holder.group = (TextView) convertView.findViewById(R.id.listHeader);
        holder.group.setTypeface(null, Typeface.BOLD);
        holder.group.setText((String) getGroup(groupPosition));
        
        /*
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.listHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);*/
        
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

				ContentValues cv = new ContentValues();
				cv.put(PlaceTableClass.IS_IN_SAVED, 1);
				//Log.d(tag,"adapter onclick: "+holder.placeName);
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