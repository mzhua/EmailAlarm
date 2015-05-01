package im.mz.EmailAlarm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import im.mz.EmailAlarm.R;
import im.mz.EmailAlarm.entity.FlymeRightEntity;
import im.mz.EmailAlarm.entity.SettingLeftEntity;

/**
 * Created by mzhua_000 on 2014/12/29.
 */
public class SettingLeftAdapter extends BaseAdapter implements AdapterView.OnItemClickListener{
    private Context context;
    private List<SettingLeftEntity> drawableList;

    private LayoutInflater inflater;

    private OnClickListener onClickListener;

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        onClickListener.onClick(position);
    }

    public interface OnClickListener{
        public void onClick(int position);
    }

    public SettingLeftAdapter(Context context, List<SettingLeftEntity> drawableList){
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.drawableList = drawableList;

    }

    public void notifyDataChange(List<SettingLeftEntity> drawableList){
        this.drawableList = drawableList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return drawableList.size();
    }

    @Override
    public Object getItem(int position) {
        return drawableList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LeftViewHolder leftViewHolder ;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.activity_flyme_left_item,null);
            leftViewHolder = new LeftViewHolder();
            leftViewHolder.desccIcon = (ImageView) convertView.findViewById(R.id.iv_flyme_left);
            leftViewHolder.desccIcon.setTag(position);
            convertView.setTag(leftViewHolder);

        }else{

            leftViewHolder = (LeftViewHolder) convertView.getTag();
        }

        leftViewHolder.desccIcon.setImageDrawable(drawableList.get(position).getDrawable());
//        leftViewHolder.desccIcon.setLayoutParams(new LinearLayout.LayoutParams(44,44));
        if(drawableList.get(position).isPressed()){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.list_item_pressed));
        }else{
            convertView.setBackgroundResource(R.drawable.selector_list_item);
        }
        return convertView;
    }

    class LeftViewHolder{
        ImageView desccIcon;
    }
}
