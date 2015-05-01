package im.mz.EmailAlarm.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import net.frederico.showtipsview.ShowTipsBuilder;
import net.frederico.showtipsview.ShowTipsView;
import net.frederico.showtipsview.ShowTipsViewInterface;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.entity.AlarmListEntity;
import im.mz.EmailAlarm.utils.NotifyUtil;
import im.mz.EmailAlarm.utils.PreferenceUtils;
import im.mz.EmailAlarm.view.EACircletime;

import java.util.Date;

/**
 * Created by Hua on 2014/10/20.
 */
public class MyTopNextFragment extends Fragment {

    private View mView;
    private EACircletime myCircletime;
    private TextView tv_next_time;
    private TextView tv_next_location;

    private PreferenceUtils preferences;

    //    private String detail="";
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferences = new PreferenceUtils(getActivity());

        mView = inflater.inflate(R.layout.activity_main_top_next, container, false);

        tv_next_time = (TextView) mView.findViewById(R.id.tv_next_time);
        tv_next_location = (TextView) mView.findViewById(R.id.tv_next_location);

        myCircletime = (EACircletime) mView.findViewById(R.id.myCircletime);
        myCircletime.setOnTimeChangeListener(new EACircletime.OnTimeChangeListener() {
            @Override
            public void onTimeChange(EACircletime myCircletime) {
                setTimeView(myCircletime.getDay(), myCircletime.getHour(), myCircletime.getMinute());

            }
        });
        myCircletime.setInterval(1000);
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.setBackgroundColor(preferences.getCurrentTheme());
        refreshDetail();
        if (preferences.getBoolean(PrefenceKeyConstants.FLAG_FIRST_START, true)) {
            showGuide();
            preferences.getEditor().putBoolean(PrefenceKeyConstants.FLAG_FIRST_START, false).commit();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 软件引导
     */
    private void showGuide() {
        final ShowTipsBuilder showTipsBuilder = new ShowTipsBuilder(getActivity()).setBackgroundColor(getResources().getColor(R.color.tipsBg))
                .setCircleColor(getResources().getColor(R.color.white))
                .setShape(ShowTipsView.SHAPE_ROUNDRECT)
                .setTitleColor(preferences.getCurrentTheme())
                .setButtonText(getResources().getString(R.string.guide_btn))
                .setButtonBgColor(preferences.getCurrentTheme())
//                .displayOneTime(5)
                .setDelay(0)
                .setTarget(mView).setTitle(getResources().getString(R.string.guide_next_alarm))
                .setCallback(new ShowTipsViewInterface() {
                    @Override
                    public void gotItClicked() {
                        ShowTipsBuilder showTipsBuilder = new ShowTipsBuilder(getActivity()).setBackgroundColor(getResources().getColor(R.color.tipsBg))
                                .setCircleColor(getResources().getColor(R.color.white))
                                .setTitleColor(preferences.getCurrentTheme())
                                .setButtonText(getResources().getString(R.string.guide_btn))
                                .setButtonBgColor(preferences.getCurrentTheme())
                                .setDelay(0)
                                .setTarget(myCircletime).setTitle(getResources().getString(R.string.guide_time))//"逆时针旋转的时分秒倒计时，时针1圈为24小时，分针1圈为1小时，秒针一圈为1分钟。"
//                                .displayOneTime(1)
                                .setCallback(new ShowTipsViewInterface() {
                                    @Override
                                    public void gotItClicked() {
                                        ShowTipsBuilder showTipsBuilder = new ShowTipsBuilder(getActivity()).setBackgroundColor(getResources().getColor(R.color.tipsBg))
                                                .setCircleColor(getResources().getColor(R.color.white))
                                                .setShape(ShowTipsView.SHAPE_ROUNDRECT)
                                                .setTitleColor(preferences.getCurrentTheme())
                                                .setButtonText(getResources().getString(R.string.guide_btn))
                                                .setButtonBgColor(preferences.getCurrentTheme())
//                                                .displayOneTime(2)
                                                .setDelay(0)
                                                .setTarget(tv_next_time).setTitle(getResources().getString(R.string.guide_time_right))
                                                .setCallback(new ShowTipsViewInterface() {
                                                    @Override
                                                    public void gotItClicked() {
                                                        ShowTipsBuilder showTipsBuilder = new ShowTipsBuilder(getActivity()).setBackgroundColor(getResources().getColor(R.color.tipsBg))
                                                                .setCircleColor(getResources().getColor(R.color.white))
                                                                .setShape(ShowTipsView.SHAPE_ROUNDRECT)
                                                                .setTitleColor(preferences.getCurrentTheme())
                                                                .setButtonText(Build.VERSION.SDK_INT >= 18 &&!NotifyUtil.isNotifyAccessEnabled(getActivity()) ? getResources().getString(R.string.guide_btn1) : getResources().getString(R.string.guide_btn2))
                                                                .setButtonBgColor(preferences.getCurrentTheme())
//                                                                .displayOneTime(3)
                                                                .setDelay(0)
                                                                .setTarget(tv_next_location).setTitle(getResources().getString(R.string.guide_location))
                                                                .setCallback(new ShowTipsViewInterface() {
                                                                    @Override
                                                                    public void gotItClicked() {
                                                                        //读取通知权限
                                                                        if (Build.VERSION.SDK_INT >= 18 && !NotifyUtil.isNotifyAccessEnabled(getActivity()) && preferences.getBoolean("pre_setting_sw_notify", true)) {
                                                                            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                                                                            startActivity(intent);
                                                                        }
                                                                    }
                                                                });
                                                        ShowTipsView showTipsView = showTipsBuilder.build();

                                                        showTipsView.show(getActivity());

                                                    }
                                                });
                                        ShowTipsView showTipsView = showTipsBuilder.build();

                                        showTipsView.show(getActivity());
                                    }
                                });
                        ShowTipsView showTipsView = showTipsBuilder.build();

                        showTipsView.show(getActivity());
                    }
                });
        ShowTipsView showTipsView = showTipsBuilder.build();

        showTipsView.show(getActivity());
    }

    /**
     * 更新详细信息
     */
    public void refreshDetail() {

        AlarmListEntity entity = AlarmDbUtils.getRecentAlarm(getActivity());
        if (entity != null) {
            tv_next_location.setText(entity.getLocation());
            long time = entity.getAlarmTime();
            long now = (new Date()).getTime();
            int day;
            int hour;
            int minute;
            if (time > now) {
                long countTime = time - now;
                day = (int) (countTime / (1000 * 60 * 60 * 24));
                hour = (int) (countTime % (1000 * 60 * 60 * 24) / (1000 * 60 * 60));
                minute = (int) (countTime % (1000 * 60 * 60) / (1000 * 60));
            } else {
                day = 0;
                hour = 0;
                minute = 0;
            }
            setTimeView(day, hour, minute);

            myCircletime.setTime(entity.getAlarmTime());
            myCircletime.start();
        } else {
            tv_next_location.setText(getResources().getString(R.string.main_list_item_no_location));
            setTimeView(0, 0, 0);

            myCircletime.stop();
        }


    }

    /**
     * 设置时间显示
     *
     * @param day    天
     * @param hour   小时
     * @param minute 分钟
     */
    private void setTimeView(double day, double hour, double minute) {
        tv_next_time.setText(Math.round(day) + "D " + (hour < 10 ? "0" + Math.round(hour) : Math.round(hour)) + ":" + (minute < 10 ? "0" + Math.round(minute) : Math.round(minute)));
    }


    @Override
    public void onStop() {
        super.onStop();
        if (myCircletime != null)
            myCircletime.removeOnTimeChangeListener();
    }
}