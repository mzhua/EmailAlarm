package im.mz.EmailAlarm.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;

import im.mz.EmailAlarm.R;

/**
 * Created by mzhua_000 on 2015/1/3.
 */
public class EASwitch extends Switch implements CompoundButton.OnCheckedChangeListener{
    private Context context;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private OnEACheckedChangeListener onEACheckedChangeListener;

    private String key;
    private boolean defaultValue;

    public OnEACheckedChangeListener getOnEACheckedChangeListener() {
        return onEACheckedChangeListener;
    }

    public void setOnEACheckedChangeListener(OnEACheckedChangeListener onEACheckedChangeListener) {
        this.onEACheckedChangeListener = onEACheckedChangeListener;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }


    public interface OnEACheckedChangeListener{
        public void onCheckedChange(boolean isChecked);
    }

    public EASwitch(Context context) {
        super(context);
        init(context);

    }

    public EASwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EAPreferenceView);
        key = typedArray.getString(R.styleable.EAPreferenceView_key);
        defaultValue = typedArray.getBoolean(R.styleable.EAPreferenceView_defaultValue,true);

        init(context);



    }

    public EASwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init( context);
    }

    private void init(Context context){
        this.context = context;
        setOnCheckedChangeListener(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
        setChecked(preferences.getBoolean(getKey(), defaultValue));
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        editor.putBoolean(getKey(),isChecked).commit();
        if(onEACheckedChangeListener != null){
            onEACheckedChangeListener.onCheckedChange(isChecked);
        }

    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
