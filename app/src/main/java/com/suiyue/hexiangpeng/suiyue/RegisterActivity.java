package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;


import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import http.Http;
import utils.SIMCardInfo;


/**
 * Created by hexiangpeng on 15/5/17.
 */
public class RegisterActivity extends Activity {
    private EditText useridecode;
    private EditText userpasswd;
    private EditText username;




    private String idcde;
    private String pa;

    private String[] persontext={"世界上最好的感觉就是知道有人在想你。","梦里不知身是客，醒来却道如南柯。","寂寞是下在我魂里的咒，无药可救。","如果我是太阳你会不会期待天亮"};

    private Button register;

    private Dialog dialog;

    private ImageView back;



   Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case 2:
//                    Toast.makeText(RegisterActivity.this,"注册失败",Toast.LENGTH_SHORT).show();
                case 1:

                    Log.e("信息", "message");
                    dialog.cancel();
                    dialog.dismiss();

                    EMChatManager.getInstance().login(idcde,pa,new EMCallBack() {//回调
                        @Override
                        public void onSuccess() {
                            runOnUiThread(new Runnable() {
                                public void run() {


                                    EMGroupManager.getInstance().loadAllGroups();
                                    EMChatManager.getInstance().loadAllConversations();

                                    SharedPreferences userinfo=getSharedPreferences("userinfo",0);
                                    userinfo.edit().putString("name",idcde).commit();
                                    userinfo.edit().putString("pass",pa).commit();
                                    Intent mainactivity=new Intent(RegisterActivity.this,MianHome.class);





                                    startActivity(mainactivity);



                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress, String status) {

                        }

                        @Override
                        public void onError(int code, String message) {
                            Log.d("main", "登陆聊天服务器失败！");
                        }
                    });


                    break;
                default:
                    break;
            }
        }
    };





    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        try{
            dialog.dismiss();
        }catch (Exception e) {
            System.out.println("myDialog取消，失败！");
            // TODO: handle exception
        }
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        useridecode=(EditText)findViewById(R.id.edit_regusername);
        userpasswd=(EditText)findViewById(R.id.edit_regpasswd);
        username=(EditText)findViewById(R.id.edit_name);

        register=(Button) findViewById(R.id.btn_reg);
        back=(ImageView) findViewById(R.id.register_back);











        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login=new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(login);
                overridePendingTransition(0, R.anim.base_slide_right_out);
            }
        });


        SIMCardInfo siminfo = new SIMCardInfo(RegisterActivity.this);

       // Log.e("电话",siminfo.getProvidersName());

       // useridecode.setText(siminfo.getNativePhoneNumber().toString());







        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String idcode=useridecode.getText().toString().trim();
                final String pass=userpasswd.getText().toString().trim();
                final String name=username.getText().toString().trim();

                idcde=idcode;
                pa=pass;

                if (idcode.isEmpty()||pass.isEmpty()||name.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"请输入完整信息",Toast.LENGTH_SHORT).show();
                }else{

                    dialog = new Dialog(RegisterActivity.this,R.style.mydialogstyle);
                    dialog.setContentView(R.layout.progresslayout);//此处布局为一个progressbar
                    dialog.setCancelable(true); // 可以取消
                    dialog.show();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            Http http=new Http();

                            try {
                                EMChatManager.getInstance().createAccountOnServer(idcode, pass);
                            }catch (Exception e){
                                System.out.println(e);
                            }

                            Random random1 = new Random(persontext.length);

                            System.out.println(random1.nextInt());
                            http.addUser(idcode,pass,name,persontext[random1.nextInt()%persontext.length],"");

                            String re=http.addfriend(idcode,idcode);


                            Log.e("注册",re);

                            ///
                            Message message = new Message();


                            if(re=="suc"){
                                message.what =1;
                                myHandler.sendMessage(message);
                            }else{
                                message.what=2;
                                myHandler.sendMessage(message);
                            }




                        }
                    }).start();


                    //
                }


            }
        });



    }


    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(login);
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }
}
