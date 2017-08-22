package com.example.user.database;


import java.util.Calendar;

import com.example.user.eldercare.Date;
import com.example.user.eldercare.DataObject;
import com.example.user.eldercare.FallData;
import com.example.user.eldercare.HeartData;
import com.example.user.eldercare.SleepData;
import com.example.user.eldercare.SleepFragment;
import com.example.user.eldercare.SleepDetection;
import com.example.user.eldercare.SleepRecord;
import com.example.user.eldercare.TemData;
import com.example.user.eldercare.wifidata;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

public class MyDBHelper extends SQLiteOpenHelper {

    public static String TAG = "DBHelper";
    private static final int DATABASE_VERSION = 1;
    private Context m_context;
    private static SQLiteDatabase mDb;

    private final static int _DBVersion = 1; //<-- 版本
    private final static String _DBName = "elderlycare.db";	//<-- db name
    private final static String _TableName = "Heartdata"; //<-- table name

    public MyDBHelper(Context context, String name, CursorFactory factory,
                      int version)
    {


        super(context,name, factory, DATABASE_VERSION);


        // TODO Auto-generated constructor stub

        mDb = this.getWritableDatabase();

        m_context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //Toast.makeText(m_context, "Create DB", Toast.LENGTH_LONG).show();

      /*  final String SQL = "CREATE TABLE IF NOT EXISTS " + _TableName + "( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Heart VARCHAR(50), " +
                "Date VARCHAR(50)," +
                "Time VARCHAR(50)" +
                ");";
        db.execSQL(SQL);*/

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        //Toast.makeText(m_context, "Update DB", Toast.LENGTH_LONG).show();
     /*  final String SQL = "DROP TABLE " + _TableName;

        db.execSQL(SQL);*/
    }



    public void close() {
        super.close();
    }

