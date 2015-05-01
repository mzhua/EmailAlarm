package im.mz.EmailAlarm.fragment.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.utils.NotifyUtil;
import im.mz.EmailAlarm.utils.PreferenceUtils;

/**
 * Created by mzhua_000 on 2015/1/5.
 */
public class DataFragment extends Fragment {
    private Context context;
    private PreferenceUtils preferences;
    private SharedPreferences.Editor editor;
    private RelativeLayout rlClearAll;
    private RelativeLayout rlAutoLoad;
    private TextView tvAssistAutoLoadSummary;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onClick(int id, int position);
        public void onCheckChange(int id, int position);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();
        preferences = new PreferenceUtils(context);
        editor = preferences.getEditor();

        View view = inflater.inflate(R.layout.fragment_setting_data,container,false);
        initViews(view);


        return view;
    }

    private void initViews(View view){
        rlClearAll = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_clear_all);
        rlAutoLoad = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_assist_auto_load);
        tvAssistAutoLoadSummary = (TextView) view.findViewById(R.id.tv_fragment_setting_assist_auto_load_summary);

        if(preferences.getInt(PrefenceKeyConstants.SETTING_AUTO_LOAD, PrefenceKeyConstants.SETTING_AUTO_LOAD_DEFAULT_COUNT) == 0){
            tvAssistAutoLoadSummary.setText(getResources().getString(R.string.auto_load_all));
        }else{
            tvAssistAutoLoadSummary.setText(preferences.getInt(PrefenceKeyConstants.SETTING_AUTO_LOAD, PrefenceKeyConstants.SETTING_AUTO_LOAD_DEFAULT_COUNT)+"条");
        }


        rlClearAll.setOnClickListener(new AssistOnItemClickListener());
        rlAutoLoad.setOnClickListener(new AssistOnItemClickListener());
    }


    class AssistOnItemClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_fragment_setting_clear_all:
                    AlertDialog ad;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(getResources().getString(R.string.app_name));
                    builder.setMessage("确定清除所有提醒数据？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            AlarmDbUtils.delData(context, null, null, true);
                        }
                    }).setNegativeButton("取消", null);
                    ad = builder.create();
                    ad.show();
                    break;
                case R.id.rl_fragment_setting_import_excel:
                    onItemClickListener.onClick(v.getId(),4);
                    getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("excel").replace(R.id.setting_fragment, new AssistExcelFragment()).commit();
                    break;
                case R.id.rl_fragment_setting_assist_auto_load:
                    PopupMenu popupTheme = new PopupMenu(context, rlAutoLoad);
                    popupTheme.getMenuInflater().inflate(R.menu.popup_assist_auto_load, popupTheme.getMenu());

                    popupTheme.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {

                            if(item.getItemId() == R.id.menu_assist_0){
                                tvAssistAutoLoadSummary.setText(item.getTitle());
                                editor.putInt(PrefenceKeyConstants.SETTING_AUTO_LOAD,0).commit(); //0表示不限制加载条数
                            }else{
                                tvAssistAutoLoadSummary.setText(item.getTitle()+"条");
                                editor.putInt(PrefenceKeyConstants.SETTING_AUTO_LOAD, Integer.parseInt(TextUtils.isDigitsOnly(item.getTitle())?item.getTitle().toString():"0")).commit();
                            }

                            return true;
                        }
                    });

                    popupTheme.show();
                    break;


            }
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }
}
