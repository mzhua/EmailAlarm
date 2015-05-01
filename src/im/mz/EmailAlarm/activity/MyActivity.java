package im.mz.EmailAlarm.activity;

import android.app.AlertDialog;
import android.content.*;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.*;
import android.widget.*;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.adapter.MyAdapter;
import im.mz.EmailAlarm.constants.FlymeSettingConstants;
import im.mz.EmailAlarm.constants.PrefenceKeyConstants;
import im.mz.EmailAlarm.db.AlarmDbUtils;
import im.mz.EmailAlarm.db.EADbHelper;
import im.mz.EmailAlarm.entity.AlarmListEntity;
import im.mz.EmailAlarm.fragment.MyTopCountsFragment;
import im.mz.EmailAlarm.fragment.MyTopNextFragment;
import im.mz.EmailAlarm.service.AlarmService;
import im.mz.EmailAlarm.db.EAContract.MyEntry;
import im.mz.EmailAlarm.utils.NotifyUtil;
import im.mz.EmailAlarm.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class MyActivity extends BaseFragmentActivity {
    private String[] joks = {"没有了哦", "你也太无聊了吧", "很累的", "(ˉ▽ˉ；)...", "没有就是没有了，有也没有了！"};
    private long lastToastTime = Calendar.getInstance().getTimeInMillis();
    private int pageIndex = 0;  //列表分页号

    private Context context;
    private ArrayList<AlarmListEntity> myListDataArr;

    private ViewPager mViewPager;
    private FragmentPagerAdapter mPagerAdapter;
    private List<Fragment> mDatas;

    private LinearLayout my_pb;

    private PullToRefreshListView myListView;
    private TextView main_bottom_tv_no_date;
    private ImageView main_iv_indicator_0;
    private ImageView main_iv_indicator_1;

    private LinearLayout ll_my;
    private EADbHelper EADbHelper;

    private PreferenceUtils preferences;

    private IntentFilter listDataChanged;
    private ListDateChangedListener listDateChangedListener;

    private int statusFlag = 1; //标记当前是正在查看历史提醒还是当前提醒   1:默认进入为查看当前提醒
    private boolean shouldClearListDataFirst = false;
    private String noDataDesc;//在历史提醒和当前提醒无数据时，设置的提示信息

    private boolean shouldScrollToTop = false;
    private int oldSize;

    //使用handler时首先要创建一个handler
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showListView();
                    break;

            }
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
//        this.setTheme(R.style.Theme_DeviceDefault_Light_Color_LimeGreen);

        setContentView(R.layout.activity_main);
        preferences = new PreferenceUtils(this);
        noDataDesc = getResources().getString(R.string.noDataDesc);
        EADbHelper = new EADbHelper(context);

        initViews();
        setViewPagerAdapter();


        if (Build.VERSION.SDK_INT >= 18 && !preferences.getBoolean(PrefenceKeyConstants.FLAG_FIRST_START, true)) {
            if (!NotifyUtil.isNotifyAccessEnabled(context)) {
                Toast.makeText(context, getResources().getString(R.string.toast_no_auth), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MyActivity.this, SettingFragmentActivity.class);
                Bundle data = new Bundle();
                data.putInt("initPosition", FlymeSettingConstants.ASSIST_SETTING_POSITION);
                intent.putExtras(data);
                startActivity(intent);

            }
        }
        registerMyReceiver();

