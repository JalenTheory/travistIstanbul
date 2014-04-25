package fi.metropolia.lbs.travist.database;

public class PlaceTableClass {
	
	//user table
	public static String ID ="_id";
	public static String PLACE_ID="PLACE_ID";
	public static String TABLE_NAME= "PLACE";
	public static String PLACE_NAME ="NAME";
	public static String LONGITUDE ="LONGITUDE";
	public static String LATITUDE="LATITUDE";
	public static String ADDRESS="ADDRESS";
	public static String CATEGORY_ID="CATEGORY_ID";
	public static String CATEGORY_NAME="CATEGORY_NAME";
	public static String IS_IN_TODO="IS_IN_TODO";
	public static String IS_IN_SAVED="IS_IN_SAVED";

public static String createPlaceTable(){
	String createTableQuery = "CREATE TABLE IF NOT EXISTS "
			+TABLE_NAME 
			+"("
			+ ID
			+" INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PLACE_ID
			+ " TEXT UNIQUE NOT NULL,"
			+ PLACE_NAME
			+ " TEXT,"
			+ LONGITUDE
			+ " TEXT,"
			+ LATITUDE
			+ " TEXT,"
			+ ADDRESS
			+ " TEXT, "
			+ CATEGORY_ID
			+ " TEXT,"
			+ CATEGORY_NAME
			+ " TEXT,"
			+ IS_IN_TODO
			+ " INTEGER,"
			+ IS_IN_SAVED
			+ " INTEGER"
			+ ");";
	return createTableQuery;
	}
}
