package http;

import android.util.JsonReader;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hexiangpeng on 15/5/29.
 */
public class ParserJson {
    Messagenode messagenode=new Messagenode();
    UserInfo userInfo=new UserInfo();
    FriendList friendList = new FriendList();


//    解析好友数据

    public UserInfo parserUserInfo(String result){
        JSONObject obj = null;



        try {
            JSONArray jsonArray = new JSONArray(result);
            int len=jsonArray.length();
            for (int i=0;i< len;i++){

                obj=jsonArray.getJSONObject(i);
                userInfo.idcode.add(obj.get("idcode").toString());

                System.out.println(obj.get("idcode").toString());
                userInfo.name.add(obj.get("name").toString());
                userInfo.persontext.add(obj.get("persontext").toString());
                userInfo.photo.add(obj.get("photo").toString());
                userInfo.location.add(obj.get("location").toString());

             }
        }catch (Exception e){
            System.out.println(e);
        }

        return userInfo;
    }


    public UserInfo parserUser(String result){
        JSONObject obj = null;


        try {
            JSONArray jsonArray = new JSONArray(result);
            int len=jsonArray.length();
            for (int i=0;i< len;i++){

                obj=jsonArray.getJSONObject(i);
                userInfo.idcode.add(obj.get("idcode").toString());
                userInfo.name.add(obj.get("name").toString());

                userInfo.photo.add(obj.get("photo").toString());
                userInfo.location.add(obj.get("location").toString());


            }
        }catch (Exception e){
            System.out.println(e);
        }

        return userInfo;
    }


    //解析好友说说
    public Messagenode Json(String strResult){

        JSONObject obj = null;


        try {
            JSONArray jsonArray = new JSONArray(strResult);
            int len=jsonArray.length();
            for (int i=0;i< len;i++){

                obj=jsonArray.getJSONObject(i);


                System.out.println("");

                if (!messagenode.message.contains(obj.get("msgtext").toString())) {
                    messagenode.idcode.add(obj.get("idcode").toString());
                    messagenode.message.add(obj.get("msgtext").toString());
                    messagenode.location.add(obj.get("location").toString());
                }

            }
        }catch (Exception e){
            System.out.println(e);
        }

        return messagenode;

    }

    //解析用户数据
    public FriendList parserFriendData(String result){

        JSONObject obj = null;


        try {
            JSONArray jsonArray = new JSONArray(result);
            int len=jsonArray.length();
            for (int i=0;i< len;i++){

                obj=jsonArray.getJSONObject(i);



                if (!friendList.idcode.contains(obj.get("idcode").toString())) {
                    friendList.idcode.add(obj.get("idcode").toString());
                    friendList.name.add(obj.get("name").toString());
                    friendList.persontext.add(obj.get("persontext").toString());
                }

            }
        }catch (Exception e){
            System.out.println(e);
        }

        return friendList;
    }



}
