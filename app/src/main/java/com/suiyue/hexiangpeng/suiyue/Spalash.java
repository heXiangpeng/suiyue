package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

/**
 * Created by hexiangpeng on 15/5/4.
 */
public class Spalash extends Activity {
    private final int SPLASH_DISPLAY_LENGHT = 1000; //延迟三秒

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.spalash);


        new Handler().postDelayed(new Runnable(){



            @Override
            public void run() {
                SharedPreferences userinfo=getSharedPreferences("userinfo",0);

                String name=userinfo.getString("name","");
                if(name.isEmpty()) {


                    Intent mainIntent = new Intent(Spalash.this, LoginActivity.class);
                    startActivity(mainIntent);
                }else{
                    Intent mainIntent = new Intent(Spalash.this, MianHome.class);
                    startActivity(mainIntent);
                }
                finish();

            }

        }, SPLASH_DISPLAY_LENGHT);
    }
}
