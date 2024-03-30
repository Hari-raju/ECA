package com.raju.elderlycareapplication.services;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import com.raju.elderlycareapplication.databinding.ActivityFallDetectedBinding;
import com.raju.elderlycareapplication.services.sensors.FallDetectionService;

public class FallDetectedActivity extends AppCompatActivity {

    private ActivityFallDetectedBinding fallDetectedBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fallDetectedBinding = ActivityFallDetectedBinding.inflate(getLayoutInflater());
        setContentView(fallDetectedBinding.getRoot());
        listeners();
    }

    private void listeners(){
        fallDetectedBinding.safe.setOnClickListener(v->{
            stopAlert();
            fallDetectedBinding.safe.setVisibility(View.GONE);
            fallDetectedBinding.alertMsg.setVisibility(View.GONE);
            fallDetectedBinding.resText.setVisibility(View.VISIBLE);
        });
    }

    private void stopAlert(){
        if (FallDetectionService.mediaPlayer!=null){
            Log.d("Fall Detection Alert","Clearing Audio");
            FallDetectionService.mediaPlayer.stop();
            FallDetectionService.mediaPlayer.release();
            FallDetectionService.mediaPlayer=null;
        }

        if (FallDetectionService.countDownTimer!=null){
            Log.d("Fall Detection Alert","Stopping CountDown");
            FallDetectionService.countDownTimer.cancel();
            FallDetectionService.countDownTimer=null;
        }
    }
}