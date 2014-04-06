package fi.metropolia.lbs.travist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HelperClass extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Travist.DB";
	private static final int DATABASE_VERSION = 1;
	private final String LOG_TAG = "HelperClass";

	public HelperClass(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		Log.d(LOG_TAG, "dbhelper created");
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d(LOG_TAG, "dbOnCreate");
		db.execSQL(UserTableClass.createUserTable());
		db.execSQL(PlaceTableClass.createPlaceTable());
	}

	//Implement this?
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
