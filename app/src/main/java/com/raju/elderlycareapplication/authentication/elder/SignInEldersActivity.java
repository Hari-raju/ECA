package com.raju.elderlycareapplication.authentication.elder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivitySignInEldersBinding;
import com.raju.elderlycareapplication.helpers.user_models.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.helpers.utils.Validation;

import java.util.Objects;

public class SignInEldersActivity extends AppCompatActivity {
    private ActivitySignInEldersBinding eldersBinding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eldersBinding=ActivitySignInEldersBinding.inflate(getLayoutInflater());
        setContentView(eldersBinding.getRoot());

        //Binding country code with phone editText
        eldersBinding.countryCodePicker.registerCarrierNumberEditText(eldersBinding.elderLoginPhone.getEditText());
        Listeners();
    }

    private void Listeners(){

        //Back button function
        eldersBinding.elderLoginBack.setOnClickListener(v-> finish());

        //Create Acc Function
        eldersBinding.createElderAcc.setOnClickListener(v->{
            startActivity(new Intent(SignInEldersActivity.this, SignUpEldersActivity.class));
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });

        //Login Function
        eldersBinding.elderLogin.setOnClickListener(v->{
            loading();

            //If details are valid we will check in database
            if(checkDetails()){
                //Creating a instance for fireStore db
                FirebaseFirestore database = FirebaseFirestore.getInstance();

                //Now we are in elder login we will only check whether a account exists or not in only elders collection
                //We are searching for a document which have number and pass same as entered by the user
                database.collection(Constants.KEY_ELDER_COLLECTION)
                        .whereEqualTo(Constants.KEY_ELDER_PHONE,eldersBinding.countryCodePicker.getFullNumberWithPlus())
                        .whereEqualTo(Constants.KEY_PASSWORD, Objects.requireNonNull(eldersBinding.elderLoginPass.getEditText()).getText().toString())
                        .get()
                        .addOnCompleteListener(task->{
                            //After searching we are checking document exist or not
                            if(task.getResult()!=null && task.isSuccessful() && task.getResult().getDocuments().size()>0){

                                //If doc exist we're storing it inside documentSnapshot
                                //then we are storing it in local database for future use
                                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                preferenceManager = new PreferenceManager(SignInEldersActivity.this);
                                preferenceManager.putBoolean(Constants.KEY_IS_ELDER_SIGNED_IN,true);
                                preferenceManager.putString(Constants.KEY_ELDER_ID,document.getId());
                                preferenceManager.putString(Constants.KEY_ELDER_PHONE,document.getString(Constants.KEY_ELDER_PHONE));
                                preferenceManager.putString(Constants.KEY_ELDER_NAME,document.getString(Constants.KEY_ELDER_NAME));
                                preferenceManager.putString(Constants.KEY_ELDER_DESCRIPTION,document.getString(Constants.KEY_ELDER_DESCRIPTION));
                                preferenceManager.putString(Constants.KEY_ELDER_AGE,document.getString(Constants.KEY_ELDER_AGE));
                                preferenceManager.putString(Constants.KEY_ELDER_PROFILE,document.getString(Constants.KEY_ELDER_PROFILE));
                                preferenceManager.putString(Constants.KEY_ELDER_DOB,document.getString(Constants.KEY_ELDER_DOB));

                                //Navigate to Home Activity
                                Intent intent = new Intent(SignInEldersActivity.this, ElderHomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                unLoading();
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                            }
                            else{
                                Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show();
                                unLoading();
                            }
                        })
                        .addOnFailureListener(fail->{
                            unLoading();
                            Toast.makeText(this, fail.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("SignInEldersActivity", Objects.requireNonNull(fail.getMessage()));
                        });
            }
            else{
                unLoading();
            }
        });
    }

    //Loading UI
    private void loading(){
        eldersBinding.elderLogin.setVisibility(View.GONE);
        eldersBinding.elderLoginProgress.setVisibility(View.VISIBLE);
    }

    //UnLoading UI
    private void unLoading(){
        eldersBinding.elderLogin.setVisibility(View.VISIBLE);
        eldersBinding.elderLoginProgress.setVisibility(View.GONE);
    }


    //Validation of details
    private Boolean checkDetails(){

        //Phone Num validation
        if(!eldersBinding.countryCodePicker.isValidFullNumber()){
            eldersBinding.elderLoginPhone.setError("Enter a valid phone");
            return false;
        }
        else{
            eldersBinding.elderLoginPhone.setError(null);
        }

        //Password Validation
        if(!Validation.isPassValid(Objects.requireNonNull(eldersBinding.elderLoginPass.getEditText()).getText().toString())) {
            eldersBinding.elderLoginPass.setError("Invalid password");
        }
        else{
            eldersBinding.elderLoginPass.setError(null);
        }
        return true;
    }
}