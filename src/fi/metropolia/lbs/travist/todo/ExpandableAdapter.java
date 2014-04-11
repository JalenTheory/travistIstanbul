
package fi.metropolia.lbs.travist.todo;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import travist.pack.R;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
 
public class ExpandableAdapter extends BaseExpandableListAdapter implements Parcelable{
 
    private Context context;
    private HashMap<String, List<String>> contentMap;
    private String tag="expandableAdapter";
    
    private List<String> todoList;
    private JSONObject[] places;
    private String uid;
    
    public ExpandableAdapter(Context context, JSONObject[] places, int uid){
    	this.places=places;
        this.context = context;
        this.uid=String.valueOf(uid);
        prepareLists();
        //this.contentMap = listChildData;
    }
 
    private void prepareLists(){		
		todoList = new ArrayList<String>();
		contentMap = new HashMap<String, List<String>>();
		
		for(int i=0; i<places.length;i++){
			todoList.add(places[i].optString("PLACE_NAME")+", "+places[i].optString("CATEGORY_NAME"));
			List<String> list = new ArrayList<String>();
			list.add("");
			contentMap.put(todoList.get(i), list);
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.todolist_group, null);
        }
 
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.listHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
        
        Button mapButton = (Button) convertView.findViewById(R.id.mapButton);
        mapButton.setFocusable(false);
        Button saveButton = (Button) convertView.findViewById(R.id.saveButton);
        saveButton.setFocusable(false);
        
        mapButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context, TodoActivity.class);
				//TODO: put the json object into the intent
				context.startActivity(intent);
			}			
		});
        
        saveButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View arg0) {
				String pid = places[groupPosition].optString("PID");
				String url = "http://users.metropolia.fi/~eetupa/Turkki/setSaved.php?pid="+pid+"&uid="+uid;
				Log.d(tag,"adapter url: "+url);
				UpSaved up = new UpSaved(url);
				up.upload();
			}			
		});
        
        return convertView;
    }
 
    
    //PARCELABLE
    @Override
    public boolean hasStableIds() {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
}