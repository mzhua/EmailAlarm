package im.mz.EmailAlarm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.entity.FileSelectorEntity;

/**
 * Created by mzhua_000 on 2015/1/13.
 */
public class FileSelectorAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<FileSelectorEntity> fileSelectorEntities;

    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void onDataChange(ArrayList<FileSelectorEntity> fileSelectorEntities){
        this.fileSelectorEntities = fileSelectorEntities;
        this.notifyDataSetChanged();
    }
    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener{
        public void onClick(int postion);
    }
    public FileSelectorAdapter(Context context,ArrayList<FileSelectorEntity> fileSelectorEntities) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.fileSelectorEntities = fileSelectorEntities;
    }

    @Override
    public int getCount() {
        return fileSelectorEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return fileSelectorEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(fileSelectorEntities != null && fileSelectorEntities.size() > 0){
            ViewHolder viewHolder ;
            if(convertView == null){
                convertView = inflater.inflate(R.layout.activity_setting_file_selector_item,null);
                viewHolder = new ViewHolder();
                viewHolder.llItem = (LinearLayout) convertView.findViewById(R.id.ll_setting_file_selector_item);
                viewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_setting_file_selector_item);
                viewHolder.tvName = (TextView) convertView.findViewById(R.id.tv_setting_file_selector_item);
                viewHolder.llItem.setTag(position);
                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClick(position);
                }
            });
            if(fileSelectorEntities.get(position).isFolder()){
                viewHolder.ivIcon.setImageResource(R.drawable.ic_diretory);
            }else{
                if(fileSelectorEntities.get(position).getFilePath().endsWith(".xls") || fileSelectorEntities.get(position).getFilePath().endsWith(".xlsx")){
                    viewHolder.ivIcon.setImageResource(R.drawable.ic_excel);
                }else{
                    viewHolder.ivIcon.setImageResource(R.drawable.ic_file);
                }

            }

            viewHolder.tvName.setText(fileSelectorEntities.get(position).getFileName());
            return convertView;
        }
        return null;

    }

    class ViewHolder{
        LinearLayout llItem;
        ImageView ivIcon;
        TextView tvName;
    }
}
