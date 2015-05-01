package im.mz.EmailAlarm.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.utils.MyChronometer;
import im.mz.EmailAlarm.utils.PreferenceUtils;

import java.util.Calendar;
import java.util.Date;

import static java.lang.Math.*;

/**
 * Created by mzhua_000 on 2014/12/15.
 */
public class MyCircletime extends View {
    private final float DAY_TEXT_SIZE = 15;
    private final float STROKE_WIDTH_DEFAULT = 2;
    private final float PADDING_DEFAULT = 0;
    private final int STANDARD_DEFAULT = 24;
    //自定义属性
    private int mode; //模式，0：逆时针转  1：正动
    private boolean animation;//是否需要开启动画
    private float strokeWidth = STROKE_WIDTH_DEFAULT; //0，3，6，9小时刻度的宽度
    private float paddingHorizontal = PADDING_DEFAULT;
    private float paddingVertical = PADDING_DEFAULT;
    private int standard = STANDARD_DEFAULT;//制式：12小时制，24小时制
    private boolean showDay = true;
    private boolean showHour = true;
    private boolean showMinute = true;
    private boolean showSecond = true;
    /**
     * 默认的四个指针的旋转角度，相对与旋转间隔是1S
     */
    private double ANGLE_DAY_DEFAULT = 15;
    private double ANGLE_HOUR_DEFAULT = 15;
    private double ANGLE_MINUTE_DEFAULT = 6;
    private double ANGLE_SECOND_DEFAULT = 6;

    private float dayTextSize = DAY_TEXT_SIZE;

    //秒针转动间隔
    public long interval = 1000;

    private Context context;
    private Paint paint = new Paint();

    private int contentWidth, contentHeight;
    private int graduationBig = 18; //大刻度长度
    private int graduationLess = 15;//小刻度长度
    private int circleRadius; //时钟内圈半径


    private float thinStrokeWidth;
    private int hourLong;//时针长
    private int minuteLong;//分钟长
    private int secondsLong;//秒针长

    private int radius_circle_0 = 100;
    private int radius_circle_1 = 50;
    private int radius_circle_2 = 30;

    private String timeText = "";
    private String location = "";
    private double day = 2;
    private double hour = 22;
    private double minute = 56;
    private double seconds = 0;

    private double dayAngle = ANGLE_DAY_DEFAULT;
    private double hourAngle = ANGLE_HOUR_DEFAULT;
    private double minuteAngle = ANGLE_MINUTE_DEFAULT;
    private double secondAngle = ANGLE_SECOND_DEFAULT;

    private int timeWidth = 150;//右上角事件字符宽度
    private int timeHeight = 50;//右上角事件字符高度

    private Handler handler;

    private MyChronometer myChronometer;
    private boolean firstTime = true;

    private PreferenceUtils pref;

    private OnTimeChangeListener onTimeChangeListener;

    public void setOnTimeChangeListener(OnTimeChangeListener onTimeChangeListener) {
        this.onTimeChangeListener = onTimeChangeListener;
    }

    public interface OnTimeChangeListener{
        void onTimeChange(MyCircletime myCircletime);
    }

    public MyCircletime(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public MyCircletime(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleTime);

        mode = typedArray.getInt(R.styleable.CircleTime_mode, 0);
        strokeWidth = typedArray.getDimension(R.styleable.CircleTime_strokeWidth, STROKE_WIDTH_DEFAULT);
        standard = typedArray.getInt(R.styleable.CircleTime_standard, STANDARD_DEFAULT);

        showDay = typedArray.getBoolean(R.styleable.CircleTime_showDay,true);
        showHour = typedArray.getBoolean(R.styleable.CircleTime_showHour,true);
        showMinute = typedArray.getBoolean(R.styleable.CircleTime_showMinute,true);
        showSecond = typedArray.getBoolean(R.styleable.CircleTime_showSecond,true);

        dayTextSize = typedArray.getDimension(R.styleable.CircleTime_dayTextSize,DAY_TEXT_SIZE);

        Log.d("TAG","mode="+mode);
        typedArray.recycle();

        init();
    }

