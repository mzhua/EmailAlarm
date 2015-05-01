package im.mz.EmailAlarm.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.activity.GenerateResultActivity;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.entity.AlarmListEntity;
import im.mz.EmailAlarm.utils.MyDateUtils;
import im.mz.EmailAlarm.utils.StringUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hua on 2014/10/15.
 */
public class MyAdapter extends BaseAdapter {

    Context context;
    ArrayList<AlarmListEntity> myListDataArr;
    LayoutInflater inflater;
    IUpdateListView iUpdateListView;
    SharedPreferences preferences;

    private int screenWidth;

    public interface IUpdateListView {
        public void removeItem(int position);

        public void changeStatus(int position);

    }

    public void setMyAdapterListener(IUpdateListView iUpdateListView) {
        this.iUpdateListView = iUpdateListView;
    }

    public MyAdapter(Context context, ArrayList<AlarmListEntity> myListDataArr) {
        this.context = context;
        this.myListDataArr = myListDataArr;
        this.inflater = LayoutInflater.from(context);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void onDateChange(ArrayList<AlarmListEntity> myListDataArr) {
//        this.myListDataArr.clear();
        this.myListDataArr = myListDataArr;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return myListDataArr.size();
    }

    @Override
    public Object getItem(int position) {
        return myListDataArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AlarmListEntity myListEntity = myListDataArr.get(position);

        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.activity_main_list_item, null);
            viewHolder.lv_my_item_time = (TextView) convertView.findViewById(R.id.lv_my_item_time);
            viewHolder.lv_my_item_location = (TextView) convertView.findViewById(R.id.lv_my_item_location);
            viewHolder.iv_my_item_alarm_status = (ImageView) convertView.findViewById(R.id.iv_my_item_alarm_status);
            viewHolder.iv_my_item_delete = (ImageView) convertView.findViewById(R.id.iv_my_item_delete);
            viewHolder.ll_my_list_item = (LinearLayout) convertView.findViewById(R.id.ll_my_list_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String date ;//= DateTimeUtils.formatTimeStampString(context,myListEntity.getDate(),DateTimeUtils.FORMAT_TYPE_CALL_LOGS_NEW);
        date = MyDateUtils.formatDateTime(context, myListEntity.getDate());

        viewHolder.lv_my_item_time.setText(date);
        viewHolder.lv_my_item_location.setText(myListEntity.getDetail().isEmpty() ? context.getResources().getString(R.string.main_list_item_no_detail) : myListEntity.getDetail());
        if (myListEntity.getStatus() == 1) {
            viewHolder.lv_my_item_time.setTextColor(context.getResources().getColor(R.color.black));

            String themeKey = preferences.getString(PrefenceKeyConstants.SETTING_THEME, context.getResources().getString(R.string.theme_default));
            if (themeKey.equals(context.getResources().getString(R.string.theme_blue))) {
                viewHolder.iv_my_item_alarm_status.setImageResource(R.color.theme_0);
            } else if (themeKey.equals(context.getResources().getString(R.string.theme_green))) {
                viewHolder.iv_my_item_alarm_status.setImageResource(R.color.theme_1);
            } else if (themeKey.equals(context.getResources().getString(R.string.theme_yellow))) {
                viewHolder.iv_my_item_alarm_status.setImageResource(R.color.theme_2);
            } else if (themeKey.equals(context.getResources().getString(R.string.theme_red))) {
                viewHolder.iv_my_item_alarm_status.setImageResource(R.color.theme_3);
            }

        } else {
            viewHolder.lv_my_item_time.setTextColor(context.getResources().getColor(R.color.alarm_status_off));
            viewHolder.iv_my_item_alarm_status.setImageResource(R.color.alarm_status_off);
        }

        viewHolder.iv_my_item_delete.setTag(position);
        viewHolder.ll_my_list_item.setTag(position);

        viewHolder.iv_my_item_delete.setOnClickListener(new MyOnClickListener());
        viewHolder.ll_my_list_item.setOnClickListener(new MyOnClickListener());
        viewHolder.ll_my_list_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(context, GenerateResultActivity.class);
                Bundle data = new Bundle();
                data.putString("flag", "edit");
                data.putBoolean("shouldCheckAlarm", false);
                data.putLong("id", myListDataArr.get((Integer) v.getTag()).getId());
                intent.putExtras(data);
                context.startActivity(intent);
//                Toast.makeText(context, context.getResources().getString(R.string.toast_main_list_tips), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        if (!StringUtils.isBlank(myListDataArr.get(position).getHeader())) {//是一个header数据
            return false;
        }
        return super.isEnabled(position);
    }

    private class ViewHolder {
        ImageView iv_my_item_alarm_status;
        ImageView iv_my_item_delete;
        TextView lv_my_item_time;
        TextView lv_my_item_location;
        LinearLayout ll_my_list_item;

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int position = msg.arg1;
            iUpdateListView.removeItem(position);
        }
    };


    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View v) {
            switch (v.getId()) {
                case R.id.iv_my_item_delete:
                    iUpdateListView.removeItem((Integer) v.getTag());
                   /* v.animate()
//                            .alpha(0)
//                            .rotation(360)
                            .rotation(360)
                            .scaleX(0)
                            .scaleY(0)
                            .setDuration(400).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            LinearLayout rl = (LinearLayout) v.getParent().getParent();
                            rl.animate()
//                                    .scaleY(0)
                                    .alpha(0)
                                    .x(screenWidth)
                                    .setDuration(400)
                                    .setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            synchronized (handler) {
                                                Message msg = handler.obtainMessage();
                                                msg.arg1 = (Integer) v.getTag();
                                                handler.sendMessage(msg);
                                            }

                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });


                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
*/

                    break;
                case R.id.ll_my_list_item:
                    int position = (Integer) v.getTag();

                    int status = Math.abs(myListDataArr.get(position).getStatus());
                    if (status == 1) {
                        Toast.makeText(context, context.getResources().getString(R.string.toast_main_list_tips_off), Toast.LENGTH_SHORT).show();
                    } else {
                        if (myListDataArr.get(position).getAlarmTime() < (new Date()).getTime()) {
                            Intent intent = new Intent(context, GenerateResultActivity.class);
                            Bundle data = new Bundle();
                            data.putString("flag", "edit");
                            data.putLong("id", myListDataArr.get(position).getId());
                            intent.putExtras(data);
                            context.startActivity(intent);
                            break;
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.toast_main_list_tips_on), Toast.LENGTH_SHORT).show();
                        }

                    }
                    iUpdateListView.changeStatus(position);
                    break;
            }

        }
    }


}
