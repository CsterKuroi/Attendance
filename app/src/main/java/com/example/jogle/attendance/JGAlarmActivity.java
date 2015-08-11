package com.example.jogle.attendance;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TimePicker;

import java.util.Calendar;


public class JGAlarmActivity extends Activity {
    private DatePicker datePicker;
    private TimePicker timePicker;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.jg_activity_alarm);

        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        final Calendar calendar = Calendar.getInstance();
        datePicker.updateDate(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        timePicker.setCurrentHour(calendar.get(Calendar.HOUR));
        timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        ImageButton finishButton = (ImageButton) findViewById(R.id.addalarm);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.YEAR, datePicker.getYear());
                calendar1.set(Calendar.MONTH, datePicker.getMonth());
                calendar1.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                calendar1.set(Calendar.HOUR, timePicker.getCurrentHour());
                calendar1.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                calendar1.set(Calendar.SECOND, 0);
                // Add alarm
                finish();
            }
        });
    }
}
