package http;

import android.util.Log;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by hexiangpeng on 15/5/17.
 */
public class Http {
//    public static final OkHttpClient client=new OkHttpClient();


    public String sendHttp(String ur){

        String result = null;
        URL url = null;
        HttpURLConnection connection = null;

        InputStreamReader in = null;
        try {
            url = new URL(ur);
            connection = (HttpURLConnection) url.openConnection();
            in = new InputStreamReader(connection.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(in);
            StringBuffer strBuffer = new StringBuffer();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                strBuffer.append(new String(line.getBytes(),"utf8"));
            }
            result = strBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        System.out.println(result);
        return result;
    }




    public String GetUserInfo(String idcode){
        String ur="http://suiyue520.sinaapp.com/getuserinfo?idcode="+idcode;
        String re=sendHttp(ur);
        System.out.println("返回的登录"+re);

        return re;

    }



    public String UpdateLocation(String idcode,String Location){
        String ur="http://suiyue520.sinaapp.com/updatelocation?location='"+Location+"'"+"&idcode='"+idcode+"'";

        String re=sendHttp(ur);

        return re;
    }


    public String getAllFriend(String idcode){
        String ur="http://1.suiyue520.sinaapp.com/getallfriend?idcode='"+idcode+"'";

        String re=sendHttp(ur);

        return re;
    }

    public  String httpGetMessage(String idcode1) {

        String ur="http://suiyue520.sinaapp.com/getmessage?idcode1="+idcode1;
        String re=sendHttp(ur);

        return re;

    }


    //注册新用户

    public  String  addUser(String idcode,String passwd,String name,String persontext,String photo){

        String ur="http://suiyue520.sinaapp.com/registe?idcode="+idcode+"&passwd="+passwd+"&name="+name+"&persontext="+persontext;
        String re = sendHttp(ur);

//        Log.e("re",re);

        return re;


    }

//    自己添加自己为好友
    public  String addfriend(String id1,String id2){

        String ur="http://suiyue520.sinaapp.com/addfriend?idcode1="+id1+"&idcode2="+id2;
        String re=sendHttp(ur);

        return re;

    }

//    http://suiyue520.sinaapp.com/addmessage?idcode=908&msgtext=我是908&location=日本

//    发表动态
    public  String  newMessage(String idcode,String msgText,String location){

        String ur="http://suiyue520.sinaapp.com/addmessage?idcode="+idcode+"&msgtext="+msgText+"&location="+location;
        String re=sendHttp(ur);

        return re;



    }


   public String searchFriend(String idcode){

        String ur="http://suiyue520.sinaapp.com/searchfriend?idcode="+idcode;
       String re=sendHttp(ur);

       return re;
    }

//    public static void run() throws Exception {
//
//        Request request = new Request.Builder()
//                .url("http://suiyue520.sinaapp.com/hello")
//                .build();
//
//        Log.e("请求","网络");
//        Response response = client.newCall(request).execute();
//        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
//
//        Headers responseHeaders = response.headers();
//        for (int i = 0; i < responseHeaders.size(); i++) {
//            System.out.println(responseHeaders.name(i) + ": " + responseHeaders.value(i));
//        }
//
//        System.out.println(response.body().string());
//    }


}
