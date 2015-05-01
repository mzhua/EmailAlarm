package im.mz.EmailAlarm.fragment.setting;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.*;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.activity.FileSelectorActivity;

/*import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;*/

/**
 * Created by mzhua_000 on 2015/1/5.
 */
public class AssistExcelFragment extends Fragment {
    static final private int GET_CODE = 0;
    private Context context;
    private SharedPreferences preferences;

    private ProgressDialog dialog;

    private EditText etTime;
    private EditText etLocation;
    private EditText etDetail;
    private EditText etSheet;
    private EditText etPerson;
    private EditText etSmsContent;
    private RelativeLayout rlFilePath;
    private TextView tvPath;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        this.context = getActivity();
//        getActivity().getWindow().setSoftInputMode(InputMethodManager.SHOW_FORCED);
        setHasOptionsMenu(true);  //fragment中要显示menu一定要加入这句
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);

        View view = inflater.inflate(R.layout.fragment_setting_assist_excel_import, container, false);
        initViews(view);



        return view;
    }

    private void initViews(View view) {
        etTime = (EditText) view.findViewById(R.id.et_fragment_assist_excel_time);
        etLocation = (EditText) view.findViewById(R.id.et_fragment_assist_excel_location);
        etDetail = (EditText) view.findViewById(R.id.et_fragment_assist_excel_detail);
        etSheet = (EditText) view.findViewById(R.id.et_fragment_assist_excel_sheet);
        etPerson = (EditText) view.findViewById(R.id.et_fragment_assist_excel_sms_person);
        etSmsContent = (EditText) view.findViewById(R.id.et_fragment_assist_excel_sms_content);

        tvPath = (TextView) view.findViewById(R.id.tv_fragment_setting_assist_excel_import_path);
        tvPath.setText(getResources().getString(R.string.fragment_item_assist_excel_path));
        rlFilePath = (RelativeLayout) view.findViewById(R.id.rl_fragment_setting_import_excel);
        rlFilePath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, FileSelectorActivity.class);
                startActivityForResult(intent, GET_CODE);
            }
        });
    }

    /**
     * 显示提示进度条
     */
    private void showDialog(){
        if (dialog == null) {
            dialog = new ProgressDialog(context);
        }
//                        dialog.setView(llProgressBar);
        dialog.setMessage(getResources().getString(R.string.fragment_dialog_msg));
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);
        dialog.show();
    }
    private String targetFilePath = "";

  /*  private void readFileJXL() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            if(!TextUtils.isEmpty(targetFilePath)){
//                File target = Environment.getExternalStorageDirectory();
                File file = new File(targetFilePath);
                if (file.exists()) {
                    try {
                        FileInputStream fis = new FileInputStream(file);
                        Workbook workbook = null;
                        try {
                            workbook = Workbook.getWorkbook(fis);
                        } catch (BiffException e) {
                            e.printStackTrace();
                        }
                        if (workbook != null) {
                            int sheetNum = Integer.parseInt(StringUtils.isBlank(etSheet.getText().toString()) ? "1" : etSheet.getText().toString()) - 1;
                            if (workbook.getNumberOfSheets() > sheetNum) {
                                Object[] objects = new Object[4];

                                Sheet sheet = workbook.getSheet(sheetNum);
                                objects[0] = sheet;
                                objects[1] = StringUtils.isBlank(etTime.getText().toString()) ? "1" : etTime.getText().toString();
                                objects[2] = StringUtils.isBlank(etLocation.getText().toString()) ? "2" : etLocation.getText().toString();
                                objects[3] = StringUtils.isBlank(etDetail.getText().toString()) ? "3" : etDetail.getText().toString();
                                showDialog();
                                new ImportExcelTask(context).execute(objects);
                            } else {
                                Toast.makeText(context, "Excel中不存在sheet " + (sheetNum + 1), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "读取文件出错", Toast.LENGTH_SHORT).show();
                        }
                        fis.close();
//                    workbook.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("FILE", "IOException");
                    }

                } else {
                    Toast.makeText(context, "文件"+targetFilePath+"不存在", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(context, getResources().getString(R.string.toast_select_excel_file), Toast.LENGTH_SHORT).show();
            }



        } else {
            Toast.makeText(context, getResources().getString(R.string.toast_sdcard_not_exist), Toast.LENGTH_SHORT).show();
        }

    }
*/

    /*private void readFilePOI() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File target = Environment.getExternalStorageDirectory();
            File file = new File(target, "alarm.xls");
            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    HSSFWorkbook workbook = new HSSFWorkbook(fis);
                    if (workbook != null) {
                        if (workbook.getNumberOfSheets() > 0) {
                            HSSFSheet sheet = workbook.getSheetAt(0);
                            HSSFRow r1 = sheet.getRow(0);
                            HSSFCell a1 = r1.getCell(0);
                            HSSFCell b1 = r1.getCell(1);

                            Log.d("TAG", "excel time type = " + a1.getCellType());
                            String time = String.valueOf(a1.getNumericCellValue());
                            String location = b1.getStringCellValue();
                            Log.d("TAG", "excel time = " + time + " type = " + a1.getCellType() + " location = " + location);
                        } else {
                            Toast.makeText(context, "Excel中不存在sheet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "读取文件出错", Toast.LENGTH_SHORT).show();
                    }
                    fis.close();
                    workbook.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("FILE", "IOException");
                }

            } else {
                Toast.makeText(context, "SD卡根目录下文件alarm.xlsx不存在", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "SD卡不存在", Toast.LENGTH_SHORT).show();
        }

        *//*try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader reader = new BufferedReader(streamReader);
            String texto = reader.readLine();
            Log.d("TAG","read info = " + texto);
            reader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*//*

//
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle bundle = data.getExtras();
                String path = bundle.getString("path", "");
                if (!path.endsWith(".xlsx")) {
                    if(!path.endsWith(".xls")){
                        Toast.makeText(context,getResources().getString(R.string.toast_select_excel_file),Toast.LENGTH_SHORT).show();
                        tvPath.setText(context.getResources().getString(R.string.fragment_item_assist_excel_path));
                    }else{
                        targetFilePath = path;
                        tvPath.setText(targetFilePath);
                    }

                }else{
                    Toast.makeText(context,"暂时无法解析Excel-2003以后的版本，请先保存为Excel97-2003兼容模式",Toast.LENGTH_SHORT).show();
                    tvPath.setText(context.getResources().getString(R.string.fragment_item_assist_excel_path));
                }

//                Toast.makeText(context,"path = " + path,Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.assist_excel_import, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_excel_import:
                if(TextUtils.isEmpty(etTime.getText().toString()) || TextUtils.isEmpty(etLocation.getText().toString()) || TextUtils.isEmpty(etDetail.getText().toString())){
                    Toast.makeText(context,getResources().getString(R.string.fragment_excel_toast),Toast.LENGTH_LONG).show();
                }else{
                   // readFileJXL();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
