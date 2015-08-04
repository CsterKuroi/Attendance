package com.example.jogle.attendance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jogle on 15/7/24.
 */
public class DataSet implements Serializable{
    private String picName;
    private String posDescription;
    private String context;
    private double latitude;
    private double longitude;
    private String time;
    private Bitmap thumbnail;

    public DataSet() {
        picName = null;
        posDescription = null;
        context = null;
        latitude = -1;
        longitude = -1;
        time = null;
        thumbnail = null;
    }

    public Bitmap getPicBitMap() {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        FileInputStream is = null;
        try {
            is = new FileInputStream(getPicPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeStream(is, null, opt);
        double rate1 = bmp.getWidth() / 2000.0;
        double rate2 = bmp.getHeight() / 2000.0;
        if (bmp.getWidth() >= 2000 || bmp.getHeight() >= 2000) {
            double rate = (rate1 > rate2) ? rate1: rate2;
            Bitmap thumbnailBitmap = ThumbnailUtils.extractThumbnail(bmp,
                    (int) (bmp.getWidth() / rate), (int) (bmp.getHeight() / rate), 0);
            return thumbnailBitmap;
        }
        return bmp;
    }

    public String getPicPath() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Attendance");
        String path = mediaStorageDir.getAbsolutePath() + File.separator + picName;
        return path;
    }

    public String getThumbnailString() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (thumbnail != null) {
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            try {
                return new String(baos.toByteArray(), "ISO-8859-1");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public void generateThumbnail() {
        Bitmap bmp = getPicBitMap();
        thumbnail = ThumbnailUtils.extractThumbnail(bmp, 100, 100, 0);
        bmp.recycle();
    }

    public String getPicName() {
        return picName;
    }

    public String getPosDescription() {
        return posDescription;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTime() {
        return time;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public String getContext() {
        return context;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public void setPosDescription(String posDescription) {
        this.posDescription = posDescription;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setThumbnail(String thumbnailString) {
        byte[] thumbnailByte = null;
        try {
            thumbnailByte = thumbnailString.getBytes("ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(thumbnailByte.length != 0){
            thumbnail = BitmapFactory.decodeByteArray(thumbnailByte, 0, thumbnailByte.length);
        }
        else thumbnail = null;
    }

    public void setContext(String context) {
        this.context = context;
    }
}
