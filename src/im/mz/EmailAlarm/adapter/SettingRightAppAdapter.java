package im.mz.EmailAlarm.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.db.AppsFilterDbUtils;
import im.mz.EmailAlarm.db.EAContract;
import im.mz.EmailAlarm.entity.AppInfoEntity;
import im.mz.EmailAlarm.utils.PreferenceUtils;

/**
 * Created by mzhua_000 on 2014/12/29.
 */
public class SettingRightAppAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private List<AppInfoEntity> apps;
    private LayoutInflater inflater;

    private PreferenceUtils preferenceUtils;

    private OnButtonClickListener onButtonClickListener;

    public OnButtonClickListener getOnButtonClickListener() {
        return onButtonClickListener;
    }

    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.onButtonClickListener = onButtonClickListener;
    }

    public interface OnButtonClickListener{
        public void onClick(int position,boolean isChecked);
    }
    public SettingRightAppAdapter(Context context, List<AppInfoEntity> apps) {
        this.context = context;
        this.apps = apps;
        this.inflater = LayoutInflater.from(context);

        preferenceUtils = new PreferenceUtils(context);
    }

    public synchronized void notifyDataChange(List<AppInfoEntity> apps) {
        this.apps = apps;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (apps != null && apps.size() > 0) {

            RightViewHolder rightViewHolder ;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.activity_flyme_right_item_app, null);
                rightViewHolder = new RightViewHolder();
                rightViewHolder.appIcon = (ImageView) convertView.findViewById(R.id.iv_flyme_app_icon);
                rightViewHolder.appLabel = (TextView) convertView.findViewById(R.id.tv_flyme_app_name);
                rightViewHolder.btnState = (Button) convertView.findViewById(R.id.btn_flyme_app_status);
                rightViewHolder.btnState.setBackgroundColor(preferenceUtils.getCurrentTheme());

                convertView.setTag(rightViewHolder);
            } else {
                rightViewHolder = (RightViewHolder) convertView.getTag();
            }

            rightViewHolder.appIcon.setImageDrawable(apps.get(position).getIcon());
            rightViewHolder.appLabel.setText(apps.get(position).getLabel());
            if(apps.get(position).isBtnChecked()){
                rightViewHolder.btnState.setText(context.getResources().getString(R.string.btn_app_added));
                rightViewHolder.btnState.setBackgroundColor(context.getResources().getColor(R.color.list_item_pressed));
            }else{
                rightViewHolder.btnState.setText(context.getResources().getString(R.string.btn_app_add));
                rightViewHolder.btnState.setBackgroundColor(preferenceUtils.getCurrentTheme());
            }

            rightViewHolder.btnState.setTag(position);

            rightViewHolder.btnState.setOnClickListener(this);
            convertView.setBackgroundResource(R.drawable.selector_list_item);
            return convertView;
        }

        return null;
    }

    @Override
    public void onClick(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        String packageName = apps.get(position).getPackageName();
        Button btn = (Button) v;
        if(btn.getText().equals(context.getResources().getString(R.string.btn_app_add))){   //之前尚未添加到过滤,点击加入过滤表数据
            ContentValues values = new ContentValues();
            values.put(EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME,packageName);
            AppsFilterDbUtils.insertData(context,values);

            onButtonClickListener.onClick(position,true);
        }else{ //之前已经添加到过滤，再次点击时，从数据表中删除
            String selection = EAContract.AppsFilterEntry.COLUMN_NAME_PACKAGENAME + " = ?";
            String[] selectionArgs = {String.valueOf(packageName)};
            AppsFilterDbUtils.delData(context,selection,selectionArgs);

            onButtonClickListener.onClick(position,false);
        }

//        Toast.makeText(context, "packagename="+packageName, Toast.LENGTH_SHORT).show();

    }

    class RightViewHolder {
        ImageView appIcon;
        TextView appLabel;
        Button btnState;
    }


}
