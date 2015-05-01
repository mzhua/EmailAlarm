package im.mz.EmailAlarm.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.utils.FlymeUtils;
import im.mz.EmailAlarm.utils.PreferenceUtils;

/**
 * Created by Hua on 2014/10/19.
 */
public class SettingActivity extends PreferenceActivity {
    Context context;
    private PreferenceUtils preferences;

    private ListPreference theme;
    private Preference feedBack;
    private Preference clearAllData;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new PreferenceUtils(this);
        //flyme smartbat返回图标设置
        FlymeUtils.setBackIcon(getActionBar(),getResources().getDrawable(R.drawable.mz_ic_sb_back));

        context = this;
        addPreferencesFromResource(R.xml.preference_setting);
        getViews();

        getActionBar().setBackgroundDrawable(preferences.getCurrentThemeDrawable());

        getActionBar().setDisplayHomeAsUpEnabled(true);
        //沉浸式顶栏
        if (Build.VERSION.SDK_INT >= 19)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);

            tintManager.setStatusBarTintColor(preferences.getCurrentTheme());

            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
//            int actionbarHeight = (int) getResources().getDimension(R.dimen.abc_action_bar_default_height);
            getListView().setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom());

        }

    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void getViews(){
        theme = (ListPreference)findPreference("theme");
        feedBack = findPreference("pref_setting_feedback");
        clearAllData = findPreference("pref_clear");

       /* theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Toast.makeText(context,"返回主界面后生效",Toast.LENGTH_SHORT).show();

                return true;
            }
        });*/
        feedBack.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                feedBack();
                return true;
            }
        });

        clearAllData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog ad ;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(getResources().getString(R.string.app_name));
                builder.setMessage("确定清除所有提醒数据？").setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlarmDbUtils.delData(context, null, null, true);
//                        preferences.getEditor().clear().commit();
                    }
                }).setNegativeButton("取消",null);
                ad = builder.create();
                ad.show();


                return true;
            }
        });
    }

    public void feedBack() {

        String info = "";
        info += "Build.DEVICE=" + Build.DEVICE + "\n";
        info += "Build.BRAND="+Build.BRAND + "\n";
        info += "Build.DISPLAY="+Build.DISPLAY + "\n";
        info += "Build.ID="+Build.ID + "\n";
        info += "Build.PRODUCT="+Build.PRODUCT + "\n";
        info += "Build.HARDWARE="+Build.HARDWARE + "\n";
        info += "Build.VERSION.RELEASE="+Build.VERSION.RELEASE + "\n";
        info += "Build.VERSION.SDK_INT="+Build.VERSION.SDK_INT + "\n";

        String[] reciver = new String[]{"mzhua78@hotmail.com"};
        String sub = getResources().getString(R.string.app_name) + "(" + getResources().getString(R.string.app_version) + ")反馈";

        String myCc = "";
        String mybody = "\n\n详细信息：\n"+info;
        Intent myIntent = new Intent(Intent.ACTION_SEND);

        myIntent.setType("plain/text");
        myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
        myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
        myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, sub);
        myIntent.putExtra(android.content.Intent.EXTRA_TEXT, mybody);
//        startActivity(myIntent);
        startActivity(Intent.createChooser(myIntent,"请选择邮件来反馈"));
    }

    @Override
    protected void onStop() {
        super.onStop();
//        GeTuiUtils.initGPush(context, preferences.getPreferences());
    }

}