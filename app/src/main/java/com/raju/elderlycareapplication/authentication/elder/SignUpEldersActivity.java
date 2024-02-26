package com.raju.elderlycareapplication.authentication.elder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivitySignUpEldersBinding;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.Validation;

import java.util.Objects;

public class SignUpEldersActivity extends AppCompatActivity {

    private ActivitySignUpEldersBinding signUpEldersBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signUpEldersBinding = ActivitySignUpEldersBinding.inflate(getLayoutInflater());
        setContentView(signUpEldersBinding.getRoot());
        //Here we combining the country code to the phone num editText
        signUpEldersBinding.elderSignupCountycode.registerCarrierNumberEditText(signUpEldersBinding.elderSignupPhone.getEditText());

        //On Click Listeners
        listeners();
    }

    private void listeners() {

        //Back button function
        signUpEldersBinding.elderSignupBack.setOnClickListener(v -> finish());

        //Next Button it will only work if all details are valid
        signUpEldersBinding.elderSignupNext.setOnClickListener(v -> {
            //it becomes true only if all details are correct
            if (checkDetails()) {
                //We here putting elders name,pass and phone in a object and sending it to next activity as an extra
                Elder_Model elder_model = new Elder_Model(
                        signUpEldersBinding.elderSignupCountycode.getFullNumberWithPlus(),
                        Objects.requireNonNull(signUpEldersBinding.elderSignupPass.getEditText()).getText().toString(),
                        Objects.requireNonNull(signUpEldersBinding.elderSignupElderName.getEditText()).getText().toString()
                );
                Intent intent = new Intent(SignUpEldersActivity.this, ElderDescriptionActivity.class);
                intent.putExtra("elderData", elder_model);
                startActivity(intent);
                //Entry and exit Animation
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    private Boolean checkDetails() {
        //Phone Num Validation
        if (!signUpEldersBinding.elderSignupCountycode.isValidFullNumber()) {
            signUpEldersBinding.elderSignupPhone.setError("Enter a Valid Phone");
            return false;
        } else {
            signUpEldersBinding.elderSignupPhone.setError(null);
        }

        //Elder name validation
        if (!Validation.isNameValid(Objects.requireNonNull(signUpEldersBinding.elderSignupElderName.getEditText()).getText().toString())) {
            signUpEldersBinding.elderSignupElderName.setError("Enter a valid name");
            return false;
        } else {
            signUpEldersBinding.elderSignupElderName.setError(null);
        }

        //Pass Validation
        if (!Validation.isPassValid(Objects.requireNonNull(signUpEldersBinding.elderSignupPass.getEditText()).getText().toString())) {
            signUpEldersBinding.elderSignupPass.setError("Enter a Strong Password");
            return false;
        } else {
            signUpEldersBinding.elderSignupPass.setError(null);
        }

        //Con Pass Validation
        if (!Validation.isConfirmPassValid(signUpEldersBinding.elderSignupPass.getEditText().getText().toString(), Objects.requireNonNull(signUpEldersBinding.elderSignupConfirmPass.getEditText()).getText().toString())) {
            signUpEldersBinding.elderSignupConfirmPass.setError("Password Mismatches");
            return false;
        } else {
            signUpEldersBinding.elderSignupConfirmPass.setError(null);
        }

        return true;
    }

}