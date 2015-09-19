package com.suiyue.hexiangpeng.suiyue;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;

import java.util.Random;

import http.Http;
import http.ParserJson;
import http.UserInfo;

/**
 * Created by hexiangpeng on 15/9/13.
 */
public class UploadInfo extends Fragment {
    private Dialog dialog;
    private String re;

    private View view;
    private Context context;
    private String[] persontext = {"世界上最好的感觉就是知道有人在想你。", "梦里不知身是客，醒来却道如南柯。", "寂寞是下在我魂里的咒，无药可救。", "如果我是太阳你会不会期待天亮"};

    private String phoneNumber;

    private EditText name;
    private EditText pass;
    private LinearLayout registerbutton;


    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 3:

                    System.out.println("接收");
                    dialog.cancel();
                    dialog.dismiss();
                    EMChatManager.getInstance().login(phoneNumber, pass.getText().toString(), new EMCallBack() {
                        @Override
                        public void onSuccess() {

                            tast ta = new tast();
                            ta.execute();


                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

                    break;
                default:
                    break;
            }
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.uploadinfofragment, null);
        context = this.getActivity();
        initView();
        phoneNumber = getArguments().get("phone").toString();
        addListener();
        return view;
    }

    public void initView() {
        name = (EditText) view.findViewById(R.id.ver_name);
        pass = (EditText) view.findViewById(R.id.ver_pass);
        registerbutton = (LinearLayout) view.findViewById(R.id.linear_uploadinfo);


    }

    public void addListener() {
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || pass.getText().toString().isEmpty()) {
                    Toast.makeText(context, "昵称或密码为空", Toast.LENGTH_SHORT).show();

                } else {

                    dialog = new Dialog(context, R.style.mydialogstyle);
                    dialog.setContentView(R.layout.progresslayout);//此处布局为一个progressbar
                    dialog.setCancelable(true); // 可以取消
                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Http http = new Http();

                            try {
                                EMChatManager.getInstance().createAccountOnServer(phoneNumber, pass.getText().toString());


                                Random random1 = new Random(persontext.length);

                                System.out.println(random1.nextInt());
                                http.addUser(phoneNumber, pass.getText().toString(), name.getText().toString(), persontext[random1.nextInt() % persontext.length], "");

                                String re1 = http.addfriend(phoneNumber, phoneNumber);


                                Log.e("注册", re1);

                                System.out.println("获取电话" + phoneNumber);

                                ///
                                Message message = new Message();


                                if (re1.contains("suc")) {


                                    message.what = 3;
                                    myhandler.sendMessage(message);
                                } else {


                                    message.what = 2;
                                    myhandler.sendMessage(message);
                                }

                            } catch (Exception e) {
                                System.out.println(e);
                            }


                        }
                    }).start();


                }
            }
        });
    }

    public class tast extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            Http http = new Http();
            String re2 = http.GetUserInfo(phoneNumber);
            System.out.println("async" + phoneNumber);

            return re2;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            ParserJson pa = new ParserJson();

            System.out.println("异步结果" + s);
            UserInfo userInfo = pa.parserUser(s);
            SharedPreferences userinfo = context.getSharedPreferences("userinfo", 0);
            userinfo.edit().putString("name", phoneNumber).commit();
            userinfo.edit().putString("pass", pass.getText().toString()).commit();
            userinfo.edit().putString("realname", userInfo.name.get(0).toString()).commit();
            userinfo.edit().putString("photo", userInfo.photo.get(0).toString()).commit();
            userinfo.edit().putString("location", userInfo.location.get(0).toString()).commit();

            Intent mainactivity = new Intent(context, MianHome.class);


            startActivity(mainactivity);
        }
    }


}
