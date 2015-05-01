package im.mz.EmailAlarm.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by mzhua_000 on 2015/1/3.
 */
public class FlymeSwitch extends Switch implements CompoundButton.OnCheckedChangeListener{
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public FlymeSwitch(Context context) {
        super(context);
        init(context);
    }

    public FlymeSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FlymeSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    private void init(Context context){
        this.context = context;
        this.setOnCheckedChangeListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int position = Integer.parseInt(buttonView.getTag().toString());
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
