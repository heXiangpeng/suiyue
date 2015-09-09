package adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.suiyue.hexiangpeng.suiyue.R;

import java.util.ArrayList;
import java.util.List;

import http.Messagenode;

/**
 * Created by hexiangpeng on 15/5/1.
 */
public class FriendSpaceAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Messagenode messagenode;
    private List<String> me=new ArrayList<>();

    public FriendSpaceAdapter(Messagenode messagenode){
        this.messagenode=messagenode;

        me.add("");


    }


    @Override
    public int getCount() {

            return messagenode.idcode.size();

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView==null){

                convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.friendspaceitem, parent, false);

                TextView text = (TextView) convertView.findViewById(R.id.text_text);
                text.setText(messagenode.message.get(position));


        }
        return convertView;
    }
}
