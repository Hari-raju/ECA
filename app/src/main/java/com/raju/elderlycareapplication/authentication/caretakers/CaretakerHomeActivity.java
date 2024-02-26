package com.raju.elderlycareapplication.authentication.caretakers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import com.raju.elderlycareapplication.connection_module.AddElderActivity;
import com.raju.elderlycareapplication.connection_module.ListConnectedElderActivity;
import com.raju.elderlycareapplication.databinding.ActivityCaretakerHomeBinding;

public class CaretakerHomeActivity extends AppCompatActivity {

    private ActivityCaretakerHomeBinding homeBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeBinding = ActivityCaretakerHomeBinding.inflate(getLayoutInflater());
        setContentView(homeBinding.getRoot());
        listeners();
    }

    private void listeners(){
        homeBinding.connectElder.setOnClickListener(v->{
            startActivity(new Intent(CaretakerHomeActivity.this, AddElderActivity.class));
        });

        homeBinding.viewElder.setOnClickListener(v->{
            startActivity(new Intent(CaretakerHomeActivity.this, ListConnectedElderActivity.class));
        });
    }
}