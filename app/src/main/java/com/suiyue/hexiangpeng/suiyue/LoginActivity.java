package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.umeng.analytics.MobclickAgent;

import gps.GetGPS;
import http.Http;
import http.ParserJson;
import http.UserInfo;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,AMapLocationListener{

    private LocationManagerProxy mLocationManagerProxy;

    private EditText username;
    private EditText passwd;
    private Button login;
    private Button register;
    private Dialog dialog;

    private Http http=new Http();

    private TextInputLayout textInputLayout;

  Handler myhandler=new Handler() {
      @Override
      public void handleMessage(Message msg) {
          super.handleMessage(msg);
          switch (msg.what){
              case 2:
                  dialog.cancel();
                  Intent mainactivity=new Intent(LoginActivity.this,MianHome.class);
                  startActivity(mainactivity);
                  break;
              case 3:
                  dialog.cancel();



                  Toast.makeText(LoginActivity.this,"用户名或密码有误",Toast.LENGTH_SHORT).show();
                  break;
              case 4:
                  Toast.makeText(LoginActivity.this,"登录错误,用户名或密码有误",Toast.LENGTH_SHORT).show();
                  break;
              default:
                  break;
          }
      }
  };


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finishAfterTransition();
//    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("login定位","124");
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if(aMapLocation != null && aMapLocation.getAMapException().getErrorCode() == 0){
            //获取位置信息
            Double geoLat = aMapLocation.getLatitude();
            Double geoLng = aMapLocation.getLongitude();

            Log.e("login定位",geoLat+":"+geoLng);
        }
    }



    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow()setEnterTransition(new Explode());

        setContentView(R.layout.activity_login);



        initview();


        login.setOnClickListener(this);

        register.setOnClickListener(this);

    }

    private void init() {
        mLocationManagerProxy = LocationManagerProxy.getInstance(this);
        //此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        //注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
        //在定位结束后，在合适的生命周期调用destroy()方法
        //其中如果间隔时间为-1，则定位只定一次
        mLocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork, 60*1000, 15, this);
        mLocationManagerProxy.setGpsEnable(false);
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

    public void initview(){
//        textInputLayout=(TextInputLayout) findViewById(R.id.pwd);
        username=(EditText)findViewById(R.id.edit_username);
        passwd= (EditText)findViewById(R.id.edit_passwd);
        login=(Button)findViewById(R.id.btn_login);
        register=(Button)findViewById(R.id.btn_register);
    }

//    添加监听
   public void onClick(View v){
       int id=v.getId();

       switch (id){
           case R.id.btn_login:

               if (username.getText().toString().isEmpty() || passwd.getText().toString().isEmpty()){

                   Toast.makeText(this,"用户名或密码为空",Toast.LENGTH_SHORT).show();
               }else{
                   login(username.getText().toString(),passwd.getText().toString());
//                   finish();
               }


               break;
           case R.id.btn_register:

               Intent regis=new Intent(LoginActivity.this,RegisterFragments.class);
               startActivity(regis);
               finish();


               break;
       }

   }

    public void login(final String name, final String passwd){
        dialog = new Dialog(LoginActivity.this,R.style.mydialogstyle);
        dialog.setContentView(R.layout.progresslayout);//此处布局为一个progressbar
        dialog.setCancelable(true); // 可以取消
        dialog.show();
        EMChatManager.getInstance().login(name,passwd,new EMCallBack() {
            @Override
            public void onSuccess() {

                EMGroupManager.getInstance().loadAllGroups();
                EMChatManager.getInstance().loadAllConversations();


//               获取个人信息


                ParserJson pa=new ParserJson();
               String re= http.GetUserInfo(name);
               UserInfo userInf = pa.parserUser(re);



                if(userInf.name.size()>0) {
                    SharedPreferences userinfo = getSharedPreferences("userinfo", 0);
                    userinfo.edit().putString("name", name).commit();
                    userinfo.edit().putString("pass", passwd).commit();
                    userinfo.edit().putString("realname", userInf.name.get(0)).commit();
                    userinfo.edit().putString("photo", userInf.photo.get(0)).commit();
                    userinfo.edit().putString("location", userInf.location.get(0)).commit();

                    Message message = new Message();
                    message.what = 2;
                    myhandler.sendMessage(message);
                }else {
                    Message message = new Message();
                    message.what = 4;
                    myhandler.sendMessage(message);
                }

//                RoundedBitmapDrawable aa= RoundedBitmapDrawableFactory.create(getResources(),r);
//                aa.setCornerRadius(100);
//                aa.setAntiAlias(true);
            }

            @Override
            public void onError(int i, String s) {
                Message message = new Message();
                message.what =3;
                myhandler.sendMessage(message);

            }

            @Override
            public void onProgress(int i, String s) {

            }
        });

    }

}
