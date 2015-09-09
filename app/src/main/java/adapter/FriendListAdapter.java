package adapter;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.suiyue.hexiangpeng.suiyue.R;

import java.util.List;

import http.UserInfo;

/**
 * Created by hexiangpeng on 15/4/13.
 *
 *
 */
public class FriendListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private UserInfo username;



   public FriendListAdapter(UserInfo username){
       this.username=username;
       //创建默认的ImageLoader配置参数



   }

    @Override
    public int getCount() {
        return username.idcode.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View  view=LayoutInflater.from(parent.getContext())
                .inflate(R.layout.friendlistitem, parent, false);;



            viewHolder viewholder=new viewHolder();

            viewholder.personalphoto=(ImageView) view.findViewById(R.id.img_photo);
            viewholder.name=(TextView) view.findViewById(R.id.text_name);
            viewholder.sign=(TextView) view.findViewById(R.id.text_sign);
            viewholder.distanse=(TextView) view.findViewById(R.id.text_distanse);
            view.setTag(viewholder);




        final viewHolder holder=(viewHolder) view.getTag();

        holder.name.setText(username.name.get(position).toString());
        holder.sign.setText(username.persontext.get(position).toString());
        holder.distanse.setText(username.distance.get(position)+"Km");
        Log.e("距离2",username.distance.get(position));
        String imageUrl="http://suiyue520.sinaapp.com/"+username.photo.get(position).toString();

        ImageLoader.getInstance().loadImage(imageUrl, new SimpleImageLoadingListener(){



            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                holder.personalphoto.setImageBitmap(loadedImage);



            }

        });


        return view;
    }


    static class  viewHolder{
        public ImageView personalphoto;
        public TextView  name;
        public TextView  sign;
        public TextView  distanse;
    }
}
