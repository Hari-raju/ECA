package com.raju.elderlycareapplication.authentication.caretakers;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.databinding.ActivityElderDetailsBinding;
import com.raju.elderlycareapplication.helpers.user_models.ConnectedElderModel;
import com.raju.elderlycareapplication.helpers.user_models.MedReminderModel;
import com.raju.elderlycareapplication.helpers.user_models.ReminderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;

import java.util.List;

public class ElderDetailsActivity extends AppCompatActivity {
    private ActivityElderDetailsBinding detailsBinding;
    private FirebaseFirestore database;
    private ConnectedElderModel model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailsBinding = ActivityElderDetailsBinding.inflate(getLayoutInflater());
        setContentView(detailsBinding.getRoot());
        database = FirebaseFirestore.getInstance();
        //Getting Connected Elder Details
        model = (ConnectedElderModel) getIntent().getSerializableExtra("connectedModel");
        //For Initialization of values
        init();
        //For Listeners
        listeners();
    }

    private void listeners(){
        //For back button function
        detailsBinding.elderDetailsBack.setOnClickListener(v->finish());
    }

    private void init(){
        detailsBinding.elderDetailsProfile.setImageBitmap(EncoderDecoder.decodeImage(model.getElderProfile()));
        detailsBinding.elderDetailsName.setText(model.getElderName());
        detailsBinding.elderDetailsPhone.setText(model.getElderPhone());
        database.collection(Constants.KEY_ELDER_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE,model.getElderPhone())
                .get()
                .addOnCompleteListener(task -> {
                    if(task.getResult()!=null && task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        detailsBinding.elderDetailsDob.setText(doc.getString(Constants.KEY_ELDER_DOB));
                        detailsBinding.elderDetailsDesc.setText(doc.getString(Constants.KEY_ELDER_DESCRIPTION));
                        database.collection(Constants.KEY_ELDER_MED_REM_COLLECTION)
                                .whereEqualTo(Constants.KEY_ELDER_PHONE,model.getElderPhone())
                                .get().addOnCompleteListener(task1 -> {
                                    if(task1.getResult()!=null && task1.isSuccessful() && !task1.getResult().getDocuments().isEmpty()){
                                        //Getting that reminder model object
                                        DocumentSnapshot documentSnapshot = task1.getResult().getDocuments().get(0);
                                        ReminderModel reminder = documentSnapshot.toObject(ReminderModel.class);

                                        //Now retrieving values particular medicine list
                                        List<MedReminderModel> medReminderModelList = reminder.getListModel();

                                        //Now only we gonna extract values med name and med purpose
                                        StringBuilder medicines = new StringBuilder();

                                        //Getting all those values one by one
                                        for (int j = 0; j < medReminderModelList.size(); j++) {
                                            MedReminderModel i = medReminderModelList.get(j);
                                            medicines.append(i.getEldersMedName());
                                            medicines.append(" : ");
                                            medicines.append(i.getEldersMedPurpose());
                                            if (j < medReminderModelList.size() - 1) {
                                                medicines.append("\n");
                                            }
                                        }

                                        detailsBinding.elderDetailsMed.setText(medicines);
                                        detailsBinding.elderDetailsProgress.setVisibility(View.GONE);
                                    }
                                })
                                .addOnFailureListener(failed->{
                                    detailsBinding.elderDetailsProgress.setVisibility(View.GONE);
                                    Toast.makeText(this, "Failed to Fetch", Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(fail->{
                    detailsBinding.elderDetailsProgress.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to Fetch", Toast.LENGTH_SHORT).show();
                });
    }
}