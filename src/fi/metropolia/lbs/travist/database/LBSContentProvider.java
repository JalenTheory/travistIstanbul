package fi.metropolia.lbs.travist.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class LBSContentProvider extends ContentProvider {

	// For logging (Ex. Log.d)
	private final String LOG_TAG = "LBSContentProvider";

	// Helper class, which provides access to database
	private HelperClass dbHelper;
	private SQLiteDatabase db;

	// This is used to authenticate
	public static final String AUTHORITY = "fi.metropolia.lbs.travist.contentProvider";

	// Path to the tables
	public static final Uri USERS_URI = Uri.parse("content://" + AUTHORITY
										+ "/users");
	public static final Uri PLACES_URI = Uri.parse("content://" + AUTHORITY
										+ "/places");

	// IDs for the switch-cases (below)
	static final int USERS = 10;
	static final int USERS_ID = 11;
	static final int PLACES = 20;
	static final int PLACES_ID = 21;

	// UriMatcher
	private static final UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		URI_MATCHER.addURI(AUTHORITY, "users", USERS);
		URI_MATCHER.addURI(AUTHORITY, "users/#", USERS_ID);
		URI_MATCHER.addURI(AUTHORITY, "places", PLACES);
		URI_MATCHER.addURI(AUTHORITY, "places/#", PLACES_ID);
	}

	// The constructor calls for HelperClass's constructor and creates the
	// necessary tables if the tables don't exist previously.
	@Override
	public boolean onCreate() {
		dbHelper = new HelperClass(getContext());
		return true;
	}

	//With this you can query to the database by using the URI, if the URI
	//matches, then the selected table will be queried with the provided
	//parameters
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		Cursor cursor = null;

		switch (URI_MATCHER.match(uri)) {
		case USERS:
			queryBuilder.setTables(UserTableClass.TABLE_NAME);
			break;
		case PLACES:
			queryBuilder.setTables(PlaceTableClass.TABLE_NAME);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI" + uri);
		}
		
		if (queryBuilder.getTables() != null) {
			db = dbHelper.getReadableDatabase();
			cursor = queryBuilder.query(db, projection, selection,
							selectionArgs, null, null, sortOrder);
			cursor.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return cursor;
	}

	@Override
	public Uri insert(Uri uri, ContentValues cv) {
		long rowId = 0;

		switch (URI_MATCHER.match(uri)) {
		case PLACES:
			if (!checkForDuplicates(uri, cv)) {
				db = dbHelper.getWritableDatabase();
				rowId = db.insert(PlaceTableClass.TABLE_NAME, null, cv);
			}
			break;

		case USERS:
			if (!checkForDuplicates(uri, cv)) {
				db = dbHelper.getWritableDatabase();
				rowId = db.insert(UserTableClass.TABLE_NAME, null, cv);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}

		if (rowId > 0) {
			Log.d(LOG_TAG, "Insert was succesful!");
			getContext().getContentResolver().notifyChange(uri, null);
			Uri newUri = ContentUris.withAppendedId(uri, rowId);
			return newUri;
		}
		Log.d(LOG_TAG, "Item already exists in the database");
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int rowsDeleted = 0;
		String id;

		switch (URI_MATCHER.match(uri)) {
		case PLACES:
			db = dbHelper.getWritableDatabase();
			rowsDeleted = db.delete(PlaceTableClass.TABLE_NAME, selection,
							selectionArgs);
			break;
			
		case PLACES_ID:
			db = dbHelper.getWritableDatabase();
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(PlaceTableClass.TABLE_NAME,
								PlaceTableClass.ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(PlaceTableClass.TABLE_NAME,
								PlaceTableClass.ID + "=" + id + " and " + selection,
								selectionArgs);
			}
			break;

		case USERS:
			db = dbHelper.getWritableDatabase();
			rowsDeleted = db.delete(UserTableClass.TABLE_NAME, selection,
							selectionArgs);
			break;
			
		case USERS_ID:
			db = dbHelper.getWritableDatabase();
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsDeleted = db.delete(UserTableClass.TABLE_NAME,
								UserTableClass.ID + "=" + id, null);
			} else {
				rowsDeleted = db.delete(UserTableClass.TABLE_NAME,
								UserTableClass.ID + "=" + id + " and " + selection,
								selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsDeleted;
	}

	@Override
	public int update(Uri uri, ContentValues cv, String selection, String[] selectionArgs) {
		int rowsUpdated = 0;
		String id;

		switch (URI_MATCHER.match(uri)) {
		case PLACES:
			db = dbHelper.getWritableDatabase();
			rowsUpdated = db.update(PlaceTableClass.TABLE_NAME, cv, selection,
							selectionArgs);
			break;
			
		case PLACES_ID:
			db = dbHelper.getWritableDatabase();
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(PlaceTableClass.TABLE_NAME,
								cv, PlaceTableClass.ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(PlaceTableClass.TABLE_NAME,
								cv, PlaceTableClass.ID + "=" + id + " and " + selection,
								selectionArgs);
			}
			break;

		case USERS:
			db = dbHelper.getWritableDatabase();
			rowsUpdated = db.update(UserTableClass.TABLE_NAME, cv, selection,
							selectionArgs);
			break;
			
		case USERS_ID:
			db = dbHelper.getWritableDatabase();
			id = uri.getLastPathSegment();
			if (TextUtils.isEmpty(selection)) {
				rowsUpdated = db.update(UserTableClass.TABLE_NAME,
								cv, UserTableClass.ID + "=" + id, null);
			} else {
				rowsUpdated = db.update(UserTableClass.TABLE_NAME,
								cv, UserTableClass.ID + "=" + id + " and " + selection,
								selectionArgs);
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return rowsUpdated;
	}

	// Do we need this?
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	// Check for duplicate entries by using PLACE_ID and/or EMAIL.
	// If cursor count is bigger than 0, false is returned, hence email or
	// place_id already exists.
	private boolean checkForDuplicates(Uri uri, ContentValues cv) {
		Cursor cursor = null;

		switch (URI_MATCHER.match(uri)) {
		case PLACES:
			String place = cv.getAsString(PlaceTableClass.PLACE_ID);
			Log.d(LOG_TAG, "Place: " + place);
			cursor = this
					.getContext()
					.getContentResolver()
					.query(LBSContentProvider.PLACES_URI, null,
							PlaceTableClass.PLACE_ID + " = '" + place + "'",
							null, null);
			
			if (cursor.getCount() != 0) {
				return true;
			}
			break;

		case USERS:
			String email = cv.getAsString(UserTableClass.EMAIL);
			Log.d(LOG_TAG, "Email: " + email);
			cursor = this
					.getContext()
					.getContentResolver()
					.query(LBSContentProvider.PLACES_URI, null,
							email + " = '" + UserTableClass.EMAIL + "'", null,
							null);
			
			if (cursor.getCount() != 0) {
				return true;
			}
			break;

		default:
			throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		return false;
	}
}