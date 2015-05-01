package im.mz.EmailAlarm.fragment.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.adapter.FileSelectorAdapter;
import im.mz.EmailAlarm.entity.FileSelectorEntity;

/**
 * Created by mzhua_000 on 2015/1/14.
 */
public class FileSelectorFragment extends Fragment {
    private Context context;
    private ListView lvFile;
    private FileSelectorAdapter fileSelectorAdapter;
    private ArrayList<FileSelectorEntity> fileSelectorEntities;

    private File targetFile = null;
    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        public void onClick(File file);
    }
    public static FileSelectorFragment newInstance(String filePath){
        FileSelectorFragment fileSelectorFragment = new FileSelectorFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filePath",filePath);
        fileSelectorFragment.setArguments(bundle);
        return fileSelectorFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.context = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            String filePath = getArguments().getString("filePath","");
            targetFile = new File(filePath);
            Log.d("TAG","filePath = " + filePath);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_setting_file_selector_list,null);
        lvFile = (ListView) view.findViewById(R.id.lv_setting_file_selector_new);

        if(targetFile != null){
            getFiles(targetFile);
            fileSelectorAdapter = new FileSelectorAdapter(context, fileSelectorEntities);
            fileSelectorAdapter.setOnClickListener(new FileSelectorAdapter.OnClickListener() {
                @Override
                public void onClick(int postion) {

                    if (fileSelectorEntities.get(postion).isFolder()) {
                        targetFile = new File(fileSelectorEntities.get(postion).getFilePath());
                        if(targetFile != null){
                            if(targetFile.listFiles().length > 0){
                                getActivity().getActionBar().setTitle(fileSelectorEntities.get(postion).getFilePath().replace("/storage/emulated/0", "存储盘"));
                                FileSelectorFragment fileSelectorFragment = new FileSelectorFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("filePath",targetFile.getAbsolutePath());
                                fileSelectorFragment.setArguments(bundle);
                                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("fsf").replace(R.id.fl_setting_file_selector_list, fileSelectorFragment).commit();
                                /*getFiles(targetFile);
                                fileSelectorAdapter.onDataChange(fileSelectorEntities);

                                lvFile.smoothScrollToPosition(0);*/
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
                                getActivity().setResult(Activity.RESULT_OK, intent);
                                getFragmentManager().popBackStackImmediate();
                            }

                        }else{
                            Toast.makeText(context,"暂时无法解析Excel-2003以后的版本，请先保存为Excel97-2003兼容模式", Toast.LENGTH_LONG).show();
                        }

//                    Toast.makeText(context, "file path = " + fileSelectorEntities.get(postion).getFilePath(), Toast.LENGTH_SHORT).show();
                    }

                }
            });
            lvFile.setAdapter(fileSelectorAdapter);
        }


        return view;
    }

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

    private void getFiles(File file) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            File[] files = file.listFiles();
            if(fileSelectorEntities == null){
                fileSelectorEntities = new ArrayList<FileSelectorEntity>();
            }else{
                fileSelectorEntities.clear();
            }
        /*    fileSelectorEntities = null;
            fileSelectorEntities = new ArrayList<FileSelectorEntity>();*/


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
}
