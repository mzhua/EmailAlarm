package im.mz.EmailAlarm.activity;

import android.app.AlertDialog;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.*;
import android.widget.*;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.db.EADbHelper;
import im.mz.EmailAlarm.entity.AlarmListEntity;
import im.mz.EmailAlarm.fragment.DatePickerFragment;
import im.mz.EmailAlarm.fragment.TimePickerFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

import im.mz.EmailAlarm.db.EAContract.MyEntry;
import im.mz.EmailAlarm.service.AlarmService;
import im.mz.EmailAlarm.utils.MyDateUtils;
import im.mz.EmailAlarm.utils.PatternMatcherUtils;
import im.mz.EmailAlarm.utils.StringUtils;

/**
 * Created by Hua on 2014/10/16.
 */
public class GenerateResultActivity extends BaseFragmentActivity implements DatePickerFragment.IDateChange, TimePickerFragment.ITimeChange {
    Context context;
    private SharedPreferences preferences;

    private TextView tv_gr_date;
    private TextView tv_gr_time;
    private EditText et_gr_location;
    private EditText et_gr_detail;
    private TextView tv_gr_ringtone_summary;
    private Switch sw_gr_vibrate;
    private Button bt_gr_delete;
    private TextView tv_gr_remind;

    private LinearLayout ll_gr;

    private String flag = "add";//add：添加   edit：编辑  控制是否需要显示删除按钮
//    private boolean shouldCheckAlarm = false;//根据不同的进入方式，区分要不要再保存后检查下一个提醒
    private long id;

