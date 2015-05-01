package im.mz.EmailAlarm.activity;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.adapter.SettingLeftAdapter;
import im.mz.EmailAlarm.adapter.FlymeRightAdapter;
import im.mz.EmailAlarm.adapter.SettingRightAppAdapter;
import im.mz.EmailAlarm.constants.FlymeSettingConstants;
import im.mz.EmailAlarm.entity.AppInfoEntity;
import im.mz.EmailAlarm.entity.FlymeRightEntity;
import im.mz.EmailAlarm.entity.SettingLeftEntity;
import im.mz.EmailAlarm.utils.PreferenceUtils;
/**
 * Created by mzhua_000 on 2014/12/29.
 */
public class FlymeActivity extends BaseFragmentActivity {
    private Context context;
    private LinearLayout llFlyme;
    private LinearLayout llFlymePb;
    private ListView lvFlymeLeft;
    private ListView lvFlymeRight;

    private List<SettingLeftEntity> drawableList = new ArrayList<SettingLeftEntity>();
    private List<AppInfoEntity> listApps;
    private List<FlymeRightEntity> flymeRightEntityList;

    private SettingLeftAdapter settingLeftAdapter;
    private FlymeRightAdapter flymeRightAdapter;
    private SettingRightAppAdapter rightAppAdapter;
    private PackageManager pm;

