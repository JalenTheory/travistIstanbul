package fi.metropolia.lbs.travist.browsemenu;


import travist.pack.R;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class ListViewAdapter extends ArrayAdapter<String>{
private final Activity context;
private final String[] categoryname;
private final Integer[] iconId;
public ListViewAdapter(Activity context,String[] category, Integer[] imageId) {
super(context, R.layout.categoryname, category);
this.context = context;
this.categoryname= category;
this.iconId = imageId;
}
@Override
public View getView(int position, View view, ViewGroup parent) {
LayoutInflater inflater = context.getLayoutInflater();
View rowView= inflater.inflate(R.layout.categoryname, null, true);
TextView txtTitle = (TextView) rowView.findViewById(R.id.categorynametextView);
ImageView imageView = (ImageView) rowView.findViewById(R.id.imageViewCategoryIcon);
txtTitle.setText(categoryname[position]);
imageView.setImageResource(iconId[position]);
return rowView;
}
}
