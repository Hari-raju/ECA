package com.raju.elderlycareapplication.authentication.home;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.authentication.caretakers.CaretakerHomeActivity;
import com.raju.elderlycareapplication.authentication.elder.ElderHomeActivity;
import com.raju.elderlycareapplication.databinding.ActivityProfileBinding;
import com.raju.elderlycareapplication.helpers.user_models.CaretakerModel;
import com.raju.elderlycareapplication.helpers.user_models.Constants;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding profileBinding;
    private Elder_Model elder_model;
    private String whatToDo;
    private String profileUrl;
    private PreferenceManager preferenceManager;
    private CaretakerModel caretakerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileBinding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(profileBinding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        //We are getting from this activity is called
        whatToDo = getIntent().getStringExtra("whatToDo");
        //If its from elder then get elder data object
        if (whatToDo != null) {
            if (whatToDo.equals("elderAcc")) {
                elder_model = (Elder_Model) getIntent().getSerializableExtra("elderData");
            } else {
                caretakerModel = (CaretakerModel) getIntent().getSerializableExtra("caretakerData");
            }
        }
        listeners();
    }

    private void listeners() {
        //Adding a profile
        profileBinding.addProfileBtn.setOnClickListener(v -> launcher.launch("image/*"));

        //Done is to create a Account
        profileBinding.done.setOnClickListener(v -> {
            if (profileUrl != null) {
                loading();
                createAcc();
            } else {
                Toast.makeText(this, "Add a Profile!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri o) {
                    if (o != null) {
                        profileBinding.usersProfile.setImageURI(o);
                        profileBinding.done.setVisibility(View.VISIBLE);
                        profileUrl = EncoderDecoder.encodeImage(o, ProfileActivity.this);

                        //Same Checking to identify where its coming from whether elder or caretaker activity
                        if (whatToDo != null) {
                            //If elders means set their pic string to the model else set it to the caretakers model
                            if (whatToDo.equals("elderAcc")) {
                                elder_model.setElderProfile(profileUrl);
                            } else {
                                caretakerModel.setCaretakerProfile(profileUrl);
                            }
                        }
                    }
                }
            }
    );

    private void loading() {
        profileBinding.addProfileBtn.setVisibility(View.GONE);
        profileBinding.done.setVisibility(View.GONE);
        profileBinding.progressProfile.setVisibility(View.VISIBLE);
    }

    private void unloading() {
        profileBinding.addProfileBtn.setVisibility(View.VISIBLE);
        profileBinding.done.setVisibility(View.VISIBLE);
        profileBinding.progressProfile.setVisibility(View.GONE);
    }

    private void createAcc() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (whatToDo != null) {
            if (whatToDo.equals("elderAcc")) {
                database.collection(Constants.KEY_ELDER_COLLECTION)
                        .add(elder_model)
                        .addOnSuccessListener(
                                documentReference -> {
                                    //After Creating Acc we're storing those values in local DB for future use
                                    preferenceManager.putBoolean(Constants.KEY_IS_ELDER_SIGNED_IN, true);
                                    preferenceManager.putString(Constants.KEY_ELDER_ID, documentReference.getId());
                                    preferenceManager.putString(Constants.KEY_ELDER_NAME, elder_model.getElderName());
                                    preferenceManager.putString(Constants.KEY_ELDER_AGE, elder_model.getElderAge());
                                    preferenceManager.putString(Constants.KEY_ELDER_DOB, elder_model.getDate_of_birth());
                                    preferenceManager.putString(Constants.KEY_ELDER_DESCRIPTION, elder_model.getElderDescription());
                                    preferenceManager.putString(Constants.KEY_ELDER_PHONE, elder_model.getElderPhone());
                                    preferenceManager.putString(Constants.KEY_ELDER_PROFILE, elder_model.getElderProfile());
                                    Intent intent = new Intent(ProfileActivity.this, ElderHomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                        )
                        .addOnFailureListener(failure -> {
                            unloading();
                            Toast.makeText(this, "Failed to create", Toast.LENGTH_SHORT).show();
                        });
            } else {
                database.collection(Constants.KEY_CARETAKER_COLLECTION)
                        .add(caretakerModel)
                        .addOnSuccessListener(
                                documentReference -> {
                                    preferenceManager.putBoolean(Constants.KEY_IS_CARETAKER_SIGNED_IN,true);
                                    preferenceManager.putString(Constants.KEY_CARETAKER_ID,documentReference.getId());
                                    preferenceManager.putString(Constants.KEY_CARETAKER_NAME,caretakerModel.getCaretakerName());
                                    preferenceManager.putString(Constants.KEY_CARETAKER_PHONE,caretakerModel.getCaretakerPhone());
                                    preferenceManager.putString(Constants.KEY_CARETAKER_PROFILE, caretakerModel.getCaretakerProfile());
                                    Intent intent = new Intent(ProfileActivity.this, CaretakerHomeActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                        .addOnFailureListener(failure -> {
                            unloading();
                            Toast.makeText(this, "Failed to create", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }
}
