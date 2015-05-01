package im.mz.EmailAlarm.db;

import android.provider.BaseColumns;

/**
 * Created by Hua on 2014/10/17.
 */
public class EAContract {
    public static final String[] impleAlarmProjection = {
            MyEntry._ID,
            MyEntry.COLUMN_NAME_DATE,
            MyEntry.COLUMN_NAME_ALARM_TIME,
            MyEntry.COLUMN_NAME_LOCATION,
            MyEntry.COLUMN_NAME_STATUS
    };

    public static final String[] AlarmProjection = {
            MyEntry._ID,
            MyEntry.COLUMN_NAME_DATE,
            MyEntry.COLUMN_NAME_ALARM_TIME,
            MyEntry.COLUMN_NAME_LOCATION,
            MyEntry.COLUMN_NAME_DETAIL,
            MyEntry.COLUMN_NAME_REMIND,
            MyEntry.COLUMN_NAME_RINGTONE,
            MyEntry.COLUMN_NAME_VIBRATE,
            MyEntry.COLUMN_NAME_SORT,
            MyEntry.COLUMN_NAME_STATUS
    };

    public static final String[] AppProjection = {
            AppsFilterEntry._ID,
            AppsFilterEntry.COLUMN_NAME_PACKAGENAME
    };

    public static String[] projection_date={MyEntry._ID,MyEntry.COLUMN_NAME_DATE};

    public static abstract class MyEntry implements BaseColumns{
        public static final String TABLE_NAME_ALARM = "alarms";
        public static final String COLUMN_NAME_DATE = "alarm_date";
        public static final String COLUMN_NAME_ALARM_TIME = "alarm_time";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_DETAIL = "detail";
        public static final String COLUMN_NAME_RINGTONE = "ringtone";
        public static final String COLUMN_NAME_VIBRATE = "vibrate";
        public static final String COLUMN_NAME_REMIND = "remind";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_SORT = "sort";

    }

    /**
     * 提醒次数纪录表
     */
    public static abstract class CountEntry implements BaseColumns{
        public static final String TABLE_NAME_COUNT = "counts";
        public static final String COLUMN_NAME_DATE = "counts_date";
        public static final String COLUMN_NAME_COUNTS = "counts";
        public static final String COLUMN_NAME_WEEK = "week";
    }

    /**
     * 过滤apps表，存在此表中的app，将不监听其通知
     */
    public static abstract class AppsFilterEntry implements BaseColumns{
        public static final String TABLE_NAME_APP_FILTER = "appsfilter";
        public static final String COLUMN_NAME_PACKAGENAME = "packagename";
    }
}
