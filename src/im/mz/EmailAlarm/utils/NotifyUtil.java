package im.mz.EmailAlarm.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.activity.GenerateResultActivity;
import im.mz.EmailAlarm.activity.MyActivity;
import im.mz.EmailAlarm.activity.SettingFragmentActivity;
import im.mz.EmailAlarm.constants.NotificationConstants;

/**
 * Created by mzhua_000 on 2014/12/23.
 */
public class NotifyUtil {

    Context context;
    SharedPreferences pref;
    private NotificationManager notificationManager;

    public NotifyUtil(Context context){
        this.context = context;
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    /**
     * 设置通知栏快捷启动
     */
    public void initQuickStartNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText("点击快速建立提醒");
        Intent intent = new Intent(context, GenerateResultActivity.class);
        Bundle data = new Bundle();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //使用context启动activity时使用
        data.putString("flag", "add");
//        data.putBoolean("shouldCheckAlarm", true);
        intent.putExtras(data);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setAutoCancel(false);
        /*if(pref.getBoolean("pre_setting_cb_light",true)){
            builder.setLights(getResources().getColor(R.color.list_item_pressed_blue),2000,20000);
        }*/

        builder.setContentIntent(pendingIntent);
        Log.d("TAG", "notify");

        Notification notification = builder.build();
//        notification.setLatestEventInfo(context,"title","tag",pendingIntent);
        notification.flags = Notification.FLAG_NO_CLEAR;
        //notification.defaults |= Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NotificationConstants.QUICK_START_NOTIFY_ID, notification);
    }

    /**
     * 设置稍后提醒的通知栏
     */
    public void initDelayNotification(long id, String detail, String time) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("稍后提醒:" + time)
                .setContentText(detail).setWhen((new Date()).getTime());
        Intent intent = new Intent(context, GenerateResultActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //使用context启动activity时使用
        Bundle data = new Bundle();
        data.putLong("id", id);
        data.putString("flag", "edit");
//        data.putBoolean("shouldCheckAlarm", true);
        intent.putExtras(data);

        builder.setAutoCancel(true);
        if (pref.getBoolean("setting_light", true)) {
            builder.setLights(context.getResources().getColor(R.color.list_item_pressed_blue), 2000, 20000);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();

        notificationManager.notify(NotificationConstants.DELAY_ALARM_NOTIFY_ID, notification);
    }

    /**
     * 设置未读提醒的通知栏
     * @param id  提醒事件数据库中的id
     * @param detail  事件的详细信息
     * @param time 显示时间
     */
    public void initUnreadNotification(long id,String title,String detail,String time){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(title+":"+time).setTicker(detail)
                .setContentText(detail).setWhen((new Date()).getTime());
        Intent intent = new Intent(context, GenerateResultActivity.class);
        Bundle data = new Bundle();
        data.putLong("id",id);
        data.putString("flag", "edit");
//        data.putBoolean("shouldCheckAlarm", true);
        intent.putExtras(data);

        builder.setAutoCancel(true);
        if(pref.getBoolean("setting_light",true)){
            builder.setLights(context.getResources().getColor(R.color.list_item_pressed_blue),2000,20000);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Log.d("TAG","delay alarm notify");

        Notification notification = builder.build();

        notificationManager.notify(NotificationConstants.UNREAD_ALARM_NOTIFY_ID,notification);
    }


    /**
     * 导入excel数据
     */
    public void excelImportProgressNotify(int max,int useful,int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getResources().getString(R.string.app_name));
        if(progress < max){
            builder.setContentText("正在导入：" + progress + "/" + max)
                    .setProgress(max, progress, false);
        }else{
            builder.setContentText("导入完成，共"+ max + "条数据，" + "其中成功导入"+useful+"条有效数据");
            builder.setSound(RingtoneManager.getActualDefaultRingtoneUri(context,RingtoneManager.TYPE_NOTIFICATION));
        }

        Intent intent = new Intent(context, MyActivity.class);
        Bundle data = new Bundle();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //使用context启动activity时使用
        data.putInt("initPosition", 1);
        intent.putExtras(data);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setAutoCancel(true);

        builder.setContentIntent(pendingIntent);

        Notification notification = builder.build();
        if(progress < max){
            notification.flags = Notification.FLAG_NO_CLEAR;
        }else{
            notification.flags = Notification.FLAG_AUTO_CANCEL;
        }
        notificationManager.notify(NotificationConstants.EXCEL_IMPORT_NOTIFY_ID, notification);
    }

    /**
     * 判断是否有读取通知的权限
     * @return
     */
    private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
    public static boolean isNotifyAccessEnabled(Context context){
        String pkgName = context.getPackageName();
        final String flat = Settings.Secure.getString(context.getContentResolver(), ENABLED_NOTIFICATION_LISTENERS);
        if(!TextUtils.isEmpty(flat)){
            final String[] names = flat.split(":");
            for(int i = 0;i < names.length;i++){
                final ComponentName cn = ComponentName.unflattenFromString(names[i]);
                if(cn != null){
                    if(TextUtils.equals(cn.getPackageName(),pkgName)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * 重新授权
     */
    public static void reAuthorize(Context context){
        Intent intent=new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        context.startActivity(intent);
    }

    public void removeNotify(int id){
        notificationManager.cancel(id);
    }
}
