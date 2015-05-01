package im.mz.EmailAlarm.fragment.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.*;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.adapter.SettingRightAppAdapter;
import im.mz.EmailAlarm.db.AppsFilterDbUtils;
import im.mz.EmailAlarm.entity.AppInfoEntity;
import im.mz.EmailAlarm.view.EASwitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mzhua_000 on 2015/1/5.
 */
public class AppsFragment extends Fragment implements SearchView.OnQueryTextListener {
    private Context context;

    private ListView lvApps;
    private EASwitch swApps;
    private EASwitch swNotify;
    private LinearLayout llLoading;

    private Thread thread;

    private SettingRightAppAdapter rightAppAdapter;
    private PackageManager pm;
    private List<AppInfoEntity> listApps;
    private List<AppInfoEntity> listAppsBackup;

    private boolean loadDataFinish = true;//后一次查询一定要在前一次查询完成后才进行，防止奔溃
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(isAdded()){
                        initLoadingView(true);
                        initAppsListAdapter();
                        loadDataFinish = true;
                    }

                    break;
            }
            return false;
        }
    });

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
        pm = context.getPackageManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting_apps, container, false);
        initViews(view);
        setHasOptionsMenu(true);  //fragment中要显示menu一定要加入这句
        return view;
    }

    /**
     * 设置app listview的适配器
     */
    private synchronized void initAppsListAdapter() {
        if (rightAppAdapter == null) {
            if (listApps != null) {
                rightAppAdapter = new SettingRightAppAdapter(context, listApps);
                rightAppAdapter.setOnButtonClickListener(new SettingRightAppAdapter.OnButtonClickListener() {
                    @Override
                    public void onClick(int position, boolean isChecked) {
                        if (isChecked) {
                            AppInfoEntity appInfoEntity = new AppInfoEntity();
                            appInfoEntity.setPackageName(listApps.get(position).getPackageName());
                            filterAppInfoList.add(appInfoEntity);
                        }


                        refreshAppList(position, isChecked);
                    }
                });
                /*lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("监听设置");
                        View customView = getActivity().getLayoutInflater().inflate(R.layout.dialog_app_filter, null);
                        final EditText reg = (EditText) customView.findViewById(R.id.dialog_app_et_reg);
                        builder.setView(customView);
                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getActivity(),"text = " + reg.getText(),Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("取消", null);
                        builder.create().show();
                    }
                });*/
                lvApps.setAdapter(rightAppAdapter);
            }

        } else {
            rightAppAdapter.notifyDataChange(listApps);
        }
    }

    /**
     *
     * 更新app的列表选中状态
     *
     * @param position
     * @param isChecked
     */
    private void refreshAppList(int position, boolean isChecked) {
        listApps.get(position).setBtnChecked(isChecked);
        initAppsListAdapter();
    }


    /**
     * 获取所有view，并设置监听器，初始化首次进入的界面
     *
     * @param view
     */
    private void initViews(View view) {
        lvApps = (ListView) view.findViewById(R.id.lv_fragment_setting_apps);
        swApps = (EASwitch) view.findViewById(R.id.sw_fragment_setting_apps);
        swNotify = (EASwitch) view.findViewById(R.id.sw_fragment_setting_apps_notify);

        llLoading = (LinearLayout) view.findViewById(R.id.ll_fragment_setting_loading);

        swApps.setOnEACheckedChangeListener(new EASwitch.OnEACheckedChangeListener() {
            @Override
            public void onCheckedChange(boolean isChecked) {
                if (isChecked) {
                    swNotify.setVisibility(View.VISIBLE);
                    initAppsListView("");
                } else {
                    swNotify.setVisibility(View.GONE);
                    if (rightAppAdapter != null) {
                        listApps.clear();
                        rightAppAdapter.notifyDataChange(listApps);
                        rightAppAdapter = null;
                        listApps = null;
                        lvApps.setOnItemClickListener(null);
                    }
                }
            }
        });

        if (swApps.isChecked()) {
            swNotify.setVisibility(View.VISIBLE);
            initAppsListView("");
        } else {
            swNotify.setVisibility(View.GONE);
        }

    }

    /**
     * 初始化界面，判断是显示loading还是现实list数据
     *
     * @param isLoadingFinish 数据是否加载完
     */
    private void initLoadingView(boolean isLoadingFinish) {
        if (isLoadingFinish) {
            llLoading.setVisibility(View.GONE);
            lvApps.setVisibility(View.VISIBLE);
        } else {
            llLoading.setVisibility(View.VISIBLE);
            lvApps.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化应用列表，异步获取数据
     */
    private synchronized void initAppsListView(final String filter) {
        initLoadingView(false);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                loadDataFinish = false;

                listApps = getAllApps(filter, listApps == null ? true : false);
                if(listAppsBackup == null){
                    listAppsBackup = listApps;
                }
                handler.sendEmptyMessage(0);

            }
        });
        thread.start();


    }

    private List<ApplicationInfo> listApplicationInfo;

    /**
     * 获取已安装应用信息
     */
    private synchronized List<AppInfoEntity> getAllApps(String filter, boolean shouldQueryDb) {

        if (listApplicationInfo == null) {
            listApplicationInfo = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        }

        Collections.sort(listApplicationInfo, new ApplicationInfo.DisplayNameComparator(pm));

        List<AppInfoEntity> appsInfo = new ArrayList<AppInfoEntity>();
        if(appsInfo != null) {
            if (appsInfo.size() > 0) {
                appsInfo.clear();
            }
        }

        if (shouldQueryDb) {
            String packageName;
            for (ApplicationInfo applicationInfo : listApplicationInfo) {
                packageName = (String) applicationInfo.loadLabel(pm);
                if (!packageName.contains("com.android")) {
                    if (applicationInfo.loadLabel(pm).toString().toUpperCase().contains(filter.toUpperCase()) || TextUtils.isEmpty(filter)) {
                        appsInfo.add(getAppInfo(applicationInfo));
                    }
                }

            }
        } else {
            for (AppInfoEntity appInfoEntity : listAppsBackup) {
                String label = appInfoEntity.getLabel();
                if (label.toUpperCase().contains(filter.toUpperCase()) || TextUtils.isEmpty(filter)) {
                    appsInfo.add(appInfoEntity);
                }

            }
        }

        appsInfo = sortByStatus(appsInfo);
        return appsInfo;
    }

    /**
     * @param appInfoEntityList
     * @return
     */
    private List<AppInfoEntity> sortByStatus(List<AppInfoEntity> appInfoEntityList) {
        AppInfoEntity appInfoEntity;
        if (appInfoEntityList != null && appInfoEntityList.size() > 0) {
            Log.d("TAG", "list size before sort = " + appInfoEntityList.size());
            for (int i = 0; i < appInfoEntityList.size(); i++) {
                if (appInfoEntityList.get(i).isBtnChecked()) {
                    appInfoEntity = appInfoEntityList.get(i);
                    appInfoEntityList.remove(i);
                    appInfoEntityList.add(0, appInfoEntity);
                }
            }
            Log.d("TAG", "list size after sort = " + appInfoEntityList.size());
        }


        return appInfoEntityList;
    }

    private ArrayList<AppInfoEntity> filterAppInfoList;

    /**
     * 获取单个应用的信息
     *
     * @param applicationInfo
     * @return
     */
    private AppInfoEntity getAppInfo(ApplicationInfo applicationInfo) {

        String packageName = applicationInfo.packageName;
        AppInfoEntity appInfo = new AppInfoEntity();
        appInfo.setLabel((String) applicationInfo.loadLabel(pm));
        appInfo.setIcon(applicationInfo.loadIcon(pm));
        appInfo.setPackageName(packageName);
        appInfo.setBtnChecked(false);

        if (filterAppInfoList == null) {
            filterAppInfoList = AppsFilterDbUtils.query(context, null, null, null, null, null);
        }

        if (filterAppInfoList != null && filterAppInfoList.size() > 0) {
//            Log.d("TAG", "size=" + appInfoEntityArrayList.size());
            for (AppInfoEntity appInfoEntity : filterAppInfoList) {
//                Log.d("TAG", "packagename11=" + appInfoEntity.getPackageName());
                if (packageName.equals(appInfoEntity.getPackageName())) {
                    Log.d("TAG", "packageName=" + packageName);
                    appInfo.setBtnChecked(true);
                    break;
                }
            }
        }


        return appInfo;
    }

    private SearchView mSearchView;

    //setHasOptionsMenu(true);  //fragment中要显示menu一定要加入这句
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_file_selector, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.fragment_search_hint));
        setupSearchView(searchItem);
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        mSearchView.setOnQueryTextListener(this);
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        listApps = null;
        listAppsBackup = null;
        rightAppAdapter = null;
        listApplicationInfo = null;
        filterAppInfoList = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();


    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (swApps.isChecked() && loadDataFinish) {

            initAppsListView(query);
        }
        return false;

    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (swApps.isChecked() && loadDataFinish) {

            initAppsListView(newText);
        }

        return false;
    }
}
