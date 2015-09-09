package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import http.Http;

/**
 * Created by hexiangpeng on 15/5/17.
 */
public class NewMessageActivity extends Activity {

    private Button send;
    private TextView newmessage;
    private Http http;

    private Dialog dialog;


    private ImageView back;




    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    newmessage.setText("");
                    dialog.cancel();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newmessage);
        newmessage=(TextView) findViewById(R.id.edit_newmessage);
        send=(Button) findViewById(R.id.btn_sendmess);

        back=(ImageView) findViewById(R.id.newmessage_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent friendspace=new Intent(NewMessageActivity.this,FriendSpaceActivity.class);
                startActivity(friendspace);
                overridePendingTransition(0, R.anim.base_slide_right_out);

                finish();
            }
        });

        http=new Http();

        SharedPreferences userinfo=getSharedPreferences("userinfo",0);

        final String name=userinfo.getString("name","");


        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text=newmessage.getText().toString();
                if (text.isEmpty()){
                    Toast.makeText(NewMessageActivity.this,"还没写东西哦",Toast.LENGTH_SHORT).show();
                }else{


                    dialog = new Dialog(NewMessageActivity.this,R.style.mydialogstyle);
                    dialog.setContentView(R.layout.progresslayout);//此处布局为一个progressbar
                    dialog.setCancelable(true); // 可以取消
                    dialog.show();

                    upload(name,text,"");
                    Message message=new Message();
                    message.what=1;
                    myHandler.sendMessage(message);

                }

            }
        });
    }


    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }


    public void upload(final String id, final String msg, final String location){
        new Thread(new Runnable() {
            @Override
            public void run() {

                http.newMessage(id,msg,location);


            }
        }).start();
    }
}