    private PreferenceUtils preferenceUtils;
    private Handler handler;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_flyme);
        this.context = this;

        preferenceUtils = new PreferenceUtils(context);
        initViews();


        //处理右侧设置界面的操作都由handler处理
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {

                getActionBar().setTitle(getResources().getStringArray(R.array.setting_actionbar_title)[msg.what]);
                if (msg.what != FlymeSettingConstants.APPS_SETTING_POSITION) {
                    if (rightAppAdapter != null) {
                        listApps.clear();
                        rightAppAdapter.notifyDataChange(listApps);
                        rightAppAdapter = null;
                        listApps = null;
                        lvFlymeRight.setOnItemClickListener(null);
                    }
                    initRightSettingData(msg.what);
                    if(flymeRightAdapter == null){
                        flymeRightAdapter = new FlymeRightAdapter(context, flymeRightEntityList);
                        lvFlymeRight.setAdapter(flymeRightAdapter);
                        lvFlymeRight.setOnItemClickListener(flymeRightAdapter);
                    }else{
                        flymeRightAdapter.notifyDataChange(flymeRightEntityList);
                    }



                } else {//过滤程序界面
                    if(flymeRightAdapter != null){
                        flymeRightEntityList.clear();
                        flymeRightAdapter.notifyDataChange(flymeRightEntityList);
                        flymeRightAdapter = null;
                        flymeRightEntityList = null;
                        lvFlymeRight.setOnItemClickListener(null);
                    }
                    lvFlymeRight.setVisibility(View.VISIBLE);
                    llFlymePb.setVisibility(View.GONE);

                        /*Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                listApps = getAllApps();

                            }
                        });
                        thread.start();*/
                    listApps = getAllApps();
                    if(rightAppAdapter == null){
                        rightAppAdapter = new SettingRightAppAdapter(FlymeActivity.this, listApps);
                        lvFlymeRight.setAdapter(rightAppAdapter);
                    }else{
                        rightAppAdapter.notifyDataChange(listApps);
                    }

                }
                return false;
            }
        });


    }

    private void initViews() {
        llFlyme = (LinearLayout) findViewById(R.id.ll_flyme);
        llFlymePb = (LinearLayout) findViewById(R.id.ll_flyme_pb);
        lvFlymeLeft = (ListView) findViewById(R.id.lv_flyme_left);
        lvFlymeRight = (ListView) findViewById(R.id.lv_flyme_right);

    }

    /**
     * 获取设置界面数据
     */
    private void initRightSettingData(int postion) {
        if(flymeRightEntityList == null){
            flymeRightEntityList = new ArrayList<FlymeRightEntity>();
        }else{
            flymeRightEntityList.clear();
        }

        if(postion == FlymeSettingConstants.THEME_SETTING_POSITION){
            FlymeRightEntity flymeRightEntity0 = new FlymeRightEntity();
            flymeRightEntity0.setTitle("主题");
            flymeRightEntity0.setSummary("");
            flymeRightEntity0.setItemType(FlymeSettingConstants.LIST_ITEM_TYPE);
            flymeRightEntity0.setLeftItemPosition(postion);
            flymeRightEntityList.add(flymeRightEntity0);
            FlymeRightEntity flymeRightEntity1 = new FlymeRightEntity();
            flymeRightEntity1.setTitle("图表");
            flymeRightEntity1.setSummary("");
            flymeRightEntity1.setItemType(FlymeSettingConstants.LIST_ITEM_TYPE);
            flymeRightEntity1.setLeftItemPosition(postion);
            flymeRightEntityList.add(flymeRightEntity1);
        }else if(postion == FlymeSettingConstants.ASSIST_SETTING_POSITION){
            FlymeRightEntity flymeRightEntity0 = new FlymeRightEntity();
            flymeRightEntity0.setTitle("快速建立");
            flymeRightEntity0.setItemType(FlymeSettingConstants.SWITCH_ITEM_TYPE);
            flymeRightEntity0.setLeftItemPosition(postion);
            flymeRightEntity0.setSwitchState(preferenceUtils.getBoolean("pre_setting_cb_quick",false));
            flymeRightEntityList.add(flymeRightEntity0);

            FlymeRightEntity flymeRightEntity1 = new FlymeRightEntity();
            flymeRightEntity1.setTitle("点亮呼吸灯");
            flymeRightEntity1.setItemType(FlymeSettingConstants.SWITCH_ITEM_TYPE);
            flymeRightEntity1.setLeftItemPosition(postion);
            flymeRightEntity1.setSwitchState(preferenceUtils.getBoolean("pre_setting_cb_light",true));
            flymeRightEntityList.add(flymeRightEntity1);

            FlymeRightEntity flymeRightEntity2 = new FlymeRightEntity();
            flymeRightEntity2.setTitle("监听剪切板");
            flymeRightEntity2.setItemType(FlymeSettingConstants.SWITCH_ITEM_TYPE);
            flymeRightEntity2.setLeftItemPosition(postion);
            flymeRightEntity2.setSwitchState(preferenceUtils.getBoolean("pre_setting_cb_listen",true));
            flymeRightEntityList.add(flymeRightEntity2);

            FlymeRightEntity flymeRightEntity3 = new FlymeRightEntity();
            flymeRightEntity3.setTitle("监听应用通知");
            flymeRightEntity3.setItemType(FlymeSettingConstants.SWITCH_ITEM_TYPE);
            flymeRightEntity3.setLeftItemPosition(postion);
            flymeRightEntity3.setSwitchState(preferenceUtils.getBoolean("pre_setting_sw_notify",true));
            flymeRightEntityList.add(flymeRightEntity3);

            FlymeRightEntity flymeRightEntity4 = new FlymeRightEntity();
            flymeRightEntity4.setTitle("自动清理");
            flymeRightEntity4.setItemType(FlymeSettingConstants.SWITCH_ITEM_TYPE);
            flymeRightEntity4.setLeftItemPosition(postion);
            flymeRightEntity4.setSwitchState(preferenceUtils.getBoolean("pre_setting_delete_cache",true));
            flymeRightEntityList.add(flymeRightEntity4);

            FlymeRightEntity flymeRightEntity5 = new FlymeRightEntity();
            flymeRightEntity5.setTitle("初始化程序");
            flymeRightEntity5.setSummary("删除所有提醒数据并初始化程序设置");
            flymeRightEntity5.setItemType(FlymeSettingConstants.TEXT_ITEM_TYPE);
            flymeRightEntity5.setLeftItemPosition(postion);
            flymeRightEntityList.add(flymeRightEntity5);
        }else if(postion == FlymeSettingConstants.ABOUT_SETTING_POSITION){
            FlymeRightEntity flymeRightEntity0 = new FlymeRightEntity();
            flymeRightEntity0.setTitle("作者");
            flymeRightEntity0.setSummary("疾风Hua");
            flymeRightEntity0.setItemType(FlymeSettingConstants.TEXT_ITEM_TYPE);
            flymeRightEntity0.setLeftItemPosition(postion);
            flymeRightEntityList.add(flymeRightEntity0);
            FlymeRightEntity flymeRightEntity1 = new FlymeRightEntity();
            flymeRightEntity1.setTitle("版本号");
            flymeRightEntity1.setSummary("1.0.1");
            flymeRightEntity1.setItemType(FlymeSettingConstants.TEXT_ITEM_TYPE);
            flymeRightEntity1.setLeftItemPosition(postion);
            flymeRightEntityList.add(flymeRightEntity1);
        }

    }

    private void initLeftIconsList() {
        if(drawableList != null){
            drawableList.clear();
        }else {
            drawableList = new ArrayList<SettingLeftEntity>();
        }
        SettingLeftEntity settingLeftEntity;
        Drawable[] leftIcon = {getResources().getDrawable(R.drawable.ic_setting_theme),
                getResources().getDrawable(R.drawable.ic_setting_assist),
                getResources().getDrawable(R.drawable.ic_setting_apps),
                getResources().getDrawable(R.drawable.ic_setting_info)};
        for(int i=0;i<leftIcon.length;i++ ){
            settingLeftEntity = new SettingLeftEntity();
            settingLeftEntity.setDrawable(leftIcon[i]);
            if(i == 0){
                settingLeftEntity.setPressed(true);
            }else{
                settingLeftEntity.setPressed(false);
            }

        }
//        drawableList.add(getResources().getDrawable(R.drawable.ic_setting_theme));
//        drawableList.add(getResources().getDrawable(R.drawable.ic_setting_assist));
////        drawableList.add(getResources().getDrawable(R.drawable.ic_setting_security));
//        drawableList.add(getResources().getDrawable(R.drawable.ic_setting_apps));
////        drawableList.add(getResources().getDrawable(R.drawable.ic_setting_location));
//
//        drawableList.add(getResources().getDrawable(R.drawable.ic_setting_info));

    }

    @Override
    protected void onStart() {
        super.onStart();
        initLeftViewAdapter();
        handler.sendEmptyMessage(0);//初始化右侧设置界面
    }

    private void initLeftViewAdapter() {
        initLeftIconsList();
        if(settingLeftAdapter == null){
            settingLeftAdapter = new SettingLeftAdapter(this,drawableList);
            settingLeftAdapter.setOnClickListener(new SettingLeftAdapter.OnClickListener() {
                @Override
                public void onClick(int position) {
                    if(position == FlymeSettingConstants.APPS_SETTING_POSITION){
                        lvFlymeRight.setVisibility(View.GONE);
                        llFlymePb.setVisibility(View.VISIBLE);
                    }
                    handler.sendEmptyMessage(position);
                }
            });
            lvFlymeLeft.setAdapter(settingLeftAdapter);
            lvFlymeLeft.setOnItemClickListener(settingLeftAdapter);
        }else{
            settingLeftAdapter.notifyDataChange(drawableList);
        }

    }

    /**
     * 获取已安装应用信息
     */
    private List<AppInfoEntity> getAllApps() {
        pm = this.getPackageManager();
        List<ApplicationInfo> listApplicationInfo = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        Collections.sort(listApplicationInfo, new ApplicationInfo.DisplayNameComparator(pm));

        List<AppInfoEntity> appsInfo = new ArrayList<AppInfoEntity>();
        appsInfo.clear();
        String packageName;
        for (ApplicationInfo applicationInfo : listApplicationInfo) {
            packageName = (String) applicationInfo.loadLabel(pm);
            if(!packageName.contains("com.android")){
                appsInfo.add(getAppInfo(applicationInfo));
            }

        }

        return appsInfo;
    }

    /**
     *获取单个应用的信息
     * @param applicationInfo
     * @return
     */
    private AppInfoEntity getAppInfo(ApplicationInfo applicationInfo) {

        AppInfoEntity appInfo = new AppInfoEntity();
        appInfo.setLabel((String) applicationInfo.loadLabel(pm));
        appInfo.setIcon(applicationInfo.loadIcon(pm));
        appInfo.setPackageName(applicationInfo.packageName);

        return appInfo;
    }



    @Override
    protected void onStop() {
        super.onStop();
        drawableList = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}