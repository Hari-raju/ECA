package com.raju.elderlycareapplication.reminder_scheduling;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.raju.elderlycareapplication.databinding.ActivityElderListBinding;
import com.raju.elderlycareapplication.helpers.adapters.ReportElderListAdapter;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.ElderReportListener;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class ElderListActivity extends AppCompatActivity implements ElderReportListener {
    private ActivityElderListBinding listBinding;
    private FirebaseFirestore database;
    private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listBinding = ActivityElderListBinding.inflate(getLayoutInflater());
        setContentView(listBinding.getRoot());
        preferenceManager = new PreferenceManager(this);
        database = FirebaseFirestore.getInstance();
        init();
        listeners();
    }

    private void listeners(){
        listBinding.eldersListBack.setOnClickListener(v->finish());
    }
    private void init(){
        loading();
        database.collection(Constants.KEY_CONNECT_COLLECTION)
                .whereEqualTo(Constants.KEY_CARETAKER_PHONE,preferenceManager.getString(Constants.KEY_CARETAKER_PHONE))
                .get()
                .addOnCompleteListener(task -> {
                   if(task.getResult()!=null && task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
                       List<ConnectedElderModel> connectedElder = new ArrayList<>();
                       for (QueryDocumentSnapshot snapshot : task.getResult()) {
                           ConnectedElderModel connectedElderModel = new ConnectedElderModel(
                                   snapshot.getString(Constants.KEY_ELDER_PHONE),
                                   snapshot.getString(Constants.KEY_ELDER_NAME),
                                   snapshot.getString(Constants.KEY_ELDER_PROFILE)
                           );
                           connectedElder.add(connectedElderModel);
                       }
                       if (!connectedElder.isEmpty()) {
                           ReportElderListAdapter adapter = new ReportElderListAdapter(connectedElder,this);
                           listBinding.eldersList.setAdapter(adapter);
                           unloading();
                       } else {
                           unloading();
                           showError("No Connected Elder's");
                       }
                   }
                   else{
                       unloading();
                       showError("No Connected Elder's");
                   }
                })
                .addOnFailureListener(fail->{
                    unloading();
                    showError(fail.getLocalizedMessage());
                });
    }

    private void loading() {
        listBinding.elderListProgress.setVisibility(View.VISIBLE);
        listBinding.eldersList.setVisibility(View.GONE);
    }

    private void unloading() {
        listBinding.elderListProgress.setVisibility(View.GONE);
        listBinding.eldersList.setVisibility(View.VISIBLE);
    }

    private void showError(String text) {
        listBinding.elderListProgress.setVisibility(View.GONE);
        listBinding.elderListErrorMsg.setText(text);
        listBinding.elderListErrorMsg.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClicked(Elder_Model model) {
        Intent intent = new Intent(ElderListActivity.this,GenerateReportActivity.class);
        intent.putExtra("elderDate",model);
        startActivity(intent);
    }
}