package com.raju.elderlycareapplication.reminder_scheduling;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.databinding.ActivityGenerateReportBinding;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.user_models.MedReminderModel;
import com.raju.elderlycareapplication.helpers.user_models.ReminderModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class GenerateReportActivity extends AppCompatActivity {
    private ActivityGenerateReportBinding generateReportBinding;
    private FirebaseFirestore database;
    private Elder_Model model;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        generateReportBinding = ActivityGenerateReportBinding.inflate(getLayoutInflater());
        setContentView(generateReportBinding.getRoot());
        database=FirebaseFirestore.getInstance();
        model = (Elder_Model) getIntent().getSerializableExtra("elderDate");
        generateReportBinding.root.setVisibility(View.INVISIBLE);
        init();
        listeners();
    }

    private void init(){
        //Setting Name and Profile
        generateReportBinding.reportEldername.setText(model.getElderName().toUpperCase());
        generateReportBinding.reportProfile.setImageBitmap(EncoderDecoder.decodeImage(model.getElderProfile()));

        database.collection(Constants.KEY_ELDER_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE,model.getElderPhone())
                .get()
                .addOnCompleteListener(task -> {
                   if(task.getResult()!=null && task.isSuccessful() & !task.getResult().getDocuments().isEmpty()) {
                       DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                       //Setting DOB and Description
                       generateReportBinding.reportElderdob.setText(doc.getString(Constants.KEY_ELDER_DOB));
                       generateReportBinding.reportDescription.setText(doc.getString(Constants.KEY_ELDER_DESCRIPTION));

                       //Gonna Medicines
                       database.collection(Constants.KEY_ELDER_MED_REM_COLLECTION)
                               .whereEqualTo(Constants.KEY_ELDER_PHONE,model.getElderPhone())
                               .get().addOnCompleteListener(task2->{
                                   if(task2.getResult()!=null && task2.isSuccessful() && !task2.getResult().getDocuments().isEmpty()){

                                       //Getting that reminder model object
                                       DocumentSnapshot documentSnapshot = task2.getResult().getDocuments().get(0);
                                       ReminderModel reminder = documentSnapshot.toObject(ReminderModel.class);

                                       //Now retrieving particular medicine list
                                       List<MedReminderModel> medReminderModelList = reminder.getListModel();

                                       //Now only we gonna extract med name
                                       StringBuilder medicines = new StringBuilder();

                                       for (int j = 0; j < medReminderModelList.size(); j++) {
                                           MedReminderModel i = medReminderModelList.get(j);
                                           medicines.append(i.getEldersMedName());
                                           if (j < medReminderModelList.size() - 1) {
                                               medicines.append(", ");
                                           }
                                       }

                                       //Now we gonna set those values
                                       generateReportBinding.reportMedicines.setText(medicines);
                                       generateReportBinding.generateReportProgressBar.setVisibility(View.GONE);
                                       generateReportBinding.root.setVisibility(View.VISIBLE);
                                   }
                               })
                               .addOnFailureListener(fail->{
                                   Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                               });
                   }
                })
                .addOnFailureListener(fail->{
                    Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void listeners(){

        //Back btn func
        generateReportBinding.reportGenerateBack.setOnClickListener(v->finish());

        //Download
        generateReportBinding.reportDownload.setOnClickListener(v->download());

        //Symptoms Generate
        generateReportBinding.reportSymGenerate.setOnClickListener(v->{
            loading();
            String sym = generateReportBinding.reportSymInput.getEditText().getText().toString();
            if(sym.isEmpty()){
                generateReportBinding.reportSymInput.setError("Enter Symptoms of your Elder");
                unLoading();
            }
            else {
                generateReportBinding.reportSym.setText(sym);
                generateReportBinding.reportSymInput.setVisibility(View.GONE);
                generateReportBinding.reportSymGenerate.setVisibility(View.GONE);
                generateReportBinding.reportSymProgress.setVisibility(View.GONE);
                generateReportBinding.reportLayout.setVisibility(View.VISIBLE);
            }

        });
    }

    private void loading(){
        generateReportBinding.reportSymGenerate.setVisibility(View.GONE);
        generateReportBinding.reportSymProgress.setVisibility(View.VISIBLE);
    }

    private void unLoading(){
        generateReportBinding.reportSymGenerate.setVisibility(View.VISIBLE);
        generateReportBinding.reportSymProgress.setVisibility(View.GONE);
    }

    private void download() {
        // Create a PdfDocument instance
        PdfDocument pdfDocument = new PdfDocument();

        // Create a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                generateReportBinding.root.getWidth(),
                generateReportBinding.root.getHeight(),
                1
        ).create();

        // Start a page
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);

        // Draw the view onto the page's Canvas
        Canvas canvas = page.getCanvas();
        generateReportBinding.root.draw(canvas);

        // Finish the page
        pdfDocument.finishPage(page);

        // Save the PDF document to a file
        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), generateFileName());
        try {
            pdfDocument.writeTo(new FileOutputStream(pdfFile));
            Toast.makeText(this, "PDF saved to: " + pdfFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d("fileLoc",pdfFile.getAbsolutePath());
            pdfDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private String generateFileName() {
        Calendar calendar = Calendar.getInstance();
        StringBuilder filename = new StringBuilder();
        filename.append(model.getElderName());
        filename.append("_");
        String date = calendar.get(Calendar.DAY_OF_MONTH) + "_" + calendar.get(Calendar.MONTH) + "_" + calendar.get(Calendar.YEAR);
        filename.append(date);
        filename.append(".pdf");
        return filename.toString();
    }
}