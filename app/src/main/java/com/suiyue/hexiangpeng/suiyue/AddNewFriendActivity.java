package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;

import android.text.TextWatcher;

import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.view.View.OnKeyListener;
import android.view.View.OnClickListener;
import android.widget.Toast;


import com.easemob.chat.EMContactManager;

import adapter.AddNewFriendAdapter;
import dbutil.StaticData;
import http.FriendList;
import http.Http;
import http.ParserJson;
import http.UserInfo;
import navgationbar.SystemBarTintManager;


/**
 * Created by hexiangpeng on 15/6/9.
 *
 *
 *
 * 搜索好友  点击之后查询数据库是否有相似的用户
 */
public class AddNewFriendActivity extends Activity  {

    private SearchView sv;


    private Http http;
    private ParserJson parserJson;
    private FriendList friendList;

    private int indexofIdcode=0;


    private AddNewFriendAdapter addNewFriendAdapter;


    private ListView lv;

    private EditText editTextSearch;

    private ImageView clearText;

    SelectPicPopupWindow menuWindow;



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

    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:

                    addNewFriendAdapter=new AddNewFriendAdapter(friendList);
                    lv.setAdapter(addNewFriendAdapter);
                    addNewFriendAdapter.notifyDataSetChanged();
                    break;

                case 10:


                    Toast.makeText(AddNewFriendActivity.this,"好友请求成功！",Toast.LENGTH_SHORT).show();



                    break;

                default:

                    break;



            }

//            Toast.makeText(AddNewFriendActivity.this,"已经是你的好友",Toast.LENGTH_SHORT).show();
        }
    };






    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
//        setStatusStyle();
        setContentView(R.layout.activity_addnewfriend);



        editTextSearch=(EditText) findViewById(R.id.search_et_input);

        clearText=(ImageView)findViewById(R.id.search_iv_delete);

        Log.e("好友个数",StaticData.userInfo.idcode.size()+"");


        http = new Http();
        parserJson = new ParserJson();
        friendList = new FriendList();

        lv = (ListView) findViewById(R.id.list_searchresult);
        //lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,mStrings));
        lv.setTextFilterEnabled(true);

       lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
          @Override
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              menuWindow = new SelectPicPopupWindow(AddNewFriendActivity.this, onClickListener);

              //显示窗口

              indexofIdcode=position;

              menuWindow.showAtLocation(AddNewFriendActivity.this.findViewById(R.id.addnewfriend_main), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
          }
      });

        clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.setText("");

                clearText.setVisibility(View.GONE);
            }
        });


        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int textLength = editTextSearch.getText().length();

                if (textLength > 0) {

                   clearText.setVisibility(View.VISIBLE);

                } else {

                    clearText.setVisibility(View.GONE);

                }

            }
        });


        editTextSearch.setOnKeyListener(new OnKeyListener() {




            @Override
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {


                if (keyCode == KeyEvent.KEYCODE_ENTER) {


                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String re=http.searchFriend(editTextSearch.getText().toString());
                           friendList = parserJson.parserFriendData(re);
                            Log.e("搜索结果",re);
//                            friendList =clearFriend(friendList,StaticData.userInfo);
                            Message msg=new Message();
                            msg.what=1;
                            myHandler.sendMessage(msg);
                        }
                    }).start();


                }

                return false;

            }

        });





    }


    private OnClickListener onClickListener=new OnClickListener() {


        @Override
        public void onClick(View v) {

            menuWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_take_addfriend:
                    //加好友


                    SharedPreferences userinfo=getSharedPreferences("userinfo",0);

                    final String name=userinfo.getString("name","");

                    if (!StaticData.userInfo.idcode.contains(friendList.idcode.get(indexofIdcode).toString())) {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    String re = http.addfriend(name, friendList.idcode.get(indexofIdcode).toString());
                                    String re1 = http.addfriend(friendList.idcode.get(indexofIdcode).toString(), name);
                                    EMContactManager.getInstance().addContact(friendList.idcode.get(indexofIdcode).toString(), "通过账号查找");//需异步处理

                                    Log.d("好友申请", "好友请求申请");

                                    if (re == "suc") {
                                        Message msg = new Message();
                                        msg.what = 10;
                                        myHandler.sendMessage(msg);
                                    }


                                } catch (Exception e) {

                                }

                            }
                        }).start();
                    }else{
                        Toast.makeText(AddNewFriendActivity.this,"已经是你的好友",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.btn_cancel:
                    break;
                default:
                    break;
            }
        }
    };


}
