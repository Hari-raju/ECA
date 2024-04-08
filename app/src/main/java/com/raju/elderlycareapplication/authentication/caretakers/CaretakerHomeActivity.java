package com.raju.elderlycareapplication.authentication.caretakers;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.raju.elderlycareapplication.connection_module.AddElderActivity;
import com.raju.elderlycareapplication.connection_module.ListConnectedElderActivity;
import com.raju.elderlycareapplication.databinding.ActivityCaretakerHomeBinding;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.helpers.utils.notifications.FCMNotificationService;
import com.raju.elderlycareapplication.helpers.utils.notifications.NotificationUtils;
import com.raju.elderlycareapplication.reminder_scheduling.ElderListActivity;

public class CaretakerHomeActivity extends AppCompatActivity {

    private ActivityCaretakerHomeBinding homeBinding;
    private final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 100;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityCaretakerHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(this);
        listeners();
        checkPermissionForNotification();
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(token -> {
                    Log.d("CareTakerHome","token :"+token.getResult());
                    database.collection(Constants.KEY_CARETAKER_COLLECTION)
                            .document(preferenceManager.getString(Constants.KEY_CARETAKER_ID))
                            .update(Constants.KEY_FCM_TOKEN,token.getResult())
                            .addOnSuccessListener(task->{
                                preferenceManager.putString(Constants.KEY_FCM_TOKEN,token.getResult());
                                Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(res->{
                                Toast.makeText(this, "Fail :"+res.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(failed -> {

                });
    }

    private void checkPermissionForNotification() {
        String TAG = "CareTakerHome";
        Log.d(TAG, "checkPermissions");
        NotificationUtils.createNotificationChannel(this);

        //For Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Log.d(TAG, "reqPermissions");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                //Getting Token
                getToken();
                Log.d(TAG, "permission for notification is already granted");
            } else {
                //Request Permission
                Log.d(TAG, "requesting permission for notification");
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
        else {
            //Getting Token
            getToken();
        }
    }

    private final ActivityResultLauncher<String> notificationLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            //Getting Token
            getToken();
            Toast.makeText(this, "Allowed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please Allow Alarm", Toast.LENGTH_SHORT).show();
        }
    });

    private void listeners() {

        //View Account
        homeBinding.accountDetails.setOnClickListener(v -> {
            Intent intent = new Intent(CaretakerHomeActivity.this, CaretakerAccountActivity.class);
            startActivity(intent);
        });

        //Generate Report
        homeBinding.sendReport.setOnClickListener(v -> checkPermissions());

        homeBinding.connectElder.setOnClickListener(v -> startActivity(new Intent(CaretakerHomeActivity.this, AddElderActivity.class)));

        homeBinding.viewElder.setOnClickListener(v -> startActivity(new Intent(CaretakerHomeActivity.this, ListConnectedElderActivity.class)));
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                moveToReport();
            } else {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                    reqLauncher.launch(intent);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    reqLauncher.launch(intent);
                }
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                moveToReport();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private final ActivityResultLauncher<Intent> reqLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), o -> {
        if (o.getResultCode() == Activity.RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    moveToReport();
                } else {
                    //Add a Alert Box
                    Toast.makeText(CaretakerHomeActivity.this, "You have to accept permission to generate report", Toast.LENGTH_SHORT).show();
                }
            }
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                moveToReport();
            } else {
                Toast.makeText(this, "You have to accept permission to generate report", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveToReport() {
        Intent intent = new Intent(CaretakerHomeActivity.this, ElderListActivity.class);
        startActivity(intent);
    }
}