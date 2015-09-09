package solvemessage;

import com.easemob.chat.EMMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hexiangpeng on 15/5/4.
 *
 *
 *  在message队列中取出位置消息
 */
public class RemoveLocation {

    public static List<EMMessage> removeLocation(List<EMMessage> messages){
        List<EMMessage> mes=new ArrayList<>();
        for(int i=0;i<messages.size();i++){
            if (messages.get(i).getType() == EMMessage.Type.TXT){
                mes.add(messages.get(i));
            }
        }

        return mes;

    }
}
