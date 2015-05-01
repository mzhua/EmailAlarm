package im.mz.EmailAlarm.entity;

import android.content.Intent;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * entity for the flymeRightAdapter
 * Created by mzhua_000 on 2015/1/2.
 */
public class FlymeRightEntity {
    private String title;
    private String summary;
    private List<String> arrayName;
    private List<Integer> arrayValue;
    private int itemType;               //显示的item的类型   0：TextView   1：Switch
    private int leftItemPosition;       //与左侧对应的功能的位置
    private boolean switchState;

    /**
     * 以下四个为flymeseting中的app选择
     */
    private String label;
    private Drawable icon;
    private Intent intent;
    private String packageName;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getArrayName() {
        return arrayName;
    }

    public void setArrayName(List<String> arrayName) {
        this.arrayName = arrayName;
    }

    public List<Integer> getArrayValue() {
        return arrayValue;
    }

    public void setArrayValue(List<Integer> arrayValue) {
        this.arrayValue = arrayValue;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean isSwitchState() {
        return switchState;
    }

    public void setSwitchState(boolean switchState) {
        this.switchState = switchState;
    }

    public int getLeftItemPosition() {
        return leftItemPosition;
    }

    public void setLeftItemPosition(int leftItemPosition) {
        this.leftItemPosition = leftItemPosition;
    }
}