    public String[] QueryDate(String TablbeName)throws SQLException  {
        Cursor cur = null;
        String Date[];
        try
        {
            cur = mDb.rawQuery("SELECT Date FROM " + TablbeName +" ORDER BY Date DESC" , null);

            Log.e(TAG, String.valueOf("SELECT Date FROM " + TablbeName +" ORDER BY Date DESC"));

            int dataCount = cur.getCount();

            Date = new String[dataCount];
            if (cur.getCount() == 0)
                Date = null;
            if(cur.moveToFirst())
            {
                int i=0;
                do
                {
                    Date [i++] =  cur.getString(0);
                    Log.e(TAG, " SR.Date =" +  Date[i-1]);
                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  Date;
    }

   /* public SleepRecord QueryRecordDate_c(String fieldValue)throws SQLException  {

        SleepRecord SR = new SleepRecord();
        Cursor cur = null;
        String sql="";
        try
        {
            cur = mDb.rawQuery("SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'", null);
            sql="SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'";
            Log.e(TAG, sql);
            if (cur.getCount() == 0)
                SR = null;
            if(cur.moveToFirst())
            {
                do
                {

                    SR.Date =  cur.getString(0);
                    SR.Quality = cur.getString(1);
                    SR.DeepSleep = cur.getString(2);
                    SR.TotalSleep = cur.getString(3);
                    SR.sleeptime = cur.getString(4);
                    SR.deeptime = cur.getString(5);
                    SR.weektime = cur.getString(6);
                    SR.insomnia = cur.getString(7);
                    Log.e(TAG, " SR.DeepSleep =" +  SR.DeepSleep);

                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  SR;
    }*/


   public String[] QueryH(String TablbeName)throws SQLException  {
       Cursor cur = null;
       String Date[];
       try
       {
           cur = mDb.rawQuery("SELECT _id FROM " + TablbeName +" ORDER BY _id ASC" , null);
           Log.e(TAG, String.valueOf("SELECT _id FROM " + TablbeName +" ORDER BY _id ASC"));
           int dataCount = cur.getCount();
           Date = new String[dataCount];
           if (cur.getCount() == 0)
               Date = null;
           if(cur.moveToFirst())
           {
               int i=0;
               do
               {
                   Date [i++] =  cur.getString(0);

               }
               while(cur.moveToNext());
           }
       }
       finally
       {
           cur.close();
       }

       return  Date;
   }
    public String[] QueryT(String TablbeName)throws SQLException  {
        Cursor cur = null;
        String Date[];
        try
        {
            cur = mDb.rawQuery("SELECT ID FROM " + TablbeName +" ORDER BY ID ASC" , null);
            Log.e(TAG, String.valueOf("SELECT ID FROM " + TablbeName +" ORDER BY ID ASC"));
            int dataCount = cur.getCount();
            Date = new String[dataCount];
            if (cur.getCount() == 0)
                Date = null;
            if(cur.moveToFirst())
            {
                int i=0;
                do
                {
                    Date [i++] =  cur.getString(0);

                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  Date;
    }

    public String[] QueryL(String TablbeName)throws SQLException  {
        Cursor cur = null;
        String Date[];
        try
        {
            cur = mDb.rawQuery("SELECT wifi_ID FROM " + TablbeName +" ORDER BY wifi_ID ASC" , null);
            Log.e(TAG, String.valueOf("SELECT wifi_ID FROM " + TablbeName +" ORDER BY wifi_ID ASC"));
            int dataCount = cur.getCount();
            Date = new String[dataCount];
            if (cur.getCount() == 0)
                Date = null;
            if(cur.moveToFirst())
            {
                int i=0;
                do
                {
                    Date [i++] =  cur.getString(0);

                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  Date;
    }
   public boolean insertWifiData(String tableName, wifidata ssid) {
       String sql = "";

       String replace_content =
               "'" + ssid.wifi_ID +"'"
                       + ",'" + ssid.mssid + "'"
                       + ",'" + ssid.date + "'"
                       + ",'" + ssid.time + "'";

       try {
           sql = "INSERT into '" + tableName + "' values(" + replace_content + ")";
           Log.e(TAG, "sql=" + sql);

           mDb.execSQL(sql);

           return true;

       } catch (Exception e) {
           return false;
       }
   }

   public boolean insertFallData(String tableName, FallData falldata) {

       String sql = "";

       String replace_content = "'" + falldata.Fall_ID + "'" + ",'"
               + falldata.Ax + "'" + ",'" + falldata.Ay + "'" + ",'"
               + falldata.Az + "'" + ",'" + falldata.SVM + "'" + ",'"
               + falldata.deSVM + "'" + ",'" + falldata.deSMA + "'" + ",'"
               + falldata.Date + "'" + ",'" + falldata.Time +"'";

       try {
           sql = "INSERT into '" + tableName + "' values(" + replace_content
                   + ")";
           Log.e(TAG, "sql=" + sql);

           mDb.execSQL(sql);

           return true;

       } catch (Exception e) {
           return false;
       }
   }
    public DataObject QueryDate()throws SQLException  {

        DataObject date = new DataObject();
        Cursor cur = null;
        String sql="";
        try
        {
            cur = mDb.rawQuery("SELECT Year, MonthOfYear, DayOfMonth, NextYear, NextMonthOfYear, NextDayOfMonth, Internal FROM Service", null);
//        	sql="SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'";
//        	Log.e(TAG, sql);
            if (cur.getCount() == 0)
                date = null;
            if(cur.moveToFirst())
            {
                do
                {

                    date.Year =  Integer.valueOf(cur.getString(0));
                    date.MonthOfYear = Integer.valueOf(cur.getString(1));
                    date.DayOfMonth = Integer.valueOf(cur.getString(2));
                    date.NextYear = Integer.valueOf(cur.getString(3));
                    date.NextMonthOfYear = Integer.valueOf(cur.getString(4));
                    date.NextDayOfMonth = Integer.valueOf(cur.getString(5));
                    date.Internal = Integer.valueOf(cur.getString(6));
//	    			Log.e(TAG, " date.Internal =" +  date.Internal);

                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  date;
    }


    public boolean updatedeeptime(SleepData SR,String TableName, String Date) {

        String sql = "";
        try {
            String replace_content  =  "deeptime = '" +  SR.deeptime + "',"
                    +  "insomnia = '" +  SR.insomnia + "'";


            sql = "UPDATE " + TableName + " SET " + replace_content + " WHERE Date='" + Date +"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateweektime(SleepData SR,String TableName, String Date) {

        String sql = "";
        try {
            String replace_content  ="weektime = '" +  SR.weektime + "',"
                    +  "insomnia = '" +  SR.insomnia + "'";

            sql = "UPDATE " + TableName + " SET " + replace_content + " WHERE Date='" + Date +"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    // update "table:Line" with "content"
    public boolean updatesleeptime(SleepData SR,String TableName, String Date) {

        String sql = "";
        try {
            String replace_content  =  "sleeptime = '" +  SR.sleeptime + "',"
                    +  "insomnia = '" +  SR.insomnia + "'";

            sql = "UPDATE " + TableName + " SET " + replace_content + " WHERE Date='" + Date +"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean updateSetDate(String TableName,DataObject date) {
        date = new DataObject();
        String sql = "";
        try {
            String replace_content  =  "Year = '" +  date.Year + "',"
                    +  "MonthOfYear = '" +  date.MonthOfYear + "',"
                    +  "DayOfMonth = '" +  date.DayOfMonth + "',"
                    +  "NextYear = '" +  date.NextYear + "',"
                    +  "NextMonthOfYear = '" +  date.NextMonthOfYear + "',"
                    +  "NextDayOfMonth = '" +  date.NextDayOfMonth + "',"
                    +  "Internal = '" +  date.Internal;

//		   	date.Internal

            sql = "UPDATE " + TableName + " SET " + replace_content +"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public SleepRecord QueryRecordDate_C(String fieldValue)throws SQLException  {
        SleepRecord SR = new SleepRecord();
        Cursor cur = null;
        String sql="";
        try
        {
            cur = mDb.rawQuery("SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'", null);
            sql="SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'";
            Log.e(TAG, sql);
            if (cur.getCount() == 0)
                SR = null;
            if(cur.moveToFirst())
            {
                do
                {

                    SR.Date =  cur.getString(0);
                    SR.Quality = cur.getString(1);
                    SR.DeepSleep = cur.getString(2);
                    SR.TotalSleep = cur.getString(3);
                    SR.sleeptime = cur.getString(4);
                    SR.deeptime = cur.getString(5);
                    SR.weektime = cur.getString(6);
                    SR.insomnia = cur.getString(7);
                    Log.e(TAG, " SR.DeepSleep =" +  SR.DeepSleep);

                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  SR;
    }


    public SleepData QueryRecordDate(String fieldValue)throws SQLException  {

        SleepData SR = new SleepData();
        Cursor cur = null;
        String sql="";
        try
        {
            cur = mDb.rawQuery("SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'", null);
            sql="SELECT Date, sleepQuality, deepSleep, totalSleep, sleeptime, deeptime, weektime, insomnia FROM LastSleepData WHERE Date = '" + fieldValue +"'";
            Log.e(TAG, sql);
            if (cur.getCount() == 0)
                SR = null;
            if(cur.moveToFirst())
            {
                do
                {

                    SR.Date =  cur.getString(0);
                    SR.Quality = cur.getString(1);
                    SR.deepsleep = cur.getString(2);
                    SR.totalsleep = cur.getString(3);
                    SR.sleeptime = cur.getString(4);
                    SR.deeptime = cur.getString(5);
                    SR.weektime = cur.getString(6);
                    SR.insomnia = cur.getString(7);
                    Log.e(TAG, " SR.Date =" +  SR.Date);

                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  SR;
    }
    public String[] queryFirstArray()

            throws SQLException {

        String[] queryArray;
        Cursor cur = null;
        try {
            cur = mDb.rawQuery("SELECT Date FROM LastSleepData",
                    null);
            //Log.e(TAG, " cur =" + cur);
            int dataCount = cur.getCount();
            queryArray = new String[dataCount];
            if (cur.getCount() == 0)
                queryArray = null;
            if (cur.moveToFirst()) {
                int i = 0;
                do {

                    queryArray[i++] = cur.getString(0);
                    //Log.e(TAG, " queryArray =" + cur.getString(0));

                } while (cur.moveToNext());
            }
        } finally {
            cur.close();
        }

        return queryArray;
    }

    public boolean updateHeart(HeartData HR,String TableName,String Date , String Time) {

        String sql = "";


        try {
            String replace_content  =  "_id = '" +  HR.ID + "',"
                    +"Heart = '" +  HR.Heart + "',"
                    +  "Date = '" + HR.Date + "',"
                    +  "Time = '" +  HR.Time + "'";

            sql = "UPDATE " + TableName + " SET " + replace_content + " WHERE Date='" + Date +"' AND Time='"+Time+"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean insertLastSleepData(String tableName, SleepData sleepData) {

        String sql = "";

        String replace_content =
                "'" + sleepData.Date +"'"
                        + ",'" + sleepData.sleepQuality + "'"
                        + ",'" + sleepData.deepsleep + "'"
                        + ",'" + sleepData.totalsleep + "'"
                        + ",'" + sleepData.sleeptime + "'"
                        + ",'" + sleepData.deeptime + "'"
                        + ",'" + sleepData.weektime + "'"
                        + ",'" + sleepData.insomnia + "'" ;

        try {
            sql = "INSERT into '" + tableName + "' values(" + replace_content + ")";
            Log.e(TAG, "sql=" + sql);
            mDb.execSQL(sql);
            return true;

        } catch (Exception e) {
            return false;
        }
    }
    // get DB content list from db
   /* public SleepData queryData(String TablbeName, String fieldName, String fieldValue)throws SQLException  {

        SleepData sleepData = new SleepData();
        Cursor cur = null;
        try
        {
            cur = mDb.rawQuery("SELECT * FROM " + TablbeName + " WHERE " + fieldName+ " = '" + fieldValue +"'", null);
            int dataCount = cur.getCount();
            if (cur.getCount() == 0)
                sleepData = null;
            if(cur.moveToFirst())
            {
                do
                {
                    sleepData.ID =  cur.getString(0);
                    sleepData.Date = cur.getString(1);
                    sleepData.Time = cur.getString(2);
                    sleepData.AT = cur.getString(3);
                    sleepData.AL = cur.getString(4);
                    sleepData.PD = cur.getString(5);
                    sleepData.sleepQuality = cur.getString(6);
                    sleepData.rolloverCount = cur.getString(7);


                    Log.e(TAG, " sleepData.Date =" +  sleepData.Date);


                }
                while(cur.moveToNext());
            }
        }
        finally
        {
            cur.close();
        }

        return  sleepData;
    }*/




    // insert "content" to "tableName"
    public boolean insertSleepDirect(String tableName, SleepData sleepData) {

        String sql = "";

        String replace_content =
                "'" + sleepData.Date +"'"
                        + ",'" + sleepData.Time + "'"
                        + ",'" + sleepData.AT + "'"
                        + ",'" + sleepData.AL + "'"
                        + ",'" + sleepData.PD + "'"
                        + ",'" + sleepData.sleepQuality + "'"
                        + ",'" + sleepData.rolloverCount + "'"
                        + ",'" + sleepData.ID + "'"
                        + ",'" + sleepData.deepsleep + "'"
                        + ",'" + sleepData.totalsleep + "'";
//	   		   	 				+ ",'" + sleepData.AZ + "'" ;
//	   	 for(int i=1; i<content.length ; i++)
//	   		replace_content +=  (",'" + content[i] + "'" );

        try {
            sql = "INSERT into '" + tableName + "' values(" + replace_content + ")";
            Log.e(TAG, "sql=" + sql);
            mDb.execSQL(sql);
            Toast.makeText(m_context, "AT:"+sleepData.AT.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(m_context, "AL:"+sleepData.AL.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(m_context, "PD:"+sleepData.PD.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(m_context, "rolloverCount:"+sleepData.rolloverCount.toString(), Toast.LENGTH_LONG).show();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

   /* public boolean insertSleepData(String tableName, SleepData sleepData) {

        String sql = "";

        String replace_content =
                "'" + sleepData.ID_SleepData +"'"
                        + ",'" + sleepData.sensor_date + "'"
                        + ",'" + sleepData.sensor_x + "'"
                        + ",'" + sleepData.sensor_y + "'";

        try {
            sql = "INSERT into '" + tableName + "' values(" + replace_content + ")";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }*/

   public boolean insertHeartData(String tableName, HeartData heartData) {

       String sql = "";

       String replace_content =
               "'" + heartData.ID +"'"
                       + ",'" + heartData.Heart + "'"
                       + ",'" + heartData.Time + "'"
                       + ",'" + heartData.Date +"'";

       try {
           sql = "INSERT into '" + tableName + "' values(" + replace_content + ")";
           Log.e(TAG, "sql=" + sql);

           mDb.execSQL(sql);

           return true;

       } catch (Exception e) {
           return false;
       }
   }

    public boolean insertTemData(String tableName, TemData temData) {

        String sql = "";

        String replace_content =
                "'" + temData.ID +"'"
                        + ",'" + temData.tem + "'"
                        + ",'" + temData.time + "'"
                        + ",'" + temData.Date +"'";

        try {
            sql = "INSERT into '" + tableName + "' values(" + replace_content + ")";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }

    public boolean updateTem(TemData temData,String TableName,String Date , String Time) {

        String sql = "";


        try {
            String replace_content  =  "ID = '" +  TemData.ID + "',"
                    +"Date = '" +  TemData.tem + "',"
                    +  "Time = '" +  TemData.Date + "',"
                    +  "AT = '" +  TemData.time + "'";

            sql = "UPDATE " + TableName + " SET " + replace_content + " WHERE Date='" + Date +"' AND Time='"+Time+"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        }
        catch (Exception e) {
            return false;
        }
    }


    public boolean deleteOne(String tableName, String fieldName, String fieldValue) {

        String sql = "";


        try {


            sql = "DELETE from '" + tableName + "' WHERE " + fieldName + "='" + fieldValue +"'";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }


    // update "table:Line" with "content"
    public boolean updateDB(SleepData sleepData,String TableName,String Date , String Time) {

        String sql = "";


        try {
            String replace_content  =  "ID = '" +  sleepData.ID + "',"
                    +"Date = '" +  sleepData.Date + "',"
                    +  "Time = '" +  sleepData.Time + "',"
                    +  "AT = '" +  sleepData.AT + "',"
                    +  "AL = '" +  sleepData.AL + "',"
                    +  "PD = '" +  sleepData.PD + "',"
                    +  "sleepQuality = '" +  sleepData.sleepQuality + "',"
                    +  "rolloverCount = '" +  sleepData.rolloverCount + "',"
                    +  "deepsleep = '" +  sleepData.deepsleep + "',"
                    +  "totalsleep = '" +  sleepData.totalsleep + "'";

            sql = "UPDATE " + TableName + " SET " + replace_content + " WHERE Date='" + Date +"' AND Time='"+Time+"';";
            Log.e(TAG, "sql=" + sql);

            mDb.execSQL(sql);

            return true;

        } catch (Exception e) {
            return false;
        }
    }
    public boolean insertServiceData(String string, Date d) {

        return false;
    }



}

