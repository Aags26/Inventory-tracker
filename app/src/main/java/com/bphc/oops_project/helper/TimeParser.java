package com.bphc.oops_project.helper;

import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeParser {

    public static String parseDate(String time) {
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dayOfTheWeek = "";
        try {
            Date date = dateSdf.parse(time);
            dayOfTheWeek = time.split(" ")[0] + " (" + DateFormat.format("EEEE", date) + ")";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayOfTheWeek;
    }

    public static long getMillis(String time) {
        SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        long millis = 0;
        try {
            Date date = dateSdf.parse(time);
            if (date != null)
                millis = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return millis;
    }

    public static String parseTime(String time) {
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String t = time.split(" ")[1].substring(0, time.split(" ")[1].lastIndexOf(':'));

        try {
            Date _24HourDt = _24HourSDF.parse(t);
            assert _24HourDt != null;
            return _12HourSDF.format(_24HourDt);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

}
