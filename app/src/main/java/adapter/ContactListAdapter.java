package adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.suiyue.hexiangpeng.suiyue.R;

import java.util.List;

import solvemessage.RemoveLocation;

/**
 * Created by hexiangpeng on 15/4/23.
 */
public class ContactListAdapter extends BaseAdapter {
    private List<EMMessage> messages;
    private EMConversation conversation;
    private String username;

    int numbermessage;

    Animation textmessage;

    public  ContactListAdapter(String username,Context context){
        this.username=username;

        textmessage=AnimationUtils.loadAnimation(context,R.anim.contact_photo_in);

        conversation = EMChatManager.getInstance().getConversation(username);
//获取此会话的所有消息
        messages = conversation.getAllMessages();
        numbermessage=messages.size();

        this.messages= RemoveLocation.removeLocation(messages);


    }

    /**
     * 刷新页面
     */
    public void refresh() {
        notifyDataSetChanged();
        conversation = EMChatManager.getInstance().getConversation(username);
//获取此会话的所有消息
        messages = conversation.getAllMessages();

        this.messages= RemoveLocation.removeLocation(messages);


        notifyDataSetChanged();

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
        View  view;
        viewHolder viewholder=new viewHolder();
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatitem, parent, false);

        if(messages.get(position).direct==EMMessage.Direct.RECEIVE) {

            RelativeLayout send= (RelativeLayout) view.findViewById(R.id.chart_to_container);
            send.setVisibility(View.GONE);




            TextView textViewRecive=(TextView) view.findViewById(R.id.chatfrom_content);
            if (messages.get(position).getType()==EMMessage.Type.TXT) {



                TextMessageBody txtBody = (TextMessageBody) messages.get(position).getBody();

                if (txtBody.getMessage().length()>0) {

               textViewRecive.setText(Html.fromHtml(txtBody.getMessage()));

                }
            }






        }else{
            LinearLayout recive=(LinearLayout) view.findViewById(R.id.chart_from_container);
            recive.setVisibility(View.GONE);
            TextView textViewSend=(TextView) view.findViewById(R.id.chatto_content);
            if (messages.get(position).getType()==EMMessage.Type.TXT) {



                TextMessageBody txtBody = (TextMessageBody) messages.get(position).getBody();

                if (txtBody.getMessage().length()>0) {



                        textViewSend.setText(Html.fromHtml(txtBody.getMessage()));


                }
            }

        }




        return view;
    }


    @Override
    public int getCount() {
        return messages.size();
    }



    static class  viewHolder{

        public TextView message;

    }


}
