package fi.metropolia.lbs.travist.savedlist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;
import fi.metropolia.lbs.travist.database.PlaceTableClass;

@SuppressLint("NewApi")
public class ListAdapter extends CursorAdapter{

	private LayoutInflater inflater;
	private Context context;
	private ViewHolder holder;
	
	public ListAdapter(Context context, Cursor c, int flags) {
		super(context, c, flags);
		this.context=context;
		holder = new ViewHolder();
		inflater = LayoutInflater.from(context);
	}

	public static class ViewHolder{
		public String itemName;
		public String itemAddress;
		public TextView itemInfo;
		public TextView itemInfoAddress;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		int index = cursor.getColumnIndex(PlaceTableClass.PLACE_NAME);
		holder.itemName = cursor.getString(index);
		int index1 = cursor.getColumnIndex(PlaceTableClass.ADDRESS);
		holder.itemAddress = cursor.getString(index1);
		holder.itemInfo.setText(holder.itemName);
		holder.itemInfoAddress.setText(holder.itemAddress);
		
		/*String nameAddress = (String) getGroup(groupPosition);
        String name = nameAddress.substring(0, nameAddress.indexOf(","));
        holder.group.setText(name);
        
        holder.address = (TextView) convertView.findViewById(R.id.todo_list_address);
        holder.address.setTypeface(null, Typeface.BOLD);
        String address = nameAddress.substring(nameAddress.lastIndexOf(",") + 1);
        holder.address.setText(address);*/
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
		
		View view = inflater.inflate(R.layout.saved_row, viewgroup, false);
		holder.itemInfo = (TextView) view.findViewById(R.id.saved_listname);
		holder.itemInfoAddress = (TextView) view.findViewById(R.id.saved_listaddress);
		
		view.setTag(holder);
		return view;
	}

}
