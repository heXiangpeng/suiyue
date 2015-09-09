package com.suiyue.hexiangpeng.suiyue;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.widget.Toast;


import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.LocationMessageBody;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import adapter.FriendListAdapter;
import adapter.FriendSpaceAdapter;
import dbutil.StaticData;
import http.AllFriend;
import http.Friendtype;
import http.Http;
import http.ParserJson;
import http.UserInfo;
import navgationbar.SystemBarTintManager;
import utils.ComputeDistance;
import zxing.activity.CaptureActivity;

/**
 * Created by hexiangpeng on 15/4/13.
 * 监听位置变化，给所有好友发送自己的位置信息
 * <p/>
 * 监听消息请求，解析好友的位置，计算相应的距离
 * <p/>
 * 经纬度存储于服务器端，定时请求好友的经纬度
 */
public class FriendListActivity extends Activity {


    private ParserJson parserJson = new ParserJson();

    private SwipeRefreshLayout swipeRefreshLayout;


    private LayoutInflater inflater;
    private ImageView imgpop;
    private View popview;
    private PopupWindow pop;
    String name;

    double latitude = 0;
    double longitude = 0;


    private LinearLayout scan;
    private LinearLayout addfriend;

    public static UserInfo username;

    private ListView friendlist;


    private LocationManager lm;

    Location location;

    private Http http = new Http();

    EMConversation conversationLocation;
    List<EMMessage> locationatitude;

    List<String> dist;

    private List<EMMessage> locationDistanse = new ArrayList<>();

    private static final String TAG = "GpsActivity";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        lm.removeUpdates(locationListener);
    }

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

    private void setStatusStyle() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }

        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.color.navgation);
        // tintManager.setStatusBarTintDrawable(getResources().getDrawable(R.drawable.index_top_bg));


    }


    Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.e("接收", "接收");
                    FriendListAdapter friendlistadapter = new FriendListAdapter(username);

                    friendlist.setAdapter(friendlistadapter);

                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_friendlist);


        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);

        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);


        SharedPreferences userinfo = getSharedPreferences("userinfo", 0);

        name = userinfo.getString("name", "");

        gethttp(name);


        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        inflater = LayoutInflater.from(this);


        initview();


        addlistener();


//        定位实现

        String bestProvider = lm.getBestProvider(getCriteria(), true);

        location = lm.getLastKnownLocation(bestProvider);


        updateView(location);


//            lm.addGpsStatusListener(listener);


        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 1, locationListener);//10秒刷新一次gps纪录

//          lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1,locationListener); //网络定位


