package fi.metropolia.lbs.travist.browsemenu;

import org.mapsforge.map.android.graphics.AndroidGraphicFactory;

import travist.pack.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import fi.metropolia.lbs.travist.CheckInternetConnectivity;
import fi.metropolia.lbs.travist.foursquare_api.Criteria;
import fi.metropolia.lbs.travist.offline_map.TravistMapViewAdapter;
import fi.metropolia.lbs.travist.offline_map.TravistMapViewAdapterFragment;
import android.view.ActionMode;

@SuppressLint("NewApi")
public class BrowseMenuActivity extends Activity implements ActionMode.Callback {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] categoriesItem;
	private ActionMode mActionMode;
	private TravistMapViewAdapter tmva;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.categorydrawer);
		
		if (savedInstanceState == null) {
			AndroidGraphicFactory.createInstance(getApplication());
			
			Fragment fragment = new TravistMapViewAdapterFragment();
			tmva = TravistMapViewAdapter.getInstance();
			Bundle bundle = this.getIntent().getExtras();
			
			if (bundle == null) {
				//This is for the regular browse-intent, so nothing's added as arguments
			}
			
			else {
				fragment.setArguments(this.getIntent().getExtras());
			}
			
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
		}
	
		
		Integer[] drawableIcons ={R.drawable.arts,R.drawable.food,R.drawable.nightlife,R.drawable.medical,R.drawable.shopping,R.drawable.travel};

		mTitle = mDrawerTitle = getTitle();
		categoriesItem = getResources().getStringArray(R.array.categories);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerList.setAdapter(new ListViewAdapter(this, categoriesItem, drawableIcons));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		if (!CheckInternetConnectivity.isInternetAvailable(this)){// if no internet then dont fuck with drawer navigation--with love--->ram
			
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setHomeButtonEnabled(false);
			setTitle("Travist-Offline Mode");
			mDrawerTitle=mTitle;
			Toast.makeText(this, mTitle, Toast.LENGTH_SHORT);
			
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
			for (int i=0; i<3; i++){
				
				Toast.makeText(this, "No Internet connection", Toast.LENGTH_LONG).show();
			}
		}

		

		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				
				//Log.i("title",mTitle);
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		return super.onCreateOptionsMenu(menu);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.action_textview:
			Toast.makeText(this, "works", Toast.LENGTH_SHORT);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/* The click listener for ListView in the navigation drawer */
private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
			
			Criteria crit = new Criteria();
			crit.setNear("istanbul");
			crit.setLimit("30");
				
			//ADD HERE GPS LOC / CRIT.SETLATLONG
			double lat = 0;
			double lng = 0;
			
			if (TravistMapViewAdapter.getInstance().getLocOverLay().getPosition() != null) {
				lat = TravistMapViewAdapter.getInstance().getLocOverLay().getPosition().latitude;
				lng = TravistMapViewAdapter.getInstance().getLocOverLay().getPosition().longitude;
			}/*else {
				if (TravistMapViewAdapter.getInstance().getLocOverLay() != null) {
					lat = TravistMapViewAdapter.getInstance().getLocOverLay().getLastLocation().getLatitude();
					lng = TravistMapViewAdapter.getInstance().getLocOverLay().getLastLocation().getLongitude();
				}
			}*/
			
			Log.d("LOG", "LatLng:" + lat + ", " + lng);
			if (lat != 0 && lng != 0) {
				crit.setLatLon(lat, lng);
			}
			
			switch(position) {
			case 0:
				crit.setCategoryId(Criteria.ARTS_AND_ENTERTAIMENT);
				break;
			case 1:
				crit.setCategoryId(Criteria.FOOD);
				break;
			case 2:
				crit.setCategoryId(Criteria.NIGHTLIFE_SPOTS);
				break;
			case 3: 
				crit.setCategoryId(Criteria.MEDICAL_CENTER);
				break;
			case 4: 
				crit.setCategoryId(Criteria.SHOP_AND_SERVICE);
				break;
			case 5:
				crit.setCategoryId(Criteria.TRAVEL_AND_TRANSPORT);
				break;
			}
			TravistMapViewAdapter.getInstance().loadPlaces(crit);
		}
	}



	private void selectItem(int position) {		 
		mDrawerList.setItemChecked(position, true);
		setTitle(categoriesItem[position]);
		mDrawerLayout.closeDrawer(mDrawerList);
		Log.d("LOG", "Click: " + position);
		//Fragment fragment = new TestTravistMapViewAdapterFragment();
	//	Bundle args = new Bundle();
		//args.putInt(TestTravistMapViewAdapterFragment.TEST_CATEGORY, position);
	//	fragment.setArguments(args);
	//	FragmentManager fragmentManager = getFragmentManager();
		//fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
        case R.id.actionmode_item_route:
        	tmva.logD("routing", this);
            
            // handles the ontap of origin
            tmva.changeViewToSelectOrigin();
            //tmva.closeLastBubble();
            //tmva.refreshPois();
            mode.finish(); // Action picked, so close the CAB
            return true;
        case R.id.actionmode_item_todolist:
        	tmva.logD("todolist", this);
        	
        	tmva.addToTodolist();
        	tmva.closeLastBubble();
        	tmva.refreshPois();
        	mode.finish(); // Action picked, so close the CAB
        	return true;
        default:
            return false;
        }
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		// Inflate a menu resource providing context menu items
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.action_mode, menu);
        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub
		mActionMode = null;
		
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

}
