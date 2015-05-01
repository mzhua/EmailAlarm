package im.mz.EmailAlarm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.widget.Toast;

import im.mz.EmailAlarm.entity.AlarmListEntity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hua on 2014/10/17.
 */
public class AlarmDbUtils {

    public AlarmDbUtils() {

    }

    /**
     * 每月1号自动清理过期的提醒
     * @param context
     */
    public static void clearHistoryDate(Context context){
        // Define 'where' part of query.
        Calendar calendar = Calendar.getInstance();
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        if(dayOfMonth == 1){
            String selection = EAContract.MyEntry.COLUMN_NAME_STATUS + " = ? and " + EAContract.MyEntry.COLUMN_NAME_DATE + " < ?";
            String[] selectionArgs = {String.valueOf(1),String.valueOf(calendar.getTimeInMillis())};
            delData(context,selection,selectionArgs,false);
        }


    }
    /**
     * 获取最近的一个需要提醒的事件
     */
    public static AlarmListEntity getRecentAlarm(Context context) {

        String sortOrder =
                EAContract.MyEntry.COLUMN_NAME_ALARM_TIME + " ASC" ;
        String selection = EAContract.MyEntry.COLUMN_NAME_ALARM_TIME+" >= ? and " + EAContract.MyEntry.COLUMN_NAME_STATUS + " = ?";
        Date dates = new Date();
        long times  = dates.getTime();
        String[] selectionArgs = {String.valueOf(times),"1"};

        ArrayList<AlarmListEntity> listEntities = query(context,selection,selectionArgs,null,null,sortOrder,0,0);
        AlarmListEntity entity = listEntities!=null&&listEntities.size()>0?listEntities.get(0):null;

        return entity;
    }

    /**
     * 根据id查询数据
     * @param context
     * @param id
     * @return
     */
    public static AlarmListEntity queryById(Context context,long id) {

        String selection = EAContract.MyEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        ArrayList<AlarmListEntity> listEntities = query(context,selection,selectionArgs,null,null,null,0,0);
        AlarmListEntity entity = listEntities!=null&&listEntities.size()>0?listEntities.get(0):null;

        return entity;

    }

    /**
     * pageSize为0则全部查询
     * @param context
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param pageIndex 页号
     * @param pageSize 分页大小
     * @return
     */
    public static ArrayList<AlarmListEntity> query(Context context,String selection,String[] selectionArgs,String groupBy,String having,String orderBy,int pageIndex,int pageSize) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getReadableDatabase();

        String limit;
        if(pageSize == 0){
            limit = null;
        }else{
            limit =  (pageSize * pageIndex) +"," + pageSize;
        }

/*        if(!TextUtils.isEmpty(limit)){
            if(limit.equals("0") || !TextUtils.isDigitsOnly(limit)){//设置为0时，即不限制条数,出现数字外的其他情况也都设置为不限制条数
                limit = null;
            }
        }*/
        Cursor cursor = db.query(
                true,
                EAContract.MyEntry.TABLE_NAME_ALARM,  // The table to query
                EAContract.AlarmProjection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                groupBy,                                     // don't group the rows
                having,                                     // don't filter by row groups
                orderBy,                                 // The sort order
                limit
        );
        ArrayList<AlarmListEntity> myListDataArr = new ArrayList<AlarmListEntity>();


        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            AlarmListEntity entity = new AlarmListEntity();
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(EAContract.MyEntry._ID));
            long date = cursor.getLong(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_DATE));
            long alarmTime = cursor.getLong(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_ALARM_TIME));
            String location = cursor.getString(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_LOCATION));
            String detail = cursor.getString(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_DETAIL));
            int remind = cursor.getInt(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_REMIND));
            String ringtone = cursor.getString(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_RINGTONE));
            String vibrate = cursor.getString(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_VIBRATE));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_STATUS));


            entity.setId(id);
            entity.setDate(date);
            entity.setAlarmTime(alarmTime);
            entity.setLocation(location);
            entity.setDetail(detail);
            entity.setRemind(remind);
            entity.setRingtone(ringtone);
            entity.setVibrate(vibrate);
            entity.setStatus(status);
            entity.setHeader("");

            myListDataArr.add(entity);

            while (cursor.moveToNext()) {
                AlarmListEntity myListEntity = new AlarmListEntity();
                id = cursor.getLong(cursor.getColumnIndexOrThrow(EAContract.MyEntry._ID));
                date = cursor.getLong(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_DATE));
                alarmTime = cursor.getLong(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_ALARM_TIME));
                detail = cursor.getString(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_DETAIL));
                location = cursor.getString(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_LOCATION));
                status = cursor.getInt(cursor.getColumnIndexOrThrow(EAContract.MyEntry.COLUMN_NAME_STATUS));

                myListEntity.setId(id);
                myListEntity.setDate(date);
                myListEntity.setAlarmTime(alarmTime);
                myListEntity.setLocation(location);
                myListEntity.setStatus(status);
                myListEntity.setDetail(detail);

                myListDataArr.add(myListEntity);
            }
        }


        cursor.close();
        db.close();
        return myListDataArr;
    }

    /**
     * 更新数据
     *
     * @param id 主键
     */
    public static void updateDataById(Context context,long id,ContentValues contentValues) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getReadableDatabase();



        String selection = EAContract.MyEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        int count = db.update(
                EAContract.MyEntry.TABLE_NAME_ALARM,
                contentValues,
                selection,
                selectionArgs);

        db.close();

    }

    /**
     * 更新状态，时间小于现在都设置为0
     * @param context
     */
    public static void updateStatus(Context context) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(EAContract.MyEntry.COLUMN_NAME_STATUS, 0);

        String selection = EAContract.MyEntry.COLUMN_NAME_ALARM_TIME + " < ?";
        Date date = new Date();
        long time = date.getTime();
        String[] selectionArgs = {String.valueOf(time)};

        int count = db.update(
                EAContract.MyEntry.TABLE_NAME_ALARM,
                values,
                selection,
                selectionArgs);

        db.close();

    }

    /**
     * 插入数据
     */
    public static long insertData(Context context,ContentValues values) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getWritableDatabase();

        long id = 0;
        if(values != null){
            id = db.insert(EAContract.MyEntry.TABLE_NAME_ALARM, null, values);
        }
        db.close();

        return id;
    }
    /**
     * 删除数据
     */
    public static void delData(Context context,String selection,String[] selectionArgs,boolean showToast) {
        EADbHelper eaDbHelper = new EADbHelper(context);
        SQLiteDatabase db = eaDbHelper.getWritableDatabase();

        int counts = db.delete(EAContract.MyEntry.TABLE_NAME_ALARM, selection, selectionArgs);
        if(showToast){
            Toast.makeText(context,"清除" + counts + "条提醒",Toast.LENGTH_SHORT).show();
        }

        db.close();
    }
}
