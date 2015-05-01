package im.mz.EmailAlarm.service;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.*;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.*;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.activity.GenerateResultActivity;
import im.mz.EmailAlarm.constants.NotificationConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.db.EAContract;
import im.mz.EmailAlarm.entity.AlarmListEntity;
import im.mz.EmailAlarm.utils.MyDateUtils;
import im.mz.EmailAlarm.utils.NotifyUtil;
import im.mz.EmailAlarm.utils.PreferenceUtils;
import im.mz.EmailAlarm.view.ScrollTextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import static android.os.Build.VERSION.SDK_INT;

/**
 * Created by Hua on 2014/11/3.
 */
public class RemindService extends Service {
    private Context context;

    private PreferenceUtils preferenceUtils;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    private LinearLayout dialogRemind;
    private TextView time;
    private TextView location;
    private ScrollTextView detail;
    private TextView dialog_remind_edit;
    private TextView tv_dialog_remind_from;
    private ImageView iv_dialog_remind_from;

    private MediaPlayer mPlayer;
    private Vibrator vibrate;

    private AlertDialog ad;
    //private boolean killSelf = false;//是否需要stop自己，在runnable中用于判断，如果是用户手动触及屏幕导致dialog消失的则为true，如果是由于后一个提醒把前一个提醒覆盖而消失，则不要stop自己，即为false


    //点亮屏幕
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private NotificationManager notificationManager;

    private TelephonyManager telephonyManager;
    private MyPhoneStateListener myPhoneStateListener;
    private boolean shouldAlarm = true;//用于标记是否有来电的状态，有的话则为false，这时如果有提醒则暂停，并且不要stop本service

    private AlarmListEntity entity = null;
    //使用handler时首先要创建一个handler
    private Handler handler;

    private NotifyUtil notifyUtil;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        if (SDK_INT >= 15) {
            setTheme(android.R.style.Theme_DeviceDefault_Light_Dialog);
        }

        preferenceUtils = new PreferenceUtils(context);
        pref = preferenceUtils.getPreferences();
        editor = preferenceUtils.getEditor();

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        pm = (PowerManager) this.getSystemService(POWER_SERVICE);
        vibrate = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        notifyUtil = new NotifyUtil(context);

        myPhoneStateListener = new MyPhoneStateListener();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0://点击确定，stop service,如果是电话状态为ring或offhook，则暂时不要停止服务，因为电话结束后要继续显示之前的提醒
                        if (shouldAlarm) {
                            notificationManager.cancel(NotificationConstants.UNREAD_ALARM_NOTIFY_ID);
                            RemindService.this.stopSelf();
                        }

