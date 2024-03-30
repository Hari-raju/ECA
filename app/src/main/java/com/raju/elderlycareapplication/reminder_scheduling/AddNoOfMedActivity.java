package com.raju.elderlycareapplication.reminder_scheduling;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.raju.elderlycareapplication.databinding.ActivityAddNoOfMedBinding;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;

import java.util.Objects;

public class AddNoOfMedActivity extends AppCompatActivity {

    private ActivityAddNoOfMedBinding noOfMedBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        noOfMedBinding = ActivityAddNoOfMedBinding.inflate(getLayoutInflater());
        setContentView(noOfMedBinding.getRoot());
        listeners();
    }

    private void listeners(){
        noOfMedBinding.addNofMedBack.setOnClickListener(v->finish());
        noOfMedBinding.addNofMedNext.setOnClickListener(v-> checkDetails());
    }

    private void checkDetails(){
        String val = noOfMedBinding.noMed.getEditText().getText().toString();
        if(val.isEmpty()){
            noOfMedBinding.noMed.setError("Invalid Input Detected");
        }
        else{
            int noOfMed = Integer.parseInt(Objects.requireNonNull(val));
            if(noOfMed<=0){
                noOfMedBinding.noMed.setError("Invalid Input Detected");
            }
            else{
                noOfMedBinding.noMed.setError(null);
                Intent intent = new Intent(this, AddMedReminderActivity.class);
                intent.putExtra("NoMed",Integer.parseInt(Objects.requireNonNull(val)));
                ConnectedElderModel model = (ConnectedElderModel) getIntent().getSerializableExtra("connectedModel");
                intent.putExtra("connectedModel",model);
                startActivity(intent);
                finish();
            }
        }
    }
}