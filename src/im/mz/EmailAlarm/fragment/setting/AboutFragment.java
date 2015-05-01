package im.mz.EmailAlarm.fragment.setting;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.utils.PreferenceUtils;

/**
 * Created by mzhua_000 on 2015/1/5.
 */
public class AboutFragment extends Fragment implements View.OnClickListener {
    private PreferenceUtils preferenceUtils;
    private RelativeLayout rlFeedBack;
    private RelativeLayout rlGuide;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferenceUtils = new PreferenceUtils(getActivity());

        View view  = inflater.inflate(R.layout.fragment_setting_about,container,false);
        rlFeedBack = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_feedback);
        rlGuide = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_guide);
        rlFeedBack.setOnClickListener(this);
        rlGuide.setOnClickListener(this);
        return view;
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

        //个推cid


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
    public void onClick(View v) {
        if(v.getId() == R.id.rl_fragment_setting_feedback){
            feedBack();
        }else if(v.getId() == R.id.rl_fragment_setting_guide){
            preferenceUtils.getEditor().putBoolean(PrefenceKeyConstants.FLAG_FIRST_START, true).commit();
            getActivity().finish();
        }
    }
}
