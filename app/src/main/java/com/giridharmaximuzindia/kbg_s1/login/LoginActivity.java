package com.giridharmaximuzindia.kbg_s1.login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.giridharmaximuzindia.kbg_s1.OptionActivity;
import com.giridharmaximuzindia.kbg_s1.R;
import com.giridharmaximuzindia.kbg_s1.profile.ProfileActivity;
import com.giridharmaximuzindia.kbg_s1.signup.SignUpActivity;
import com.giridharmaximuzindia.kbg_s1.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private AppCompatEditText phone, otp;
    private AppCompatTextView signUpBtn;
    private AppCompatButton otpBtn, loginBtn;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    private FirebaseFirestore db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            ((ActionBar) actionBar).hide();;
        }

        otpBtn =(AppCompatButton)findViewById(R.id.otp_btn);
        loginBtn = (AppCompatButton)findViewById(R.id.login_btn);
        signUpBtn = (AppCompatTextView) findViewById(R.id.signup_btn);
        phone = (AppCompatEditText)findViewById(R.id.phone);
        otp = (AppCompatEditText)findViewById(R.id.otp_get);
        db = FirebaseFirestore.getInstance();

        StartFirebaseLogin();

        otpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phonenumber = phone.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+91" + phonenumber,
                        60,
                        TimeUnit.SECONDS,
                        LoginActivity.this,
                        mCallback
                );
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String otpnumber = otp.getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otpnumber);
                SigninWithPhone(credential);
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(signUpIntent);
            }
        });

    }

    private void StartFirebaseLogin(){
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this, "Verification Complete", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, "Verification Failed"+ e.getMessage(), Toast.LENGTH_SHORT).show();

            }
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken){
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(LoginActivity.this, "Code Sent",Toast.LENGTH_SHORT).show();
            }
        };

    }

    private void SigninWithPhone(PhoneAuthCredential credential){
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final String phonenumber = phone.getText().toString();

                            CollectionReference playerRef = db.collection(Constants.USER_COLLECTION);
                            Query query = playerRef.whereEqualTo("phone",phonenumber);
                            query.get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                                    String phone = documentSnapshot.getString("phone");

                                                    if(phone.equals(phonenumber)){
                                                        String playoption = documentSnapshot.getString("playOption");
                                                        if(playoption.equals("0")){
                                                            Toast.makeText(LoginActivity.this, "New Player", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(LoginActivity.this, OptionActivity.class);
                                                            startActivity(intent);
                                                            return;

                                                        }else if(playoption.equals("1")){
                                                            Toast.makeText(LoginActivity.this, "Player Solo", Toast.LENGTH_SHORT).show();
                                                        }else {
                                                            Toast.makeText(LoginActivity.this, "Player Mate", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }else {
                                                        Toast.makeText(LoginActivity.this, "Please Register First ",Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            }
                                            Toast.makeText(LoginActivity.this, "Please Register First ",Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                                            finish();
                                        }
                                    });

                        }else {
                            Toast.makeText(LoginActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}
