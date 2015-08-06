package com.example.jogle.attendance;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
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
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Main2Activity extends Activity implements Runnable{
    public static DataSet dataSet1;
    public static DataSet dataSet2;
    private TextView time;
    private AnimationSet animationSet;
    private LocationClient mLocationClient = null;
    private Button b4;
    private Button b5;
    private RoundImageView view7;
    private RoundImageView view9;
    private TextView t12;
    private TextView t35;
    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {

                dataSet1.setLatitude(location.getLatitude());
                dataSet1.setLongitude(location.getLongitude());
                dataSet2.setLatitude(location.getLatitude());
                dataSet2.setLongitude(location.getLongitude());
                if (dataSet1.getPosition() == null) {
                    dataSet1.setPosition(location.getAddrStr());
                    TextView addr = (TextView) findViewById(R.id.address);
                    addr.setText(dataSet1.getPosition());
                }
                if (dataSet2.getPosition() == null) {
                    dataSet2.setPosition(location.getAddrStr());
                }
            }
        }
    };
    public Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            dataSet1.setTime((String) msg.obj);
            dataSet2.setTime((String) msg.obj);
            time.setText((String) msg.obj);
        }
    };
    private static final int CAPTURE_REQUEST_CODE1 = 101;
    private static final int CAPTURE_REQUEST_CODE2 = 102;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_REQUEST_CODE1) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    dataSet1.generateThumbnail();
                    view7.setImageBitmap(dataSet1.getThumbnail());
                    view7.setClickable(false);
                    b4.setClickable(true);
                    b4.setBackgroundColor(0xff01aff4);
                    view7.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Main2Activity.this, ShowActivity.class);
                            intent.putExtra("pic_path", dataSet1.getPicPath());
                            startActivity(intent);
                        }
                    });
                    break;
                case Activity.RESULT_CANCELED:
                    dataSet1.setTimeStamp(null);
                    break;
            }
        }
        else if (requestCode == CAPTURE_REQUEST_CODE2) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    dataSet2.generateThumbnail();
                    view9.setImageBitmap(dataSet2.getThumbnail());
                    view9.setClickable(false);
                    b5.setClickable(true);
                    b5.setBackgroundColor(0xff01aff4);
                    view9.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Main2Activity.this, ShowActivity.class);
                            intent.putExtra("pic_path", dataSet2.getPicPath());
                            startActivity(intent);
                        }
                    });
                    break;
                case Activity.RESULT_CANCELED:
                    dataSet2.setTimeStamp(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void run() {
        while (true) {
            try {
                URL url = new URL("http://www.baidu.com");// 取得资源对象 
                URLConnection uc = url.openConnection();// 生成连接对象    
                uc.connect(); // 发出连接 
                long ldate = uc.getDate(); // 取得网站日期时间（时间戳）
                Date date = new Date(ldate);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                String dateStr = sdf.format(c.getTime());
                Message message = new Message();
                message.obj = dateStr;
                handler.sendMessage(message);
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
        if (dataSet1 == null) {
            dataSet1 = new DataSet();
            dataSet1.setType(1);
        }
        if (dataSet2 == null) {
            dataSet2 = new DataSet();
            dataSet2.setType(2);
        }
        time = (TextView) findViewById(R.id.time);
        if (dataSet1.getTime() != null)
            time.setText(dataSet1.getTime());
        new Thread(this).start();

        t12 = (TextView) findViewById(R.id.textView12);
        t35 = (TextView) findViewById(R.id.textView35);
        b4 = (Button) findViewById(R.id.button4);
        b5 = (Button) findViewById(R.id.button5);
        view7 = (RoundImageView) findViewById(R.id.imageView7);
        view9 = (RoundImageView) findViewById(R.id.imageView9);

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
        addr.setText(dataSet1.getPosition());
        addr.setText(dataSet2.getPosition());

        if (dataSet1.getTimeStamp() == null) {
            b4.setClickable(false);
            b4.setBackgroundColor(0xffdddddd);
            view7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view7.startAnimation(animationSet);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                    Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE1);
                }

                public Uri getOutputMediaFileUri() {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Attendance");
                    // This location works best if you want the created images to be shared
                    // between applications and persist after your app has been uninstalled.
                    // Create the storage directory if it does not exist
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            return null;
                        }
                    }
                    // Create a media file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File mediaFile;
                    dataSet1.setTimeStamp(timeStamp);
                    mediaFile = new File(dataSet1.getPicPath());
                    return Uri.fromFile(mediaFile);
                }
            });
        }

        if (dataSet2.getTimeStamp() == null) {
            b5.setClickable(false);
            b5.setBackgroundColor(0xffdddddd);
            view9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view9.startAnimation(animationSet);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机
                    Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
                    // start the image capture Intent
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE2);
                }

                public Uri getOutputMediaFileUri() {
                    // To be safe, you should check that the SDCard is mounted
                    // using Environment.getExternalStorageState() before doing this.
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "Attendance");
                    // This location works best if you want the created images to be shared
                    // between applications and persist after your app has been uninstalled.
                    // Create the storage directory if it does not exist
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            return null;
                        }
                    }
                    // Create a media file name
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    File mediaFile;
                    dataSet2.setTimeStamp(timeStamp);
                    mediaFile = new File(dataSet2.getPicPath());
                    return Uri.fromFile(mediaFile);
                }
            });

        }

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //b4.startAnimation(animationSet);
                DBOperation operation = new DBOperation(getApplicationContext());
                operation.save(dataSet1);
                t12.setText(dataSet1.getTime().substring(dataSet1.getTime().length() - 8));
                b4.setBackgroundColor(0xffdddddd);
                b4.setClickable(false);
                b4.setText("已签到");
            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //b5.startAnimation(animationSet);
                DBOperation operation = new DBOperation(getApplicationContext());
                operation.save(dataSet2);
                t35.setText(dataSet2.getTime().substring(dataSet2.getTime().length() - 8));
                b5.setBackgroundColor(0xffdddddd);
                b5.setClickable(false);
                b5.setText("已签退");
            }
        });

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

    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        super.onDestroy();
    }
}
