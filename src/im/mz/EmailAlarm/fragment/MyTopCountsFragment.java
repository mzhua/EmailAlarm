package im.mz.EmailAlarm.fragment;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.interfaces.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.*;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.utils.PreferenceUtils;

import java.util.ArrayList;

/**
 * Created by Hua on 2014/10/20.
 */
public class MyTopCountsFragment extends Fragment implements OnChartValueSelectedListener{
    private PreferenceUtils preferences;

    private View mView;
    private int screenWidth;
    private BarChart mBarChart;
    private PieChart mPieChart;
    private LineChart mLineChart;
    
    private String[] mWeekDays = {"一","二","三","四","五","六","日"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        preferences = new PreferenceUtils(getActivity());

        Display display = getActivity().getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;

//        Toast.makeText(getActivity(),"screenWidth="+screenWidth,Toast.LENGTH_SHORT).show();
        mView = inflater.inflate(R.layout.activity_main_top_counts,container,false);
        mBarChart = (BarChart) mView.findViewById(R.id.chart_bar);
        mPieChart = (PieChart) mView.findViewById(R.id.chart_pie);
        mLineChart = (LineChart) mView.findViewById(R.id.chart_line);

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
         mView.setBackgroundColor(preferences.getCurrentTheme());
        String chart = preferences.getString(PrefenceKeyConstants.SETTING_CHART,getString(R.string.chart_default));
        if(chart.equals(getString(R.string.chart_pie))){
            mBarChart.setVisibility(View.GONE);
            mPieChart.setVisibility(View.VISIBLE);
            initPieChart();
        }else if(chart.equals(getString(R.string.chart_column))){
            mBarChart.setVisibility(View.VISIBLE);
            mPieChart.setVisibility(View.GONE);
            initBarChart();
        }


    }

    /**
     * 初始化柱图
     */
    private void initBarChart(){


        // enable the drawing of values
        mBarChart.setDrawYValues(false);

        mBarChart.setDrawValueAboveBar(false);

        mBarChart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mBarChart.setMaxVisibleValueCount(60);

        // disable 3D
        mBarChart.set3DEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        mBarChart.setPinchZoom(false);

        // draw shadows for each bar that show the maximum value
        // mBarChart.setDrawBarShadow(true);

        mBarChart.setUnit("");

        // mBarChart.setDrawXLabels(false);

        mBarChart.setDrawHorizontalGrid(true);
        mBarChart.setDrawVerticalGrid(false);

        mBarChart.setDrawGridBackground(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setScaleEnabled(false);
        // mBarChart.setDrawYLabels(false);

        // sets the text size of the values inside the chart
        mBarChart.setValueTextSize(10f);
        mBarChart.setDrawBorder(false);
        mBarChart.setDrawLegend(false);
        mBarChart.setDrawYLabels(true);
        mBarChart.setDrawBarShadow(false);
        mBarChart.setNoDataText("");
        mBarChart.setNoDataTextDescription(getResources().getString(R.string.noData));//暂无数据
//        mBarChart.setValueTextColor(getResources().getColor(R.color.white));
        mBarChart.setGridColor(getResources().getColor(R.color.white));
        mBarChart.setDrawingCacheBackgroundColor(getResources().getColor(R.color.white));
        //格式化成整数显示
        mBarChart.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((Math.round(value)+""));
            }
        });
        setBarData();

        XLabels xl = mBarChart.getXLabels();
        xl.setPosition(XLabels.XLabelPosition.BOTTOM);
        xl.setCenterXLabelText(true);
        xl.setTextColor(getResources().getColor(R.color.white));

        YLabels yl = mBarChart.getYLabels();
        yl.mDecimals = 0;
        yl.setLabelCount(7);
        yl.setTextColor(getResources().getColor(R.color.white));

        yl.setPosition(YLabels.YLabelPosition.LEFT);

        mBarChart.animateXY(1000, 2000);

    }

    /**
     * 设置柱图数据
     */
    private void setBarData() {
        int count = 7;
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(mWeekDays[i % 7]);
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        for (int i = 0; i < count; i++) {
            yVals1.add(new BarEntry(preferences.getInt(String.valueOf(i),0), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, getResources().getString(R.string.week));
        set1.setBarSpacePercent(25f);
        set1.setColor(getResources().getColor(R.color.white));
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);

        mBarChart.setData(data);
    }

    /**
     * 初始化饼图
     */
    private void initPieChart(){
        // change the color of the center-hole
        mPieChart.setHoleColor(Color.rgb(235, 235, 235));

/*
        Typeface tf = Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf");

        mPieChart.setValueTypeface(tf);
        mPieChart.setCenterTextTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Light.ttf"));
*/

        mPieChart.setNoDataTextDescription(getResources().getString(R.string.noData));
        mPieChart.setHoleRadius(screenWidth/15);
        mPieChart.setTransparentCircleRadius(screenWidth/15);

        mPieChart.setDescription("");

        mPieChart.setDrawYValues(true);
        mPieChart.setDrawCenterText(true);
        mPieChart.setDrawHoleEnabled(true);

        mPieChart.setRotationAngle(0);

        // draws the corresponding description value into the slice
        mPieChart.setDrawXValues(true);

        // enable rotation of the chart by touch
        mPieChart.setRotationEnabled(false);
        // display percentage values
        mPieChart.setUsePercentValues(false);
        // mPieChart.setUnit(" €");
        // mPieChart.setDrawUnitsInChart(true);
        // add a selection listener
        //mPieChart.setOnChartValueSelectedListener(this);
         mPieChart.setTouchEnabled(false);
        mPieChart.setCenterTextSize(screenWidth / 35);
        mPieChart.setValueTextColor(Color.BLACK);
        mPieChart.setValueTextSize(screenWidth / 65);
        mPieChart.setDrawLegend(false);

        //格式化成整数显示
        mPieChart.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((Math.round(value)+""));
            }
        });

        setPieData();

        mPieChart.animateXY(1500, 1500);
        // mPieChart.spin(2000, 0, 360);

       /* Legend l = mPieChart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);

        l.setXEntrySpace(7f);
        l.setYEntrySpace(2f);*/
    }

    /**
     * 设置饼图数据
     */
    private void setPieData() {
        int count = 7;

        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        // IMPORTANT: In a PieChart, no values (Entry) should have the same
        // xIndex (even if from different DataSets), since no values can be
        // drawn above each other.
        int total = 0;
        for (int i = 0; i < count ; i++) {
            int alarm_count = preferences.getInt(String.valueOf(i),0);
            if(alarm_count > 0){
                yVals1.add(new Entry(alarm_count, i));
                xVals.add(mWeekDays[i]);
            }
            total += alarm_count;
        }
        mPieChart.setCenterText(String.valueOf(total));
        PieDataSet set1 = new PieDataSet(yVals1, "Election Results");
        set1.setSliceSpace(1f);


        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        set1.setColors(colors);

        PieData data = new PieData(xVals, set1);

        mPieChart.setData(data);

        // undo all highlights
        mPieChart.highlightValues(null);

        mPieChart.invalidate();
    }


    public void clear(){
        mBarChart.clear();
    }
    public void refresh(){
        String chart = preferences.getString(PrefenceKeyConstants.SETTING_CHART,getString(R.string.chart_default));
        if(chart.equals(getString(R.string.chart_pie))){
            setPieData();
            mPieChart.animateXY(1500, 1500);
        }else if(chart.equals(getString(R.string.chart_column))){
            setBarData();
            mBarChart.animateXY(1000, 2000);
        }

    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex) {
        
    }

    @Override
    public void onNothingSelected() {

    }
}