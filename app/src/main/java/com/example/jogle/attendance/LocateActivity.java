package com.example.jogle.attendance;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocateActivity extends Activity {
    private ListView poiList;
    private List<String> poiData;
    private TextView pos;
    private boolean isFirstLoc = true;
    private BDLocation mLocation;
    private MapView mMapView = null;
    private LocationClient mLocationClient = null;
    private BaiduMap mBaiduMap;
    private BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);    //设置定位数据

//            Toast.makeText(getApplicationContext(), location.getAddrStr() + "|" + location.getLocType(),
//                    Toast.LENGTH_SHORT).show();

            if (location.getLocType() == BDLocation.TypeGpsLocation ||
                    location.getLocType() == BDLocation.TypeNetWorkLocation ||
                    location.getLocType() == BDLocation.TypeOffLineLocation) {

                if (isFirstLoc) {
                    isFirstLoc = false;

                    LatLng ll = new LatLng(location.getLatitude(),
                            location.getLongitude());
                    MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 16);   //设置地图中心点以及缩放级别
                    mBaiduMap.animateMapStatus(u);
                }
            }
            mLocation = location;
        }
    };
    private PoiSearch mPoiSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_locate);

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.showZoomControls(false);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);

        pos = (TextView)findViewById(R.id.pos);
        pos.setText(MainActivity.dataSet.getPosDescription());

        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        initLocation();
        mLocation = new BDLocation();
//        myListener.onReceiveLocation(mLocation);
        mLocationClient.start();

        LatLng geolat =new LatLng(MainActivity.dataSet.getLatitude(),
                MainActivity.dataSet.getLongitude());
        ReverseGeoCodeOption option = new ReverseGeoCodeOption();
        option.location(geolat);
        GeoCoder coder = GeoCoder.newInstance();
        coder.reverseGeoCode(option);
        coder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                poiData.clear();
                poiData.add(MainActivity.dataSet.getPosDescription());
                for (int i = 0; i < reverseGeoCodeResult.getPoiList().size(); i++) {
                    if (!MainActivity.dataSet.getPosDescription().equals(
                            reverseGeoCodeResult.getPoiList().get(i).name.trim()))
                        poiData.add(reverseGeoCodeResult.getPoiList().get(i).name.trim());
                }
                ((ArrayAdapter<String>) poiList.getAdapter()).notifyDataSetChanged();
            }
        });

        poiData = new ArrayList<String>();
        poiList = (ListView) findViewById(R.id.poilist);
        poiList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData()));
        poiList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.dataSet.setPosDescription(poiData.get(i));
                pos.setText(poiData.get(i));
                Intent intent = new Intent(LocateActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //!
                Intent intent = new Intent(LocateActivity.this, MainActivity.class);
                startActivity(intent);
                //showCancelDialog();
            }
        });
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
        mBaiduMap.setMyLocationEnabled(false);
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this).setTitle("提示")
                .setMessage("是否取消此次考勤打卡？")
                .setPositiveButton("否", null)
                .setNegativeButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), "Attendance");
                        String path = mediaStorageDir.getAbsolutePath() + File.separator + MainActivity.dataSet.getPicName();
                        File pic = new File(path);
                        pic.delete();
                        Intent intent = new Intent(LocateActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).show();
    }

    private List<String> getData() {
        if (poiData.size() == 0) {
            poiData.add("请稍候……");
        }
        return poiData;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(LocateActivity.this, MainActivity.class);
            startActivity(intent);
            return false;
        }
        return false;
    }
}

