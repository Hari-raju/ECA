package com.raju.elderlycareapplication.authentication.caretakers;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivitySignInCareTakersBinding;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.helpers.utils.Validation;

import java.util.Objects;

public class SignInCareTakersActivity extends AppCompatActivity {
    private ActivitySignInCareTakersBinding careTakersBinding;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        careTakersBinding=ActivitySignInCareTakersBinding.inflate(getLayoutInflater());
        setContentView(careTakersBinding.getRoot());

        //Here we binding the countryCode with number in editText
        careTakersBinding.countryCodePickerCaretaker.registerCarrierNumberEditText(careTakersBinding.caretakerLoginPhone.getEditText());
        Listeners();
    }

    private void Listeners(){

        //Back button function
        careTakersBinding.caretakerLoginBack.setOnClickListener(v-> finish());

        //Create Acc Function
        careTakersBinding.caretakerCreateAcc.setOnClickListener(v->{
            startActivity(new Intent(SignInCareTakersActivity.this, SignUpCaretakersActivity.class));
            overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
        });

        //Login Function
        careTakersBinding.caretakerLogin.setOnClickListener(v->{
            loading();
            //If details are valid we will check in database
            if(checkDetails()){
                //Creating a instance for fireStore db
                FirebaseFirestore database = FirebaseFirestore.getInstance();

                //Now we are in caretaker login we will only check whether a account exists or not in only caretakers collection
                //We are searching for a document which have number and pass same as entered by the user
                database.collection(Constants.KEY_CARETAKER_COLLECTION)
                        .whereEqualTo(Constants.KEY_CARETAKER_PHONE,careTakersBinding.countryCodePickerCaretaker.getFullNumberWithPlus())
                        .whereEqualTo(Constants.KEY_PASSWORD, Objects.requireNonNull(careTakersBinding.caretakerLoginPass.getEditText()).getText().toString())
                        .get()
                        .addOnCompleteListener(
                                task->{
                                    //After searching we are checking document exist or not
                                    if (task.getResult()!=null && task.isSuccessful() && task.getResult().getDocuments().size()>0){

                                        //If doc exist we're storing it inside documentSnapshot
                                        //then we are storing it in local database for future use
                                        DocumentSnapshot document = task.getResult().getDocuments().get(0);
                                        preferenceManager=new PreferenceManager(SignInCareTakersActivity.this);
                                        preferenceManager.putBoolean(Constants.KEY_IS_CARETAKER_SIGNED_IN,true);
                                        preferenceManager.putString(Constants.KEY_CARETAKER_ID,document.getId());
                                        preferenceManager.putString(Constants.KEY_CARETAKER_PHONE,document.getString(Constants.KEY_CARETAKER_PHONE));
                                        preferenceManager.putString(Constants.KEY_CARETAKER_NAME,document.getString(Constants.KEY_CARETAKER_NAME));
                                        preferenceManager.putString(Constants.KEY_CARETAKER_PROFILE,document.getString(Constants.KEY_CARETAKER_PROFILE));

                                        //Navigate to Home Activity
                                        Intent intent = new Intent(SignInCareTakersActivity.this,CaretakerHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        unloading();
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                                    }
                                    else{
                                        unloading();
                                        Toast.makeText(this, "Account not found", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        )
                        .addOnFailureListener(fail->{
                            Toast.makeText(this, fail.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Log.d("SignInCareTakersActivity", Objects.requireNonNull(fail.getMessage()));
                        });
            }
            else{
                unloading();
            }
        });
    }

    //Loading UI
    private void loading(){
        careTakersBinding.caretakerLogin.setVisibility(View.GONE);
        careTakersBinding.caretakerLoginProgress.setVisibility(View.VISIBLE);
    }

    //UnLoading UI
    private void unloading(){
        careTakersBinding.caretakerLogin.setVisibility(View.VISIBLE);
        careTakersBinding.caretakerLoginProgress.setVisibility(View.GONE);
    }
    private Boolean checkDetails(){
        //Lets first check phone
        if(!careTakersBinding.countryCodePickerCaretaker.isValidFullNumber()){
            careTakersBinding.caretakerLoginPhone.setError("Enter a valid phone");
            return false;
        }
        else{
            careTakersBinding.caretakerLoginPhone.setError(null);
        }

        //Now check password
        if(!Validation.isPassValid(Objects.requireNonNull(careTakersBinding.caretakerLoginPass.getEditText()).getText().toString())){
            careTakersBinding.caretakerLoginPass.setError("Invalid password");
            return false;
        }
        else{
            careTakersBinding.caretakerLoginPass.setError(null);
        }
        return true;
    }
}