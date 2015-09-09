package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.OnMessageNotifyListener;

import navgationbar.SystemBarTintManager;

/**
 * Created by hexiangpeng on 15/4/13.
 * 介绍
 */
public class MianHome extends TabActivity {
    private LinearLayout message;
    private LinearLayout frienlist;
    private LinearLayout set;


    private TabHost tabHost;

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
        setContentView(R.layout.activity_mainhome);


        setStatusStyle();


        Resources res=getResources();
        tabHost=getTabHost();

        TabHost.TabSpec spec;

        Intent intent;

         intent=new Intent().setClass(this,MessageListActivity.class);
         spec = tabHost.newTabSpec("消息").setIndicator("消息",res.getDrawable(R.drawable.mes)).setContent(intent);
         tabHost.addTab(spec);

        Intent  intent1=new Intent().setClass(this,FriendListActivity.class);
        TabHost.TabSpec spec1 = tabHost.newTabSpec("联系人").setIndicator("联系人",res.getDrawable(R.drawable.friendlist)).setContent(intent1);
        tabHost.addTab(spec1);


        Intent intent2=new Intent().setClass(this,SetActivity.class);
        TabHost.TabSpec spec2 = tabHost.newTabSpec("设置").setIndicator("设置",res.getDrawable(R.drawable.set)).setContent(intent2);
        tabHost.addTab(spec2);

        message=(LinearLayout) findViewById(R.id.liner_message);
        frienlist=(LinearLayout) findViewById(R.id.liner_friendlist);
        set=(LinearLayout) findViewById(R.id.liner_set);

        tabHost.setCurrentTab(1);


        message.setTag(1);
        frienlist.setTag(2);
        set.setTag(3);



        //获取到配置options对象
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
//设置自定义的文字提示
        options.setNotifyText(new OnMessageNotifyListener() {

            @Override
            public int onSetSmallIcon(EMMessage emMessage) {
                return R.drawable.icon;
            }

            @Override
            public String onSetNotificationTitle(EMMessage emMessage) {
                return "新消息";
            }

            @Override
            public String onNewMessageNotify(EMMessage message) {
                //可以根据message的类型提示不同文字，这里为一个简单的示例
                return message.getFrom() + "发来了一条消息哦";
            }

            @Override
            public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
                return fromUsersNum + "好友发来了" + messageNum + "条消息";
            }
        });



        message.setOnClickListener(new linearListener());
        frienlist.setOnClickListener(new linearListener());
        set.setOnClickListener(new linearListener());

    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

       // this.getWindow().setType(.TYPE_KEYGUARD);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_MENU){

            Toast.makeText(this, "KeyEvent.KEYCODE_MENU", Toast.LENGTH_LONG);
        }
        return true;
    }

    private final class linearListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            int tag=(Integer) v.getTag();
            switch (tag){
                case 1:
                    tabHost.setCurrentTab(0);
                    tabHost.setCurrentTabByTag("消息");
                    Log.e("click","1");
                    break;
                case 2:
                    tabHost.setCurrentTab(1);
                    tabHost.setCurrentTabByTag("联系人");

                    break;
                case 3:
                    tabHost.setCurrentTab(2);
                    tabHost.setCurrentTabByTag("设置");

                    break;
            }
        }
    }
}


