package com.raju.elderlycareapplication.authentication.elder;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.authentication.home.OtpActivity;
import com.raju.elderlycareapplication.databinding.ActivityElderDescriptionBinding;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.Constants;

import java.util.Calendar;

public class ElderDescriptionActivity extends AppCompatActivity {

    private ActivityElderDescriptionBinding elderDescriptionBinding;
    private Elder_Model elder_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        elderDescriptionBinding = ActivityElderDescriptionBinding.inflate(getLayoutInflater());
        setContentView(elderDescriptionBinding.getRoot());

        //Here we getting the extra that we put in a previous activity to a object
        elder_model = (Elder_Model)getIntent().getSerializableExtra("elderData");

        //On CLick Listeners
        listeners();
    }

    private void listeners() {
        //Back button func
        elderDescriptionBinding.elderDescBack.setOnClickListener(v -> finish());

        //Select Dob func here by getting the dob we gonna calculate age
        elderDescriptionBinding.ageSelector.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    ElderDescriptionActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        elderDescriptionBinding.ageSelector.setText(dayOfMonth + "/" + (month1 + 1) + "/" + year1);
                        Calendar dob  = Calendar.getInstance();
                        dob.set(year1,month1,dayOfMonth);

                        //Fucntion to calculate age
                        calculateAge(dob);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });

        //Next Button this will only work if dob and description are entered correctly
        elderDescriptionBinding.elderDescNext.setOnClickListener(v->{
            if(elder_model.getElderAge()!=null){
                if(!elderDescriptionBinding.elderMedDesc.getEditText().getText().toString().isEmpty()){
                    elder_model.setDate_of_birth(elderDescriptionBinding.ageSelector.getText().toString());
                    elder_model.setElderDescription(elderDescriptionBinding.elderMedDesc.getEditText().getText().toString());
                    Intent intent = new Intent(ElderDescriptionActivity.this, OtpActivity.class);

                    //Here we passing the object after adding age,dob and description
                    intent.putExtra("elderData",elder_model);

                    //Here we putting this so that next activity can know from which activity its coming!
                    intent.putExtra(Constants.KEY_WHAT_TO_DO,"elder_signUp_otp");
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                }
                else{
                    Toast.makeText(this, "Please enter your medical description", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Please select your D.O.B", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void calculateAge(Calendar date_of_birth){
        Calendar today = Calendar.getInstance();
        //Entered year - Curr Year
        int age = today.get(Calendar.YEAR) - date_of_birth.get(Calendar.YEAR);

        //This is for checking diff in those years if its not exactly a yr diff then we have to reduce age by 1
        if (today.get(Calendar.DAY_OF_YEAR) < date_of_birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        elder_model.setElderAge(String.valueOf(age));
    }
}