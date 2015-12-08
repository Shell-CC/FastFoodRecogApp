package com.xiaoxiguo.fastfoodrecog;

import android.database.sqlite.*;
import android.database.Cursor;
import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

/**
 * Created by Harris on 12/6/15.
 */

public class DatabaseReader extends SQLiteOpenHelper{
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_PATH = "/data/data/com.xiaoxiguo.fastfoodrecog/databases/";
    public static final String DATABASE_NAME = "restaurants";
    private static final String TABLE_NAME = "Restaurant";
    private static final String ID = "ID";
    private static final String NAME = "Food_Name";
    private static final String CAL = "Calorie";
    private final Context myContext;
    private SQLiteDatabase myDatabase;

    public DatabaseReader(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    /**
     * create specific table with tableId
     */
    public void onCreate(SQLiteDatabase db, int tableId) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + String.valueOf(tableId)
                + "(" + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + CAL + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    /** upgrade specific table with tableId */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion, int tableId) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME + String.valueOf(tableId));
        // Create tables again
        onCreate(db, tableId);
    }

    /** create an empty database and rewrite it with your local database*/
    public void createDatabase() throws IOException {
        try {
            boolean dbExist = checkDatabase();
            if(dbExist){
                //do nothing, database already exists
            }else{
                //By calling this method and empty database will be created into the default system path
                //of your application so we are gonna be able to overwrite that database with our database.
                this.getReadableDatabase();
                copyDatabase();
            }
        }
        catch (Exception ignored) {

        }
    }

    /** check if database already exist to avoid re-copying */
    private boolean checkDatabase() {
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DATABASE_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch (SQLiteException e) {
            Log.v("CheckDatabase", "database doesn't exist");
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDatabase() throws IOException{
        try {
            // open local db as input stream
            InputStream myInput = myContext.getAssets().open("Database/"+DATABASE_NAME);
            // path to the created empty db
            String outFileName = DATABASE_PATH + DATABASE_NAME;
            // open empty db as output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            //transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            //close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ignored) {

        }
    }

    public SQLiteDatabase openDatabase() throws SQLException{
        String myPath = DATABASE_PATH + DATABASE_NAME;
        myDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        return myDatabase;
    }

    public synchronized void close() {
        if(myDatabase != null) {
            myDatabase.close();
        }
        super.close();
    }

    /** Get Food_name and Calorie based on tableId and foodId */
    public  String[] readDb(int tableId, int foodId) {
        String[] result = new String[2];

        SQLiteDatabase db = this.getWritableDatabase();
        // select corresponding food name and calorie with foodId
        Cursor cursor = db.query(TABLE_NAME + String.valueOf(tableId), new String[]{NAME, CAL},
                ID + "=?", new String[]{String.valueOf(foodId)}, null, null, null, null);
        // pass query results to string
        if (cursor != null){
            while (cursor.moveToNext()) {
                result[0] = cursor.getString(cursor.getColumnIndex("Food_Name"));
                result[1] = cursor.getString(cursor.getColumnIndex("Calorie"));
            }
            cursor.close();
        }
        return result;
    }
}
