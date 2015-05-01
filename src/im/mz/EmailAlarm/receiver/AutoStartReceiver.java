package im.mz.EmailAlarm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.service.AlarmService;

import java.util.Calendar;

/**
 * Created by 敏华 on 2014/10/28.
 */
public class AutoStartReceiver extends BroadcastReceiver {
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();

        Intent intent1;
        intent1 = new Intent(context, AlarmService.class);
//        intent1.setAction("im.mz.EmailAlarm.service.AlarmService");
        context.startService(intent1);

    }
}
