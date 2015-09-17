package com.suiyue.hexiangpeng.suiyue;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;
import zxing.encoding.EncodingHandler;

/**
 * Created by hexiangpeng on 15/5/3.
 */
public class QrcodeActivity extends SwipeBackActivity {
    private ImageView qrcode;
    private SwipeBackLayout mSwipeBackLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSwipeBackLayout = getSwipeBackLayout();
        //设置滑动方向，可设置EDGE_LEFT, EDGE_RIGHT, EDGE_ALL, EDGE_BOTTOM
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
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
