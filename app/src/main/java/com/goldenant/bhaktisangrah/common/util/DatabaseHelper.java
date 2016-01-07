package com.goldenant.bhaktisangrah.common.util;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.goldenant.bhaktisangrah.model.NotificationRecord;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

@SuppressLint("SdCardPath")
public class DatabaseHelper extends SQLiteOpenHelper {
	private static String DB_PATH = "";
	private static final String DB_NAME = "Database.sqlite";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	private static DatabaseHelper mDBConnection;
	static int version_val = 1;

	private static final String TABLE_TRACK = "notification";
	private static final String TABLE_UPCOMING_JOBS = "upcomingjobs";
	private static final String KEY_IDTRACK = "id";
	private static final String KEY_TITLE = "title";
	private static final String KEY_CONTENT = "content";
	private static final String KEY_DATE = "date";
	private static final String KEY_TIME = "time";
	private static final String KEY_POSTCODE = "postcode";
	private static final String KEY_HOUSE_NUMBER = "house_number";
	private static final String KEY_STREET_ADD = "street_add";
	private static final String KEY_CUSTOMER_NOTE = "customer_note";
	private static final String KEY_MASSAGE_TYPE = "massage_type";
	private static final String KEY_DURATION = "duration";
	private static final String KEY_STATUS = "status";
	private static final String KEY_LANDMARK = "landmark";

	public DatabaseHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.myContext = context;
		DB_PATH = "/data/data/"
				+ context.getApplicationContext().getPackageName()
				+ "/databases/";
	}

	public static synchronized DatabaseHelper getDBAdapterInstance(
			Context context) {
		if (mDBConnection == null) {
			mDBConnection = new DatabaseHelper(context, DB_NAME, null,
					version_val);
		}
		return mDBConnection;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String CREATE_TABLE_TRACK = "CREATE TABLE " + TABLE_TRACK + "("
				+ KEY_IDTRACK + " INTEGER PRIMARY KEY ,"
				+ KEY_TITLE + " TEXT ," + KEY_CONTENT + " TEXT," + KEY_DATE
				+ " TEXT ," + KEY_TIME + " TEXT ," + KEY_POSTCODE + " TEXT ,"
				+ KEY_HOUSE_NUMBER + " TEXT ,"+ KEY_STREET_ADD + " TEXT ,"
				+ KEY_CUSTOMER_NOTE + " TEXT ,"+ KEY_MASSAGE_TYPE + " TEXT ,"
				+ KEY_DURATION + " TEXT,"+ KEY_STATUS + " TEXT,"+ KEY_LANDMARK + " TEXT"+")";
		
		
		String CREATE_TABLE_UPCOMING_JOBS = "CREATE TABLE " + TABLE_UPCOMING_JOBS + "("
                + KEY_IDTRACK + " INTEGER PRIMARY KEY ,"+ KEY_TITLE + " TEXT ," + KEY_CONTENT + " TEXT," + KEY_DATE
				+ " TEXT ," + KEY_TIME + " TEXT ," + KEY_POSTCODE + " TEXT ,"
				+ KEY_HOUSE_NUMBER + " TEXT ,"+ KEY_STREET_ADD + " TEXT ,"
				+ KEY_CUSTOMER_NOTE + " TEXT ,"+ KEY_MASSAGE_TYPE + " TEXT ,"
				+ KEY_DURATION + " TEXT,"+ KEY_STATUS + " TEXT,"+ KEY_LANDMARK + " TEXT"+")";
		
		db.execSQL(CREATE_TABLE_TRACK);
		db.execSQL(CREATE_TABLE_UPCOMING_JOBS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			// do nothing - database already exist
		} else {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) 
			{
				e.printStackTrace();
				//throw new Error("Error copying database:" + e.toString());
			}
		}
	}

	public boolean databaseExist() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	private boolean checkDataBase() {

		File dbFile = myContext.getDatabasePath(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	public long Insert_Record(NotificationRecord notif) {
		// TODO Auto-generated method stub
		long rawId = 0;
		try {
			ContentValues CV = new ContentValues();

			CV.put("content", notif.getContent());
			CV.put("date", notif.getDate());
			
			rawId = myDataBase.insert("notification", null, CV);

		} catch (Exception e) {

		}
		return rawId;
	}
	


	public ArrayList<NotificationRecord> Select_Record()
	{
		// TODO Auto-generated method stub
		ArrayList<NotificationRecord> category = new ArrayList<NotificationRecord>();
		Cursor cursor = myDataBase.rawQuery("SELECT * FROM notification", null);
		if (cursor.moveToFirst()) 
		{
			do 
			{
				NotificationRecord bean = new NotificationRecord();

				bean.setContent(cursor.getString(2));
				bean.setDate(cursor.getString(3));


				category.add(bean);

			} while (cursor.moveToNext());
		}
		cursor.close();
		return category;
	}
	


	public long delete_record(int id) {
		String wheres = "id=?";
		String IDS = String.valueOf(id);

		String[] whereArgs = { IDS };

		long l = myDataBase.delete("notification", wheres, whereArgs);

		return l;

	}

	public void delete_all_record() 
	{

		String DEL_ALL = "DELETE FROM notification";
		myDataBase.execSQL(DEL_ALL);
	}
	
	public void delete_all_record_from_upcomingjobs() 
	{

		String DEL_ALL = "DELETE FROM upcomingjobs";
		myDataBase.execSQL(DEL_ALL);
	}
	
	public String validateNotification(String time)
	{
		String str = "SELECT time FROM notification where time='" + time+"'";
		
		Cursor c = myDataBase.rawQuery(str, null);
		String result = "NULL";
		
		try
		{
			if(c != null)
			{
				c.moveToFirst();
			}
			result = c.getString(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		c.close();
		
		return result;
	}
	
	public String getNotifId(String time)
	{
		String str = "SELECT id FROM notification where time='" + time+"'";
		
		Cursor c = myDataBase.rawQuery(str, null);
		String result = "0";
		
		try
		{
			if(c != null)
			{
				c.moveToFirst();
			}
			result = c.getString(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		c.close();
		
		return result;
	}
	
	public String getUpcomingJobId(String time)
	{
		String str = "SELECT id FROM upcomingjobs where time='" + time+"'";
		
		Log.i("time", ""+time);
		
		Cursor c = myDataBase.rawQuery(str, null);
		String result = "0";
		
		try
		{
			if(c != null)
			{
				c.moveToFirst();
			}
			result = c.getString(0);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		c.close();
		
		return result;
	}
}