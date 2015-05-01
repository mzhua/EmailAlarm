package im.mz.EmailAlarm.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import im.mz.EmailAlarm.db.EAContract.CountEntry;
/**
 * Created by HUA on 2014/11/8.
 */
public class CountDbHelper extends SQLiteOpenHelper {

    private static final String SQL_CREATE_COUNT_ENTRIES =
            "CREATE TABLE " + EAContract.CountEntry.TABLE_NAME_COUNT + " (" +
                    CountEntry._ID + " INTEGER PRIMARY KEY," +
                    CountEntry.COLUMN_NAME_DATE + EADbHelper.LONG_TYPE + EADbHelper.COMMA_SEP +
                    CountEntry.COLUMN_NAME_COUNTS + EADbHelper.INT_TYPE + EADbHelper.COMMA_SEP +
                    CountEntry.COLUMN_NAME_WEEK + EADbHelper.TEXT_TYPE + EADbHelper.COMMA_SEP +

                    " )";
    private static final String SQL_DELETE_COUNT_ENTRIES =
            "DROP TABLE IF EXISTS " + EAContract.CountEntry.TABLE_NAME_COUNT;

    public CountDbHelper(Context context) {
        super(context, EADbHelper.DATABASE_NAME, null, EADbHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_COUNT_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_COUNT_ENTRIES);
        onCreate(db);
    }
}
