package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.easemob.chat.EMChatManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import adapter.SetAdapter;
import dbutil.DataBaseSuiyue;
import navgationbar.SystemBarTintManager;

/**
 * Created by hexiangpeng on 15/4/14.
 */
public class SetActivity extends Activity implements View.OnClickListener {

    private ListView listview;
    private SetAdapter adapter;

    private TextView setname;


    private ImageView photo;




    private DataBaseSuiyue dataBaseSuiyue;
    private SQLiteDatabase db;


    private String[] mDataset={"附近消息","好友圈","我的二维码","关于","注销"};
    private int[] img={R.drawable.near,R.drawable.friend,R.drawable.qrcode,R.drawable.about,R.drawable.exit};


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finishAfterTransition();
//    }

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
//        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
//        getWindow().setEnterTransition(new Explode());

        setContentView(R.layout.activity_set);


        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);

        dataBaseSuiyue=new DataBaseSuiyue(this);
        db=dataBaseSuiyue.getWritableDatabase();

//        setStatusStyle();
        dataBaseSuiyue=new DataBaseSuiyue(SetActivity.this);
        db=dataBaseSuiyue.getWritableDatabase();

        listview=(ListView) findViewById(R.id.my_recycler_view);

        photo=(ImageView) findViewById(R.id.set_userphoto);
        SharedPreferences userinfo=getSharedPreferences("userinfo",0);

        String name=userinfo.getString("realname","");

        String photour=userinfo.getString("photo","");

        ImageLoader.getInstance().loadImage("http://suiyue520.sinaapp.com/"+photour, new SimpleImageLoadingListener(){



            @Override
            public void onLoadingComplete(String imageUri, View view,
                                          Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
               photo.setImageBitmap(loadedImage);



            }

        });






        initview();
        setname.setText(name);



        adapter=new SetAdapter(mDataset,img);
        listview.setAdapter(adapter);

        Animation animation=(Animation) AnimationUtils.loadAnimation(SetActivity.this,R.anim.list_anim);


        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setDelay(0.3f);  //设置动画间隔时间
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL); //设置列表的显示顺序

        listview.setLayoutAnimation(lac);


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("list",position+"点击了"+id);
                if (position==1){
                    Intent friendSpace=new Intent(SetActivity.this,FriendSpaceActivity.class);



                    startActivity(friendSpace);
                }

                if (position==2){
                    Intent qrcode=new Intent(SetActivity.this,QrcodeActivity.class);
                    startActivity(qrcode);
                }


                if (position==4){

                    SharedPreferences userinfo=getSharedPreferences("userinfo",0);
                    userinfo.edit().clear().commit();

//                    清空数据库


                    db.delete("messagelist1",null,null);

                    EMChatManager.getInstance().logout();//此方法为同步方法
                    System.exit(0);
                }

            }
        });
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()){


        }

    }

    public void initview(){
        setname=(TextView) findViewById(R.id.text_setname);

    }
}
