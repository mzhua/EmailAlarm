package im.mz.EmailAlarm.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.adapter.FileSelectorAdapter;
import im.mz.EmailAlarm.entity.FileSelectorEntity;

/**
 * Created by mzhua_000 on 2015/1/13.
 */
public class FileSelectorActivity extends BaseFragmentActivity implements SearchView.OnQueryTextListener{
    private Context context;
    private LinearLayout llFileSelector;
    private ListView lvFile;

    private FileSelectorAdapter fileSelectorAdapter;
    private ArrayList<FileSelectorEntity> fileSelectorEntities;

    private File targetFile;
    private int fileLevel = 0;
    private int preLevelFirstVisiblePosition = 0;

    private LayoutAnimationController controller;
    private Animation animation;

    Comparator<FileSelectorEntity> comparator = new Comparator<FileSelectorEntity>() {
        @Override
        public int compare(FileSelectorEntity lhs, FileSelectorEntity rhs) {
            if (lhs.isFolder() && !rhs.isFolder()) {
                return -1;
            } else if (!lhs.isFolder() && rhs.isFolder()) {
                return 1;
            } else {
                return lhs.getFileName().compareTo(rhs.getFileName());
            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting_file_selector);
        getActionBar().setTitle(getResources().getString(R.string.actionbar_disk));
        this.context = this;

        targetFile = Environment.getExternalStorageDirectory();

        llFileSelector = (LinearLayout) findViewById(R.id.ll_setting_file_selector);
        lvFile = (ListView) findViewById(R.id.lv_setting_file_selector);

        animation = new AlphaAnimation(0f, 1f);;//new TranslateAnimation(-500f,0f,0f,0f);
        animation.setDuration(300);
        //1f为延时
        controller = new LayoutAnimationController(animation, 0.6f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        lvFile.setLayoutAnimation(controller);

        fileLevel = 0;
        getFiles(targetFile,"");
        fileSelectorAdapter = new FileSelectorAdapter(this, fileSelectorEntities);
        fileSelectorAdapter.setOnClickListener(new FileSelectorAdapter.OnClickListener() {
            @Override
            public void onClick(int postion) {

                if (fileSelectorEntities.get(postion).isFolder()) {
                    targetFile = new File(fileSelectorEntities.get(postion).getFilePath());
                    if(targetFile != null){
                        if(targetFile.listFiles().length > 0){
                            getActionBar().setTitle(fileSelectorEntities.get(postion).getFilePath().replace("/storage/emulated/0",getResources().getString(R.string.actionbar_disk)));
//                            preLevelFirstVisiblePosition = lvFile.getFirstVisiblePosition();
                            mSearchView.setQuery("",true);
                            fileLevel++;
                            refreshListUI(targetFile,"");

                            lvFile.scrollTo(0, 0);//.smoothScrollToPosition(0);
                        }else{
                            Toast.makeText(context, getResources().getString(R.string.toast_empty_folder), Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(context, getResources().getString(R.string.toast_empty_folder), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    String path = fileSelectorEntities.get(postion).getFilePath();
                    if (!path.endsWith(".xlsx")) {
                        if(!path.endsWith(".xls")){
                            Toast.makeText(context,getResources().getString(R.string.toast_select_excel_file),Toast.LENGTH_LONG).show();
                        }else{
                            Intent intent = new Intent();
                            Bundle data = new Bundle();
                            data.putString("path",path);
                            intent.putExtras(data);
                            setResult(Activity.RESULT_OK,intent);
                            FileSelectorActivity.this.finish();
                        }

                    }else{
                        Toast.makeText(context,"暂时无法解析Excel-2003以后的版本，请先保存为Excel97-2003兼容模式",Toast.LENGTH_LONG).show();
                    }

//                    Toast.makeText(context, "file path = " + fileSelectorEntities.get(postion).getFilePath(), Toast.LENGTH_SHORT).show();
                }

            }
        });
        lvFile.setAdapter(fileSelectorAdapter);
    }

    private synchronized void refreshListUI(File file,String filter){
        if(fileSelectorAdapter != null){
            getFiles(file,filter);
            lvFile.setLayoutAnimation(controller);
            fileSelectorAdapter.onDataChange(fileSelectorEntities);
        }


    }
    private synchronized void getFiles(File file, final String filter) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            FilenameFilter filenameFilter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if(filename.toUpperCase().contains(filter.toUpperCase()) || TextUtils.isEmpty(filter)){
                        return true;
                    }
                    return false;
                }
            };
            File[] files = file.listFiles(filenameFilter);//.listFiles();
            if(fileSelectorEntities == null){
                fileSelectorEntities = new ArrayList<FileSelectorEntity>();
            }else{
                fileSelectorEntities.clear();
            }
            fileSelectorEntities = null;
            fileSelectorEntities = new ArrayList<FileSelectorEntity>();


            for (int i = 0; i < files.length; i++) {
                if (!files[i].getName().startsWith(".")) {
                    FileSelectorEntity fileSelectorEntity = new FileSelectorEntity();
                    fileSelectorEntity.setFileName(files[i].getName());
                    fileSelectorEntity.setFilePath(files[i].getAbsolutePath());
                    fileSelectorEntity.setFolder(files[i].isDirectory());
                    fileSelectorEntities.add(fileSelectorEntity);

                    Log.d("TAG", "file name = " + files[i].getAbsolutePath() + " isDirectory = " + files[i].isDirectory());
                }
            }
            Collections.sort(fileSelectorEntities, comparator);
        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_sdcard_not_exist), Toast.LENGTH_SHORT).show();
        }

    }

    private SearchView mSearchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_file_selector, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.activity_file_search_hint));
        setupSearchView(searchItem);
        return true;
    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

/*        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            // Try to use the "applications" global search provider
            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            mSearchView.setSearchableInfo(info);
        }*/

        mSearchView.setOnQueryTextListener(this);
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        Toast.makeText(context,newText,Toast.LENGTH_SHORT).show();
        refreshListUI(targetFile,newText);
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fileLevel > 0) {
                fileLevel--;
                targetFile = targetFile.getParentFile();
                getActionBar().setTitle(targetFile.getAbsolutePath().replace("/storage/emulated/0", getResources().getString(R.string.actionbar_disk)));
                getFiles(targetFile,"");
                fileSelectorAdapter.onDataChange(fileSelectorEntities);

                return false;
            }
        }
        return super.onKeyDown(keyCode,event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fileSelectorEntities = null;
        fileSelectorAdapter = null;
    }


}