    //日期、时间选择控件初始化使用
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private ClipboardManager clipboardManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gr);
        context = this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        getViews();



        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        initView(data);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getViews() {
        ll_gr = (LinearLayout) findViewById(R.id.ll_gr);

        tv_gr_date = (TextView) findViewById(R.id.tv_gr_date);
        tv_gr_time = (TextView) findViewById(R.id.tv_gr_time);
        et_gr_location = (EditText) findViewById(R.id.et_gr_location);
        et_gr_detail = (EditText) findViewById(R.id.et_gr_detail);
        tv_gr_ringtone_summary = (TextView) findViewById(R.id.tv_gr_ringtone_summary);
        sw_gr_vibrate = (Switch) findViewById(R.id.sw_gr_vibrate);
        bt_gr_delete = (Button) findViewById(R.id.bt_gr_delete);
        tv_gr_remind = (TextView) findViewById(R.id.tv_gr_remind);

    }

    /**
     * 初始化删除按钮的颜色
     */
    private void initDeleteBtnBg(){
        String themeKey = preferences.getString(PrefenceKeyConstants.SETTING_THEME,context.getResources().getString(R.string.theme_default));
        if(themeKey.equals(context.getResources().getString(R.string.theme_blue))){
            bt_gr_delete.setBackgroundColor(getResources().getColor(R.color.theme_0));
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_green))){
            bt_gr_delete.setBackgroundColor(getResources().getColor(R.color.theme_1));
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_yellow))){
            bt_gr_delete.setBackgroundColor(getResources().getColor(R.color.theme_2));
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_red))){
            bt_gr_delete.setBackgroundColor(getResources().getColor(R.color.theme_3));
        }


    }
    /**
     * 根据传参初始化界面
     */
    private void initView(Bundle data) {
        initDeleteBtnBg();


        bt_gr_delete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        bt_gr_delete.setBackgroundColor(getResources().getColor(R.color.summary));
                        break;
                    case MotionEvent.ACTION_UP:
                        initDeleteBtnBg();
                        break;
                }
                return false;
            }
        });

        if (data != null) {
            Calendar calendar = Calendar.getInstance();

            flag = data.getString("flag", "add");


//            shouldCheckAlarm = data.getBoolean("shouldCheckAlarm", false);

            long date = calendar.getTimeInMillis();
            String date_title = "";
            String time_title = "";
            String location = "";
            String detail = "";
            int remind = 2;
            Uri ringtone = null;
            boolean vibrate = true;

            //区分是添加还是编辑
            if ("add".equals(flag)) {
                getActionBar().setTitle(getResources().getString(R.string.action_bar_title_add_alarm));
                bt_gr_delete.setVisibility(View.GONE);

                //获取剪切板内容
               /* String clipData = "";
                ClipData clip = clipboardManager.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    clipData = clip.getItemAt(0).coerceToText(context).toString();
                }
                detail = clipData;
                Matcher matcher = PatternMatcherUtils.pLocation.matcher(clipData);

                if (matcher.find()) {
                    location = matcher.group();
                }
*/
                ringtone = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
            } else if("edit".equals(flag)) {
                id = data.getLong("id", 1);

                getActionBar().setTitle(getResources().getString(R.string.action_bar_title_edit_alarm));
                bt_gr_delete.setVisibility(View.VISIBLE);

                AlarmListEntity entity = AlarmDbUtils.queryById(context, id);//根据id获取数据,保存于
                if (entity != null) {
                    date = entity.getDate();
                    calendar.setTimeInMillis(date);

                    location = entity.getLocation();
                    location = location.equals(getResources().getString(R.string.main_list_item_no_location)) ? "" : location;
                    detail = entity.getDetail();
                    remind = entity.getRemind();
                    ringtone = Uri.parse(entity.getRingtone());
                    vibrate = entity.getVibrate().equals("1") ? true : false;
                }

            }else if("auto".equals(flag)){
                getActionBar().setTitle(getResources().getString(R.string.action_bar_title_add_alarm));
                bt_gr_delete.setVisibility(View.GONE);

                String clipData = data.getString("data","");
                /*ClipData clip = clipboardManager.getPrimaryClip();
                if (clip != null && clip.getItemCount() > 0) {
                    clipData = clip.getItemAt(0).coerceToText(context).toString();
                }*/
                if(!StringUtils.isBlank(clipData)){
                    detail = clipData;
                    Matcher matcher = PatternMatcherUtils.pLocation.matcher(clipData);

                    if (matcher.find()) {
                        location = matcher.group();
                    }
                    date = PatternMatcherUtils.analyzeData(clipData);

                    calendar.setTimeInMillis(date);
                }

                ringtone = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);

            }
            //初始化日期时间选择控件
            calendar.setTimeInMillis(date);
            this.year = calendar.get(Calendar.YEAR);
            this.month = calendar.get(Calendar.MONTH);
            this.day = calendar.get(Calendar.DAY_OF_MONTH);
            this.hour = calendar.get(Calendar.HOUR_OF_DAY);
            this.minute = calendar.get(Calendar.MINUTE);

            date_title = MyDateUtils.formatToDate(context, date);
            time_title = MyDateUtils.formatToTime(context, date);

            //日期
            tv_gr_date.setText(date_title);
            tv_gr_date.setTag(date);//时间
            //时间
            tv_gr_time.setText(time_title);
            tv_gr_time.setTag(date);
            //地点
            et_gr_location.setText(location);
            //详细
            et_gr_detail.setText(detail);
            //提醒
            tv_gr_remind.setText(getResources().getStringArray(R.array.remind_array)[remind]);
            tv_gr_remind.setTag(remind);
            //铃声
            if (ringtone != null && !"null".equals(ringtone.toString())) {
                tv_gr_ringtone_summary.setText(RingtoneManager.getRingtone(context, ringtone).getTitle(context));
            } else {
                tv_gr_ringtone_summary.setText(getResources().getString(R.string.activity_result_mute));
            }

            tv_gr_ringtone_summary.setTag(ringtone);
            //振动
            sw_gr_vibrate.setChecked(vibrate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.generate_result, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 时间选择控件,布局中定义了click事件
     *
     * @param v
     */
    public void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = new TimePickerFragment();
        newFragment.setCancelable(true);
//        newFragment.setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Dialog);
        newFragment.setiTimeChange(this);
        newFragment.initialData(this.hour, this.minute);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void timeChanged(int hourOfDay, int minute) {
        Date date = MyDateUtils.formatToDate(hourOfDay, minute);

        setTimeView(date.getTime());
    }

    private void setTimeView(long date) {
        String title = MyDateUtils.formatToTime(context, date);

        tv_gr_time.setText(title);
        tv_gr_time.setTag(date);

    }

    /**
     * 日期选择控件
     *
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setCancelable(true);
        newFragment.setiDataChange(this);
        newFragment.initialData(this.year, this.month, this.day);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void dateChanged(int year, int month, int day) {
        Date date = MyDateUtils.formatToDate(year, month, day);

        if (date == null) {
            date = new Date();
//            Toast.makeText(context, "设置日期出错，请反馈开发人员！", Toast.LENGTH_LONG).show();
        }

        setDateView(date.getTime());

    }

    /**
     * 设置日期显示
     *
     * @param date
     */
    private void setDateView(long date) {
        String title = MyDateUtils.formatToDate(context, date);
        tv_gr_date.setText(title);
        tv_gr_date.setTag(date);
    }

    /**
     * 选择铃声
     *
     * @param v
     */
    public void showRingtonePicker(View v) {
        Intent intent = new Intent();
        intent.setAction(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getResources().getString(R.string.activity_result_ringtone_picker_title));
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, (Uri) tv_gr_ringtone_summary.getTag());
        startActivityForResult(intent, 0);
    }

    /**
     * 铃声选择结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {

            Bundle bundle = data.getExtras();
            if (requestCode == 0) {//选择铃声
                Uri uri = (android.net.Uri) bundle.get(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                String title;
                if (uri == null) {
                    title = getResources().getString(R.string.activity_result_mute);
                } else {
                    title = RingtoneManager.getRingtone(context, uri).getTitle(context);
                }


                tv_gr_ringtone_summary.setText(title);
                tv_gr_ringtone_summary.setTag(uri);
            }

        }

    }

    /**
     * 选择提醒延时
     *
     * @param v
     */
    public void showRemindMenu(View v) {
        /*PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.getMenuInflater().inflate(R.menu.pop_gr_remind,popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        popupMenu.show();*/

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(getResources().getString(R.string.activity_result_remind_title))
                .setSingleChoiceItems(R.array.remind_array, (Integer) tv_gr_remind.getTag(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // The 'which' argument contains the index position
                        // of the selected item
                        tv_gr_remind.setText(context.getResources().getStringArray(R.array.remind_array)[which]);
                        tv_gr_remind.setTag(which);
                        dialog.dismiss();
                    }
                });
        builder.create().show();

    }

    /**
     * 删除
     * 在布局中的onclick中调用，此处不要删除
     *
     * @param v
     */
    public void delAlarm(View v) {
        delData(id);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_generate_result_save:
                if ("add".equals(flag) || "auto".equals(flag)) {
                    insertData();
                } else {
                    updateData(id);
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 格式化日期和时间控件选择好的时间
     *
     * @return
     */
    private Date getDateTime() {
        Calendar calendar_date = Calendar.getInstance();
        calendar_date.setTimeInMillis((Long) tv_gr_date.getTag());

        Calendar calendar_time = Calendar.getInstance();
        calendar_time.setTimeInMillis((Long) tv_gr_time.getTag());

        Date date = null;

        int y = calendar_date.get(Calendar.YEAR);
        int mon = calendar_date.get(Calendar.MONTH) + 1;
        int d = calendar_date.get(Calendar.DAY_OF_MONTH);
        int h = calendar_time.get(Calendar.HOUR_OF_DAY);
        int min = calendar_time.get(Calendar.MINUTE);

        date = MyDateUtils.formatToDate(y, mon, d, h, min);
        return date;
    }

    /**
     * 删除数据
     */
    private void delData(long id) {
        String selection = MyEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        AlarmDbUtils.delData(context,selection,selectionArgs,false);

        this.finish();
    }

    /**
     * 插入数据
     */
    private void insertData() {
        ContentValues values = initUpdateContentValue();
        if(values != null){
            AlarmDbUtils.insertData(context, values);
            this.finish();
        }

    }


    /**
     * 更新数据
     *
     * @param id 主键
     */
    private void updateData(long id) {
        ContentValues values = initUpdateContentValue();

        if(values != null){
            AlarmDbUtils.updateDataById(context, id, values);

//            if (shouldCheckAlarm) {
                //启动AlarmService，重新检查是否有下一个提醒需要设置
                Intent mIntent = new Intent(GenerateResultActivity.this, AlarmService.class);
                startService(mIntent);
//            }

            this.finish();
        }

    }

    /**
     * 验证设置的时间是否合理
     * 规则：设置的时间-提前的时间  >  现在的时间
     *
     * @return
     */
    private long validateDate() {
        long alarmTime = getDateTime().getTime();
        int remind = Integer.parseInt(String.valueOf(tv_gr_remind.getTag()));

        return MyDateUtils.validateDate(alarmTime,remind);
        /*long early = remind == 0 ? 0 : (remind == 1 ? 10 * 60 * 1000 : (remind == 2 ? 30 * 60 * 1000 : 60 * 60 * 1000));

        if (time >= getDateTime().getTime() - early) {
            return -1;
        } else {
            return early;
        }*/
    }

    /**
     * 初始化要插入或者更新的数据
     *
     * @return
     */
    private ContentValues initUpdateContentValue() {
        long date = getDateTime().getTime();
        ContentValues values = new ContentValues();
        values.put(MyEntry.COLUMN_NAME_DATE, date);

        long early = validateDate();
        if (early == -1) {
            Toast.makeText(context, getResources().getString(R.string.toast_validate_time), Toast.LENGTH_SHORT).show();
            values = null;
        } else {
            values.put(MyEntry.COLUMN_NAME_ALARM_TIME, date - early);
            values.put(MyEntry.COLUMN_NAME_LOCATION, String.valueOf(et_gr_location.getText()).equals("") ? getResources().getString(R.string.main_list_item_no_location) : String.valueOf(et_gr_location.getText()));
            values.put(MyEntry.COLUMN_NAME_DETAIL, String.valueOf(et_gr_detail.getText()));
            values.put(MyEntry.COLUMN_NAME_REMIND, String.valueOf(tv_gr_remind.getTag()));
            values.put(MyEntry.COLUMN_NAME_RINGTONE, String.valueOf(tv_gr_ringtone_summary.getTag()));
            values.put(MyEntry.COLUMN_NAME_VIBRATE, sw_gr_vibrate.isChecked() ? "1" : "0");//1：振动  0：不振动
            values.put(MyEntry.COLUMN_NAME_STATUS, 1);
        }


        return values;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}