package com.raju.elderlycareapplication.reminder_display;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivityElderMedReminderBinding;
import com.raju.elderlycareapplication.helpers.user_models.MedReminderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.reminder_display.background_services.MedReminderReceiver;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class ElderMedReminderActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;
    private ActivityElderMedReminderBinding reminderBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderBinding = ActivityElderMedReminderBinding.inflate(getLayoutInflater());
        setContentView(reminderBinding.getRoot());
        //Stop Alert
        stopAlert();
        //Initializing Local DB
        preferenceManager = new PreferenceManager(this);
        //initialize values
        init();
    }

    private void init(){
        if(getIntent().getStringExtra("data")!=null){
            Log.d("ElderHome",getIntent().getStringExtra("data"));
            String medTime = getIntent().getStringExtra("data");
            // Retrieve the list of medication reminders from preferences
            String listObj = preferenceManager.getString(Constants.KEY_ELDER_MEDICINE);
            // Deserialize the list
            List<MedReminderModel> list = null;
            try{
                if (listObj != null) {
                    byte[] val = Base64.decode(listObj, Base64.DEFAULT);
                    ByteArrayInputStream bais = new ByteArrayInputStream(val);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    list = (List<MedReminderModel>) ois.readObject();
                    ois.close();
                }
            }
            catch (Exception e){
                Log.d("ElderHome",e.getLocalizedMessage());
            }
            if (list != null && !list.isEmpty()){
                for(MedReminderModel model : list){
                    if(model.getEldersMedTime().equals(medTime)){
                        setDetails(model);
                        return;
                    }
                }
            }
        }
    }

    private void setDetails(MedReminderModel reminderModel){
        Glide.with(this)
                .load(reminderModel.getEldersMedPic())
                .placeholder(R.drawable.place_holder)
                .error(R.drawable.error)
                .into(reminderBinding.medImage);

        reminderBinding.medName.setText(reminderModel.getEldersMedName());
        reminderBinding.medPur.setText(reminderModel.getEldersMedPurpose());
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