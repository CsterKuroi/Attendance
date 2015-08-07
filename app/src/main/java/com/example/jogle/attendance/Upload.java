package com.example.jogle.attendance;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;


/**
 * Created by Administrator on 15-8-7.
 */
public class Upload {
     private final String wsuri = "ws://192.168.50.11:8000/ws";
    //private final String wsuri = "ws://192.168.50.101:8001/ws";
    private String tempjson="";
    private WebSocketConnection mConnection;

    public String changeArrayDateToJson(DataSet ds) {
        try {
            JSONObject object = new JSONObject();
            String type = String.valueOf(ds.getType());
            String userID = String.valueOf(ds.getUserID());
            String userName = ds.getUserName();
            String time = ds.getTime();
            String positon = ds.getPosition();
            String content = ds.getContent();
            String timeStamp = ds.getTimeStamp();

            object.put("cmd", "neiqin");
            object.put("type", "5");
            object.put("myType", type);
            object.put("userID", userID);
//          object.put("userName", userName);
            object.put("time", time);
            object.put("position", positon);
            object.put("content", content);
            object.put("timeStamp", timeStamp);

            return object.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void up(String json) {
        mConnection = new WebSocketConnection();
        if (mConnection.isConnected()) {
            mConnection.sendTextMessage(json);
            Log.d("test", "发送Json字段" + json);
        }else {
            try {
                tempjson=json;
                mConnection.connect(wsuri, new WebSocketHandler() {
                    public void onOpen() {
                        Log.e("test", "发送Json字段"+tempjson);
                        mConnection.sendTextMessage(tempjson);
                    }
                });
            }catch (WebSocketException e) {
                    e.printStackTrace();
            }
        }
    }
}