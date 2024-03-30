package com.raju.elderlycareapplication.helpers.utils.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.authentication.caretakers.CaretakerHomeActivity;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.Constants;

import java.util.Random;

public class FCMNotificationService extends FirebaseMessagingService {
    public static MediaPlayer mediaPlayer;
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        Log.d("ElderHome","message received");
        Elder_Model model = new Elder_Model();
        model.setElderName(message.getData().get(Constants.KEY_NAME));
        model.setElderPhone(message.getData().get(Constants.KEY_ELDER_PHONE));

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationUtils.createNotificationChannel(this);
        }

        int notificationId = new Random().nextInt();
        Intent intent = new Intent(this, CaretakerHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("alert","yes");
        int id = 1008;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,id,intent,PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

        //Build Notification
        Notification notification = new NotificationCompat.Builder(this, "ECA")
                .setContentTitle(model.getElderName())
                .setContentText(message.getData().get(Constants.KEY_ALERT_MESSAGE))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        message.getData().get(Constants.KEY_ALERT_MESSAGE)
                ))
                .setAutoCancel(true)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
        if (mediaPlayer == null) {
            Log.d("Notification","inside mediaplayer");
            mediaPlayer = MediaPlayer.create(this, Settings.System.DEFAULT_RINGTONE_URI);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        }
        else{
            Log.d("Notification","outside mediaplayer");
        }
    }
}
