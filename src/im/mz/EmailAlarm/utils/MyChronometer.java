package im.mz.EmailAlarm.utils;

import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

/**
 * 自定义计时工具
 * Created by mzhua_000 on 2014/12/18.
 */
public class MyChronometer {

    //间隔
    public long interval = 1000;
    /**
     * A callback that notifies when the chronometer has incremented on its own.
     */
    public interface OnMyChronometerTickListener {

        /**
         * Notification that the chronometer has changed.
         */
        void onChronometerTick(MyChronometer myChronometer);

    }

    private boolean mRunning = false;

    private static final int TICK_WHAT = 2;

    private OnMyChronometerTickListener onMyChronometerTickListener;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            if (mRunning) {
                dispatchChronometerTick();
                sendMessageDelayed(Message.obtain(this, TICK_WHAT), interval);
            }
        }
    };

    public void start(){
        mHandler.removeMessages(TICK_WHAT);
        mRunning = true;
        mHandler.sendMessageDelayed(Message.obtain(mHandler, TICK_WHAT), interval);
    }

    public void stop(){
        mRunning = false;
        mHandler.removeMessages(TICK_WHAT);
    }

    public void toggle(){
        mRunning = !mRunning;
    }
    void dispatchChronometerTick() {
        if (onMyChronometerTickListener != null) {
            onMyChronometerTickListener.onChronometerTick(this);
        }
    }

    public void setOnMyChronometerTickListener(OnMyChronometerTickListener onMyChronometerTickListener) {
        this.onMyChronometerTickListener = onMyChronometerTickListener;
    }

    public long getInterval() {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }
}
