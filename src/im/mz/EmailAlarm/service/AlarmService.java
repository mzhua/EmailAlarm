package im.mz.EmailAlarm.service;

import android.app.*;
import android.content.*;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.*;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.activity.GenerateResultActivity;
import im.mz.EmailAlarm.constants.NotificationConstants;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.entity.AlarmListEntity;
import im.mz.EmailAlarm.utils.*;
import im.mz.EmailAlarm.view.ScrollTextView;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * Created by 敏华 on 2014/10/28.
 */
public class AlarmService extends Service {

    private Context context;
    private NotificationManager notificationManager;
    private AlarmManager alarmManager;
    private ClipboardManager clipboard;

    private PreferenceUtils pref;
    private SharedPreferences.Editor editor;

    private PendingIntent resultPendingIntent = null;

    private Handler handler;
    private AlertDialog ad;

    private NotifyUtil notifyUtil;

    //通知监听
    private IntentFilter notifyFilter;
    private NLServiceReceiver nlServiceReceiver;

    //views
    private LinearLayout dialogRemind;
    private ScrollTextView dr_time;
    private TextView dr_location;
    private ScrollTextView dr_detail;
    private TextView dialog_remind_edit;
    private TextView tv_dialog_remind_from;
    private ImageView iv_dialog_remind_from;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        if (android.os.Build.VERSION.SDK_INT >= 15) {
            Log.d("TAG", "sdk_int");
            setTheme(android.R.style.Theme_DeviceDefault_Light_Dialog);
        }
        pref = new PreferenceUtils(context);
        editor = pref.getEditor();

        clearHistoryData();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        notifyUtil = new NotifyUtil(context);

        initialListener();
        handler = new Handler() {
            @Override
            public void handleMessage(final Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                final String sourceString = data.getString("data");//String) msg.obj;
                switch (msg.what) {
                    case 0:

                        initDialogView(data);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setIcon(R.drawable.ic_launcher);
                        builder.setView(dialogRemind);
                        builder.setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AlarmService.this, GenerateResultActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Bundle data = new Bundle();
                                data.putString("flag", "auto");

                                data.putString("data", sourceString);
                                Log.d("TAG", "data=" + sourceString);
                                intent.putExtras(data);
                                startActivity(intent);

                            }
                        }).setNegativeButton("取消", null);
                        if (ad != null && ad.isShowing()) {
                            ad.dismiss();
                            ad = null;

                        }
                        ad = builder.create();
                        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                        ad.show();
                        break;
                }
            }
        };

    }

    /**
     * 清除过期数据
     */
    private void clearHistoryData() {
        //自动清理
        if(pref.getBoolean(PrefenceKeyConstants.SETTING_AUTO_CLEAR,false)){
            AlarmDbUtils.clearHistoryDate(context);
        }
        //重置preference中的一周7天的统计数据
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if(dayOfWeek == Calendar.MONDAY){
            for(int i = 0;i < 7;i++){
                editor.putInt(String.valueOf(i),0);
            }
            editor.commit();
        }
    }

    /**
     * 初始化自动创建提醒Dialog
     */
    private void initDialogView(Bundle data) {
        String detail = data.getString("data", "");

        long time = PatternMatcherUtils.analyzeData(detail);
        String location = "";
        Matcher matcher = PatternMatcherUtils.pLocation.matcher(detail);

        if (matcher.find()) {
            location = matcher.group();
        }

        dialogRemind = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_remind, null);

        dr_time = (ScrollTextView) dialogRemind.findViewById(R.id.dialog_remind_time);
        dr_location = (TextView) dialogRemind.findViewById(R.id.dialog_remind_location);
        dr_detail = (ScrollTextView) dialogRemind.findViewById(R.id.dialog_remind_detail);
        dialog_remind_edit = (TextView) dialogRemind.findViewById(R.id.dialog_remind_edit);
        tv_dialog_remind_from = (TextView) dialogRemind.findViewById(R.id.tv_dialog_remind_from);
        iv_dialog_remind_from = (ImageView) dialogRemind.findViewById(R.id.iv_dialog_remind_from);
        dialog_remind_edit.setVisibility(View.GONE);

        dr_time.setText(MyDateUtils.formatToDateFull(context, time));
        dr_location.setText(location);
        dr_detail.setText(detail);

        tv_dialog_remind_from.setTextColor(pref.getCurrentTheme());

        PackageManager packageManager = getPackageManager();
        String appLabel = "";
        Drawable appIcon = null;
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(data.getString("packageName", ""), 0);
            appLabel = (String) packageManager.getApplicationLabel(applicationInfo);
            appIcon = packageManager.getApplicationIcon(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(!StringUtils.isBlank(appLabel)){
            tv_dialog_remind_from.setText("来自："+appLabel);
            iv_dialog_remind_from.setVisibility(View.VISIBLE);
            iv_dialog_remind_from.setImageDrawable(appIcon);
        }else{
            tv_dialog_remind_from.setText(getResources().getString(R.string.app_name));
            iv_dialog_remind_from.setVisibility(View.GONE);
        }

    }

    /**
     * 注册剪切板监听和通知监听
     */
    private void initialListener() {

        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {

                if (pref.getBoolean(PrefenceKeyConstants.SETTING_CLIP, true)) {
                    String clipData = "";
                    ClipData clip = clipboard.getPrimaryClip();
                    if (clip != null && clip.getItemCount() > 0) {
                        clipData = clip.getItemAt(0).coerceToText(context).toString();
                    }
                    //只有复制的内容中有相关的提醒信息时才会创建
                    if (PatternMatcherUtils.isMatched(clipData)) {
                        Message msg = handler.obtainMessage();
                        msg.what = 0;
                        Bundle bundle = new Bundle();
                        bundle.putString("data", clipData);
                        bundle.putString("packageName","");
                        msg.setData(bundle);
                        handler.sendMessage(msg);

                    }
                }


            }
        });

        if (Build.VERSION.SDK_INT >= 18) {
            notifyFilter = new IntentFilter();
            notifyFilter.addAction("im.mz.EmailAlarm.NLService");
            nlServiceReceiver = new NLServiceReceiver();
            registerReceiver(nlServiceReceiver, notifyFilter);
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setAlarm();

        if (pref.getBoolean(PrefenceKeyConstants.SETTING_QUICK_START, false)) {
            notifyUtil.initQuickStartNotification();
        } else {
            notificationManager.cancel(NotificationConstants.QUICK_START_NOTIFY_ID);
        }
        return super.onStartCommand(intent, flags, startId);
    }




    /**
     * 设置提醒
     */
    private void setAlarm() {
//        Date date = new Date();
        long time = 0;//date.getTime() + 3000;
        AlarmListEntity entity = AlarmDbUtils.getRecentAlarm(context);

        if (entity != null) {

            Intent resultIntent = new Intent(this, RemindService.class);
            Bundle data = new Bundle();
            data.putLong("id", entity.getId());
            resultIntent.putExtras(data);

            time = entity.getAlarmTime();
            Log.d("TAG", "recent date=" + time + "   id=" + entity.getId());
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            if (resultPendingIntent != null) {
                alarmManager.cancel(resultPendingIntent);
                resultPendingIntent = null;
            }

            resultPendingIntent = PendingIntent.getService(
                    this,
                    0,
                    resultIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

            if (time >= (new Date()).getTime()) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, resultPendingIntent);
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nlServiceReceiver);
    }


    private class NLServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();

            Message msg = new Message();//handler.obtainMessage();
            msg.what = 0;
            msg.obj = data.getString("data", "");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }


}
