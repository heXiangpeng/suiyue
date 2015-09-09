package com.suiyue.hexiangpeng.suiyue;

import android.app.ActivityManager;
import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;


import com.easemob.chat.EMChat;

import java.util.Iterator;
import java.util.List;

/**
 * Created by hexiangpeng on 15/4/15.
 */
public class SuiYue extends Application {



    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("","初始化");

        int pid = android.os.Process.myPid();

        String processAppName = getAppName(pid);

        if (processAppName == null ||!processAppName.equalsIgnoreCase("com.suiyue.hexiangpeng.suiyue")) {
            Log.e("服务", "enter the service process!");
            //"com.easemob.chatuidemo"为demo的包名，换到自己项目中要改成自己包名

            // 则此application::onCreate 是被service 调用的，直接返回
            return;
        }


        EMChat.getInstance().init(this);







    }






    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);


        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();


        PackageManager pm = this.getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
                    // Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
                    // info.processName +"  Label: "+c.toString());
                    // processName = c.toString();
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return processName;
    }

}
