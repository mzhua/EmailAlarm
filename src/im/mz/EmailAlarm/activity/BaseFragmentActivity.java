package im.mz.EmailAlarm.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.utils.FlymeUtils;
import im.mz.EmailAlarm.utils.PreferenceUtils;

/**
 * Created by mzhua_000 on 2014-12-14.
 */
public class BaseFragmentActivity extends FragmentActivity {
    private PreferenceUtils preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = new PreferenceUtils(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    protected void onResume() {

        //flyme smartbat返回图标设置
        FlymeUtils.setBackIcon(getActionBar(), getResources().getDrawable(R.drawable.mz_ic_sb_back));

        getActionBar().setBackgroundDrawable(preferences.getCurrentThemeDrawable());

        //沉浸式顶栏
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);

            if(FlymeUtils.hasSmartBar()){
                tintManager.setStatusBarAlpha(255);
            }else{
                tintManager.setStatusBarTintEnabled(true);
                tintManager.setStatusBarTintColor(preferences.getCurrentTheme());
            }

            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
//            int actionbarHeight = (int) getResources().getDimension(R.dimen.abc_action_bar_default_height);
            View contentView = findViewById(android.R.id.content);
            contentView.setPadding(0, config.getPixelInsetTop(true), config.getPixelInsetRight(), config.getPixelInsetBottom() );

        }
        super.onResume();
    }
}