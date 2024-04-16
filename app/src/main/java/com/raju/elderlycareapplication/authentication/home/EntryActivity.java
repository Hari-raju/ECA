package com.raju.elderlycareapplication.authentication.home;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.authentication.caretakers.SignInCareTakersActivity;
import com.raju.elderlycareapplication.authentication.elder.SignInEldersActivity;
import com.raju.elderlycareapplication.databinding.ActivityEntryBinding;

public class EntryActivity extends AppCompatActivity {
    private ActivityEntryBinding entryBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entryBinding = ActivityEntryBinding.inflate(getLayoutInflater());
        setContentView(entryBinding.getRoot());
        Listeners();
    }

    private void Listeners() {
        entryBinding.elderLogin.setOnClickListener(v -> {
                    startActivity(new Intent(EntryActivity.this, SignInEldersActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
        );

        entryBinding.caretakerLogin.setOnClickListener(v -> {
                    startActivity(new Intent(EntryActivity.this, SignInCareTakersActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                }
        );
    }
}