//        定位结束


        NewLocationBroadcastReciver msgReceiver = new NewLocationBroadcastReciver();

        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
    }


    public void gethttp(final String name) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = http.getAllFriend(name);

                if (result != null) {
                    Log.e("获取结果", result);

                    username = parserJson.parserUserInfo(result);
                    StaticData.userInfo = username;

                    String selfdis[] = {"0", "0"};
                    for (int i = 0; i < username.location.size(); i++) {


                        Log.e("名字", username.idcode.get(i));
                        if (username.idcode.get(i).contains(name)) {

                            Log.e("名字相同：", username.idcode.get(i).toString());
                            selfdis = username.location.get(i).toString().split(":");
                        }
                    }

                    dist = new ArrayList<String>();
                    Log.e("请求的人数：", username.location.size() + "");
                    for (int i = 0; i < username.location.size(); i++) {
                        String dis = username.location.get(i);

                        String distance;

                        if (!dis.isEmpty()) {
                            String di[] = dis.split(":");
                            Log.e("请求的数据：", dis);

                            if (longitude != 0.0) {


                                distance = ComputeDistance.GetDistance(latitude, longitude, Double.valueOf(di[0]).doubleValue(), Double.valueOf(di[1]).doubleValue());

                                Log.e("ad12", longitude + "");
                            } else {
                                Log.e("addd", selfdis[0]);

                                distance = ComputeDistance.GetDistance(Double.valueOf(selfdis[0]).doubleValue(), Double.valueOf(selfdis[1]).doubleValue(), Double.valueOf(di[0]).doubleValue(), Double.valueOf(di[1]).doubleValue());

                            }

                            Log.e("address", longitude + "");

                            dist.add(distance);

                        } else {
                            dist.add("");
                        }
                    }

                    username.distance = dist;


                    Message message = new Message();
                    message.what = 1;
                    myhandler.sendMessage(message);
                }
            }
        }).start();
    }


    //取出扫描结果，且提交好友请求
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString("result");
            Log.e("扫描结果：", scanResult);

            final String toAddUsername = scanResult.split(":")[1];


            //参数为要添加的好友的username和添加理由
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMContactManager.getInstance().addContact(toAddUsername, "扫一扫添加");//需异步处

                    } catch (Exception e) {
                        System.out.println("添加好友产生错误" + e);
                    }

                }
            }).start();


        }
    }

    public void initview() {

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.friendlist_pullrefresh);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 停止刷新
                        gethttp(name);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000); // 5秒后发送消息，停止刷新

            }
        });

        swipeRefreshLayout.setDistanceToTriggerSync(400);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        swipeRefreshLayout.setProgressBackgroundColor(R.color.white); // 设定下拉圆圈的背景
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE); // 设置圆圈的大小
        friendlist = (ListView) findViewById(R.id.listview_friendlist);
        imgpop = (ImageView) findViewById(R.id.img_more);

        popview = inflater.inflate(R.layout.friendpopupwindow, null);

        scan = (LinearLayout) popview.findViewById(R.id.linear_scan);
        addfriend = (LinearLayout) popview.findViewById(R.id.linear_addfriend);

        pop = new PopupWindow(popview, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, false);
        pop.setBackgroundDrawable(new BitmapDrawable());
        //设置点击窗口外边窗口消失
        pop.setOutsideTouchable(true);
        // 设置此参数获得焦点，否则无法点击
        pop.setFocusable(true);

    }


    private class NewLocationBroadcastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msgId = intent.getStringExtra("msgid");
            //发消息的人的username(userid)
            String msgFrom = intent.getStringExtra("from");
            //消息类型，文本，图片，语音消息等,这里返回的值为msg.type.ordinal()。

            int msgType = intent.getIntExtra("type", 0);


            Log.e("loca", "new message id:" + msgId + " from:" + msgFrom + " type:" + msgType);
            //更方便的方法是通过msgId直接获取整个message
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);


        }
    }


    private void updateView(Location location) {

        if (location != null) {

//          保存用户自己的地址位置
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            final String address = latitude + ":" + longitude;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    SharedPreferences userinfo = getSharedPreferences("userinfo", 0);

                    String name = userinfo.getString("name", "");
                    http.UpdateLocation(name, address);

                }
            }).start();


        } else {
            //清空EditText对象
            Log.e("", "无法获取地理位置");


        }
    }


    //位置监听
    private LocationListener locationListener = new LocationListener() {

        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location) {
            updateView(location);
            System.out.println("shijian");

            Log.i(TAG, "时间：" + location.getTime());
            Log.i(TAG, "经度：" + location.getLongitude());
            Log.i(TAG, "纬度：" + location.getLatitude());
            Log.i(TAG, "海拔：" + location.getAltitude());
        }

        /**
         * GPS状态变化时触发
         */
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                //GPS状态为可见时

                case LocationProvider.AVAILABLE:
                    Log.i(TAG, "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:
                    Log.i(TAG, "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.i(TAG, "当前GPS状态为暂停服务状态");

                    Toast.makeText(FriendListActivity.this, "gps暂停服务", Toast.LENGTH_SHORT).show();
                    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1, locationListener); //网络定位
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider) {
            Location location = lm.getLastKnownLocation(provider);
            updateView(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider) {
            updateView(null);
        }


    };


    GpsStatus.Listener listener = new GpsStatus.Listener() {

        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
//                    Log.i(TAG, "第一次定位");

                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
//                    Log.i(TAG, "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus = lm.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星

                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }


                    System.out.println("搜索到：" + count + "颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
//                    Log.i(TAG, "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
//                    Log.i(TAG, "定位结束");
                    break;
            }
        }
    };

    /**
     * 返回查询条件
     *
     * @return
     */
    private Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        //   criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);


        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }


    public void addlistener() {


        friendlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent contact = new Intent(FriendListActivity.this, ContactActivity.class);
                contact.putExtra("msgfromidcode", username.idcode.get(position));
                contact.putExtra("msgfromname", username.name.get(position));

                contact.putExtra("msgfromphoto", "http://suiyue520.sinaapp.com/" + username.photo.get(position));
                startActivity(contact);
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openCameraIntent = new Intent(FriendListActivity.this, CaptureActivity.class);


                startActivityForResult(openCameraIntent, 0);
            }
        });


        addfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openaddfriend = new Intent(FriendListActivity.this, AddNewFriendActivity.class);


                startActivity(openaddfriend);

            }
        });

        imgpop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pop.isShowing()) {
                    // 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
                    pop.dismiss();
                } else {
                    // 显示窗口
                    pop.showAsDropDown(v);


                }
            }
        });


    }
}
