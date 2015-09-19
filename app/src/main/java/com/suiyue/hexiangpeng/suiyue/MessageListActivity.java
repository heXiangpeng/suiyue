package com.suiyue.hexiangpeng.suiyue;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import adapter.MessageListAdapter;
import dbutil.DataBaseSuiyue;
import dbutil.StaticData;
import domain.User;
import http.FriendList;
import navgationbar.SystemBarTintManager;

/**
 * Created by hexiangpeng on 15/4/14.
 */
public class MessageListActivity extends Activity {


    private DataBaseSuiyue dataBaseSuiyue;
    private SQLiteDatabase db;

    public List<String> usersidcode = new ArrayList<String>();
    public List<String> usersname = new ArrayList<String>();
    public List<String> usersphoto = new ArrayList<String>();


    private Map<String, User> contactList;
    static MessageListAdapter adapter;

    private ListView listMessage;

    PendingIntent pendingIntent;

    NotificationManager manager;


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finishAfterTransition();
//    }


    public static void refresh() {
        adapter.refresh();
    }

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

    private void setStatusStyle() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.navgation);
        // tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.index_top_bg));


    }


    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:


                    Notification notification = new Notification.Builder(MessageListActivity.this)
                            .setSmallIcon(R.drawable.icon)
                            .setTicker("好友请求")
                            .setContentTitle("好友请求")
                            .setContentText("请求添加你为好友")
                            .setContentIntent(pendingIntent).setNumber(1).build();

                    notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
                    manager.notify(1, notification);// 步
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setEnterTransition(new Explode());
        setContentView(R.layout.activity_messagelist);


        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);


        //注册接收消息
        NewMessageBroadcastReciver msgReceiver = new NewMessageBroadcastReciver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);


        //初始化数据库
        dataBaseSuiyue = new DataBaseSuiyue(this);
        db = dataBaseSuiyue.getWritableDatabase();

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Spalash.class), 0);

        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        listMessage = (ListView) findViewById(R.id.list_message);


        EMChat.getInstance().setAppInited();

        adapter = new MessageListAdapter(MessageListActivity.this, 1, db);


        listMessage.setAdapter(adapter);


        listMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Cursor cursor = db.rawQuery("select * from messagelist1", null);
                while (cursor.moveToNext()) {


                    String idc = cursor.getString(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    Log.e("取出的用户", name);
                    String photo = cursor.getString(cursor.getColumnIndex("photo"));
                    Log.e("取出的照片", name);
                    if (!usersidcode.contains(idc)) {
                        usersidcode.add(idc);
                        Log.e("取出的照片1", name);
                        usersname.add(name);
//                            Log.e("取出的用户",name);
                        usersphoto.add(photo);
                    }

                }
                //  }
                Log.e("用户总数", usersidcode.size() + "");
                Intent intent = new Intent(MessageListActivity.this, ContactActivity.class);
                intent.putExtra("msgfromidcode", usersidcode.get(position));
                intent.putExtra("msgfromname", usersname.get(position));
                intent.putExtra("msgfromphoto", usersphoto.get(position));


                startActivity(intent);

            }
        });


        addFriendListener();


    }


    public void insert(String id, String name, String lastmessage, int count, String photo) {

        Cursor cursor = db.query("messagelist1", new String[]{"id"}, "id=?", new String[]{id}, null, null, null);

        ContentValues values = new ContentValues();
        if (cursor.getCount() > 0) {
            Log.e("此", id + "已经在数据苦衷");
            //执行更新

            values.put("lastmessage", lastmessage);
            values.put("count", count);
            db.update("messagelist", values, "id=?", new String[]{id});
        } else {
            Log.e("此", id + "不在数据苦衷");

            //执行添加操作
            values.put("id", id);
            values.put("name", name);//从用户列表中查询某id号的用户昵称
            values.put("lastmessage", lastmessage);
            values.put("count", count);
            values.put("photo", photo);
            db.insert("messagelist1", null, values);

        }

    }


