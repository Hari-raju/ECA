package com.raju.elderlycareapplication.reminder_display.background_services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.notifications.NotificationHelper;
import com.raju.elderlycareapplication.helpers.utils.notifications.NotificationUtils;
import com.raju.elderlycareapplication.reminder_display.ElderCheckReminderActivity;
import com.raju.elderlycareapplication.reminder_display.ElderMedReminderActivity;

import java.util.Objects;

public class MedReminderReceiver extends BroadcastReceiver {
    public static MediaPlayer mediaPlayer;
    public static CountDownTimer countDownTimer;
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("ElderHome","onReceive");

        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, Settings.System.DEFAULT_RINGTONE_URI);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }

        //Check which alarm
        if(intent.getStringExtra(Constants.KEY_WHAT_TO_DO)!=null && Objects.requireNonNull(intent.getStringExtra(Constants.KEY_WHAT_TO_DO)).equals("medAlarm")){
            String time = intent.getStringExtra("alarmTime");
            Log.d("ElderHome", Objects.requireNonNull(time));
            MedReminderService.setMedAlarmForNextDay(context,time);
            //posting notification
            postNotificationMedication(context,time);
        }
        else{
            //Post Check up notification
            MedReminderService.setCheckNextAlarm(context);
            postNotificationCheckUp(context);
        }

        //starting countdown
        startCountDown(context);
    }

    private void postNotificationCheckUp(Context context){
        Log.d("ElderHome","notification creating");
        Intent notificationIntent = new Intent(context, ElderCheckReminderActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 6000, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationUtils.createNotificationChannel(context);
        Notification notification = new NotificationCompat.Builder(context, "ECA")
                .setContentTitle("Check up Alert!")
                .setContentText("Are you ok?")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(9000, notification);
    }

    private void postNotificationMedication(Context context,String time){
        Log.d("ElderHome","notification creating");
        Intent notificationIntent = new Intent(context, ElderMedReminderActivity.class);
        notificationIntent.putExtra("data",time);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 1000, notificationIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationUtils.createNotificationChannel(context);
        Notification notification = new NotificationCompat.Builder(context, "ECA")
                .setContentTitle("Medication Reminder")
                .setContentText("Time to take medicine!")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.app_logo)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        notificationManager.notify(2000, notification);

    }

    private void startCountDown(Context context){
        //60000 - > 1min 1000 ->interval
        Log.d("ElderHome","countDown");
        countDownTimer = new CountDownTimer(60000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("ElderHome",String.valueOf(millisUntilFinished/1000));
            }

            @Override
            public void onFinish() {
                if(mediaPlayer != null && mediaPlayer.isPlaying()){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer=null;
                }
                Log.d("ElderHome","Finished");
                NotificationHelper.sendNotification(context,"Elder is not responding please check them!");
            }
        }.start();
    }
}
