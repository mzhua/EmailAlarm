package im.mz.EmailAlarm.fragment.setting;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.utils.NotifyUtil;

/**
 * Created by mzhua_000 on 2015/1/5.
 */
public class AssistFragment extends Fragment {
    private Context context;
    private RelativeLayout rlAuth;


    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        public void onClick(int id,int position);
        public void onCheckChange(int id,int position);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();

        View view = inflater.inflate(R.layout.fragment_setting_assist,container,false);
        initViews(view);


        return view;
    }

    private void initViews(View view){
        rlAuth = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_auth);

        initNotifyAccessUI();

    }

    /**
     * 授权读取通知显示与否的初始化
     */
    private void initNotifyAccessUI() {
        if(NotifyUtil.isNotifyAccessEnabled(context)){
            rlAuth.setVisibility(View.GONE);
        }else if(Build.VERSION.SDK_INT >= 18){
            rlAuth.setVisibility(View.VISIBLE);
            rlAuth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NotifyUtil.reAuthorize(context);
                }
            });
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initNotifyAccessUI();
    }
}
