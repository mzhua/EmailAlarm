package im.mz.EmailAlarm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;

/**
 * Created by mzhua_000 on 2014/12/18.
 */
public class PreferenceUtils {
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public PreferenceUtils(Context context){
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }

    /**
     * 获取当前设置的主题
     * @return
     */
    public int getCurrentTheme(){
        int theme = context.getResources().getColor(R.color.theme_0);
        String themeKey = preferences.getString(PrefenceKeyConstants.SETTING_THEME,context.getResources().getString(R.string.theme_default));
        if(themeKey.equals(context.getResources().getString(R.string.theme_blue))){
            theme = context.getResources().getColor(R.color.theme_0);
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_green))){
            theme = context.getResources().getColor(R.color.theme_1);
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_yellow))){
            theme = context.getResources().getColor(R.color.theme_2);
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_red))){
            theme = context.getResources().getColor(R.color.theme_3);
        }

  /*      switch (){
            case "魅族蓝":
                theme = context.getResources().getColor(R.color.theme_0);
                break;
            case "青草绿":
                theme = context.getResources().getColor(R.color.theme_1);
                break;
            case "大麦黄":
                theme = context.getResources().getColor(R.color.theme_2);
                break;
            case "闷骚红":
                theme = context.getResources().getColor(R.color.theme_3);
                break;
        }*/
        return theme;
    }

    public Drawable getCurrentThemeDrawable(){
        Drawable drawable = context.getResources().getDrawable(R.drawable.theme_1);

        String themeKey = preferences.getString(PrefenceKeyConstants.SETTING_THEME,context.getResources().getString(R.string.theme_default));
        if(themeKey.equals(context.getResources().getString(R.string.theme_blue))){
            drawable = context.getResources().getDrawable(R.drawable.theme_0);
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_green))){
            drawable = context.getResources().getDrawable(R.drawable.theme_1);
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_yellow))){
            drawable = context.getResources().getDrawable(R.drawable.theme_2);
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_red))){
            drawable = context.getResources().getDrawable(R.drawable.theme_3);
        }

      /*  switch (Integer.parseInt(preferences.getString(PrefenceKeyConstants.SETTING_THEME,"1"))){
            case 0:
                drawable = context.getResources().getDrawable(R.drawable.theme_0);
                break;
            case 1:
                drawable = context.getResources().getDrawable(R.drawable.theme_1);
                break;
            case 2:
                drawable = context.getResources().getDrawable(R.drawable.theme_2);
                break;
            case 3:
                drawable = context.getResources().getDrawable(R.drawable.theme_3);
                break;
        }*/
        return drawable;
    }

    public int getCurrentThemeSelector(){
        int selector = R.drawable.selector_theme_1;

        String themeKey = preferences.getString(PrefenceKeyConstants.SETTING_THEME,context.getResources().getString(R.string.theme_default));
        if(themeKey.equals(context.getResources().getString(R.string.theme_blue))){
            selector = R.drawable.selector_theme_0;
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_green))){
            selector = R.drawable.selector_theme_1;
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_yellow))){
            selector = R.drawable.selector_theme_2;
        }else if(themeKey.equals(context.getResources().getString(R.string.theme_red))){
            selector = R.drawable.selector_theme_3;
        }

       /* switch (Integer.parseInt(preferences.getString(PrefenceKeyConstants.SETTING_THEME,"1"))){
            case 0:
                selector = R.drawable.selector_theme_0;
                break;
            case 1:
                selector = R.drawable.selector_theme_1;
                break;
            case 2:
                selector = R.drawable.selector_theme_2;
                break;
            case 3:
                selector = R.drawable.selector_theme_3;
                break;
        }*/
        return selector;
    }

    public String getString(String key,String defaultValue){
        return preferences.getString(key,defaultValue);
    }

    public int getInt(String key, int i) {
        return preferences.getInt(key,i);
    }

    public boolean getBoolean(String key,boolean defaultValue){
        return preferences.getBoolean(key,defaultValue);
    }

    public SharedPreferences.Editor getEditor() {
        return editor;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }
}
