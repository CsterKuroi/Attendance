package com.example.jogle.calendar.doim;

import android.content.Context;

import com.example.jogle.calendar.widget.CalendarView;
import com.example.jogle.calendar.widget.CalendarView.CallBack;

public class CalendarViewBuilder {
	private CalendarView[] calendarViews;

	public  CalendarView[] createMassCalendarViews(Context context,int count,int style,CallBack callBack){
		calendarViews = new CalendarView[count];
		for(int i = 0; i < count;i++){
			calendarViews[i] = new CalendarView(context, style,callBack);
		}
		return calendarViews;
	}

	public  CalendarView[] createMassCalendarViews(Context context,int count,CallBack callBack){
		return createMassCalendarViews(context, count, CalendarView.MONTH_STYLE,callBack);
	}
}
