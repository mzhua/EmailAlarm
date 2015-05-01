/*
package im.mz.EmailAlarm.async;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.db.EAContract;
import im.mz.EmailAlarm.utils.MyDateUtils;
import im.mz.EmailAlarm.utils.NotifyUtil;
import jxl.Cell;
import jxl.CellType;
import jxl.DateCell;
import jxl.Sheet;

import java.util.Date;

*/
/**
 * Created by mzhua_000 on 2015/1/11.
 *//*

public  class ImportExcelTask extends AsyncTask<Object, Integer, Long> {
    private Context context;
    public ImportExcelTask(Context context) {
        this.context = context;
    }

    // Do the long-running work in here
    protected Long doInBackground(Object... objects) {
        Log.d("TAG", "asyn start");
        Sheet sheet = (Sheet) objects[0];
        int timePosition = Integer.parseInt(String.valueOf(objects[1])) - 1;
        int locationPosition = Integer.parseInt(String.valueOf(objects[2]))  - 1;
        int detailPosition = Integer.parseInt(String.valueOf(objects[3]))  - 1;

        Cell a1; //time
        Cell b1; //location
        Cell c1; //detail
        Date time;
        String location;
        String detail;

        long totalSize = 0;
        long validateDataSize = 0;

        if(sheet != null){
            totalSize = sheet.getRows();
            Log.d("TAG","sheet size = " + totalSize);

            NotifyUtil notifyUtil = new NotifyUtil(context);
            for(int i = 0;i < sheet.getRows() ; i++){
                a1 = sheet.getCell(timePosition,i);
                b1 = sheet.getCell(locationPosition,i);
                c1 = sheet.getCell(detailPosition,i);
                if(a1.getType() == CellType.DATE){
                    time = ((DateCell)a1).getDate();
                }else{
                    time = new Date();
                }

                location = b1.getContents();
                detail = c1.getContents();

                boolean validate = insertData(time,location,detail);
                if(validate){
                    validateDataSize++;
                }
                notifyUtil.excelImportProgressNotify((int) totalSize, (int) validateDataSize,i+1);
                Log.d("TAG",i + " excel time = " + time + " type = " + a1.getType()   + " location = " + location);
            }

        }

        return totalSize;
    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Integer... progress) {
//            setProgressPercent(progress[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Long result) {
//            showNotification("Downloaded " + result + " bytes");
    }

    */
/**
     * 插入数据
     * @param time 时间
     * @param location 地点/事件
     * @param detail 详细
     * @return 是否为有效数据插入
     *//*

    private boolean insertData(Date time,String location,String detail) {
        ContentValues values = initUpdateContentValue(time, location, detail);
        if (values != null) {
            AlarmDbUtils.insertData(context, values);
            return true;
        }
        return false;
    }
    */
/**
     * 初始化要插入或者更新的数据
     *
     * @return
     *//*

    private ContentValues initUpdateContentValue(Date time,String location,String detail) {
        long date = time.getTime();
        Uri ringtone = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
        ContentValues values = new ContentValues();
        values.put(EAContract.MyEntry.COLUMN_NAME_DATE, date);

        long early = validateDate(date);
        if (early == -1) {
            values = null;
        } else {
            values.put(EAContract.MyEntry.COLUMN_NAME_ALARM_TIME, date - early);
            values.put(EAContract.MyEntry.COLUMN_NAME_LOCATION, location);
            values.put(EAContract.MyEntry.COLUMN_NAME_DETAIL, detail);
            values.put(EAContract.MyEntry.COLUMN_NAME_REMIND, 0);
            values.put(EAContract.MyEntry.COLUMN_NAME_RINGTONE, String.valueOf(ringtone));
            values.put(EAContract.MyEntry.COLUMN_NAME_VIBRATE, 1);//1：振动  0：不振动
            values.put(EAContract.MyEntry.COLUMN_NAME_STATUS, 1);
        }


        return values;
    }

    */
/**
     * 验证设置的时间是否合理
     * 规则：设置的时间-提前的时间  >  现在的时间
     *
     * @return
     *//*

    private long validateDate(long alarmTime) {

        int remind = 2;
        return MyDateUtils.validateDate(alarmTime,remind);
    }
}
*/