    private void init(){
        firstTime = true;
        thinStrokeWidth = 2*(strokeWidth/3);
        pref = new PreferenceUtils(context);
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
//                Log.d("TAG","handleMessage1111111111111111");
                switch (msg.what){
                    case 0:
                        if(onTimeChangeListener != null){
                            onTimeChangeListener.onTimeChange(MyCircletime.this);
                        }
                        if(mode == 0){
                            countDown();
                        }else{
                            timing();
                        }

                        break;
                }

            }
        };

        if(myChronometer != null){
            myChronometer.stop();
            myChronometer = null;
        }
        myChronometer = new MyChronometer();
        myChronometer.setOnMyChronometerTickListener(new MyChronometer.OnMyChronometerTickListener() {
            @Override
            public void onChronometerTick(MyChronometer myChronometer) {
                handler.sendEmptyMessage(0);
            }
        });
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (firstTime) {
            firstTime = false;
            initParams();
            Log.d("TAG", "onDraw");
        }

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(strokeWidth);
        paint.setAntiAlias(true);

        //刻度
        canvas.drawLine(paddingHorizontal + graduationBig + circleRadius, paddingVertical + graduationBig, paddingHorizontal + graduationBig + circleRadius, paddingVertical, paint);
        canvas.drawLine(paddingHorizontal + graduationBig + circleRadius, paddingVertical + graduationBig + 2 * circleRadius, paddingHorizontal + graduationBig + circleRadius, paddingVertical + 2 * graduationBig + 2 * circleRadius, paint);
        canvas.drawLine(paddingHorizontal, paddingVertical + graduationBig + circleRadius, paddingHorizontal + graduationBig, paddingVertical + graduationBig + circleRadius, paint);
        canvas.drawLine(paddingHorizontal + graduationBig + 2 * circleRadius, paddingVertical + graduationBig + circleRadius, paddingHorizontal + 2 * graduationBig + 2 * circleRadius, paddingVertical + graduationBig + circleRadius, paint);

        paint.setStrokeWidth(thinStrokeWidth);
        for(int i = 1 ; i <= 12 ;i++){
            canvas.drawLine((float)(paddingHorizontal + graduationBig + circleRadius*(1+ sin(toRadians(i*30)))),(float)(paddingVertical + graduationBig + circleRadius*(1- cos(toRadians(i*30)))),(float)(paddingHorizontal + graduationBig + (circleRadius + (circleRadius + graduationLess)*sin(toRadians(i*30)))),(float)(paddingVertical + graduationBig + (circleRadius - (circleRadius + graduationLess)* cos(toRadians(i*30)))),paint);
        }

        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(strokeWidth);
        if(showHour){
            //时针
            canvas.drawLine(paddingHorizontal + graduationBig + secondsLong, paddingVertical + graduationBig + secondsLong, (float) (paddingHorizontal + graduationBig + secondsLong + hourLong * sin(toRadians(hourAngle * hour))), (float) (paddingVertical + graduationBig + secondsLong - hourLong * cos(toRadians(hourAngle * hour))), paint);
        }
        if(showMinute){
            //分针
            canvas.drawLine(paddingHorizontal + graduationBig + secondsLong, paddingVertical + graduationBig + secondsLong, (float) (paddingHorizontal + graduationBig + secondsLong + minuteLong * sin(toRadians(minuteAngle * minute))), (float) (paddingVertical + graduationBig + secondsLong - minuteLong * cos(toRadians(minuteAngle * minute))), paint);
        }
        if(showSecond){
            paint.setColor(Color.RED );
            paint.setStrokeWidth(thinStrokeWidth);
            //秒针
            canvas.drawLine(paddingHorizontal + graduationBig + secondsLong, paddingVertical + graduationBig + secondsLong, (float) (paddingHorizontal + graduationBig + secondsLong * (1 + sin(toRadians(secondAngle * seconds)))), (float) (paddingVertical + graduationBig + secondsLong * (1 - cos(toRadians(secondAngle * seconds)))), paint);

        }

        //天数
        /*final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

        paint.setColor(Color.WHITE);
        paint.setTextSize(dayTextSize);
        paint.setStrokeWidth(0);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("120D", padding + 2 * graduationBig + 2 * circleRadius + dayTextSize, padding + graduationBig + circleRadius,paint);
        canvas.drawText("23H", padding + 2 * graduationBig + 2 * circleRadius + dayTextSize, padding + graduationBig + circleRadius + dayTextSize,paint);
*/

        //表盘中心的圈
        paint.setColor(Color.WHITE);
        canvas.drawCircle(paddingHorizontal + graduationBig + circleRadius,paddingVertical + graduationBig + circleRadius,6,paint);
        paint.setColor(pref.getCurrentTheme());
        canvas.drawCircle(paddingHorizontal + graduationBig + circleRadius,paddingVertical + graduationBig + circleRadius,4,paint);

    }


    /**
     * 参数初始化
     * 因为在onMeasure之后布局的大小才确定，所以在ondraw中调用
     */
    private void initParams() {
        contentWidth = this.getWidth();
        contentHeight = this.getHeight();

        /*DisplayMetrics metrics = getResources().getDisplayMetrics();
        contentWidth = metrics.widthPixels;
        contentHeight = metrics.heightPixels/3;*/

        //(contentHeight < contentWidth?contentHeight:contentWidth)
        circleRadius = (int) ((Math.min(contentWidth,contentHeight) - 2 * PADDING_DEFAULT - 2 * graduationBig) / 2);

        if(contentHeight < contentWidth){
            paddingHorizontal = (contentWidth - 2 * circleRadius - 2 * graduationBig)/2;
        }else{
            paddingVertical = (contentHeight - 2 * circleRadius - 2 * graduationBig)/2;
        }
        hourLong = circleRadius / 2;
        minuteLong = 3 * (circleRadius / 4);
        secondsLong = circleRadius;
        Log.d("TAG", "width=" + contentWidth + "   height = " + contentHeight + "  circleRadius=" + circleRadius);

        radius_circle_0 = contentWidth / 4;
        radius_circle_1 = 3 * radius_circle_0 / 5;
        radius_circle_2 = 3 * radius_circle_1 / 5;

        //设置时针的单位角度,根据一圈是12个小时或24个小时来设置
        if(standard == 24){
            hourAngle = 15;
        }else{
            hourAngle = 30;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    public void setTime(long time){

        if(mode == 0){
            long now = (new Date()).getTime();
            if(time > now){
                long countTime = time - now;
                day = (countTime / (1000 * 60 * 60 * 24)) ;
                seconds =  (countTime % (1000 * 60 )/1000 ) * (1000d/interval);
                minute =  (countTime % (1000 * 60 * 60 ) / (1000 * 60))  ;
                hour = (countTime % (1000 * 60 * 60 * 24)/(1000 * 60 * 60) ) ;
                Log.d("TAG","hour="+hour + "   minute="+minute + "  second="+seconds);
            }else{
                day = 0;
                hour = 0;
                minute = 0;
                seconds = 0;
            }
        }else{
            Calendar calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);

        }


        refreshClock();
    }

    /**
     * 刷新
     */
    public void refreshClock(){
        this.invalidate();
    }

    /**
     * 顺计时间
     */

    public void timing(){
        if(seconds >= 60){
            seconds = 0;
            if(minute >= 60){
                minute = 0;
                if(hour >= 24){
                    hour = 0;
                }
                hour++;
            }
            minute++;
        }
        seconds++;
        this.invalidate();
    }
    /**
     * 倒计时
     */
    public void countDown(){
        if(seconds <= 0){
            seconds = 60 * (1000d / interval);
            if(minute <= 0){
                minute = 60 ;
                if(hour <= 0){
                    hour = 24 ;
                }
                hour--;
            }
            minute--;
        }
        seconds --;

        this.invalidate();
    }

    public void setLocation(String location){
        this.location = location;
    }

    public String getLocation(){
        return this.location;
    }

    public void start(){
        if(mode == 1){ //顺时针旋转模式
            Calendar calendar = Calendar.getInstance();
            day = calendar.get(Calendar.DAY_OF_MONTH);
            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minute = calendar.get(Calendar.MINUTE);
            seconds = calendar.get(Calendar.SECOND);
        }

        myChronometer.start();
    }

    /**
     * 停止
     */
    public void stop(){
        day = 0;
        hour = 0;
        minute = 0;
        seconds = 0;
        setLocation("");
        myChronometer.stop();
        refreshClock();
    }

    public void removeOnTimeChangeListener(){
        this.stop();
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public boolean isAnimation() {
        return animation;
    }

    public void setAnimation(boolean animation) {
        this.animation = animation;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStandard() {
        return standard;
    }

    public void setStandard(int standard) {
        this.standard = standard;
    }

    public boolean isShowDay() {
        return showDay;
    }

    public void setShowDay(boolean showDay) {
        this.showDay = showDay;
    }

    public boolean isShowHour() {
        return showHour;
    }

    public void setShowHour(boolean showHour) {
        this.showHour = showHour;
    }

    public boolean isShowMinute() {
        return showMinute;
    }

    public void setShowMinute(boolean showMinute) {
        this.showMinute = showMinute;
    }

    public boolean isShowSecond() {
        return showSecond;
    }

    public void setShowSecond(boolean showSecond) {
        this.showSecond = showSecond;
    }

    public double getDay() {
        return day ;
    }

    public void setDay(double day) {
        this.day = day;
    }

    public double getHour() {
        return hour ;
    }

    public void setHour(double hour) {
        this.hour = hour;
    }

    public double getMinute() {
        return minute ;
    }

    public void setMinute(double minute) {
        this.minute = minute;
    }

    public double getSeconds() {
        return seconds * (interval / 1000d);
    }

    public void setSeconds(double seconds) {
        this.seconds = seconds;
    }

    public float getDayTextSize() {
        return dayTextSize;
    }

    public void setDayTextSize(float dayTextSize) {
        this.dayTextSize = dayTextSize;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
        myChronometer.setInterval(interval);
        secondAngle = (interval / 1000d) * ANGLE_SECOND_DEFAULT;
//        minuteAngle = (interval / 1000d) * ANGLE_MINUTE_DEFAULT;
//        hourAngle = (interval / 1000d) * ANGLE_HOUR_DEFAULT;

    }
}
