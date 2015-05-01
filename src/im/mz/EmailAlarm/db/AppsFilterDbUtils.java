package im.mz.EmailAlarm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import im.mz.EmailAlarm.entity.AppInfoEntity;

/**
 * Created by mzhua_000 on 2015/1/6.
 */
public class AppsFilterDbUtils {

    public static void initApps(Context context){
        ContentValues values = new ContentValues();
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.alibaba.mobileim");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.tencent.mm");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.android.mms");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.android.email");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.tencent.mobileqq");
        insertData(context,values);

        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.alibaba.android.babylon");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.tjut.mianliao");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.immomo.momo");
        insertData(context,values);
        values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,"com.sina.weibo");
        insertData(context,values);

    }

    public static ArrayList<AppInfoEntity> query(Context context,String selection,String[] selectionArgs,String groupBy,String having,String orderBy) {

        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dbInit",true)){
            initApps(context);
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean("dbInit",false).commit();
        }


        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                EAContract.AppsFilterEntry.TABLE_NAME_APP_FILTER,  // The table to query
                EAContract.AppProjection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                groupBy,                                     // don't group the rows
                having,                                     // don't filter by row groups
                orderBy                                 // The sort order
        );
        ArrayList<AppInfoEntity> appInfoEntityList = new ArrayList<AppInfoEntity>();
        if(cursor != null && cursor.getCount() > 0){
            cursor.moveToFirst();
            while (cursor.moveToNext()){
                AppInfoEntity appInfoEntity = new AppInfoEntity();
                appInfoEntity.setPackageName(cursor.getString(cursor.getColumnIndexOrThrow(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME)));

                appInfoEntityList.add(appInfoEntity);
            }

            return appInfoEntityList;
        }
        cursor.close();
        db.close();
        return null;
    }

    /**
     * 插入数据
     */
    public static void insertData(Context context,ContentValues values) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getWritableDatabase();

        long id = 0;
        if(values != null){
            id = db.insert(EAContract.AppsFilterEntry.TABLE_NAME_APP_FILTER, null, values);
        }
//        Toast.makeText(context, "insert id="+id, Toast.LENGTH_SHORT).show();
        db.close();

    }
    /**
     * 删除数据
     */
    public static void delData(Context context,String selection,String[] selectionArgs) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getWritableDatabase();

        int counts = db.delete(EAContract.AppsFilterEntry.TABLE_NAME_APP_FILTER, selection, selectionArgs);

//        Toast.makeText(context, "del counts="+counts, Toast.LENGTH_SHORT).show();
        db.close();
    }

}
