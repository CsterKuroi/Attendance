package com.example.jogle.attendance;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class ShotActivity extends Activity {
    private String picName;
    private Camera.AutoFocusCallback myAutoFocusCallback = null;
    private LinearLayout title = null;
    private int cameraCount;
    private int cameraPosition = 1;
    private Camera camera = null;
    private MySurfaceView mySurfaceView = null;
    private ImageButton btn_camera_capture = null;
    private ImageButton btn_camera_cancel = null;
    private ImageButton btn_camera_ok = null;
    private ImageButton btn_camera_change = null;
    private byte[] buffer = null;

    private final int TYPE_FILE_IMAGE = 1;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            if (data == null){
                Log.i("MyPicture", "picture taken data: null");
            }else{
                Log.i("MyPicture", "picture taken data: " + data.length);
            }

            buffer = new byte[data.length];
            buffer = data.clone();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shot);

        title = (LinearLayout)findViewById(R.id.titlebox);
        if (!checkCameraHardWare(getApplicationContext())){
            Dialog dialog = new AlertDialog.Builder(this).setTitle("提示").setMessage("没有找到相机").setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(ShotActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }).create();
            dialog.show();
        }

        btn_camera_capture = (ImageButton) findViewById(R.id.camera_capture);
        btn_camera_ok = (ImageButton) findViewById(R.id.camera_ok);
        btn_camera_cancel = (ImageButton) findViewById(R.id.camera_cancel);
        btn_camera_change = (ImageButton) findViewById(R.id.camera_change);

        cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
        if (cameraCount == 1) {
            btn_camera_change.setVisibility(View.INVISIBLE);
        }

        btn_camera_capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.autoFocus(myAutoFocusCallback);
                camera.takePicture(null, null, pictureCallback);

                btn_camera_capture.setVisibility(View.INVISIBLE);
                btn_camera_ok.setVisibility(View.VISIBLE);
                btn_camera_cancel.setVisibility(View.VISIBLE);
            }
        });
        btn_camera_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //保存图片
                saveImageToFile();

                Intent intent = new Intent(ShotActivity.this, LocateActivity.class);
                Bundle bundle = new Bundle();
                intent.putExtra("img_name", picName);
                startActivity(intent);

                //camera.startPreview();
                btn_camera_capture.setVisibility(View.VISIBLE);
                btn_camera_ok.setVisibility(View.INVISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);
            }
        });
        btn_camera_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                camera.startPreview();
                btn_camera_capture.setVisibility(View.VISIBLE);
                btn_camera_ok.setVisibility(View.INVISIBLE);
                btn_camera_cancel.setVisibility(View.INVISIBLE);
            }
        });

        btn_camera_change.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                int cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数
                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
                    if (cameraPosition == 1) {
                        // 现在是后置，变更为前置
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            /**
                             * 记得释放camera，方便其他应用调用
                             */
                            if (camera != null) {
                                camera.setPreviewCallback(null);
                                camera.stopPreview();// 停掉原来摄像头的预览
                                camera.release();
                                camera = null;
                            }
                            // 打开当前选中的摄像头
                            camera = Camera.open(i);
                            // 通过surfaceview显示取景画面
                            try {
                                camera.setDisplayOrientation(90);
                                camera.setPreviewDisplay(mySurfaceView.getSurfaceHolder());
                                camera.startPreview();
                            } catch (IOException e) {
                                //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                            }
                            cameraPosition = 0;
                            break;
                        }
                    } else {
                        // 现在是前置， 变更为后置
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            /**
                             * 记得释放camera，方便其他应用调用
                             */
                            if (camera != null) {
                                camera.setPreviewCallback(null);
                                camera.stopPreview();// 停掉原来摄像头的预览
                                camera.release();
                                camera = null;
                            }
                            camera = Camera.open(i);
                            try {
                                camera.setDisplayOrientation(90);
                                camera.setPreviewDisplay(mySurfaceView.getSurfaceHolder());
                                camera.startPreview();
                            } catch (IOException e) {
                                //Log.d(TAG, "Error starting camera preview: " + e.getMessage());
                            }
                            cameraPosition = 1;
                            break;
                        }
                    }
                }
            }
        });

        ImageButton backButton = (ImageButton) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShotActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mOrientationListener.disable();
    }

    @Override
    protected void onPause() {
        super.onPause();

        camera.release();
        camera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (camera == null){
            camera = getCameraInstance();
        }
        //必须放在onResume中，不然会出现Home键之后，再回到该APP，黑屏

        camera.setDisplayOrientation(90);
        mySurfaceView = new MySurfaceView(this, getApplicationContext(), camera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        double heightDivWidth = (double) wm.getDefaultDisplay().getWidth() / wm.getDefaultDisplay().getHeight();
        Camera.Parameters parameters = camera.getParameters();

        // make camera view be center
        FrameLayout.LayoutParams lp1 = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp1.gravity = Gravity.CENTER_VERTICAL;
        preview.addView(mySurfaceView, lp1);

        // calculate view size
        ViewGroup.LayoutParams lp = mySurfaceView.getLayoutParams();
        lp.width = wm.getDefaultDisplay().getWidth();
        Camera.Size previewSize = parameters.getPreviewSize();
        lp.height = lp.width * previewSize.width / previewSize.height;
        mySurfaceView.setLayoutParams(lp);

        title.bringToFront();
    }

    /*得到一相机对象*/
    private Camera getCameraInstance(){
        Camera camera = null;
        try{
            camera = camera.open();
        }catch(Exception e){
            e.printStackTrace();
        }
        //camera.autoFocus(autoFocusCallback);
        return camera;
    }

    private boolean checkCameraHardWare(Context context){
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }
    private void saveImageToFile(){
        File file = getOutFile(TYPE_FILE_IMAGE);
        if (file == null){
            Toast.makeText(getApplicationContext(), "文件创建失败", Toast.LENGTH_SHORT).show();
            return ;
        }

//        Toast.makeText(getApplicationContext(), "图片保存路径：" + file.getPath(), Toast.LENGTH_SHORT).show();
        if (buffer != null){
            try{
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(buffer);
                fos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    //生成输出文件
    private File getOutFile(int fileType) {
        File fileDir = getFilesDir();
        File picDir = new File(fileDir.getAbsolutePath() + File.separator + "pic");
        if (!picDir.exists()) {
            picDir.mkdir();
        }

        File file = new File(getFilePath(picDir, fileType));
        return file;
    }
    //生成输出文件路径
    private String getFilePath(File mediaStorageDir, int fileType){
        String timeStamp =new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE){
            picName = "IMG_" + timeStamp + ".jpg";
            filePath += ("IMG_" + timeStamp + ".jpg");
        }else{
            picName = "";
            return null;
        }
        return filePath;
    }

}