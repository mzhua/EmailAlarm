package im.mz.EmailAlarm.utils;

import android.app.ActionBar;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by HUA on 2014/11/19.
 */
public class FlymeUtils {
    /**
     * smartbar设置返回图标
     * @param actionbar
     * @param backIcon
     */
    public static void setBackIcon(ActionBar actionbar, Drawable
            backIcon) {

        if(FlymeUtils.hasSmartBar()){
            try {
                Method method =
                        Class.forName("android.app.ActionBar").getMethod(
                                "setBackButtonDrawable", new Class[]
                                        { Drawable.class });
                try {
                    method.invoke(actionbar, backIcon);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 反射，判断是否有smartbar,间接用于判断是否是魅族的系统
     * @return
     */
    public static boolean hasSmartBar() {
        try {
// 新型号可用反射调用Build.hasSmartBar()
            Method method =
                    Class.forName("android.os.Build").getMethod("hasSmartBar");
            return ((Boolean) method.invoke(null)).booleanValue();
        } catch (Exception e) {
        }
        // 反射不到Build.hasSmartBar()，则用Build.DEVICE判断
        if (Build.DEVICE.equals("mx2") || Build.DEVICE.equals("mx3") || Build.DEVICE.equals("mx4")) {
            return true;
        } else if (Build.DEVICE.equals("mx") ||
                Build.DEVICE.equals("m9")) {
            return false;
        }
        return false;
    }
}
