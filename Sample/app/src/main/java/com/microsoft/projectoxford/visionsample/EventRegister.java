package com.microsoft.projectoxford.visionsample;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kakaminaoto on 2016/07/13.
 */
public class EventRegister {
    private final String TAG = "EventRegister";
    private Context context;

    public EventRegister(Context context){
        this.context = context;
    }

    public void logCalender(){
        ContentResolver cr = context.getContentResolver();

        String[] projection = {
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.NAME,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.ACCOUNT_TYPE,
        };
        Cursor cursor = cr.query(CalendarContract.Calendars.CONTENT_URI, projection, null, null, null);
        for (boolean hasNext = cursor.moveToFirst(); hasNext; hasNext = cursor.moveToNext()) {
            String id = cursor.getString(0);
            String name = cursor.getString(1);
            String accountName = cursor.getString(2);
            String accountType = cursor.getString(3);

            Log.i(TAG, id + ":" + name);
            Log.i(TAG, accountName);
            Log.i(TAG, accountType);
            Log.i(TAG, "-----------------------------------");
        }
        cursor.close();
    }

    public void registEvent(String line){
        Log.d(TAG, "registEvent()");
        Log.i(TAG, "line:" + line);


        ArrayList<String> strs = splitLine(line);
        if(strs.size() == 3) {
            int month = Integer.parseInt(strs.get(0));
            int day = Integer.parseInt(strs.get(1));
            String event = strs.get(2);
            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy/MM/dd").parse("2016/" + String.valueOf(month) + "/" + String.valueOf(day));
                Log.i(TAG, "date:" + date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }

            ContentResolver resolver = context.getContentResolver();
            ContentValues values = new ContentValues();
            values.put(CalendarContract.Events.CALENDAR_ID, 2);
            values.put(CalendarContract.Events.TITLE, event);
            values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
            //values.put(CalendarContract.Events.DTSTART, System.currentTimeMillis() + 1000 * 60 * 60);
            //values.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 1000 * 60 * 60 * 2);
            values.put(CalendarContract.Events.DTSTART, date.getTime());
            values.put(CalendarContract.Events.DTEND, date.getTime());

            Uri uri = resolver.insert(CalendarContract.Events.CONTENT_URI, values);
            Long eventID = Long.parseLong(uri.getLastPathSegment());
            Log.i(TAG, "eventID:" + eventID);
        }
    }

    private ArrayList<String> splitLine(String line){
        Log.d(TAG, "splitLine()");
        Log.i(TAG, "line:" + line);
        ArrayList<String> strs = new ArrayList<>();

        Matcher m = Pattern.compile("(\\d).(\\d{1,2})(.*)").matcher(line.replaceAll(" ", ""));
        if(m.find()){
            strs.add(m.group(1));
            strs.add(m.group(2));
            strs.add(m.group(3));
            Log.i(TAG, "month:" + strs.get(0));
            Log.i(TAG, "day:" + strs.get(1));
            Log.i(TAG, "event:" + strs.get(2));
        }

        return strs;
    }
}
