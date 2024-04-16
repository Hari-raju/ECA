package com.raju.elderlycareapplication.authentication.caretakers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.R;
import com.raju.elderlycareapplication.authentication.home.OtpActivity;
import com.raju.elderlycareapplication.databinding.ActivitySignUpCaretakersBinding;
import com.raju.elderlycareapplication.helpers.user_models.CaretakerModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.utils.Validation;

import java.util.Objects;

public class SignUpCaretakersActivity extends AppCompatActivity {

    private ActivitySignUpCaretakersBinding caretakersBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        caretakersBinding = ActivitySignUpCaretakersBinding.inflate(getLayoutInflater());
        setContentView(caretakersBinding.getRoot());
        //Bind country code to phone
        caretakersBinding.caretakerSignupCountycode.registerCarrierNumberEditText(caretakersBinding.caretakerSignupPhone.getEditText());
        //View's on Click Listener
        listeners();
    }

    private void listeners() {

        //Back button Func
        caretakersBinding.caretakerSignupBack.setOnClickListener(v -> finish());

        //Validating entered details if they press next button
        caretakersBinding.caretakerSignupNext.setOnClickListener(v -> {
            loading();
            if (checkDetails()) {
                String phoneNumber = caretakersBinding.caretakerSignupCountycode.getFullNumberWithPlus();
                FirebaseFirestore database = FirebaseFirestore.getInstance();

                database.collection(Constants.KEY_CARETAKER_COLLECTION)
                        .whereEqualTo(Constants.KEY_CARETAKER_PHONE, phoneNumber)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.getResult()!=null && task.isSuccessful()) {
                                if (task.getResult().getDocuments().size()>0) {
                                    // Number found in caretaker collection
                                    unloading();
                                    Toast.makeText(this, "Number is already registered as caretaker, please sign in", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // Number not found in caretaker collection, check elder collection
                                database.collection(Constants.KEY_ELDER_COLLECTION)
                                        .whereEqualTo(Constants.KEY_ELDER_PHONE, phoneNumber)
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.getResult()!=null && task1.isSuccessful()) {
                                                if (!task1.getResult().getDocuments().isEmpty()) {
                                                    // Number found in elder collection
                                                    unloading();
                                                    Toast.makeText(this, "Number is already registered as elder, please sign in", Toast.LENGTH_LONG).show();
                                                } else {
                                                    // Number not found in either collection, proceed to next activity
                                                    unloading();
                                                    CaretakerModel caretakerModel = new CaretakerModel(
                                                            caretakersBinding.caretakerSignupUsername.getEditText().getText().toString(),
                                                            phoneNumber,
                                                            Objects.requireNonNull(caretakersBinding.caretakerSignupPass.getEditText()).getText().toString()
                                                    );
                                                    Intent intent = new Intent(SignUpCaretakersActivity.this, OtpActivity.class);
                                                    intent.putExtra("caretakerData", caretakerModel);
                                                    intent.putExtra(Constants.KEY_WHAT_TO_DO, "caretaker_signUp_otp");
                                                    unloading();
                                                    startActivity(intent);
                                                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                                }
                                            } else {
                                                unloading();
                                                Toast.makeText(this, "Error checking elder collection: " + Objects.requireNonNull(task1.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                unloading();
                                Toast.makeText(this, "Error checking caretaker collection: " + Objects.requireNonNull(task.getException()).getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private Boolean checkDetails() {

        //Validating phone
        if (!caretakersBinding.caretakerSignupCountycode.isValidFullNumber()) {
            caretakersBinding.caretakerSignupPhone.setError("Enter valid phone");
            return false;
        } else {
            caretakersBinding.caretakerSignupPhone.setError(null);
        }

        if (!Validation.isNameValid(Objects.requireNonNull(caretakersBinding.caretakerSignupUsername.getEditText()).getText().toString())) {
            caretakersBinding.caretakerSignupUsername.setError("Enter a valid name");
            return false;
        } else {
            caretakersBinding.caretakerSignupUsername.setError(null);
        }

        if (!Validation.isPassValid(Objects.requireNonNull(caretakersBinding.caretakerSignupPass.getEditText()).getText().toString())) {
            caretakersBinding.caretakerSignupPass.setError("Strong password required");
            return false;
        } else {
            caretakersBinding.caretakerSignupPass.setError(null);
        }

        if (!Validation.isConfirmPassValid(caretakersBinding.caretakerSignupPass.getEditText().getText().toString(), Objects.requireNonNull(caretakersBinding.caretakerSignupConfirmPass.getEditText()).getText().toString())) {
            caretakersBinding.caretakerSignupConfirmPass.setError("Password mismatches");
            return false;
        } else {
            caretakersBinding.caretakerSignupConfirmPass.setError(null);
        }
        return true;
    }

    private void loading() {
        caretakersBinding.caretakerSignupNext.setVisibility(View.GONE);
        caretakersBinding.caretakerSignupProgress.setVisibility(View.VISIBLE);
    }

    private void unloading() {
        caretakersBinding.caretakerSignupNext.setVisibility(View.VISIBLE);
        caretakersBinding.caretakerSignupProgress.setVisibility(View.GONE);
    }
}