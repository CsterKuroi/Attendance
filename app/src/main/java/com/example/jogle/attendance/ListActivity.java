package com.example.jogle.attendance;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListActivity extends Activity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

//        listView = (ListView) findViewById(R.id.attendanceList);
//        SimpleAdapter adapter = new SimpleAdapter(this, getData(), R.layout.list_item,
//                new String[]{"title","info", "img"},
//                new int[]{R.id.title,R.id.info,R.id.img});
//        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
//            @Override
//            public boolean setViewValue(View view, Object o, String s) {
//                if((view instanceof ImageView) && (o instanceof Bitmap))
//                {
//                    ImageView iv = (ImageView) view;
//                    Bitmap bmp = (Bitmap) o;
//                    iv.setImageBitmap(bmp);
//                    return true;
//                }
//                return false;
//            }
//        });
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                view.setBackgroundColor(0xeb00b9e6);
//            }
//        });

        Button apartment = (Button) findViewById(R.id.apartment);
        apartment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        DBOperation operation = new DBOperation(getApplicationContext());
        List<DataSet> listOfDataSet = operation.getAll();
        if (listOfDataSet != null) {
            for (int i = listOfDataSet.size() - 1; i >= 0; i--) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", listOfDataSet.get(i).getTime());
                map.put("info", listOfDataSet.get(i).getPosDescription());
                map.put("img", listOfDataSet.get(i).getThumbnail());
                map.put("recordFile", listOfDataSet.get(i).getPicName());
                list.add(map);
            }
        }

        return list;
    }

}
