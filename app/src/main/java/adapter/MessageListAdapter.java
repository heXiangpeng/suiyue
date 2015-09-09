package adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.suiyue.hexiangpeng.suiyue.MessageListActivity;
import com.suiyue.hexiangpeng.suiyue.R;

import java.util.List;

/**
 * Created by hexiangpeng on 15/4/15.
 */
public class MessageListAdapter extends BaseAdapter {
    private LayoutInflater inflater;

    private SQLiteDatabase db;

    private Cursor cursor;


    public MessageListAdapter(Context context, int textViewResourceId, SQLiteDatabase db){

        inflater = LayoutInflater.from(context);
      this.db=db;
        cursor=db.query("messagelist1",null,null,null,null,null,null);

    }

    public void refresh(){
        cursor=db.query("messagelist1",null,null,null,null,null,null);
        Log.e("消息","消息列表刷新");
//        notifyDataSetChanged();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public int getCount() {
        return cursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView=inflater.inflate(R.layout.messagelistitem,parent,false);
        }
       final ViewHolder holder = new ViewHolder();
//        if (holder == null){
//            holder=new ViewHolder();
            holder.avatar=(ImageView)convertView.findViewById(R.id.img_messagephoto);
            holder.name=(TextView) convertView.findViewById(R.id.text_messagefrom);
            holder.lastmesg=(TextView) convertView.findViewById(R.id.text_lastmessage);
            holder.unreadnum=(TextView) convertView.findViewById(R.id.text_mesgnum);
            convertView.setTag(holder);

//        }

//
//       String username=users.get(position);
//        // 获取与此用户/群组的会话
//
//        EMConversation conversation = EMChatManager.getInstance().getConversation(username);

        if(cursor.moveToFirst()) {
            cursor.move(position);

            holder.name.setText(cursor.getString(cursor.getColumnIndex("name")));

//            MessageListActivity.usersidcode.add(cursor.getString(cursor.getColumnIndex("id")));

            holder.unreadnum.setText("");
            String ur=cursor.getString(cursor.getColumnIndex("photo"));
            ImageLoader.getInstance().loadImage(ur, new SimpleImageLoadingListener(){



                @Override
                public void onLoadingComplete(String imageUri, View view,
                                              Bitmap loadedImage) {
                    super.onLoadingComplete(imageUri, view, loadedImage);
                    holder.avatar.setImageBitmap(loadedImage);



                }

            });


            holder.lastmesg.setText(cursor.getString(cursor.getColumnIndex("lastmessage")));

        }




        return convertView;
    }





    private static class ViewHolder{
        ImageView avatar;
        TextView name;
        TextView unreadnum;
        TextView lastmesg;
    }

}
