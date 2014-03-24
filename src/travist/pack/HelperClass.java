package travist.pack;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HelperClass extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "Travist.DB";
	private static final int DATABASE_VERSION = 1;
	
	public HelperClass(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION); 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(UserTableClass.createUserTable());
		db.execSQL(PlaceTableClass.createPlaceTable());
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	 
public static Context provideDBContext (){
	
	
	
	return null;
}
}
