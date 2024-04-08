package com.raju.elderlycareapplication.authentication.caretakers;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivityEmergencyElderBinding;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.helpers.utils.notifications.FCMNotificationService;

public class EmergencyElderActivity extends AppCompatActivity {

    private ActivityEmergencyElderBinding emergencyElderBinding;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emergencyElderBinding = ActivityEmergencyElderBinding.inflate(getLayoutInflater());
        setContentView(emergencyElderBinding.getRoot());
        if(FCMNotificationService.mediaPlayer!=null){
            FCMNotificationService.mediaPlayer.stop();
            FCMNotificationService.mediaPlayer.release();
            FCMNotificationService.mediaPlayer=null;
        }
        database = FirebaseFirestore.getInstance();
        init();
    }

    private void init(){
        Intent intent = getIntent();
        emergencyElderBinding.elderNameEmg.setText(intent.getStringExtra(Constants.KEY_ELDER_NAME));
        emergencyElderBinding.elderPhoneEmg.setText(intent.getStringExtra(Constants.KEY_ELDER_PHONE));
        emergencyElderBinding.emgMsg.setText(intent.getStringExtra(Constants.KEY_ALERT_MESSAGE));
        emergencyElderBinding.lastAddress.setText(intent.getStringExtra(Constants.KEY_LAST_ADDRESS));

        database.collection(Constants.KEY_ELDER_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE,intent.getStringExtra(Constants.KEY_ELDER_PHONE))
                .get()
                .addOnCompleteListener(task->{
                   if(task.isSuccessful() && task.getResult()!=null && !task.getResult().getDocuments().isEmpty()){
                       DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                       emergencyElderBinding.elderProfileEmg.setImageBitmap(
                               EncoderDecoder.decodeImage(documentSnapshot.getString(Constants.KEY_ELDER_PROFILE))
                       );
                   }
                });
    }
}