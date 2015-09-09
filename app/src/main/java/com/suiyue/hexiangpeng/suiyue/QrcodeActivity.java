package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import zxing.encoding.EncodingHandler;

/**
 * Created by hexiangpeng on 15/5/3.
 */
public class QrcodeActivity extends Activity {
    private ImageView qrcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        qrcode=(ImageView) findViewById(R.id.iv_qr_image);

        SharedPreferences userinfo=getSharedPreferences("userinfo",0);

        String name=userinfo.getString("name","");

        String code="suiyue:"+name;


        Log.e("生成的：",code);

        try {
            Bitmap qrCodeBitmap = EncodingHandler.createQRCode(code, 500);
            qrcode.setImageBitmap(qrCodeBitmap);
        }catch (Exception e){
            System.out.println(e);
        }






    }


}
