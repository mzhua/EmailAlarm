package im.mz.EmailAlarm.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AppsFilterDbUtils;
import im.mz.EmailAlarm.entity.AppInfoEntity;
import im.mz.EmailAlarm.utils.PatternMatcherUtils;

import java.util.ArrayList;

/**
 * Created by HUA on 2014/11/14.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NLService extends NotificationListenerService {
    private SharedPreferences preference;

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("TAG", "onNotificationPosted");
        if (preference.getBoolean(PrefenceKeyConstants.SETTING_APPS_NOTIFY, true) && Build.VERSION.SDK_INT >= 18) {
//            String tickerText = sbn.getNotification().tickerText.toString();
            String text = null;
            if (Build.VERSION.SDK_INT >= 19) {
                if (sbn.getNotification().extras != null) {
                    if (!TextUtils.isEmpty(sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT))) {
                        text = sbn.getNotification().extras.getCharSequence(Notification.EXTRA_TEXT).toString();
                    } else {
                        text = sbn.getNotification().tickerText.toString();
                    }
                } else {
                    text = sbn.getNotification().tickerText.toString();
                }

            } else if (Build.VERSION.SDK_INT >= 18) {
                text = sbn.getNotification().tickerText.toString();
            }

            String packageName = sbn.getPackageName();
            boolean shouldListen = false;  //是否需要监听
            ArrayList<AppInfoEntity> appInfoEntityArrayList = AppsFilterDbUtils.query(getApplicationContext(), null, null, null, null, null);
            if (appInfoEntityArrayList != null && appInfoEntityArrayList.size() > 0) {
                for (AppInfoEntity appInfoEntity : appInfoEntityArrayList) {
                    if (packageName.equals(appInfoEntity.getPackageName())) {
                        shouldListen = true;
                        break;
                    }
                }
            }

            if (shouldListen) {
                //只有复制的内容中有相关的提醒信息时才会创建
                if (PatternMatcherUtils.isMatched(text)) {
                    //自动删除通知
                    if (preference.getBoolean(PrefenceKeyConstants.SETTING_APPS_AUTO_REMOVE_NOTIFY, false)) {
                        cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
                    }
                    Intent i = new Intent();
                    i.setAction("im.mz.EmailAlarm.NLService");
                    Bundle bundle = new Bundle();

                    bundle.putString("data", text);
                    bundle.putString("packageName", packageName);
                    i.putExtras(bundle);
                    sendBroadcast(i);
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public StatusBarNotification[] getActiveNotifications() {
        return super.getActiveNotifications();
    }

    public NLService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preference = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d("TAG", "onCreate");

    }
}
