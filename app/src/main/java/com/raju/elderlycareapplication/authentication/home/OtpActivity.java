package com.raju.elderlycareapplication.authentication.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.raju.elderlycareapplication.authentication.caretakers.CaretakerHomeActivity;
import com.raju.elderlycareapplication.databinding.ActivityOtpBinding;
import com.raju.elderlycareapplication.helpers.user_models.CaretakerModel;
import com.raju.elderlycareapplication.helpers.user_models.ConnectModel;
import com.raju.elderlycareapplication.helpers.utils.Constants;
import com.raju.elderlycareapplication.helpers.user_models.Elder_Model;
import com.raju.elderlycareapplication.helpers.utils.PreferenceManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding otpBinding;
    private Elder_Model elder_model;
    private CaretakerModel caretakerModel;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String fromActivity;
    private ConnectModel connectModel;
    private String phone;
    private PreferenceManager preferenceManager;
    private PhoneAuthProvider.ForceResendingToken token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        otpBinding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(otpBinding.getRoot());
        //Creating FireBase Authentication instance
        mAuth = FirebaseAuth.getInstance();
        otpBinding.resendOtp.setVisibility(View.GONE);
        listeners();
        //Receive the extra to know from which activity it is coming from
        fromActivity = getIntent().getStringExtra(Constants.KEY_WHAT_TO_DO);
        preferenceManager = new PreferenceManager(OtpActivity.this);
        if (fromActivity != null) {
            //Then we are checking whether its coming from elder signup and getting the elder model object or caretakers signup
            if (fromActivity.equals("elder_signUp_otp")) {
                elder_model = (Elder_Model) getIntent().getSerializableExtra("elderData");
                otpBinding.sentOtpNum.setText("Please enter the otp sent to your number\n" + elder_model.getElderPhone());
                phone = elder_model.getElderPhone();
                //Calling otp func
                sendOtpToUser(phone, false);
            } else if (fromActivity.equals("caretaker_signUp_otp")) {
                caretakerModel = (CaretakerModel) getIntent().getSerializableExtra("caretakerData");
                otpBinding.sentOtpNum.setText("Please enter the otp sent to your number\n" + caretakerModel.getCaretakerPhone());
                phone = caretakerModel.getCaretakerPhone();
                //Calling otp func
                sendOtpToUser(caretakerModel.getCaretakerPhone(), false);
            } else {
                connectModel = (ConnectModel) getIntent().getSerializableExtra("connectModel");
                otpBinding.sentOtpNum.setText("Please enter the otp sent to your number\n" + connectModel.getElderPhone());
                phone = connectModel.getElderPhone();
                //Calling otp func
                sendOtpToUser(connectModel.getElderPhone(), false);
            }
        }
    }


    private void listeners() {

        //If user click verify button then this will verify the entered code to sent code
        otpBinding.otpVerify.setOnClickListener(v -> {
            verifyCode(otpBinding.otpNum.getText().toString());
            loading();
        });

        otpBinding.resendOtp.setOnClickListener(v -> {
            otpBinding.otpNum.setText(null);
            sendOtpToUser(phone, true);
        });
    }

    private void sendOtpToUser(String phone, boolean resend) {
        loading();
        PhoneAuthOptions options;
        if (!resend) {
            options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(OtpActivity.this)
                    .setCallbacks(callbacks)
                    .build();

        } else {
            options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(phone)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(OtpActivity.this)
                    .setCallbacks(callbacks)
                    .setForceResendingToken(token)
                    .build();

        }
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //It works if a number is on the same phone as the app
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            unloading();
            Log.d("OtpActivity", "onverificatiion");
            final String code = phoneAuthCredential.getSmsCode();
            otpBinding.otpNum.setText(code);
            verifyCode(code);
        }

        //If any error occurs it will handle
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            otpBinding.otpVerify.setVisibility(View.VISIBLE);
            otpBinding.otpProgress.setVisibility(View.GONE);
            Log.d("OtpActivity", "onverificatiionFailed");
            Toast.makeText(OtpActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        //It will work if number and app are in different phone
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
            token = forceResendingToken;
            resendDisable();
            startCountDown();
            otpBinding.resendOtp.setVisibility(View.VISIBLE);
            unloading();
        }
    };

    // below method is use to verify code from Firebase.
    private void verifyCode(String code) {
        Log.d("OtpActivity", "verificatiion");
        // below line is used for getting
        // credentials from our verification id and code.
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // after getting credential we are
        // calling sign in method.
        signInWithCredential(credential);
    }


    //This method is to check that the entered otp is correct or not
    private void signInWithCredential(PhoneAuthCredential credential) {
        // inside this method we are checking if
        // the code entered is correct or not.
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // if the code is correct and the task is successful
                            // we are sending our user to new activity.
                            unloading();
                            //Then we passing the data object and from activity to next activity
                            Intent intent = new Intent(OtpActivity.this, ProfileActivity.class);
                            //If what to do is not null
                            if (fromActivity != null) {
                                //if what to do is equals elder_signup_otp means it is coming from elder signup activity
                                // else it is coming from caretaker signup activity
                                if (fromActivity.equals("elder_signUp_otp")) {
                                    intent.putExtra("whatToDo", "elderAcc");
                                    intent.putExtra("elderData", elder_model);
                                    startActivity(intent);
                                } else if (fromActivity.equals("caretaker_signUp_otp")) {
                                    intent.putExtra("whatToDo", "caretakerAcc");
                                    intent.putExtra("caretakerData", caretakerModel);
                                    startActivity(intent);
                                } else {
                                    connectToElder();
                                }
                            }
                        } else {
                            unloading();
                            // if the code is not correct then we are
                            // displaying an error message to the user.
                            Log.d("OtpActivity", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                            Toast.makeText(OtpActivity.this, "Incorrect Otp ", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


    private void loading() {
        otpBinding.otpVerify.setVisibility(View.GONE);
        otpBinding.otpProgress.setVisibility(View.VISIBLE);
    }

    private void unloading() {
        otpBinding.otpVerify.setVisibility(View.VISIBLE);
        otpBinding.otpProgress.setVisibility(View.GONE);
    }

    private void resendDisable() {
        otpBinding.resendOtp.setEnabled(false);
    }

    private void resendEnable() {
        otpBinding.resendOtp.setEnabled(true);
    }

    private void startCountDown() {
        new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                long sec = (millisUntilFinished / 1000) % 60;
                otpBinding.resendOtp.setText("Resend otp in " + sec + " seconds");
            }

            @Override
            public void onFinish() {
                otpBinding.resendOtp.setText("Resend");
                resendEnable();
            }
        }.start();
    }

    private void connectToElder() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();

        database.collection(Constants.KEY_ELDER_COLLECTION)
                .whereEqualTo(Constants.KEY_ELDER_PHONE, connectModel.getElderPhone())
                .get().addOnCompleteListener(task -> {
                    if (task.getResult()!=null && task.isSuccessful() && !task.getResult().getDocuments().isEmpty()){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        connectModel.setElderName(documentSnapshot.getString(Constants.KEY_ELDER_NAME));
                        connectModel.setElderProfile(documentSnapshot.getString(Constants.KEY_ELDER_PROFILE));
                        database.collection(Constants.KEY_CARETAKER_COLLECTION).whereEqualTo(Constants.KEY_CARETAKER_PHONE,connectModel.getCaretakerPhone())
                                .get()
                                .addOnCompleteListener(result->{
                                   if(result.getResult()!=null && result.isSuccessful() && !result.getResult().getDocuments().isEmpty()){
                                       DocumentSnapshot snapshot = result.getResult().getDocuments().get(0);
                                       connectModel.setCaretakerName(snapshot.getString(Constants.KEY_CARETAKER_NAME));
                                       database.collection(Constants.KEY_CONNECT_COLLECTION)
                                               .add(connectModel)
                                               .addOnSuccessListener(success -> {
                                                   Toast.makeText(this, "Connected...", Toast.LENGTH_SHORT).show();
                                                   startActivity(new Intent(OtpActivity.this, CaretakerHomeActivity.class));
                                                   finish();
                                               })
                                               .addOnFailureListener(
                                                       fail -> {
                                                           Toast.makeText(this, fail.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                           finish();
                                                       }
                                               );
                                   }
                                })
                                .addOnFailureListener(error->{
                                    Toast.makeText(this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(failed -> {
                    Toast.makeText(this, failed.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                });
    }
} 