package com.raju.elderlycareapplication.reminder_display.background_services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.raju.elderlycareapplication.helpers.user_models.MedReminderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.helpers.utils.notifications.NotificationUtils;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.Calendar;
import java.util.List;

public class MedReminderService extends Service {
    private static final String TAG = "ElderHome";

    @Override
    public void onCreate() {
        Log.d(TAG, "service:onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "service:onStartCommand");
        //Starting Foreground service
        int serviceId = 1000;
        startForeground(serviceId, getNotification());

        //Setting Alarms
        setMedAlarm(this);
        setCheckupAlarm(this);
        return START_STICKY;
    }

    private Notification getNotification() {
        Log.d(TAG, "service:creating Notification");
        NotificationUtils.createNotificationChannel(this);

        //returning notification
        return new NotificationCompat.Builder(this, "ECA")
                .setContentTitle("Alarm Service")
                .setContentText("Running")
                .build();
    }

    public static void setMedAlarm(Context context) {
        //Creating Instance for alarm manager for setting alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PreferenceManager preferenceManager = new PreferenceManager(context);

        // Retrieve the list of medication reminders from preferences
        String listObj = preferenceManager.getString(Constants.KEY_ELDER_MEDICINE);

        // Deserialize the list
        List<MedReminderModel> list = null;
        try {
            if (listObj != null) {
                byte[] val = Base64.decode(listObj, Base64.DEFAULT);
                ByteArrayInputStream bais = new ByteArrayInputStream(val);
                ObjectInputStream ois = new ObjectInputStream(bais);
                list = (List<MedReminderModel>) ois.readObject();
                ois.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        // Iterate over the list of medication reminders
        if (list != null && !list.isEmpty()) {
            int requestCode = 0; // Initialize request code
            for (MedReminderModel model : list) {
                try {
                    // Extract hour and minute from the medication reminder model
                    String[] timeComponents = model.getEldersMedTime().split(":");
                    int hour = Integer.parseInt(timeComponents[0]);
                    int minute = Integer.parseInt(timeComponents[1]);

                    // Create a unique PendingIntent using the request code
                    Intent alarmIntent = new Intent(context, MedReminderReceiver.class);
                    alarmIntent.putExtra("alarmTime", model.getEldersMedTime());
                    alarmIntent.putExtra(Constants.KEY_WHAT_TO_DO, "medAlarm");
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

                    // Create a Calendar instance and set the alarm time
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);

                    if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    // Set the alarm using AlarmManager
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    Log.d(TAG, "Setted from set med Alarm");
                    // Increment the requestCode for the next alarm
                    requestCode++;
                } catch (Exception e) {
                    Log.e(TAG, "Error setting alarm for medication reminder: " + e.getMessage(), e);
                    Toast.makeText(context, "Error setting alarm for medication reminder: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static void setMedAlarmForNextDay(Context context, String time) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String[] timeComponents = time.split(":");
            int hour = Integer.parseInt(timeComponents[0]);
            int minute = Integer.parseInt(timeComponents[1]);

            // Create a unique PendingIntent using the request code
            Intent alarmIntent = new Intent(context, MedReminderReceiver.class);
            alarmIntent.putExtra(Constants.KEY_WHAT_TO_DO, "medAlarm");
            alarmIntent.putExtra("alarmTime", time);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 100, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            // Create a Calendar instance and set the alarm time
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            //It will execute after 2 mins bcz if we dont do this time will class
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Set the alarm using AlarmManager
                AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                Log.d(TAG, "Setted for nex med");
            }, 2 * 60 * 1000);
        } catch (SecurityException e) {
            Log.d(TAG, e.getLocalizedMessage());
        }
    }

    public static void setCheckupAlarm(Context context) {

        Log.d("ElderHome", "setCheckup");

        //Creating Instance for alarm manager for setting alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PreferenceManager preferenceManager = new PreferenceManager(context);

        String startTime = preferenceManager.getString(Constants.KEY_CHECK_START_TIME);
        if (startTime != null) {
            String endTime = preferenceManager.getString(Constants.KEY_CHECK_END_TIME);

            int reqCode = 24;

            //Lets Split Time Components
            String[] timeComponents = startTime.split(":");
            int startHour = Integer.parseInt(timeComponents[0]);
            int startMinute = Integer.parseInt(timeComponents[1]);

            String[] timeComponents1 = endTime.split(":");
            int endHour = Integer.parseInt(timeComponents1[0]);
            int endMinute = Integer.parseInt(timeComponents1[1]);

            Intent alarmIntent = new Intent(context, MedReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, endHour);
            calendar1.set(Calendar.MINUTE, endMinute);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);

            long currTimeinMillis = System.currentTimeMillis();
            Log.d("time in Millis of Curr", String.valueOf(currTimeinMillis));
            Log.d("time in Millis of Start", String.valueOf(calendar.getTimeInMillis()));
            Log.d("time in Millis of End", String.valueOf(calendar1.getTimeInMillis()));

            //If start time is greater than end time like for eg: start time is 5pm = (17) and end time is 9am = (9) we will check whether current time is less than start and greater than end time
            if (calendar.get(Calendar.HOUR_OF_DAY) > calendar1.get(Calendar.HOUR_OF_DAY)) {
                Log.d("ElderHome", "Calendar If Inside setCheckup");
                if (currTimeinMillis < calendar.getTimeInMillis() && currTimeinMillis > calendar1.getTimeInMillis()) {
                    Log.d("ElderHome", "Inside If1");
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.setTimeInMillis(currTimeinMillis);
                    alarmCalendar.add(Calendar.MINUTE,60);
                    Log.d("ElderHome", String.valueOf(alarmCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(alarmCalendar.get(Calendar.MINUTE)));
                    alarmIntent.putExtra("alarmTime", String.format("%d:%d", alarmCalendar.get(Calendar.HOUR_OF_DAY), alarmCalendar.get(Calendar.MINUTE)));
                    //Setting Alarm
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarmCalendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    Log.d("ElderHome", "Check Setted");
                } else {
                    //Fire at Start Time of Alarm
                    Log.d("ElderHome", "Else Inside setCheckup");
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                }
            }
            //If start time is greater than end time like for eg: start time is 9am = (9) and end time is 5pm = (17) we will check whether current time is greater than start and less than end time
            else {
                if (currTimeinMillis > calendar.getTimeInMillis() && currTimeinMillis < calendar1.getTimeInMillis()) {
                    Log.d("ElderHome", "Calendar Else Inside setCheckup");
                    Log.d("ElderHome", "Inside If1 Inside setCheckup");
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.setTimeInMillis(currTimeinMillis);
                    alarmCalendar.add(Calendar.MINUTE, 60);
                    Log.d("ElderHome", String.valueOf(alarmCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(alarmCalendar.get(Calendar.MINUTE)));
                    alarmIntent.putExtra("alarmTime", String.format("%d:%d", alarmCalendar.get(Calendar.HOUR_OF_DAY), alarmCalendar.get(Calendar.MINUTE)));
                    //Setting Alarm
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarmCalendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    Log.d("ElderHome", "Check Setted");
                } else {
                    //Fire at Start Time of Alarm
                    Log.d("ElderHome", "Else check");
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                }
            }
        }
    }

    public static void setCheckNextAlarm(Context context) {

        Log.d("ElderHome", "setNextCheckup");
        //Creating Instance for alarm manager for setting alarm
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PreferenceManager preferenceManager = new PreferenceManager(context);

        String startTime = preferenceManager.getString(Constants.KEY_CHECK_START_TIME);
        if (startTime != null) {
            String endTime = preferenceManager.getString(Constants.KEY_CHECK_END_TIME);

            int reqCode = 240;

            //Lets Split Time Components
            String[] timeComponents = startTime.split(":");
            int startHour = Integer.parseInt(timeComponents[0]);
            int startMinute = Integer.parseInt(timeComponents[1]);

            String[] timeComponents1 = endTime.split(":");
            int endHour = Integer.parseInt(timeComponents1[0]);
            int endMinute = Integer.parseInt(timeComponents1[1]);

            Intent alarmIntent = new Intent(context, MedReminderReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, reqCode, alarmIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.HOUR_OF_DAY, endHour);
            calendar1.set(Calendar.MINUTE, endMinute);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);

            long currTimeinMillis = System.currentTimeMillis();

            //If start time is greater than end time like for eg: start time is 5pm = (17) and end time is 9am = (9) we will check whether current time is less than start and greater than end time
            if (calendar.get(Calendar.HOUR_OF_DAY) > calendar1.get(Calendar.HOUR_OF_DAY)) {
                Log.d("ElderHome", "Calendar2 If Inside checkup 2");
                if (currTimeinMillis < calendar.getTimeInMillis() && currTimeinMillis > calendar1.getTimeInMillis()) {
                    Log.d("ElderHome", "Inside If");
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.setTimeInMillis(currTimeinMillis);
                    alarmCalendar.add(Calendar.MINUTE, 60);
                    Log.d("ElderHome", String.valueOf(alarmCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(alarmCalendar.get(Calendar.MINUTE)));
                    Log.d("ElderHome", String.valueOf(alarmCalendar.get(Calendar.HOUR_OF_DAY)));
                    alarmIntent.putExtra("alarmTime", String.format("%d:%d", alarmCalendar.get(Calendar.HOUR_OF_DAY), alarmCalendar.get(Calendar.MINUTE)));
                    //Setting Alarm
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarmCalendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    Log.d("ElderHome", "Check Setted");
                } else {
                    setCheckupAlarm(context);
                }
            }

            //If start time is greater than end time like for eg: start time is 9am = (9) and end time is 5pm = (17) we will check whether current time is greater than start and less than end time
            else {
                Log.d("ElderHome", "Calendar2 Else Inside checkup 2");
                if (currTimeinMillis > calendar.getTimeInMillis() && currTimeinMillis < calendar1.getTimeInMillis()) {
                    Log.d("ElderHome", "Inside If");
                    Calendar alarmCalendar = Calendar.getInstance();
                    alarmCalendar.setTimeInMillis(currTimeinMillis);
                    alarmCalendar.add(Calendar.MINUTE, 60);
                    Log.d("ElderHome", String.valueOf(alarmCalendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(alarmCalendar.get(Calendar.MINUTE)));
                    Log.d("ElderHome", String.valueOf(alarmCalendar.get(Calendar.HOUR_OF_DAY)));
                    alarmIntent.putExtra("alarmTime", String.format("%d:%d", alarmCalendar.get(Calendar.HOUR_OF_DAY), alarmCalendar.get(Calendar.MINUTE)));
                    //Setting Alarm
                    AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(alarmCalendar.getTimeInMillis(), pendingIntent);
                    alarmManager.setAlarmClock(alarmClockInfo, pendingIntent);
                    Log.d("ElderHome", "Check Setted");
                } else {
                    Log.d("ElderHome", "Fault");
                    setCheckupAlarm(context);
                }
            }
        }
    }
}
