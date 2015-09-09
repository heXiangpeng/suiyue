package adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.suiyue.hexiangpeng.suiyue.R;

import java.util.Set;

/**
 * Created by hexiangpeng on 15/4/14.
 */
public class SetAdapter extends BaseAdapter {

    private String[] mDataset;
    private int[] img;


    public SetAdapter(String[] mDataset,int[] img){
        this.mDataset=mDataset;
        this.img=img;

    }

    @Override
    public int getCount() {
        return mDataset.length;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View  view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.setitem, parent, false);;



        viewHolder viewholder=new viewHolder();
        viewholder.setimg=(ImageView) view.findViewById(R.id.img_set);
        viewholder.settext=(TextView) view.findViewById(R.id.text_setitem);

        view.setTag(viewholder);




        viewHolder holder=(viewHolder) view.getTag();
        holder.setimg.setBackgroundResource(img[position]);
        holder.settext.setText(mDataset[position]);

        return view;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }





    static class  viewHolder{
        public ImageView setimg;
        public TextView  settext;

    }
}
