package com.raju.elderlycareapplication.connection_module;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.authentication.home.OtpActivity;
import com.raju.elderlycareapplication.databinding.ActivityAddElderBinding;
import com.raju.elderlycareapplication.helpers.user_models.ConnectModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

import java.util.Objects;

public class AddElderActivity extends AppCompatActivity {

    private final String tag = "AddElderActivityTag";
    private FirebaseFirestore database;
    private ActivityAddElderBinding connectElders;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connectElders=ActivityAddElderBinding.inflate(getLayoutInflater());
        setContentView(connectElders.getRoot());
        connectElders.addElderCcp.registerCarrierNumberEditText(connectElders.addElderNumber.getEditText());
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(AddElderActivity.this);
        listeners();
    }

    private void listeners(){
        connectElders.addElderBackButton.setOnClickListener(v->{
            finish();
        });

        connectElders.connect.setOnClickListener(v->{
            isDetailsValid();
        });
    }

    private void isDetailsValid(){
        loading();
        if(!connectElders.addElderCcp.isValidFullNumber()){
            unloading();
            connectElders.addElderNumber.setError("Invalid number");
            return;
        }
        else{
            database.collection(Constants.KEY_ELDER_COLLECTION).whereEqualTo(Constants.KEY_ELDER_PHONE,connectElders.addElderCcp.getFullNumberWithPlus()).get()
                    .addOnCompleteListener(task->{
                        if(task.getResult()!=null && task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
                            connectToElders();
                        }
                        else{
                            unloading();
                            connectElders.addElderNumber.setError("No Elder's Found");
                            return;
                        }
                    })
                    .addOnFailureListener(failure->{
                        unloading();
                        Log.d(tag, Objects.requireNonNull(failure.getMessage()));
                        Toast.makeText(this, failure.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    });
        }
    }

    private void connectToElders(){
        Intent intent = new Intent(AddElderActivity.this, OtpActivity.class);
        intent.putExtra("whatToDo","connect");
        ConnectModel connectModel = new ConnectModel(connectElders.addElderCcp.getFullNumberWithPlus(),preferenceManager.getString(Constants.KEY_CARETAKER_PHONE));
        intent.putExtra("connectModel",connectModel);
        unloading();
        startActivity(intent);
    }

    private void loading(){
        connectElders.addElderProgress.setVisibility(View.VISIBLE);
        connectElders.connect.setVisibility(View.GONE);
    }

    private void unloading(){
        connectElders.addElderProgress.setVisibility(View.GONE);
        connectElders.connect.setVisibility(View.VISIBLE);
    }
}