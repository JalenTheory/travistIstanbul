package fi.metropolia.lbs.travist.savedlist;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
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
		public TextView itemInfo;
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		int index = cursor.getColumnIndex(PlaceTableClass.PLACE_NAME);
		holder.itemName = cursor.getString(index);
		int index1 = cursor.getColumnIndex(PlaceTableClass.CATEGORY_NAME);
		String cateName = cursor.getString(index1);
		holder.itemInfo.setText(holder.itemName+", "+cateName);
		
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
		
		View view = inflater.inflate(R.layout.savedlist_item, viewgroup, false);
		holder.itemInfo = (TextView) view.findViewById(R.id.savedlistItem);
		view.setTag(holder);
		return view;
	}

}
