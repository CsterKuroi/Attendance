package com.example.jogle.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import java.io.File;


public class Main2Activity extends Activity {
    public static DataSet dataSet;
    private TextView time;
    private AnimationSet animationSet;
    private LocationClient mLocationClient = null;
    private Button b4;
    private Button b5;
    private TextView t12;
    private TextView t15;
    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {
                dataSet.setLatitude(location.getLatitude());
                dataSet.setLongitude(location.getLongitude());
                dataSet.setTime(location.getTime());
                time.setText(location.getTime());
                if (dataSet.getPosDescription() == null) {
                    dataSet.setPosDescription(location.getAddrStr());
                    TextView addr = (TextView) findViewById(R.id.address);
                    addr.setText(dataSet.getPosDescription());
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Register Baidu Map SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main2);
        // Set up animation
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation());
        animationSet.addAnimation(disappearAnimation());
        // refresh dataSet
        if (dataSet == null)
            dataSet = new DataSet();
        time = (TextView) findViewById(R.id.time);
        t12 = (TextView) findViewById(R.id.textView12);
        t15 = (TextView) findViewById(R.id.textView15);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        // Set up Baidu Location Listener
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        TextView alarm = (TextView) findViewById(R.id.setalarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });
        TextView addr = (TextView) findViewById(R.id.address);
        addr.setText(dataSet.getPosDescription());
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b4.startAnimation(animationSet);
                DBOperation operation = new DBOperation(getApplicationContext());
                operation.save(dataSet);
                t12.setText(dataSet.getTime());
                Intent intent = new Intent(Main2Activity.this, ListActivity.class);
                startActivity(intent);
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b4.startAnimation(animationSet);
                DBOperation operation = new DBOperation(getApplicationContext());
                operation.save(dataSet);
                t15.setText(dataSet.getTime());
                Intent intent = new Intent(Main2Activity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }
    private void intentToLocate() {
        Intent intent = new Intent(Main2Activity.this, LocateActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showCancelDialog();
            return false;
        }
        return false;
    }
    protected Animation scaleAnimation()
    {
        ScaleAnimation animationScale = new ScaleAnimation(1f, 2f, 1f, 2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationScale.setInterpolator(new AccelerateInterpolator());
        animationScale.setDuration(500);
        animationScale.setFillAfter(false);
        return animationScale;
    }
    protected Animation disappearAnimation()
    {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(500);//设置动画持续时间
        return animation;
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        mLocationClient.setLocOption(option);
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否取消此次考勤打卡？")
                .setPositiveButton("否", null)
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dataSet.getPicName() != null) {
                            File pic = new File(dataSet.getPicPath());
                            if (pic.exists()) {
                                pic.delete();
                            }
                        }
                        dataSet = new DataSet();
                        Intent intent = new Intent(Main2Activity.this, entry.class);
                        startActivity(intent);
                    }
                }).show();
    }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
