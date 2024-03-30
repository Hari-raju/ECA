package com.raju.elderlycareapplication.authentication.home;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.authentication.caretakers.CaretakerHomeActivity;
import com.raju.elderlycareapplication.authentication.elder.ElderHomeActivity;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

public class MainActivity extends AppCompatActivity {

    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(MainActivity.this);
        setContentView(R.layout.activity_main);
        new Handler(Looper.getMainLooper()).postDelayed(
                () -> {
                    //If elder already signed in open their account
                    if (preferenceManager.getBoolean(Constants.KEY_IS_ELDER_SIGNED_IN)) {
                        startActivity(new Intent(MainActivity.this, ElderHomeActivity.class));
                    }

                    //If caretaker already signed in then open their account
                    else if (preferenceManager.getBoolean(Constants.KEY_IS_CARETAKER_SIGNED_IN)) {
                        startActivity(new Intent(MainActivity.this, CaretakerHomeActivity.class));
                    }

                    //Else open entry activity
                    else {
                        startActivity(new Intent(MainActivity.this, EntryActivity.class));
                    }
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                },
                1000);
    }
}