package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.suiyue.hexiangpeng.suiyue.R;

/**
 * Created by hexiangpeng on 15/11/26.
 */
public class EmoijAdapter extends BaseAdapter {



    private int[] emoij = {R.drawable.emoij1,R.drawable.emoij2,R.drawable.emoij3,R.drawable.emoij4};

    private LayoutInflater inflater;


    public EmoijAdapter(LayoutInflater inflater){

        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return emoij.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder = null;
        if(convertView == null){

            convertView = inflater.inflate(R.layout.emoijitem,null);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.img_emoij);
            convertView.setTag(viewHolder);

        }else{
            viewHolder =(ViewHolder) convertView.getTag();
        }


        viewHolder.imageView.setImageResource(emoij[position]);

        return convertView;
    }



    class ViewHolder{
        ImageView imageView;

    }
}
