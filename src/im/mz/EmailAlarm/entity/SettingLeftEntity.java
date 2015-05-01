package im.mz.EmailAlarm.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by mzhua_000 on 2015/1/8.
 */
public class SettingLeftEntity {
    private Drawable drawable;
    private boolean isPressed;

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public void setPressed(boolean isPressed) {
        this.isPressed = isPressed;
    }
}
