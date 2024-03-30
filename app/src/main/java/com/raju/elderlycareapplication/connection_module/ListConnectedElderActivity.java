package com.raju.elderlycareapplication.connection_module;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.raju.elderlycareapplication.authentication.caretakers.ElderDetailsActivity;
import com.raju.elderlycareapplication.helpers.adapters.ListElderAdapter;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.ElderListener;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;
import com.raju.elderlycareapplication.databinding.ActivityListConnectedElderBinding;
import com.raju.elderlycareapplication.reminder_scheduling.AddCheckupReminderActivity;
import com.raju.elderlycareapplication.reminder_scheduling.AddNoOfMedActivity;

import java.util.ArrayList;
import java.util.List;

public class ListConnectedElderActivity extends AppCompatActivity implements ElderListener {

    private PreferenceManager preferenceManager;
    private ActivityListConnectedElderBinding listConnectedElderBinding;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listConnectedElderBinding = ActivityListConnectedElderBinding.inflate(getLayoutInflater());
        setContentView(listConnectedElderBinding.getRoot());
        database = FirebaseFirestore.getInstance();
        preferenceManager = new PreferenceManager(ListConnectedElderActivity.this);
        loadElders();
        listeners();
    }

    private void listeners() {
        listConnectedElderBinding.listEldersBack.setOnClickListener(
                v -> {
                    finish();
                }
        );
    }

    private void loadElders() {
        loading();
        database.collection(Constants.KEY_CONNECT_COLLECTION)
                .whereEqualTo(Constants.KEY_CARETAKER_PHONE, preferenceManager.getString(Constants.KEY_CARETAKER_PHONE))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<ConnectedElderModel> elderModelList = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            ConnectedElderModel connectedElderModel = new ConnectedElderModel(
                                    snapshot.getString(Constants.KEY_ELDER_PHONE),
                                    snapshot.getString(Constants.KEY_ELDER_NAME),
                                    snapshot.getString(Constants.KEY_ELDER_PROFILE)
                            );
                            elderModelList.add(connectedElderModel);
                        }
                        if (!elderModelList.isEmpty()) {
                            ListElderAdapter adapter = new ListElderAdapter(elderModelList,this);
                            listConnectedElderBinding.connectedEldersList.setAdapter(adapter);
                            unloading();
                        } else {
                            showError("No Connected Elder's");
                        }
                    }
                })
                .addOnFailureListener(fail -> {
                    showError(fail.getLocalizedMessage());
                });
    }

    private void loading() {
        listConnectedElderBinding.listElderProgress.setVisibility(View.VISIBLE);
        listConnectedElderBinding.connectedEldersList.setVisibility(View.GONE);
    }

    private void unloading() {
        listConnectedElderBinding.listElderProgress.setVisibility(View.GONE);
        listConnectedElderBinding.connectedEldersList.setVisibility(View.VISIBLE);
    }

    private void showError(String text) {
        listConnectedElderBinding.listElderProgress.setVisibility(View.GONE);
        listConnectedElderBinding.errorMsg.setText(text);
        listConnectedElderBinding.errorMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onElderClicked(ConnectedElderModel elderModel,int value) {
        Intent intent;
        //0 -> For Add Med Activity | 1 -> Elder Details | 2-> For Check Activity
        if(value==0){
            intent = new Intent(ListConnectedElderActivity.this, AddNoOfMedActivity.class);
        }
        else if(value==1){
            intent = new Intent(ListConnectedElderActivity.this, ElderDetailsActivity.class);
        }
        else{
            intent = new Intent(ListConnectedElderActivity.this, AddCheckupReminderActivity.class);
        }
        intent.putExtra("connectedModel",elderModel);
        startActivity(intent);
    }
}