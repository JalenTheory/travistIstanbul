package travist.pack;

public class UserTableClass {
	
	//user table
	public static String ID ="_id";
	public static String TABLE_NAME= "USER";
	public static String NAME ="NAME";
	public static String COUNTRY ="COUNTRY";
	public static String EMAIL="E_MAIL";
	public static String PASSWORD="PASSWORD";
	public static String SOCIAL="";
	public static String GSM ="GSM";
	
	
	

	//place table
	
	public static String createUserTable(){
		
		String createTableQuery = "CREATE TABLE IF NOT EXISTS"
									+UserTableClass.TABLE_NAME+"("
									+UserTableClass.ID
									+" INTEGER PRIMARY KEY AUTOINCREMENT, "
									+ NAME
									+ " TEXT ,"
									+COUNTRY
									+" TEXT ,"
									+EMAIL
									+" TEXT ,"
									+PASSWORD
									+" TEXT ,"
									+GSM
									+" INTEGER " +");";
		
		return createTableQuery;
		
	}
	
	
}
