package com.example.jogle.attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.jogle.calendar.CalendarActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentActivity extends Activity {
    private ListView peopleList;
    private PeopleListAdapter adapter;
    private List<Map<String, Object>> listItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_department);

        // get current date from system
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
        String dateString = sdf.format(calendar.getTime());

        // get datas of today
        DBOperation operation = new DBOperation(getApplicationContext());
        List<DataSet> list = operation.getByDate(dateString);

        peopleList = (ListView) findViewById(R.id.peoplelist);
        adapter = new PeopleListAdapter(getApplicationContext(), getData());

        peopleList.setAdapter(adapter);

        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DepartmentActivity.this, CalendarActivity.class);
                intent.putExtra("uid", ((Integer) listItems.get(position).get("uid")).intValue());
                intent.putExtra("name", (String) listItems.get(position).get("name"));
                startActivity(intent);
            }
        });
    }

    public List<Map<String, Object>> getData() {
        listItems = new ArrayList<Map<String, Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("name", "迟家升");
        map.put("department", "部门1");
        map.put("finish", "今日签到完成");
        map.put("signedDays", 5);
        map.put("passedDays", 16);
        map.put("uid", 1);
        listItems.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "李国盛");
        map.put("department", "部门2");
        map.put("finish", "今日签到完成");
        map.put("signedDays", 15);
        map.put("passedDays", 16);
        map.put("uid", 2);
        listItems.add(map);

        map = new HashMap<String, Object>();
        map.put("name", "张志良");
        map.put("department", "部门3");
        map.put("finish", "今日签到未完成");
        map.put("signedDays", 9);
        map.put("passedDays", 16);
        map.put("uid", 3);
        listItems.add(map);

        return listItems;
    }
}
