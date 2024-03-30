package com.raju.elderlycareapplication.reminder_scheduling;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivityAddMedReminderBinding;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.user_models.MedReminderModel;
import com.raju.elderlycareapplication.helpers.user_models.ReminderModel;
import com.raju.elderlycareapplication.helpers.utils.Callback;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.helpers.utils.StorageHelper;

import java.util.ArrayList;
import java.util.List;

public class AddMedReminderActivity extends AppCompatActivity {
    private ActivityAddMedReminderBinding reminderBinding;
    private int noOfMed;
    private String elderId;
    private ConnectedElderModel model;
    private int count = 0;
    private String medTime = null;
    private List<MedReminderModel> medicines;
    private FirebaseFirestore database;
    private Callback callback;
    private List<Uri> medProfile;

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reminderBinding = ActivityAddMedReminderBinding.inflate(getLayoutInflater());
        setContentView(reminderBinding.getRoot());

        //Initiating Local DB so we can use values
        preferenceManager = new PreferenceManager(this);
        //Initiating Callback
        init();

        //No of Med From Prev Activity And ConnectModel
        noOfMed = getIntent().getIntExtra("NoMed", 0);
        model = (ConnectedElderModel) getIntent().getSerializableExtra("connectedModel");

        //Based on that we are List for medications and med image
        medicines = new ArrayList<>(noOfMed);
        medProfile = new ArrayList<>(noOfMed);

        //Creating DB instance
        database = FirebaseFirestore.getInstance();

        //Loading Animation
        loading();

        //Getting ELder Id So that we can store it under that ID inside Storage
        database.collection(Constants.KEY_ELDER_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE, model.getElderPhone())
                .get()
                .addOnCompleteListener(doc -> {
                    if (doc.getResult() != null && doc.isSuccessful() && !doc.getResult().getDocuments().isEmpty()) {
                        elderId = doc.getResult().getDocuments().get(0).getId();
                        unLoading();
                    }
                })
                .addOnFailureListener(fail -> {
                    unLoading();
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                });

        //Disabling ImageView SO users Cant use its onCLick Events
        medPicDisable();

        //Listeners
        listeners();
    }

    private void listeners() {
        reminderBinding.addMedNext.setOnClickListener(v -> {
            if (checkDetails()) {
                loading();
                if (count < noOfMed) {
                    //Increment Count and Check If count <= no of
                    count++;
                    StorageHelper.uploadImageToStorage(medProfile.get(count-1),elderId,callback,AddMedReminderActivity.this);
                }
            }
        });


        //Image Adding by Pressing Plus Image
        reminderBinding.addMedPic.setOnClickListener(v -> {
            //Add Pic func
            launcher.launch("image/*");
        });


        //Image Adding via ImageView
        reminderBinding.med.setOnClickListener(v -> launcher.launch("image/*"));

        //Back Button
        reminderBinding.addMedBack.setOnClickListener(v -> finish());

        //Med Time Selector
        reminderBinding.addMedTime.setOnClickListener(v -> {
            //Fire Time Picker
            MaterialTimePicker picker = new MaterialTimePicker.Builder()
                    .setTitleText("Medication Alarm")
                    .setHour(12)
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setMinute(0)
                    .build();

            picker.show(getSupportFragmentManager(), "ECA");
            picker.addOnPositiveButtonClickListener(view -> {
                if (picker.getHour() > 12) {
                    reminderBinding.addMedTime.setText(String.format("%d : %d PM ", picker.getHour() - 12, picker.getMinute()));
                } else {
                    reminderBinding.addMedTime.setText(String.format("%d : %d AM ", picker.getHour(), picker.getMinute()));
                }
                medTime = EncoderDecoder.timePickerToString(picker);
            });
        });
    }

    private boolean checkDetails() {
        String medName = reminderBinding.addMedName.getEditText().getText().toString();
        String medPurpose = reminderBinding.addMedPurpose.getEditText().getText().toString();

//      Checking whether image uri is null or not
        if (medProfile.get(count) == null) {
            Toast.makeText(this, "Upload Medicine Photo", Toast.LENGTH_SHORT).show();
            return false;
        }

//      Checking MedName empty or not
        if (medName.isEmpty()) {
            reminderBinding.addMedName.setError("Enter Medicine Name!");
            return false;
        }


//      Checking MedPurpose empty or not
        if (medPurpose.isEmpty()) {
            reminderBinding.addMedPurpose.setError("Enter Medicine Purpose!");
            return false;
        }

//      Checking MedTime null or not
        if (medTime == null) {
            Toast.makeText(this, "Select Medicine Time", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private final ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri uri) {
                    if (uri != null) {
                        medPicEnable();
                        reminderBinding.addMedPic.setVisibility(View.GONE);
                        Glide.with(AddMedReminderActivity.this)
                                .load(uri)
                                .error(R.drawable.error)
                                .placeholder(R.drawable.place_holder)
                                .into(reminderBinding.med);

                        //Setting acc to Count
                        if(count<medProfile.size()){
                            medProfile.set(count, uri);
                        }else{
                            medProfile.add(uri);
                        }
                    }
                }
            }
    );

    private void medPicDisable() {
        reminderBinding.med.setEnabled(false);
    }

    private void medPicEnable() {
        reminderBinding.med.setEnabled(true);
    }


    //Loading Anim
    private void loading() {
        reminderBinding.addMedNext.setVisibility(View.GONE);
        reminderBinding.addMedProgress.setVisibility(View.VISIBLE);
    }

    //Unloading Anim
    private void unLoading() {
        reminderBinding.addMedNext.setVisibility(View.VISIBLE);
        reminderBinding.addMedProgress.setVisibility(View.GONE);
    }

    private void init() {
        callback = url -> {
            MedReminderModel medReminderModel = new MedReminderModel(
                    url,
                    reminderBinding.addMedPurpose.getEditText().getText().toString(),
                    reminderBinding.addMedName.getEditText().getText().toString(),
                    medTime
            );
            medicines.add(medReminderModel);

            if (count == noOfMed) {
                ReminderModel reminderModel = new ReminderModel(
                        model.getElderPhone(),
                        preferenceManager.getString(Constants.KEY_CARETAKER_PHONE),
                        Integer.toString(noOfMed),
                        medicines
                );

                database.collection(Constants.KEY_ELDER_MED_REM_COLLECTION)
                        .add(reminderModel)
                        .addOnSuccessListener(success->{
                            Toast.makeText(AddMedReminderActivity.this, "Added", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(fail->{
                            Log.e("DataBase",fail.getLocalizedMessage());
                            Toast.makeText(AddMedReminderActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        });
            }
            else{
                reminderBinding.addMedPic.setVisibility(View.VISIBLE);
                int id = getResources().getIdentifier("@drawable/medicine", null, AddMedReminderActivity.this.getPackageName());
                reminderBinding.med.setImageResource(id);
                reminderBinding.addMedPurpose.getEditText().setText(null);
                reminderBinding.addMedPurpose.setError(null);
                reminderBinding.addMedName.getEditText().setText(null);
                reminderBinding.addMedName.setError(null);
                reminderBinding.addMedTime.setText(R.string.medication_time);
                reminderBinding.addMedName.requestFocus();
                unLoading();
                Toast.makeText(AddMedReminderActivity.this, "Add Another Medication", Toast.LENGTH_SHORT).show();
            }
        };
    }
}