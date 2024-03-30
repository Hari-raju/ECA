package com.raju.elderlycareapplication.reminder_scheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.databinding.ActivityAddCheckupReminderBinding;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

import java.util.HashMap;

public class AddCheckupReminderActivity extends AppCompatActivity {
    private ActivityAddCheckupReminderBinding checkupReminderBinding;
    private MaterialTimePicker picker;
    private String startTime = null;
    private String endTime = null;
    private ConnectedElderModel model;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkupReminderBinding = ActivityAddCheckupReminderBinding.inflate(getLayoutInflater());
        setContentView(checkupReminderBinding.getRoot());

        //Initiating DB
        database = FirebaseFirestore.getInstance();

        //Initiating Local DB
        preferenceManager = new PreferenceManager(this);

        model = (ConnectedElderModel) getIntent().getSerializableExtra("connectedModel");
        //Listeners();
        listeners();
    }

    private void listeners(){

        //Back Button Function
        checkupReminderBinding.addCheckBack.setOnClickListener(v->finish());

        //Start Time Setter Func
        checkupReminderBinding.addCheckStartTime.setOnClickListener(v->{
            picker = new MaterialTimePicker.Builder()
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Check Up Time")
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .build();

            picker.show(getSupportFragmentManager(),"ECA");

            picker.addOnPositiveButtonClickListener(view ->{
               if(picker.getHour()>12){
                   checkupReminderBinding.addCheckStartTime.setText(
                           String.format("%d : %d PM",picker.getHour()-12,picker.getMinute())
                   );
               }
               else{
                   checkupReminderBinding.addCheckStartTime.setText(
                           String.format("%d : %d AM",picker.getHour(),picker.getMinute())
                   );
               }
               startTime = EncoderDecoder.timePickerToString(picker);
            });
        });

        //End Time Setter Func
        checkupReminderBinding.addCheckEnd.setOnClickListener(v->{
            picker = new MaterialTimePicker.Builder()
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Check Up Time")
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .build();

            picker.show(getSupportFragmentManager(),"ECA");

            picker.addOnPositiveButtonClickListener(view ->{
                if(picker.getHour()>12){
                    checkupReminderBinding.addCheckEnd.setText(
                            String.format("%d : %d PM",picker.getHour()-12,picker.getMinute())
                    );
                }
                else{
                    checkupReminderBinding.addCheckEnd.setText(
                            String.format("%d : %d AM",picker.getHour(),picker.getMinute())
                    );
                }
                endTime = EncoderDecoder.timePickerToString(picker);
            });
        });

        //Setting Reminders
        checkupReminderBinding.addCheckup.setOnClickListener(v->{
            if(checkDetails()){
                HashMap<String,String> map = new HashMap<>();
                map.put(Constants.KEY_CARETAKER_PHONE,preferenceManager.getString(Constants.KEY_CARETAKER_PHONE));
                map.put(Constants.KEY_ELDER_PHONE,model.getElderPhone());
                map.put(Constants.KEY_CHECK_START_TIME,startTime);
                map.put(Constants.KEY_CHECK_END_TIME,endTime);
                database.collection(Constants.KEY_CHECK_COLLECTION)
                        .add(map)
                        .addOnSuccessListener(success->{
                            Toast.makeText(this, "Setted", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(failed->{
                            Toast.makeText(this, failed.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    private boolean checkDetails(){
        if(startTime == null){
            Toast.makeText(this, "Enter Start Time", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (endTime == null){
            Toast.makeText(this, "Enter End Time", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}