package im.mz.EmailAlarm.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.FlymeSettingConstants;
import im.mz.EmailAlarm.entity.FlymeRightEntity;
import im.mz.EmailAlarm.utils.StringUtils;

/**
 * Created by mzhua_000 on 2015/1/2.
 */
public class FlymeRightAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private Context context;
    private LayoutInflater inflater;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private List<FlymeRightEntity> flymeRightEntityList;

    public FlymeRightAdapter(Context context, List<FlymeRightEntity> flymeRightEntityList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.flymeRightEntityList = flymeRightEntityList;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    public void notifyDataChange(List<FlymeRightEntity> flymeRightEntityList){
        this.flymeRightEntityList = flymeRightEntityList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return flymeRightEntityList.size();
    }

    @Override
    public Object getItem(int position) {
        return flymeRightEntityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(flymeRightEntityList != null && flymeRightEntityList.size() > 0){

            ViewHolder viewHolder = null;
            convertView = null;
            if(convertView == null ){
                viewHolder = new ViewHolder();
                if(flymeRightEntityList.get(position).getItemType() == FlymeSettingConstants.LIST_ITEM_TYPE || flymeRightEntityList.get(position).getItemType() == FlymeSettingConstants.TEXT_ITEM_TYPE){//点击弹出listview选择
                    convertView = inflater.inflate(R.layout.activity_flyme_right_item_list,null);
                    viewHolder.title = (TextView) convertView.findViewById(R.id.tv_flyme_right_item_title);
                    viewHolder.summary = (TextView) convertView.findViewById(R.id.tv_flyme_right_item_summary);
                }else if(flymeRightEntityList.get(position).getItemType() == FlymeSettingConstants.SWITCH_ITEM_TYPE){//switch开关
                    convertView = inflater.inflate(R.layout.activity_flyme_right_item_switch,null);
                    viewHolder.mSwitch = (Switch) convertView.findViewById(R.id.st_flyme_right_item);
                    viewHolder.mSwitch.setTag(position);
//                    viewHolder.mSwitch.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
                }
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if(flymeRightEntityList.get(position).getItemType() == FlymeSettingConstants.LIST_ITEM_TYPE || flymeRightEntityList.get(position).getItemType() == FlymeSettingConstants.TEXT_ITEM_TYPE){
                viewHolder.title.setText( flymeRightEntityList.get(position).getTitle());
                if(StringUtils.isBlank(flymeRightEntityList.get(position).getSummary())){
                    viewHolder.summary.setVisibility(View.GONE);
                }else {
                    viewHolder.summary.setVisibility(View.VISIBLE);
                    viewHolder.summary.setText(flymeRightEntityList.get(position).getSummary());
                }
            }else if(flymeRightEntityList.get(position).getItemType() == FlymeSettingConstants.SWITCH_ITEM_TYPE){
                viewHolder.mSwitch.setText(flymeRightEntityList.get(position).getTitle());
                viewHolder.mSwitch.setChecked(flymeRightEntityList.get(position).isSwitchState());
            }

            return convertView;
        }

        return null;
    }

    class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = Integer.parseInt(buttonView.getTag().toString());
            if(flymeRightEntityList.get(position).getLeftItemPosition() == FlymeSettingConstants.ASSIST_SETTING_POSITION){
                switch (position){
                    case 0:
                        editor.putBoolean("pre_setting_cb_quick",isChecked);
                        break;
                    case 1:
                        editor.putBoolean("pre_setting_cb_light",isChecked);
                        break;
                    case 2:
                        editor.putBoolean("pre_setting_cb_listen",isChecked);
                        break;
                    case 3:
                        editor.putBoolean("pre_setting_sw_notify",isChecked);
                        break;
                    case 4:
                        if(isChecked && isChecked != preferences.getBoolean("pre_setting_delete_cache",false)){
                            Toast.makeText(context,"自动清理本月以前的数据",Toast.LENGTH_LONG).show();
                        }
                        editor.putBoolean("pre_setting_delete_cache", isChecked);
                        break;
                }
                editor.commit();
            }

        }
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class ViewHolder{
        TextView title;
        TextView summary;
        Switch mSwitch;
    }
}
