package im.mz.EmailAlarm.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

/**
 * Created by mzhua_000 on 2014/12/29.
 */
public class AppInfoEntity {
    private String label;
    private Drawable icon;
    private Intent intent;
    private String packageName;
    private boolean btnChecked;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isBtnChecked() {
        return btnChecked;
    }

    public void setBtnChecked(boolean btnChecked) {
        this.btnChecked = btnChecked;
    }

}
