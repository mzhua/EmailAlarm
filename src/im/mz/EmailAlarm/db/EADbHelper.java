package im.mz.EmailAlarm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import im.mz.EmailAlarm.db.EAContract.MyEntry;
import im.mz.EmailAlarm.db.EAContract.AppsFilterEntry;

/**
 * Created by Hua on 2014/10/17.
 */
public class EADbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 7;
    public static final String DATABASE_NAME = "EA.db";

    public static final String TEXT_TYPE = " TEXT";
    public static final String INT_TYPE = " INT";
    public static final String LONG_TYPE = " long";
    public static final String BOOLEAN_TYPE = "boolean";
    public static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ALARM_ENTRIES =
            "CREATE TABLE " + MyEntry.TABLE_NAME_ALARM + " (" +
                    MyEntry._ID + " INTEGER PRIMARY KEY," +
                    MyEntry.COLUMN_NAME_DATE + LONG_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_ALARM_TIME + TEXT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_LOCATION + TEXT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_DETAIL + TEXT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_RINGTONE + TEXT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_VIBRATE + BOOLEAN_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_REMIND + INT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_SORT + INT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_VIBRATE + INT_TYPE + COMMA_SEP +
                    MyEntry.COLUMN_NAME_STATUS + INT_TYPE +

                    " )";
    public static final String SQL_CREATE_APPS_FILTER_ENTRIES =
            "create table " +AppsFilterEntry.TABLE_NAME_APP_FILTER + " (" +
                    AppsFilterEntry._ID + " INTEGER PRIMARY KEY," +
                    AppsFilterEntry.COLUMN_NAME_PACKAGENAME + TEXT_TYPE +
                    " )";


    public static final String SQL_DELETE_ALARM_ENTRIES =
            "DROP TABLE IF EXISTS " + MyEntry.TABLE_NAME_ALARM;

    public static final String SQL_DELETE_APPS_FILTER_ENTRIES =
            "DROP TABLE IF EXISTS " + AppsFilterEntry.TABLE_NAME_APP_FILTER;



    public EADbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_ALARM_ENTRIES);
        db.execSQL(SQL_CREATE_APPS_FILTER_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ALARM_ENTRIES);
        db.execSQL(SQL_DELETE_APPS_FILTER_ENTRIES);
        onCreate(db);
    }

}
