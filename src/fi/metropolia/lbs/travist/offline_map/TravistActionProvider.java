package fi.metropolia.lbs.travist.offline_map;

import travist.pack.R;
import android.content.Context;
import android.support.v4.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class TravistActionProvider extends ActionProvider {
	private Context mContext;

	public TravistActionProvider(Context context) {
		super(context);
		
		
	}
	public View onCreateActionView(MenuItem forItem) {
	    // Inflate the action view to be shown on the action bar.
	    LayoutInflater layoutInflater = LayoutInflater.from(mContext);
	    View view = layoutInflater.inflate(R.layout.action_provider, null);
	    ImageButton button = (ImageButton) view.findViewById(R.id.ap_test_button);
	    button.setOnClickListener(new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            Toast.makeText(mContext, "testing", Toast.LENGTH_LONG);
	        }
	    });
	    return view;
	}
	/*
	@Override
	public View onCreateActionView(){
	  View view = View.inflate(context, R.layout.action_layout, null);

	  final PopupMenu menu = new PopupMenu(mContext, view);
	  menu.inflate(R.menu.submenu);
	  menu.setOnMenuItemClickListener(this);

	  view.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v){
	        menu.show();
	    }
	});
	*/
	return view;
	@Override
	public View onCreateActionView() {
		// TODO Auto-generated method stub
		return null;
	}

}
