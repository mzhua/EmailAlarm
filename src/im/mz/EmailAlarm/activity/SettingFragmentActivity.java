package im.mz.EmailAlarm.activity;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.adapter.SettingLeftAdapter;
import im.mz.EmailAlarm.constants.FlymeSettingConstants;
import im.mz.EmailAlarm.entity.SettingLeftEntity;
import im.mz.EmailAlarm.fragment.setting.AboutFragment;
import im.mz.EmailAlarm.fragment.setting.AppsFragment;
import im.mz.EmailAlarm.fragment.setting.AssistFragment;
import im.mz.EmailAlarm.fragment.setting.DataFragment;
import im.mz.EmailAlarm.fragment.setting.ThemeFragment;
import im.mz.EmailAlarm.utils.PreferenceUtils;
/**
 * Created by mzhua_000 on 2014/12/29.
 */
public class SettingFragmentActivity extends BaseFragmentActivity {
    private Context context;
    private int initPosition = 0;

    private LinearLayout llFlyme;
    private ListView lvFlymeLeft;

    private List<SettingLeftEntity> drawableList = new ArrayList<SettingLeftEntity>();

    private SettingLeftAdapter settingLeftAdapter;

    private boolean isExcelImportClick = false;

    //处理右侧设置界面的操作都由handler处理
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(Build.VERSION.SDK_INT < 18){
                if(msg.what == 3){
                    getActionBar().setTitle(getResources().getStringArray(R.array.setting_actionbar_title)[4]);
                }else{
                    getActionBar().setTitle(getResources().getStringArray(R.array.setting_actionbar_title)[msg.what]);
                }
            }else{
                getActionBar().setTitle(getResources().getStringArray(R.array.setting_actionbar_title)[msg.what]);
            }

            Fragment rightFragment = null;//new ThemeFragment();
            if(isExcelImportClick){ //进入批量导入提醒时会将assistfragment加入back stack，所以必须要在点击其他left  icon前将其pop，否则会出现显示bug
                getSupportFragmentManager().popBackStackImmediate();
            }
            switch (msg.what){
                case FlymeSettingConstants.THEME_SETTING_POSITION:
                    if(findViewById(R.id.setting_fragment) != null){
                        rightFragment = new ThemeFragment();
                    }
                    break;
                case FlymeSettingConstants.ASSIST_SETTING_POSITION:
                    if(findViewById(R.id.setting_fragment) != null){
                        rightFragment = new AssistFragment();

                    }
                    break;
                case FlymeSettingConstants.DATA_SETTING_POSITION:
                    if(findViewById(R.id.setting_fragment) != null){
                        DataFragment dataFragment = new DataFragment();
                        rightFragment = dataFragment;
                        dataFragment.setOnItemClickListener(new DataFragment.OnItemClickListener() {
                            @Override
                            public void onClick(int id, int position) {
                                if(id == R.id.rl_fragment_setting_import_excel){
                                    isExcelImportClick = true;
                                }

                            }

                            @Override
                            public void onCheckChange(int id, int position) {

                            }
                        });
                    }
                    break;
                case FlymeSettingConstants.APPS_SETTING_POSITION:
                    if(findViewById(R.id.setting_fragment) != null){
                        if(Build.VERSION.SDK_INT >= 18){
                            rightFragment = new AboutFragment();
                        }else{
                            rightFragment = new AppsFragment();
                        }

                    }
                    break;
                case FlymeSettingConstants.ABOUT_SETTING_POSITION:
                    if(findViewById(R.id.setting_fragment) != null){
                        if(Build.VERSION.SDK_INT >= 18){
                            rightFragment = new AppsFragment();
                        }else{
                            rightFragment = new AboutFragment();

                        }

                    }
                    break;

            }
            isExcelImportClick = false;
            if(getSupportFragmentManager().findFragmentById(R.id.setting_fragment) != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.setting_fragment, rightFragment).commit();
            }else{
                getSupportFragmentManager().beginTransaction().add(R.id.setting_fragment, rightFragment).commit();
            }
            return false;
        }
    }) ;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_setting);
        this.context = this;
        Bundle data = getIntent().getExtras();
        if(data != null){
            initPosition = data.getInt("initPosition",0);
        }

        initViews();

        initLeftViewAdapter();
        handler.sendEmptyMessage(initPosition);//初始化右侧设置界面

    }

    private void initViews() {
        llFlyme = (LinearLayout) findViewById(R.id.ll_fragment_flyme);
        lvFlymeLeft = (ListView) findViewById(R.id.lv_fragment_flyme_left);

    }



    @Override
    protected void onStart() {
        super.onStart();

    }

    /**
     * 初始化左侧adapter
     */
    private void initLeftViewAdapter() {
        initLeftIconsList(initPosition);
        if(settingLeftAdapter == null){
            settingLeftAdapter = new SettingLeftAdapter(this,drawableList);
            settingLeftAdapter.setOnClickListener(new SettingLeftAdapter.OnClickListener() {
                @Override
                public void onClick(int position) {
                    initLeftIconsList(position);   //修改选中的图标
                    settingLeftAdapter.notifyDataChange(drawableList); //更新按下效果
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
     * 初始化设置左侧的图标，并设置选中的位置,有按下效果
     * @param pressedPosition
     */
    private void initLeftIconsList(int pressedPosition) {
        if(drawableList != null){
            drawableList.clear();
        }else {
            drawableList = new ArrayList<SettingLeftEntity>();
        }

        Drawable[] leftIcon = {getResources().getDrawable(R.drawable.ic_setting_theme),
                getResources().getDrawable(R.drawable.ic_setting_assist),
                getResources().getDrawable(R.drawable.ic_setting_data),
                getResources().getDrawable(R.drawable.ic_setting_apps),
                getResources().getDrawable(R.drawable.ic_setting_info)};
        if(Build.VERSION.SDK_INT < 18){
            leftIcon = new Drawable[]{getResources().getDrawable(R.drawable.ic_setting_theme),
                    getResources().getDrawable(R.drawable.ic_setting_assist),
                    getResources().getDrawable(R.drawable.ic_setting_data),
                    getResources().getDrawable(R.drawable.ic_setting_info)};
        }
        for(int i=0;i<leftIcon.length;i++ ){
            SettingLeftEntity settingLeftEntity = new SettingLeftEntity();
            settingLeftEntity.setDrawable(leftIcon[i]);
            if(i == pressedPosition){
                settingLeftEntity.setPressed(true);
            }else{
                settingLeftEntity.setPressed(false);
            }
            drawableList.add(settingLeftEntity);
        }

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