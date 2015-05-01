package im.mz.EmailAlarm.fragment.setting;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.utils.FlymeUtils;
import im.mz.EmailAlarm.utils.PreferenceUtils;

/**
 * Created by mzhua_000 on 2015/1/5.
 */
public class ThemeFragment extends Fragment {
    private Context context;
    private PreferenceUtils preferenceUtils;
    private SharedPreferences.Editor editor;

    private RelativeLayout rlTheme;
    private RelativeLayout rlChart;

    private TextView tvThemeSummary;
    private TextView tvChartSummary;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();
        preferenceUtils = new PreferenceUtils(context);
        editor = preferenceUtils.getEditor();

        View view = inflater.inflate(R.layout.fragment_setting_theme,container,false);

        initViews(view);
        return view;
    }

    private void initViews(View view) {
        rlTheme = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_theme);
        rlChart = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_chart);

        tvThemeSummary = (TextView) view.findViewById(R.id.tv_fragment_setting_theme_summary);
        tvChartSummary = (TextView) view.findViewById(R.id.tv_fragment_setting_chart_summary);

        tvThemeSummary.setText(preferenceUtils.getString(PrefenceKeyConstants.SETTING_THEME,getString(R.string.theme_default)));
        tvThemeSummary.setTextColor(preferenceUtils.getCurrentTheme());
        tvChartSummary.setText(preferenceUtils.getString(PrefenceKeyConstants.SETTING_CHART,getString(R.string.chart_default)));

        rlTheme.setOnClickListener(new ThemeOnClickListener());
        rlChart.setOnClickListener(new ThemeOnClickListener());


    }

    class ThemeOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rl_fragment_setting_theme:
                    PopupMenu popupTheme = new PopupMenu(context, rlTheme);
                    popupTheme.getMenuInflater().inflate(R.menu.popup_theme, popupTheme.getMenu());

                    popupTheme.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            editor.putString(PrefenceKeyConstants.SETTING_THEME,item.getTitle().toString()).commit();
                            tvThemeSummary.setText(item.getTitle());
                            tvThemeSummary.setTextColor(preferenceUtils.getCurrentTheme());
//                            if(FlymeUtils.hasSmartBar()){
                                getActivity().getActionBar().setBackgroundDrawable(preferenceUtils.getCurrentThemeDrawable());
//                            }
                            SystemBarTintManager tintManager = new SystemBarTintManager(getActivity());

                            if(FlymeUtils.hasSmartBar()){
                                tintManager.setStatusBarAlpha(255);
                            }else{
                                tintManager.setStatusBarTintEnabled(true);
                                tintManager.setStatusBarTintColor(preferenceUtils.getCurrentTheme());
                            }
                            return true;
                        }
                    });

                    popupTheme.show();
                    break;
                case R.id.rl_fragment_setting_chart:
                    PopupMenu popupChart = new PopupMenu(context, rlChart);
                    popupChart.getMenuInflater().inflate(R.menu.popup_chart, popupChart.getMenu());

                    popupChart.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            editor.putString(PrefenceKeyConstants.SETTING_CHART,item.getTitle().toString()).commit();
                            tvChartSummary.setText(item.getTitle());
                            return true;
                        }
                    });
                    popupChart.show();
                    break;
            }
        }
    }
}
