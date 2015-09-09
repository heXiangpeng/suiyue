package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import adapter.FriendSpaceAdapter;
import http.Http;
import http.Messagenode;
import http.ParserJson;
import navgationbar.SystemBarTintManager;
import phoenix.PullToRefreshView;

/**
 * Created by hexiangpeng on 15/5/1.
 *
 * 显示所有朋友圈的消息
 */
public class FriendSpaceActivity extends Activity{
    private ListView listFriendSpace;


    String name;

    private ImageView newMessage;

    private SwipeRefreshLayout pullToRefreshView;

    private FriendSpaceAdapter friendSpaceAdapter;
    public static final int REFRESH_DELAY = 2000;

    private Messagenode messagenode=new Messagenode();
    private ParserJson parserJson=new ParserJson();

    private Http http=new Http();





    Handler myhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Log.e("接收","接收");
                    friendSpaceAdapter=new FriendSpaceAdapter(messagenode);
                    listFriendSpace.setAdapter(friendSpaceAdapter);

                    break;
            }
        }
    };
/*

 设置顶栏颜色

 */
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    private void setStatusStyle(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.navgation);
        // tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.index_top_bg));



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendspace);
        setStatusStyle();
        initview();




        SharedPreferences userinfo=getSharedPreferences("userinfo",0);

        name=userinfo.getString("name","");


        gethttp(name);

        friendSpaceAdapter=new FriendSpaceAdapter(messagenode);
        listFriendSpace.setAdapter(friendSpaceAdapter);



        newMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addnewmes=new Intent(FriendSpaceActivity.this,NewMessageActivity.class);
                startActivity(addnewmes);
            }
        });



    }


    public void initview(){
        newMessage=(ImageView) findViewById(R.id.img_newmess);
        listFriendSpace=(ListView) findViewById(R.id.list_friendspace);
        pullToRefreshView = (SwipeRefreshLayout) findViewById(R.id.pull_to_refresh);

        pullToRefreshView.setDistanceToTriggerSync(600);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        pullToRefreshView.setProgressBackgroundColor(R.color.white); // 设定下拉圆圈的背景
        pullToRefreshView.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小

        pullToRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新

                        gethttp(name);
                        pullToRefreshView.setRefreshing(false);
                    }
                },  REFRESH_DELAY); // 5秒后发送消息，停止刷新
            }
        });
    }



    public void gethttp(final String name){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result=http.httpGetMessage(name);

                if (result!=null) {
                    Log.e("获取结果",result);
                    messagenode=parserJson.Json(result);
                    Message message=new Message();
                    message.what=1;
                    myhandler.sendMessage(message);
                }
            }
        }).start();
    }










}