                        break;
                    case 1://有电话时，暂停提醒
                        shouldAlarm = false;
                        if (ad != null && ad.isShowing()) {
                            ad.dismiss();
                        }
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.release();
                            mPlayer = null;
                        }

                        vibrate.cancel();
                        break;
                    case 2://由于之前正在接电话，所以暂停了提醒，挂断电话后，开始提醒
                        shouldAlarm = true;
                        alarm();
                        break;
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        shouldAlarm = true;

        if(intent != null){
            Bundle data = intent.getExtras();
            long id = data.getLong("id", 0);

            initDialogView(id);

            if (entity != null) {
                //取消之前由于点击稍后提醒生成的通知
                updateWeeksCounts();
                //刷新主界面，如果在前台显示的话
                refreshUI(0,false);
            }

            telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

        checkNextAlarm();

        return super.onStartCommand(intent, flags, startId);
    }


    /**
     * 发送Broadcast更新主界面（如果主界面展示在前面时）
     * @param status   0:显示关闭  1：显示开启
     * @param delay  是否为延迟提醒，如果是，则要设置该事件的提醒时间从当前事件往后推5分钟
     */
    private void refreshUI(int status,boolean delay) {
        long time = (new Date()).getTime() + 5*60*1000; //延迟5分钟

        //设置通知
        if(delay){//延迟提醒通知
            notifyUtil.initDelayNotification(entity.getId(), entity.getDetail(), MyDateUtils.formatToTime(context,time));
        }else{//未读提醒通知
            time = entity.getDate();
            notifyUtil.initUnreadNotification(entity.getId(),"未读提醒",entity.getDetail(),MyDateUtils.formatToTime(context,entity.getDate()));
        }

        //首先更新数据库
        //将其标记为已提醒
        ContentValues values = new ContentValues();
        values.put(EAContract.MyEntry.COLUMN_NAME_STATUS, status);
        if(delay){

            values.put(EAContract.MyEntry.COLUMN_NAME_DATE, time);
            values.put(EAContract.MyEntry.COLUMN_NAME_ALARM_TIME, time);

        }
        AlarmDbUtils.updateDataById(context, entity.getId(), values);

        Intent i = new Intent();
        i.setAction("im.mz.EmailAlarm.RemindService");
        Bundle bundle = new Bundle();
        bundle.putLong("id", entity.getId());
        bundle.putLong("date",time);
        bundle.putInt("status", status);
        i.putExtras(bundle);
        sendBroadcast(i);
    }

    /**
     * 更新preference中一周的提醒数
     */
    private void updateWeeksCounts() {
        //按天记录提醒数
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(entity.getDate());
        int week = calendar.get(Calendar.DAY_OF_WEEK) - 2 < 0 ? 6 : calendar.get(Calendar.DAY_OF_WEEK) - 2;

        editor.putInt(String.valueOf(week), pref.getInt(String.valueOf(week), 0) + 1).commit();
    }

    /**
     * 启动AlarmService，重新检查是否有下一个提醒需要设置
     */
    private void checkNextAlarm(){
        Intent mIntent = new Intent(RemindService.this, AlarmService.class);
        startService(mIntent);
    }

    /**
     * 初始化Dialog
     */
    private void initDialogView(long id) {

        //因为initView需要设置弹窗信息，需要上面获取的id,必须在一开始就获取数据
        entity = AlarmDbUtils.queryById(context, id);

        dialogRemind = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog_remind, null);
        time = (TextView) dialogRemind.findViewById(R.id.dialog_remind_time);
        location = (TextView) dialogRemind.findViewById(R.id.dialog_remind_location);
        detail = (ScrollTextView) dialogRemind.findViewById(R.id.dialog_remind_detail);
        dialog_remind_edit = (TextView) dialogRemind.findViewById(R.id.dialog_remind_edit);
        tv_dialog_remind_from = (TextView) dialogRemind.findViewById(R.id.tv_dialog_remind_from);
        iv_dialog_remind_from = (ImageView) dialogRemind.findViewById(R.id.iv_dialog_remind_from);

        if (entity != null) {
            time.setText(MyDateUtils.formatToTime(context, entity.getDate()));
            location.setText(entity.getLocation());
            detail.setText(entity.getDetail());
        }

        dialog_remind_edit.setTextColor(preferenceUtils.getCurrentTheme());
        tv_dialog_remind_from.setTextColor(preferenceUtils.getCurrentTheme());
        tv_dialog_remind_from.setText(getResources().getString(R.string.app_name));
        iv_dialog_remind_from.setVisibility(View.GONE);

        dialog_remind_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,GenerateResultActivity.class);
                Bundle data = new Bundle();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //使用context启动activity时使用
                data.putString("flag","edit");
                data.putBoolean("shouldCheckAlarm", true);
                data.putLong("id",entity.getId());
                intent.putExtras(data);
                startActivity(intent);

                checkNextAlarm();
                RemindService.this.stopSelf();
            }
        });

    }

    /**
     * 控制各种提醒类型
     */
    private void alarm() {
        if (shouldAlarm) {
            wakeUpScreen();
            showRemindDialog();
            ring(entity.getRingtone());
            vibrate(Integer.parseInt(entity.getVibrate()));
        }

    }

    /**
     * 显示提醒框
     */
    private void showRemindDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(RemindService.this);

        builder.setView(dialogRemind);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Message msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        }).setNegativeButton("稍后提醒", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "5分钟后再次提醒", Toast.LENGTH_LONG).show();
                refreshUI(1,true);
                checkNextAlarm();
                RemindService.this.stopSelf();
            }
        });
        ad = builder.create();
        ad.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Message msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessage(msg);
            }
        });
        ad.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        ad.show();
    }



    /**
     * 点亮屏幕
     */
    private void wakeUpScreen() {

        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "my phonestate");
        wakeLock.acquire(3000);
    }

    /**
     * 响铃
     * @param ringtone
     */
    private void ring(String ringtone) {


        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
        } else {
            mPlayer = new MediaPlayer();
        }
        mPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
        try {
            mPlayer.setDataSource(context, Uri.parse(ringtone));
        } catch (IOException e) {
            e.printStackTrace();
        }


        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
                }
                vibrate.cancel();

            }
        });
        try {
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPlayer.setLooping(false);
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mPlayer.start();
            }
        });

    }

    /**
     * 振动
     * @param mode
     */
    private void vibrate(int mode) {

        if (mode == 1 && vibrate.hasVibrator()) {
            vibrate.vibrate(new long[]{0, 500, 800, 500, 800}, 0);
        }

    }

    /**
     * 监听电话状态变化
     */
    private class MyPhoneStateListener extends PhoneStateListener {
        public void onCallStateChanged(int state, String incomingNumber) {
            // default implementation empty
            Message msg = handler.obtainMessage();
            if (state == TelephonyManager.CALL_STATE_OFFHOOK || state == TelephonyManager.CALL_STATE_RINGING) {
                msg.what = 1;
            } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                msg.what = 2;
            }
            handler.sendMessage(msg);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        vibrate.cancel();
        if (ad != null && ad.isShowing()) {
            ad.dismiss();
        }
        telephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

}