//        upgradeInfoDialog();
    }

    private void upgradeInfoDialog() {
        if (preferences.getBoolean(getResources().getString(R.string.app_version), true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LinearLayout upgradeInfoDialog = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.dialog_upgrade_info, null);
            builder.setTitle("升级说明");
            builder.setView(upgradeInfoDialog);
            builder.create().show();
            preferences.getEditor().putBoolean(getResources().getString(R.string.app_version), false).commit();
        }
    }

    /**
     * 设置BroadcastReceiver，接收界面改变的消息
     */
    private void registerMyReceiver() {

        listDataChanged = new IntentFilter();
        listDataChanged.addAction("im.mz.EmailAlarm.RemindService");
        listDateChangedListener = new ListDateChangedListener();
        registerReceiver(listDateChangedListener, listDataChanged);
    }


    /**
     * 获取所有组件
     */
    private void initViews() {
        /*Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        screenWidth = outMetrics.widthPixels;*/
        ll_my = (LinearLayout) findViewById(R.id.ll_my);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_my);
        main_bottom_tv_no_date = (TextView) findViewById(R.id.main_bottom_tv_no_date);
        myListView = (PullToRefreshListView) findViewById(R.id.listview_my);
        main_iv_indicator_0 = (ImageView) findViewById(R.id.main_iv_indicator_0);
        main_iv_indicator_1 = (ImageView) findViewById(R.id.main_iv_indicator_1);
        my_pb = (LinearLayout) findViewById(R.id.my_pb);

        myListView.setPullToRefreshEnabled(false);
        myListView.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {

                if (0 != preferences.getInt(PrefenceKeyConstants.SETTING_AUTO_LOAD, PrefenceKeyConstants.SETTING_AUTO_LOAD_DEFAULT_COUNT)) {
                    oldSize = myListDataArr.size();
                    pageIndex++;
//                refreshUI(1);
                    ArrayList<AlarmListEntity> listEntities = getAlarmListData(statusFlag);
                    if (listEntities != null) {
                        if (listEntities.size() > 0) {
                            for (AlarmListEntity listEntity : listEntities) {
                                myListDataArr.add(listEntity);
                            }
                            showListView();
                            myListView.scrollToPosition(oldSize + 1);
                            Toast.makeText(context, "加载了" + listEntities.size() + "条提醒", Toast.LENGTH_SHORT).show();
                        } else {
                            jokToast();
                        }

                    }
                } else {
                    jokToast();
                }


            }
        });
    }

    /**
     * 娱乐Toast
     */
    private void jokToast() {
        long toastIntervel = Calendar.getInstance().getTimeInMillis() - lastToastTime;
        lastToastTime = Calendar.getInstance().getTimeInMillis();
        if (toastIntervel / 1000 < 3) {
            Random random = new Random(lastToastTime);
            Toast.makeText(context, joks[(random.nextInt(joks.length))], Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "没有更多提醒了", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ViewPager初始化设置
     */
    private void setViewPagerAdapter() {
        if (mPagerAdapter == null) {
            MyTopNextFragment myTopNextFragment = new MyTopNextFragment();
            MyTopCountsFragment myTopCountsFragment = new MyTopCountsFragment();
            mDatas = new ArrayList<Fragment>();
            mDatas.add(myTopNextFragment);
            mDatas.add(myTopCountsFragment);

            //传递参数给fragment的方法
//            MyListEntity entity = DbUtils.getRecentAlarm(context);
//            Bundle next = new Bundle();
//            next.putString("detail", entity == null ? "最近无提醒" : entity.getDetail());
//            myTopNextFragment.setArguments(next);

            mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
                @Override
                public Fragment getItem(int i) {
                    return mDatas.get(i);
                }

                @Override
                public int getCount() {
                    return mDatas.size();
                }
            };
            mViewPager.setAdapter(mPagerAdapter);
            //自定义切换动画
//            mViewPager.setPageTransformer(true,new DepthPageTransformer());

            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset,
                                           int positionOffsetPx) {
                }

                @Override
                public void onPageSelected(int i) {
                    MyTopCountsFragment topCountsFragment = (MyTopCountsFragment) mDatas.get(1);
                    switch (i) {
                        case 0:
                            main_iv_indicator_0.setAlpha(1.0f);
                            main_iv_indicator_1.setAlpha(0.5f);

                            break;
                        case 1:
                            main_iv_indicator_0.setAlpha(0.5f);
                            main_iv_indicator_1.setAlpha(1.0f);
                            topCountsFragment.refresh();
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int i) {

                }
            });
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        initAutoStart();
    }


    /**
     * 开机自启动
     */
    private void initAutoStart() {
        final ComponentName cn = new ComponentName("im.mz.EmailAlarm", "im.mz.EmailAlarm.receiver.AutoStartReceiver");
        final PackageManager pm = MyActivity.this.getPackageManager();
        final int state = pm.getComponentEnabledSetting(cn);
        int newstate;
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            newstate = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            pm.setComponentEnabledSetting(cn, newstate, PackageManager.DONT_KILL_APP);
        }

    }


    @Override
    protected void onResume() {
        pageIndex = 0;
        statusFlag = 1;
        shouldScrollToTop = false;
        noDataDesc = getResources().getString(R.string.noDataDesc);
        super.onResume();
        invalidateOptionsMenu();//更新menu的title

        refreshUI();

        //service中检查是否需要设置提醒
        Intent intent1;
        intent1 = new Intent(MyActivity.this, AlarmService.class);
        context.startService(intent1);

    }

    /**
     * 开启线程更新状态，并获取最新数据，然后刷新列表
     */
    private void refreshUI() {
        my_pb.setVisibility(View.VISIBLE);
        myListView.setVisibility(View.GONE);

        if (adapter != null) {
            if (myListDataArr != null) {
                myListDataArr.clear();
                adapter.onDateChange(myListDataArr);
            }
        }
        Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                //更新过期的提醒
                AlarmDbUtils.updateStatus(context);
                myListDataArr = getAlarmListData(statusFlag);

                Message msg = handler.obtainMessage();
                msg.what = 0;
                handler.sendMessage(msg);

            }
        });

        mThread.start();
    }

    /**
     * 从数据库获取提醒数据
     *
     * @param status 0:获取历史提醒数据 1:获取尚未提醒的数据
     */
    private ArrayList<AlarmListEntity> getAlarmListData(int status) {
        String sortOrder = MyEntry.COLUMN_NAME_DATE + " ASC ";

        String selection = MyEntry.COLUMN_NAME_STATUS + " = ?";
        String[] selectionArgs = {String.valueOf(status)};

       /* if (myListDataArr != null) {
            myListDataArr.clear();
        } else*/
        if (myListDataArr == null) {
            myListDataArr = new ArrayList<AlarmListEntity>();
        }
        return AlarmDbUtils.query(context, selection, selectionArgs, null, null, sortOrder, pageIndex, preferences.getInt(PrefenceKeyConstants.SETTING_AUTO_LOAD, PrefenceKeyConstants.SETTING_AUTO_LOAD_DEFAULT_COUNT));

    }

    private MyAdapter adapter;

    /**
     * ，设置适配器,刷新，显示列表
     */
    private void showListView() {

        if (myListDataArr != null && myListDataArr.size() > 0) {
            main_bottom_tv_no_date.setVisibility(View.GONE);
            myListView.setVisibility(View.VISIBLE);

        } else {
            main_bottom_tv_no_date.setText(noDataDesc);
            main_bottom_tv_no_date.setVisibility(View.VISIBLE);
            myListView.setVisibility(View.GONE);
        }

        if (adapter == null) {
            if (myListDataArr != null && myListDataArr.size() > 0) {
                adapter = new MyAdapter(this, myListDataArr);
                adapter.setMyAdapterListener(new MyAdapter.IUpdateListView() {
                    @Override
                    public void removeItem(int position) {
                        MyActivity.this.removeItem(position);
                    }

                    @Override
                    public void changeStatus(int position) {
                        MyActivity.this.changeStatus(position);
                    }
                });
                myListView.setAdapter(adapter);

            }

        } else {
            Log.d("TAG", "list data change");
            adapter.onDateChange(myListDataArr);
        }

        my_pb.setVisibility(View.GONE);

        if (shouldScrollToTop) {
            myListView.scrollToPosition(1);
        }
    }

    /**
     * 更新top的详细信息
     */
    private void refreshMain() {
        showListView();
        MyTopNextFragment m = (MyTopNextFragment) mDatas.get(0);
        m.refreshDetail();
    }

    /**
     * 实现listview adapter接口，删除一行数据
     *
     * @param position
     */
    public void removeItem(int position) {
        //先删除数据库
        delData(myListDataArr.get(position).getId());

        myListDataArr.remove(position);

        refreshMain();
    }

    /**
     * 实现listview adapter接口，更新一行数据
     *
     * @param position
     */
    public void changeStatus(int position) {
        int status = Math.abs(myListDataArr.get(position).getStatus() - 1);

        ContentValues values = new ContentValues();
        values.put(MyEntry.COLUMN_NAME_STATUS, status);
        AlarmDbUtils.updateDataById(context, myListDataArr.get(position).getId(), values);

        myListDataArr.get(position).setStatus(status);
        refreshMain();
    }

    /**
     * 删除数据
     *
     * @param id
     */
    private void delData(long id) {
        SQLiteDatabase db = EADbHelper.getWritableDatabase();
        // Define 'where' part of query.
        String selection = MyEntry._ID + " = ?";

        String[] selectionArgs = {String.valueOf(id)};

        int counts = db.delete(MyEntry.TABLE_NAME_ALARM, selection, selectionArgs);
        db.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my, menu);

        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getItemId() == R.id.menu_my_history) {
                if (statusFlag == 1) {
                    menu.getItem(i).setTitle(getResources().getString(R.string.menu_history_alarms));
                } else {
                    menu.getItem(i).setTitle(getResources().getString(R.string.menu_current_alarms));
                }

                break;
            }
        }

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.menu_my_setting:

                Intent intent = new Intent(MyActivity.this, SettingActivity.class);
                startActivity(intent);

                break;*/
            case R.id.menu_my_add:
                addAlarm();
                break;
            case R.id.menu_my_history:
                pageIndex = 0;
                if (item.getTitle().equals(getResources().getString(R.string.menu_history_alarms))) {
                    noDataDesc = getResources().getString(R.string.noDataDesc_history);
                    //更新过期的提醒
                    statusFlag = 0;
                    shouldScrollToTop = true;
                    refreshUI();

                    item.setTitle(getResources().getString(R.string.menu_current_alarms));
                } else {
                    noDataDesc = getResources().getString(R.string.noDataDesc);
                    statusFlag = 1;
                    shouldScrollToTop = true;
                    refreshUI();
                    item.setTitle(getResources().getString(R.string.menu_history_alarms));
                }

                break;
            case R.id.menu_my_flyme_setting:
                Intent intent2 = new Intent(MyActivity.this, SettingFragmentActivity.class);
                startActivity(intent2);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 监听在RemindService中改变列表数据状态(设置为已提醒:status更新为0)
     */
    private class ListDateChangedListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            long id = data.getLong("id");
            long date = data.getLong("date");
            int status = data.getInt("status");
            if (myListDataArr != null)
                for (int i = 0; i < myListDataArr.size(); i++) {
                    if (myListDataArr.get(i).getId() == id) {
                        myListDataArr.get(i).setDate(date);
                        myListDataArr.get(i).setStatus(status);
                        refreshMain();
                        break;
                    }
                }


        }
    }

    /**
     * 添加
     */
    public void addAlarm() {
        Intent intent = new Intent(context, GenerateResultActivity.class);
        Bundle data = new Bundle();
        data.putString("flag", "add");
        intent.putExtras(data);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        myListDataArr = null;
        unregisterReceiver(listDateChangedListener);
    }
}
