package com.example.jogle.attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ApartmentActivity extends Activity {
    private ListView peopleList;
    private String[] data = {"小赵", "小钱", "小孙", "小李", "小周", "小吴", "小郑", "小王"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apartment);

        peopleList = (ListView) findViewById(R.id.peoplelist);
        peopleList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data));

        peopleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ApartmentActivity.this, ListActivity.class);
                intent.putExtra("name", data[position]);
                startActivity(intent);
            }
        });
    }
}
