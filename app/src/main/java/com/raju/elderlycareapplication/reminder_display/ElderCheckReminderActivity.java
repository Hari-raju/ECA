package com.raju.elderlycareapplication.reminder_display;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivityElderCheckReminderBinding;
import com.raju.elderlycareapplication.reminder_display.background_services.MedReminderReceiver;

public class ElderCheckReminderActivity extends AppCompatActivity {
    private ActivityElderCheckReminderBinding checkReminderBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkReminderBinding = ActivityElderCheckReminderBinding.inflate(getLayoutInflater());
        setContentView(checkReminderBinding.getRoot());

        listeners();
    }

    private void listeners(){
        checkReminderBinding.safe.setOnClickListener(v->{
            stopAlert();
            checkReminderBinding.safe.setVisibility(View.GONE);
            checkReminderBinding.checkMessage.setVisibility(View.GONE);
            checkReminderBinding.greetMsg.setVisibility(View.VISIBLE);
        });
    }


    private void stopAlert(){
        if (MedReminderReceiver.mediaPlayer!=null){
            Log.d("ElderHome","Clearing Audio");
            MedReminderReceiver.mediaPlayer.stop();
            MedReminderReceiver.mediaPlayer.release();
            MedReminderReceiver.mediaPlayer=null;
        }

        if (MedReminderReceiver.countDownTimer!=null){
            Log.d("ElderHome","Stopping CountDown");
            MedReminderReceiver.countDownTimer.cancel();
            MedReminderReceiver.countDownTimer=null;
        }
    }
}