package im.mz.EmailAlarm.utils;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Hua on 2014/10/19.
 */
public class MyDateUtils {

    /**
     * 格式化成日期字符串
     * @param context
     * @param date
     * @return
     */
    public static String formatToDate(Context context,long date){
        return DateUtils.formatDateTime(context,
                date,
                DateUtils.FORMAT_SHOW_DATE
                        /*| DateUtils.FORMAT_SHOW_WEEKDAY*/
                        | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_ABBREV_MONTH
                        | DateUtils.FORMAT_ABBREV_WEEKDAY);
    }
    /**
     * 格式化成日期字符串
     * @param context
     * @param date
     * @return
     */
    public static String formatToDateFull(Context context,long date){
        return DateUtils.formatDateTime(context,
                date,
                DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_ABBREV_MONTH
                        | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_TIME
                        | DateUtils.FORMAT_ABBREV_WEEKDAY
        );
    }

    /**
     * 格式化成日期字符串
     * @param context
     * @param date
     * @return
     */
    public static String formatToDateNoYear(Context context,long date){
        return DateUtils.formatDateTime(context,
                date,
                DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_WEEKDAY
                        | DateUtils.FORMAT_ABBREV_MONTH
                        | DateUtils.FORMAT_NO_YEAR
                        | DateUtils.FORMAT_24HOUR
                        | DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_TIME
                        | DateUtils.FORMAT_ABBREV_WEEKDAY
        );
    }

    /**
     * 格式化成没有天的日期字符串
     * @param date
     * @return
     */
    public static String formatToDateWithoutDay(long date){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月");
        Date d = new Date(date);

        return sdf.format(d);
    }

    /**
     * 格式化成时间字符串
     * @param context
     * @param date
     * @return
     */
    public static String formatToTime(Context context,long date){
        return DateUtils.formatDateTime(context,
                date,
                DateUtils.FORMAT_SHOW_TIME
                        | DateUtils.FORMAT_ABBREV_TIME);
    }

    /**
     * 格式化成日期时间字符串，如果时间是今年的，则不显示年份
     * @param time
     * @return
     */
    public static String formatDateTime(Context context,long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        Date d = new Date(time);
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int year = Integer.parseInt(TextUtils.isDigitsOnly(sdf.format(d))?sdf.format(d): currentYear+"");
        if(currentYear == year){
            return DateUtils.formatDateTime(context,
                    time,
                    DateUtils.FORMAT_SHOW_DATE
                            //| DateUtils.FORMAT_SHOW_WEEKDAY
                            //| DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            //| DateUtils.FORMAT_ABBREV_WEEKDAY
                            | DateUtils.FORMAT_ABBREV_TIME
                            | DateUtils.FORMAT_SHOW_TIME);
        }else{
            return DateUtils.formatDateTime(context,
                    time,
                    DateUtils.FORMAT_SHOW_DATE
                            //| DateUtils.FORMAT_SHOW_WEEKDAY
                            | DateUtils.FORMAT_SHOW_YEAR
                            | DateUtils.FORMAT_ABBREV_MONTH
                            //| DateUtils.FORMAT_ABBREV_WEEKDAY
                            | DateUtils.FORMAT_ABBREV_TIME
                            | DateUtils.FORMAT_SHOW_TIME);
        }

    }

    /**
     * 格式化年月日为Date
     * @param year
     * @param month
     * @param day
     * @return
     */
    public static Date formatToDate(int year,int month,int day){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String dateStr = "" + year + (month < 10 ? ("0" + month) : month) + (day < 10 ? ("0" + day) : day);
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化时间为Date
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static Date formatToDate(int hourOfDay,int minute){
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm");

        String timeStr = "" + (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + (minute < 10 ? ("0" + minute) : minute);
        Date date = null;
        try {
            date = sdf.parse(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 格式化完整的时间
     * @param year
     * @param month
     * @param day
     * @param hourOfDay
     * @param minute
     * @return
     */
    public static Date formatToDate(int year,int month,int day,int hourOfDay,int minute){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        String dateStr = "" + year + (month < 10 ? ("0" + month) : month) + (day < 10 ? ("0" + day) : day);
//        String timeStr = " " + (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + (minute < 10 ? ("0" + minute) : minute);//前面有空格
        String str = year + "-" + month + "-" + day + " " + hourOfDay + ":" + minute;

        Date time= null;//.format(today);
        try {
            time = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * 验证设置的时间是否合理
     * 规则：设置的时间-提前的时间  >  现在的时间
     * @return
     */
    public static long validateDate(long alarmTime,int remind) {
        Date date = new Date();
        long time = date.getTime();

        int remindMinute = 0; //提前提醒分钟
        switch (remind){
            case 0: //不提前
                remindMinute = 0;
                break;
            case 1://提前10分钟
                remindMinute = 10;
                break;
            case 2://提前半小时
                remindMinute = 30;
                break;
            case 3://提前一小时
                remindMinute = 1 * 60;
                break;
            case 4://提前三小时
                remindMinute = 3 * 60 ;
                break;
            case 5://提前一天
                remindMinute = 24 * 60 * 60;
                break;

        }
        long early = remindMinute * 60 * 1000;// == 0 ? 0 : (remind == 1 ? 10 * 60 * 1000 : (remind == 2 ? 30 * 60 * 1000 : 60 * 60 * 1000));

        if (time >= alarmTime - early) {
            return -1;
        } else {
            return early;
        }
    }
}