//    public void refresh() {
//        adapter = new MessageListAdapter(MessageListActivity.this, 1, users);
//       listMessage.setAdapter(adapter);
//       adapter.notifyDataSetChanged();
//    }


    @Override
    protected void onStart() {
        super.onStart();


//
        IntentFilter inviteIntentFilter = new IntentFilter(EMChatManager.getInstance().getContactInviteEventBroadcastAction());
        registerReceiver(NewFriendApplyReceiver, inviteIntentFilter);
//

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * 根据最后一条消息的时间排序
     *
     * @param
     */
    private void sortUserByLastChatTime(List<EMContact> contactList) {
        Collections.sort(contactList, new Comparator<EMContact>() {

            @Override
            public int compare(final EMContact user1, final EMContact user2) {
                EMConversation conversation1 = EMChatManager.getInstance().getConversation(user1.getUsername());
                EMConversation conversation2 = EMChatManager.getInstance().getConversation(user2.getUsername());

                EMMessage user2LastMessage = conversation2.getLastMessage();
                EMMessage user1LastMessage = conversation1.getLastMessage();
                if (user2LastMessage.getMsgTime() == user1LastMessage.getMsgTime()) {
                    return 0;
                } else if (user2LastMessage.getMsgTime() > user1LastMessage.getMsgTime()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }


    // 监听好友状态事件
    public void addFriendListener() {
        EMContactManager.getInstance().setContactListener(new EMContactListener() {
            @Override
            public void onContactAdded(List<String> strings) {

            }

            @Override
            public void onContactDeleted(List<String> strings) {

            }

            @Override
            public void onContactInvited(String s, String s2) {

                Message msg = new Message();
                msg.what = 1;
                myHandler.sendMessage(msg);

            }

            @Override
            public void onContactAgreed(String s) {

            }

            @Override
            public void onContactRefused(String s) {

            }
        });
    }

    //注册好友申请监听
    private BroadcastReceiver NewFriendApplyReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //请求理由
            final String reason = intent.getStringExtra("reason");
            final boolean isResponse = intent.getBooleanExtra("isResponse", false);
            //消息发送方username
            final String from = intent.getStringExtra("username");
//            Log.d("好友请求", from + "请求加你为好友,reason: " + reason);

            //sdk暂时只提供同意好友请求方法，不同意选项可以参考微信增加一个忽略按钮。
            if (!isResponse) {
                Message msg = new Message();
                msg.what = 1;
                myHandler.sendMessage(msg);
//
//               Notification notification=new Notification.Builder(MessageListActivity.this)
//                       .setSmallIcon(R.drawable.icon)
//                       .setTicker("好友请求")
//                       .setContentTitle("好友请求")
//                       .setContentText(from+"请求添加你为好友")
//                       .setContentIntent(pendingIntent).setNumber(1).build();
//
//               notification.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。
//               manager.notify(1, notification);// 步
//                Log.d("好友", from + "请求加你为好友,reason: " + reason);
            } else {
//                Log.d("好友", from + "同意了你的好友请求");
            }
            //具体ui上的处理参考chatuidemo。
        }
    };


    private class NewMessageBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String msgId = intent.getStringExtra("msgid");
                //发消息的人的username(userid)
                String msgFrom = intent.getStringExtra("from");
                //消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。

                int msgType = intent.getIntExtra("type", 0);

                //所以消息type实际为是enum类型
                if (msgType != 3) {
                    //  users.add(msgFrom);


                    EMConversation conversation = EMChatManager.getInstance().getConversation(msgFrom);
                    EMMessage lastMessage = conversation.getLastMessage();

                    TextMessageBody txtBody = (TextMessageBody) lastMessage.getBody();
                    //取出消息


//                通过发送人的信息，获取图片地址


                    for (int i = 0; i < StaticData.userInfo.idcode.size(); i++) {
                        if (msgFrom == StaticData.userInfo.idcode.get(i).toString()) {
                            insert(msgFrom, "", txtBody.getMessage().toString(), conversation.getUnreadMsgCount(),
                                    StaticData.userInfo.photo.get(i).toString());
//                        Log.e("插入数据", "插入信息");

                        }

                    }


                    //获取新消息后插入数据库中

                }
                adapter.refresh();
                //adapter.notifyDataSetChanged();

//            Log.e("main", "new message id:" + msgId + " from:" + msgFrom + " type:" + msgType);
                //更方便的方法是通过msgId直接获取整个message
                EMMessage message = EMChatManager.getInstance().getMessage(msgId);

            }catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
