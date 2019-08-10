package omkar.tenkale.gcoeapapers;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.io.IOException;

public class DBAdapter {

    private SQLiteDatabase mDB;
    private DBHelper mDbHelper;
    Cursor mcursor;


    public DBAdapter(Context context)
    {
        Context mContext = context;
        boolean b = context==null;
        Log.e("XXXXX","IS CONTEXT NULL DBADAPTER : "+ b);
        mDbHelper = new DBHelper(mContext);
        Log.w("DBBUGHUNT","inside dbadapter constructor");
    }

    public DBAdapter createDatabase() throws SQLException
    {
        Log.w("DBBUGHUNT","INSIDE DBADAPTER CREATEDB METHOD");
        try
        {
            mDbHelper.createDataBase();
        }
        catch (IOException mIOException)
        {
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DBAdapter open() throws SQLException
    {
        Log.w("DBBUGHUNT","INSIDE DBADAPTER OPEN METHOD");
        try
        {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDB = mDbHelper.getReadableDatabase();
        }
        catch (SQLException mSQLException){}

        return this;
    }

    public void close()
    {
        Log.w("DBBUGHUNT","INSIDE DBADAPTER CLOSE METHOD");
        mDbHelper.close();
    }

    public Cursor getData(String query){
        this.createDatabase();
        this.open();
        mcursor= mDB.rawQuery(query,null);
        return mcursor;
    }
    public Cursor getTestData(String sql)
    {
        Log.w("DBBUGHUNT","INSIDE DBADAPTER GETTESTDATA METHOD");
        try
        {

            Cursor mCur = mDB.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        }
        catch (SQLException mSQLException)
        { return null ;}
    }
    public double getVersion(){

        try
        {
            double version=-1;
            Cursor returned_cursor = mDB.rawQuery("SELECT * FROM DB_INFO", null);
            if (returned_cursor!=null)
            {
                returned_cursor.moveToNext();
            }

            if (returned_cursor.moveToFirst()) {
              //  Log.i("PAPERS.JAVA","INSIDE IF METHOD BEFORE WHILE");
                while(!returned_cursor.isAfterLast()) {
                    version= Double.parseDouble(returned_cursor.getString(returned_cursor.getColumnIndex("VERSION")));
                    returned_cursor.moveToNext();
                }}
                returned_cursor.close();
            return version;
        }
        catch (Exception e)
        { return -1 ;}

    }

}
