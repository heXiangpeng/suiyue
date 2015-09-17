package com.suiyue.hexiangpeng.suiyue;

import android.app.Fragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by hexiangpeng on 15/9/10.
 */
public class GetVerficationCodeFragment extends Fragment {
    private View view;

    private boolean ischeck = false;

    private Context context;

    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "a22902ce76da";

    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "1bf5ddfb9103326e3692b5e00e842fb7";

    private EditText phonenumber;
    private EditText code;

    private TextView getcode;
    private LinearLayout vercode; //校验验证码

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);


            if (result == SMSSDK.RESULT_COMPLETE) {
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功

                    FragmentManager fm = getFragmentManager();


                    UploadInfo uploadInfo=new UploadInfo();

                    FragmentTransaction ft = fm.beginTransaction();
                    Bundle bundle=new Bundle();
                    bundle.putString("phone",phonenumber.getText().toString());
                    uploadInfo.setArguments(bundle);
//                    ft.setCustomAnimations(R.anim.base_slide_remain,R.anim.base_slide_right_in);

                    ft.add(R.id.regist_content,uploadInfo,"uploadinfo");
//                    ft.replace(R.id.regist_content, uploadInfo);
//                    ft.addToBackStack("tag");
                    ft.commit();



                    ischeck = true;

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(context, "验证码已经发送", Toast.LENGTH_SHORT).show();
//                    textView2.setText("验证码已经发送");
                } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {//返回支持发送验证码的国家列表
//                    Toast.makeText(getApplicationContext(), "获取国家列表成功", Toast.LENGTH_SHORT).show();
//                    countryTextView.setText(data.toString());

                }
            } else {
                ((Throwable) data).printStackTrace();

                ischeck = false;

             //   Toast.makeText(context, "验证码错误", Toast.LENGTH_SHORT).show();

            }

        }

    };


    public Boolean getIscheck() {
        return ischeck;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.verificationcode, null);
        initview();

        context = this.getActivity();


        //验证码验证


        SMSSDK.initSDK(context, APPKEY, APPSECRET);

        addListener();

        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);


        return view;
    }

    public void initview() {
        phonenumber = (EditText) view.findViewById(R.id.ver_phonenumber);
        code = (EditText) view.findViewById(R.id.ver_code);
        getcode = (TextView) view.findViewById(R.id.ver_getcode);
        vercode = (LinearLayout) view.findViewById(R.id.linear_judgecode);
    }

    public void addListener() {
        vercode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (code.getText().toString().length() == 4) {

                    SMSSDK.submitVerificationCode("86", phonenumber.getText().toString(), code.getText().toString());

                } else {
                    Toast.makeText(context, "验证码输入错误", Toast.LENGTH_SHORT).show();

                }

            }
        });

        phonenumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (phonenumber.getText().length() > 10) {
                    getcode.setTextColor(context.getResources().getColor(R.color.green));
                } else {
                    getcode.setTextColor(context.getResources().getColor(R.color.grey));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        getcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phonenumber.getText().toString().length() >10) {
                    SMSSDK.getVerificationCode("86", phonenumber.getText().toString());
                } else {
                    Toast.makeText(context, "电话号码有误", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }


}
