package fi.metropolia.lbs.travist.userprofile;

import android.content.Context;
import android.database.Cursor;
import fi.metropolia.lbs.travist.database.LBSContentProvider;
import fi.metropolia.lbs.travist.database.PlaceTableClass;
import fi.metropolia.lbs.travist.database.UserTableClass;

public class UserHelper {

	private static UserHelper instance = null;
	private static Cursor cursor;
	private static String[] projection;
	protected UserHelper() {
		// Exists only to defeat instantiation.
	}

	public static UserHelper getInstance() {
		if (instance == null) {
			instance = new UserHelper();
		}
		return instance;
	}

	public String getName(Context c, String email) {
		projection = new String[]{UserTableClass.NAME};
		cursor = c.getContentResolver().query(LBSContentProvider.USERS_URI, projection, UserTableClass.EMAIL+" = '"+email+"'", null, null);
		cursor.moveToFirst();
		return cursor.getString(cursor.getColumnIndex(PlaceTableClass.PLACE_NAME));
	}
/*
	public static String getCountry(Context c, String email) {
		projection = new String[]{UserTableClass.COUNTRY};
		cursor = c.getContentResolver().query(LBSContentProvider.USERS_URI, projection, "IS_IN_TODO = '1'", null, null);
		return null;
	}

	public static String getGSM(Context c, String email) {
		projection = new String[]{PlaceTableClass.ID};
		cursor = c.getContentResolver().query(LBSContentProvider.USERS_URI, projection, "IS_IN_TODO = '1'", null, null);
		return null;
	}*/
}