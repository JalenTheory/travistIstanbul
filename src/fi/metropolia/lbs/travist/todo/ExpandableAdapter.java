
package fi.metropolia.lbs.travist.todo;
 
import java.util.HashMap;
import java.util.List;

import travist.pack.R;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
 
public class ExpandableAdapter extends BaseExpandableListAdapter {
 
    private Context context;
    private List<String> headerList; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<String>> contentMap;
    private String tag="expandableAdapter";
    
    public ExpandableAdapter(Context context, List<String> listDataHeader,
            HashMap<String, List<String>> listChildData) {
    	Log.d(tag, "constructor");
        this.context = context;
        this.headerList = listDataHeader;
        this.contentMap = listChildData;
    }
 
    public List<String> getChildList(int pos){
    	List<String> list = (List<String>) contentMap.get(this.getGroup(pos));
    	return list;
    }
    
    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.contentMap.get(this.headerList.get(groupPosition))
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
        return this.contentMap.get(this.headerList.get(groupPosition))
                .size();
    }
 
    @Override
    public Object getGroup(int groupPosition) {
        return this.headerList.get(groupPosition);
    }
 
    @Override
    public int getGroupCount() {
        return this.headerList.size();
    }
 
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
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