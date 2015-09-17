package com.suiyue.hexiangpeng.suiyue;


import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import java.util.List;


import adapter.ContactListAdapter;

import dbutil.DataBaseSuiyue;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import navgationbar.SystemBarTintManager;
import solvemessage.RemoveLocation;


/**
 * Created by hexiangpeng on 15/4/13.
 */
public class ContactActivity extends SwipeBackActivity implements View.OnClickListener {

    private SwipeBackLayout mSwipeBackLayout;


    private ImageView photo;
    private TextView name;

    private EditText textMessage;
    private Button sendbutton;

   

    private ListView listmessage;

    private ImageView back;

    int widdth;
    int height;


    private DataBaseSuiyue dataBaseSuiyue;
    private SQLiteDatabase db;


    Intent messageListActivity;

    EMConversation conversation;
    List<EMMessage> messages;
    String username;
    String idcode;
    String msgfromphoto;


    newmess msgReceiver;


    static ContactListAdapter adapter;


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

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        EMChatManager.getInstance().resetAllUnreadMsgCount();
    }


    android.os.Handler myhandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.e("insert名字", username);
                    insert(idcode, username, textMessage.getText().toString(), 1, msgfromphoto);
                    textMessage.setText("");

                    MessageListActivity.refresh();


                    adapter.refresh();
                    adapter.notifyDataSetChanged();
                    listmessage.setSelection(adapter.getCount());

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        int[] viewlocation = new int[2];

        textMessage.getLocationOnScreen(viewlocation);

        int x = viewlocation[0];
        int y = viewlocation[1];

        Log.e("输入框的位置", x + ":" + y);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackLayout = getSwipeBackLayout();
        //设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);




        setStatusStyle();

        widdth=this.getWindowManager().getDefaultDisplay().getWidth();
        height=this.getWindowManager().getDefaultDisplay().getHeight();

        setContentView(R.layout.activity_contact);
        messageListActivity = new Intent(ContactActivity.this, MianHome.class);

        dataBaseSuiyue = new DataBaseSuiyue(this);
        db = dataBaseSuiyue.getWritableDatabase();


        initview();


        Intent intent = getIntent();
        idcode = intent.getStringExtra("msgfromidcode");
        username = intent.getStringExtra("msgfromname");
        msgfromphoto = intent.getStringExtra("msgfromphoto");
        name.setText(username);
        ImageLoader.getInstance().loadImage(msgfromphoto, new SimpleImageLoadingListener() {


            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                photo.setImageBitmap(loadedImage);


            }

        });


        sendbutton.setOnClickListener(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(messageListActivity);
                finish();
            }
        });

        //获取所有的聊天纪录


        conversation = EMChatManager.getInstance().getConversation(idcode);
//获取此会话的所有消息
        messages = conversation.getAllMessages();

        messages = RemoveLocation.removeLocation(messages);

        msgReceiver = new newmess();

        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(5);
        registerReceiver(msgReceiver, intentFilter);


        adapter = new ContactListAdapter(idcode, ContactActivity.this);

        listmessage.setAdapter(adapter);
        listmessage.setSelection(adapter.getCount() - 1);


    }


    // Press the back button in mobile phone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.base_slide_right_out);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {


            case R.id.contact_send:

                if (!textMessage.getText().toString().isEmpty()) {

                    Log.e("用户ID", idcode);

                    EMConversation conversation = EMChatManager.getInstance().getConversation(idcode);
//创建一条文本消息
                    EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);

//设置消息body

                    TextMessageBody txtBody = new TextMessageBody(textMessage.getText().toString());
                    message.addBody(txtBody);
//设置接收人
                    message.setReceipt(idcode);
//把消息加入到此会话对象中
                    conversation.addMessage(message);
//发送消息
                    EMChatManager.getInstance().sendMessage(message, new EMCallBack() {
                        @Override
                        public void onSuccess() {

                            Log.e("发送", textMessage.getText().toString());
                            System.out.println(textMessage.getText().toString());


                            MessageListActivity.adapter.refresh();

                            Message message = new Message();
                            message.what = 1;
                            myhandler.sendMessage(message);


                        }

                        @Override
                        public void onError(int i, String s) {

                        }

                        @Override
                        public void onProgress(int i, String s) {

                        }
                    });

//                    listmessage.setSelection(adapter.getCount() - 1);
                    break;


                } else {
                    Toast.makeText(ContactActivity.this, "请输入聊天信息", Toast.LENGTH_SHORT).show();
                }
        }
    }


    public void insert(String id, String name, String lastmessage, int count, String photo) {

        Cursor cursor = db.query("messagelist1", new String[]{"id"}, "id=?", new String[]{id}, null, null, null);

        ContentValues values = new ContentValues();
        if (cursor.getCount() > 0) {
            Log.e("此", id + "已经在数据苦衷");
            //执行更新

            values.put("lastmessage", lastmessage);
            values.put("count", count);
            db.update("messagelist1", values, "id=?", new String[]{id});
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

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("activity不可见", "");
        unregisterReceiver(msgReceiver);

        finish();
    }

    void initview() {
        photo = (ImageView) findViewById(R.id.img_contact_photo);
        name = (TextView) findViewById(R.id.text_contact_name);




        Animation an=new TranslateAnimation(-widdth/2,0,0,0);
        an.setDuration(500);

        photo.setAnimation(an);
        name.setAnimation(an);



        textMessage = (EditText) findViewById(R.id.text_message);


        sendbutton = (Button) findViewById(R.id.contact_send);
        listmessage = (ListView) findViewById(R.id.list_contact);

        back = (ImageView) findViewById(R.id.contact_back);
        an.setStartOffset(500);
        back.setAnimation(an);

    }


    class newmess extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            conversation = EMChatManager.getInstance().getConversation(username);
//获取此会话的所有消息
            messages = conversation.getAllMessages();

            Log.d("监听消息", "监听消息");

            messages = RemoveLocation.removeLocation(messages);


//            adapter=new ContactListAdapter(messages);
//            listmessage.setAdapter(adapter);
            //adapter.notifyDataSetChanged();
//            adapter.notifyDataSetChanged();
            adapter.refresh();
            listmessage.setSelection(adapter.getCount() - 1);
        }
    }
}
