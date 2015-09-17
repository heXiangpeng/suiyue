package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import com.umeng.analytics.MobclickAgent;


/**
 * Created by hexiangpeng on 15/9/10.
 *
 * 注册页面集合
 */
public class RegisterFragments extends Activity {

    private GetVerficationCodeFragment getVerficationCodeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registerfragment);
        setFragment();


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


    void setFragment(){
        FragmentManager fm=getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction=fm.beginTransaction();

        getVerficationCodeFragment=new GetVerficationCodeFragment();


        Boolean ischeck=getVerficationCodeFragment.getIscheck();


            fragmentTransaction.replace(R.id.regist_content, getVerficationCodeFragment);


        fragmentTransaction.commit();





    }
}
