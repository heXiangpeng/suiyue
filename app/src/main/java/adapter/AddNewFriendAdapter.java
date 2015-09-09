package adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.suiyue.hexiangpeng.suiyue.AddNewFriendActivity;
import com.suiyue.hexiangpeng.suiyue.R;

import http.FriendList;

/**
 * Created by hexiangpeng on 15/6/9.
 */
public class AddNewFriendAdapter extends BaseAdapter {
    private FriendList friendList;

    public AddNewFriendAdapter(FriendList friendList){

        this.friendList=friendList;

        Log.e("geshu",friendList.name.size()+"");

    }

    @Override
    public int getCount() {
        return friendList.name.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View  view;
        viewHolder viewholder=new viewHolder();





            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.addnewfrienditem, parent, false);
            viewholder.id=(TextView) view.findViewById(R.id.addnewfriend_idcode);
            viewholder.name=(TextView) view.findViewById(R.id.addnewfriend_name);
            view.setTag(viewholder);







        viewHolder holder=(viewHolder) view.getTag();

        holder.id.setText(friendList.name.get(position).toString());
        holder.name.setText(friendList.persontext.get(position));


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
        public TextView id;
        public TextView name;

    }
}
