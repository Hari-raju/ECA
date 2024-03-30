package com.raju.elderlycareapplication.authentication.caretakers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.authentication.home.MainActivity;
import com.raju.elderlycareapplication.databinding.ActivityCaretakerAccountBinding;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.EncoderDecoder;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

import java.util.HashMap;

public class CaretakerAccountActivity extends AppCompatActivity {
    private ActivityCaretakerAccountBinding accountBinding;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding = ActivityCaretakerAccountBinding.inflate(getLayoutInflater());
        setContentView(accountBinding.getRoot());
        database = FirebaseFirestore.getInstance();
        //Initiating Caretaker Values
        preferenceManager = new PreferenceManager(this);
        init();
        //Listeners
        listeners();
    }

    private void listeners(){
        //Back Button Fun
        accountBinding.accountBack.setOnClickListener(v->finish());

        //Logout fun
        accountBinding.accountLogout.setOnClickListener(v->{
            HashMap<String,Object> value = new HashMap<>();
            value.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
            database.collection(Constants.KEY_CARETAKER_COLLECTION)
                    .document(preferenceManager.getString(Constants.KEY_CARETAKER_ID))
                    .update(value)
                    .addOnSuccessListener(comp->{
                        Toast.makeText(this, "Signing out...", Toast.LENGTH_SHORT).show();
                        preferenceManager.clear();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .addOnFailureListener(err-> Toast.makeText(this, err.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    private void init(){
        accountBinding.caretakerName.setText(preferenceManager.getString(Constants.KEY_CARETAKER_NAME));
        accountBinding.caretakerPhone.setText(preferenceManager.getString(Constants.KEY_CARETAKER_PHONE));
        accountBinding.caretakerProfile.setImageBitmap(EncoderDecoder.decodeImage(preferenceManager.getString(Constants.KEY_CARETAKER_PROFILE)));
        accountBinding.accountProgress.setVisibility(View.GONE);
    }
}