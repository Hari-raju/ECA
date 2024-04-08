package com.raju.elderlycareapplication.authentication.elder;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raju.elderlycareapplication.authentication.home.MainActivity;
import com.raju.elderlycareapplication.databinding.ActivityElderHomeBinding;
import com.raju.elderlycareapplication.helpers.user_models.MedReminderModel;
import com.raju.elderlycareapplication.helpers.user_models.ReminderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.helpers.utils.notifications.ApiClient;
import com.raju.elderlycareapplication.helpers.utils.notifications.ApiService;
import com.raju.elderlycareapplication.reminder_display.background_services.MedReminderService;
import com.raju.elderlycareapplication.helpers.utils.notifications.NotificationUtils;
import com.raju.elderlycareapplication.services.sensors.FallDetectionService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ElderHomeActivity extends AppCompatActivity {

    private ActivityElderHomeBinding homeBinding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    final String TAG = "ElderHome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityElderHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());
        preferenceManager = new PreferenceManager(this);
        database = FirebaseFirestore.getInstance();
        homeBinding.elderProfile.setImageBitmap(EncoderDecoder.decodeImage(preferenceManager.getString(Constants.KEY_ELDER_PROFILE)));
        //Lets Check permission
        checkPermissions();
        //Listeners
        listeners();
    }

    private void getToken() {
        homeBinding.elderHomeProgres.setVisibility(View.VISIBLE);
        //Getting Token So that we can send Notifcation alert to care taker
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    database.collection(Constants.KEY_ELDER_COLLECTION)
                            .document(preferenceManager.getString(Constants.KEY_ELDER_ID))
                            .update(Constants.KEY_FCM_TOKEN, task.getResult())
                            .addOnSuccessListener(complete -> {
                                homeBinding.elderHomeProgres.setVisibility(View.GONE);
                                preferenceManager.putString(Constants.KEY_FCM_TOKEN, task.getResult());
                                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(err -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
                    Log.d("ElderHome", "token from main :" + task.getResult());
                })
                .addOnFailureListener(fail -> {
                    homeBinding.elderHomeProgres.setVisibility(View.GONE);
                    Log.d("ElderHome", Objects.requireNonNull(fail.getLocalizedMessage()));
                });
    }

    private void init() {
        homeBinding.elderProfile.setImageBitmap(EncoderDecoder.decodeImage(preferenceManager.getString(Constants.KEY_ELDER_PROFILE)));

        //Storing Medicines
        database.collection(Constants.KEY_ELDER_MED_REM_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE, preferenceManager.getString(Constants.KEY_ELDER_PHONE))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null && task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        ReminderModel model = doc.toObject(ReminderModel.class);
                        List<MedReminderModel> medReminderModelList = model.getListModel();
                        Toast.makeText(this, "Serializing..", Toast.LENGTH_SHORT).show();

                        try {
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
                            oos.writeObject(medReminderModelList);
                            String objString = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
                            preferenceManager.putString(Constants.KEY_ELDER_MEDICINE, objString);
                            oos.close();
                            Toast.makeText(this, "Serialized", Toast.LENGTH_SHORT).show();
                            startServices();
                            Log.d("ElderHome", "Connected Elder Getting Query");
                        } catch (Exception e) {
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (preferenceManager.getString(Constants.KEY_CHECK_START_TIME) != null) {
                            startServices();
                        }
                    }
                    //Lets get FCM Token
                    getToken();
                });
    }

    private void listeners() {

        homeBinding.elderAccountLogout.setOnClickListener(v -> {
            HashMap<String, Object> value = new HashMap<>();
            value.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());

            //We have to delete Fcm token and Clear Preference So only logout will be successful
            database.collection(Constants.KEY_ELDER_COLLECTION)
                    .document(preferenceManager.getString(Constants.KEY_ELDER_ID))
                    .update(value)
                    .addOnSuccessListener(task -> {
                        stopServices();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
                        preferenceManager.clear();
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(res -> Toast.makeText(this, res.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void checkPermissions() {
        String TAG = "ElderHome";
        Log.d(TAG, "checkPermissions");
        NotificationUtils.createNotificationChannel(this);

        //For Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "reqPermissions");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "permission for notification is already granted");
                checkPermissionsAlarm();
            } else {
                //Request Permission
                Log.d(TAG, "requesting permission for notification");
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        } else {
            checkPermissionsAlarm();
        }
    }

    private void checkPermissionsAlarm() {
        //For Exact Alarm
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Log.d(TAG, "Greater or Equal to S");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "alarmPermGranted");
//                getConnectedCaretaker();
                checkSensorPerm();
            } else {
                Toast.makeText(this, "Allow permission to use feature", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "reqExactAlarmPermissions");
                //Request permission for exact alarm
                alarmLauncher.launch(Manifest.permission.SCHEDULE_EXACT_ALARM);
            }
        } else {
            Log.d(TAG, "Less than S");
//            getConnectedCaretaker();
            checkSensorPerm();
        }
    }

    private final ActivityResultLauncher<String> notificationLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            checkPermissionsAlarm();
            Toast.makeText(this, "Allowed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Not Allowed", Toast.LENGTH_SHORT).show();
        }
    });

    private final ActivityResultLauncher<String> alarmLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Toast.makeText(this, "Allowed", Toast.LENGTH_SHORT).show();
            checkSensorPerm();
        } else {
            Toast.makeText(this, "Not Allowed", Toast.LENGTH_SHORT).show();
        }
    });

    private void getCheckupDetails() {
        //Lets Get Check up Start and End time
        database.collection(Constants.KEY_CHECK_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE, preferenceManager.getString(Constants.KEY_ELDER_PHONE))
                .get()
                .addOnCompleteListener(comp -> {
                    if (comp.isSuccessful() && comp.getResult() != null && !comp.getResult().getDocuments().isEmpty()) {
                        Log.d("ElderHome", "Connected Elder Getting Query Successful");
                        DocumentSnapshot documentSnapshot = comp.getResult().getDocuments().get(0);
                        //Lets Store those
                        preferenceManager.putString(Constants.KEY_CHECK_START_TIME, documentSnapshot.getString(Constants.KEY_CHECK_START_TIME));
                        preferenceManager.putString(Constants.KEY_CHECK_END_TIME, documentSnapshot.getString(Constants.KEY_CHECK_END_TIME));
                    } else {
                        Log.d("ElderHome", "Empty");
                    }
                    init();
                })
                .addOnFailureListener(result -> Toast.makeText(this, "Getting Check Up Failed", Toast.LENGTH_SHORT).show());
    }

    private void getConnectedCaretaker() {
        //Lets Store Connected Caretaker Values
        database.collection(Constants.KEY_CONNECT_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE, preferenceManager.getString(Constants.KEY_ELDER_PHONE))
                .get()//caretaker_task
                .addOnCompleteListener(task -> {
                    if (task.getResult() != null && task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putString(Constants.KEY_CONNECTED_CARETAKER, snapshot.getString(Constants.KEY_CARETAKER_PHONE));
                        database.collection(Constants.KEY_CARETAKER_COLLECTION)
                                .whereEqualTo(Constants.KEY_CARETAKER_PHONE, preferenceManager.getString(Constants.KEY_CONNECTED_CARETAKER))
                                .get()
                                .addOnCompleteListener(caretaker_task -> {
                                    if (caretaker_task.getResult() != null && caretaker_task.isSuccessful() && !caretaker_task.getResult().getDocuments().isEmpty()) {
                                        DocumentSnapshot snapshot1 = caretaker_task.getResult().getDocuments().get(0);
                                        preferenceManager.putString(Constants.KEY_CARETAKER_ID, snapshot1.getId());
                                        preferenceManager.putString(Constants.KEY_CARETAKER_FCM_TOKEN, snapshot1.getString(Constants.KEY_FCM_TOKEN));
                                        Toast.makeText(this, "Connected Details Setted", Toast.LENGTH_SHORT).show();
                                        //Lets get Check Details
                                        getCheckupDetails();
                                    }
                                })
                                .addOnFailureListener(error -> Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show());
                    } else {
                        getToken();
                        Toast.makeText(this, "No Connected Elders are there", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(caretaker_fail -> Toast.makeText(this, "Failed :" + caretaker_fail.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }

    private void startServices() {
        if(preferenceManager.getString(Constants.KEY_CARETAKER_FCM_TOKEN)!=null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, MedReminderService.class));
                startForegroundService(new Intent(this, FallDetectionService.class));
            } else {
                startService(new Intent(this, MedReminderService.class));
                startService(new Intent(this, FallDetectionService.class));
            }
        }
    }

    private void checkSensorPerm(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            if(ContextCompat.checkSelfPermission(this,Manifest.permission.HIGH_SAMPLING_RATE_SENSORS)==PackageManager.PERMISSION_GRANTED){
                checkLocationPerm();
            }
            else{
                highLauncher.launch(Manifest.permission.HIGH_SAMPLING_RATE_SENSORS);
            }
        }
        else{
            checkLocationPerm();
        }
    }

    private final ActivityResultLauncher<String> highLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Toast.makeText(this, "Allowed", Toast.LENGTH_SHORT).show();
            checkLocationPerm();
        } else {
            Toast.makeText(this, "Please Allow Alarm", Toast.LENGTH_SHORT).show();
        }
    });

    private void stopServices(){
        stopService(new Intent(this, MedReminderService.class));
        stopService(new Intent(this, FallDetectionService.class));
    }

    private void checkLocationPerm(){
        Toast.makeText(this, "Getting Permission", Toast.LENGTH_SHORT).show();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkBackgroundPerm();
        } else {
            fineLocation.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void checkBackgroundPerm() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getConnectedCaretaker();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                background.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            }
        }
    }

    private final ActivityResultLauncher<String> fineLocation = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted -> {
                if(isGranted){
                    checkBackgroundPerm();
                }
            }
    );

    private final ActivityResultLauncher<String> background = registerForActivityResult(new ActivityResultContracts.RequestPermission(),
            isGranted->{
                if(isGranted){
                    getConnectedCaretaker();
                }
            });
}