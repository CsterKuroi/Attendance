package com.example.jogle.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends Activity {
    public static DataSet dataSet;
    private TextView time;
    private RoundImageView shotButton;
    private ImageButton editcustomer;
    private ImageButton editpos;
    private ImageButton finishButton;
    private ImageButton cancelButton;
    private AnimationSet animationSet;
    private LocationClient mLocationClient = null;
    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null)
                return;

            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {
                // Get data

                dataSet.setLatitude(location.getLatitude());
                dataSet.setLongitude(location.getLongitude());
                dataSet.setTime(location.getTime());
                time.setText(location.getTime());
                if (dataSet.getPicName() != null)
                    finishButton.setVisibility(View.VISIBLE);

                if (dataSet.getPosDescription() == null) {
                    dataSet.setPosDescription(location.getAddrStr());
                    TextView addr = (TextView) findViewById(R.id.address);
                    addr.setText(dataSet.getPosDescription());
                }
            }
        }
    };
    private static final int CAPTURE_REQUEST_CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    dataSet.generateThumbnail();
                    shotButton.setImageBitmap(dataSet.getThumbnail());
                    shotButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                            startActivity(intent);
                        }
                    });
                    break;
                case Activity.RESULT_CANCELED:
                    dataSet.setPicName(null);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Register Baidu Map SDK
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        // Set up animation
        animationSet = new AnimationSet(true);
        animationSet.addAnimation(scaleAnimation());
        animationSet.addAnimation(disappearAnimation());

        // refresh dataSet
        if (dataSet == null)
            dataSet = new DataSet();

        time = (TextView) findViewById(R.id.time);

        // Set up Baidu Location Listener
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();

        shotButton = (RoundImageView)findViewById(R.id.shot);

        if (dataSet.getPicName() == null) {
            shotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    shotButton.startAnimation(animationSet);

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// 调用系统相机

                    Uri fileUri = getOutputMediaFileUri(); // create a file to save the image
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

                    // start the image capture Intent
                    startActivityForResult(intent, CAPTURE_REQUEST_CODE);
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
                    dataSet.setPicName("IMG_" + timeStamp + ".jpg");
                    mediaFile = new File(mediaStorageDir.getPath() + File.separator + dataSet.getPicName());
                    return Uri.fromFile(mediaFile);
                }
            });
        }
        else {
            shotButton.setImageBitmap(dataSet.getThumbnail());
            shotButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, ShowActivity.class);
                    startActivity(intent);
                }
            });
        }
        editcustomer = (ImageButton) findViewById(R.id.editcustomer);
        editcustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editcustomer.startAnimation(animationSet);
            }
        });

        editpos = (ImageButton) findViewById(R.id.editpos);
        editpos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editpos.startAnimation(animationSet);
                intentToLocate();
            }
        });

        finishButton = (ImageButton) findViewById(R.id.finish);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishButton.startAnimation(animationSet);
                DBOperation operation = new DBOperation(getApplicationContext());
                operation.save(dataSet);
                dataSet = new DataSet();
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });

        cancelButton = (ImageButton) findViewById(R.id.cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelButton.startAnimation(animationSet);
                showCancelDialog();
            }
        });

        TextView alarm = (TextView) findViewById(R.id.setalarm);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
            }
        });

        TextView addr = (TextView) findViewById(R.id.address);
        addr.setText(dataSet.getPosDescription());

        EditText editText = (EditText) findViewById(R.id.editText);
        if (dataSet.getContext() != null)
            editText.setText(dataSet.getContext());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                dataSet.setContext(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void intentToLocate() {
        Intent intent = new Intent(MainActivity.this, LocateActivity.class);
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
                        Intent intent = new Intent(MainActivity.this, entry.class);
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
