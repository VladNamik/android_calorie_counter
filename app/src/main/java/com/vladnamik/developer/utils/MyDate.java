package com.vladnamik.developer.utils;


import java.util.Calendar;
import java.util.Date;

public class MyDate extends java.util.Date {

    public MyDate() {
        super();
    }

    public MyDate(Date date) {
        super();
        setTime(date.getTime());
    }

    @Override
    public long getTime() {
        setTime(super.getTime());
        return super.getTime();
    }

    @Override
    public void setTime(long milliseconds) {
        Date date = new Date();
        date.setTime(milliseconds);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        super.setTime(calendar.getTime().getTime());
    }
}
