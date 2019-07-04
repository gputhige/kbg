package com.giridharmaximuzindia.kbg_s1.signup;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;


import com.giridharmaximuzindia.kbg_s1.OptionActivity;
import com.giridharmaximuzindia.kbg_s1.R;
import com.giridharmaximuzindia.kbg_s1.login.LoginActivity;
import com.giridharmaximuzindia.kbg_s1.profile.ProfileActivity;

import com.giridharmaximuzindia.kbg_s1.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class SignUpActivity extends AppCompatActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();
    private AppCompatEditText userbox, emailbox, phonebox, passwordbox, repasswordbox;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_signup);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        AppCompatTextView signUpLink = (AppCompatTextView)findViewById(R.id.signup_link);
        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(loginIntent);
            }
        });

        userbox = (AppCompatEditText)findViewById(R.id.username);
        emailbox = (AppCompatEditText)findViewById(R.id.email);
        phonebox = (AppCompatEditText)findViewById(R.id.phone);
        passwordbox = (AppCompatEditText)findViewById(R.id.password);
        repasswordbox = (AppCompatEditText)findViewById(R.id.repassword);


        AppCompatButton signUpBtn = (AppCompatButton) findViewById(R.id.signup_btn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = Objects.requireNonNull(userbox.getText()).toString();
                String email = Objects.requireNonNull(emailbox.getText().toString());
                String phone = Objects.requireNonNull(phonebox.getText().toString());
                String password = Objects.requireNonNull(passwordbox.getText().toString());
                String repassword = Objects.requireNonNull(repasswordbox.getText().toString());
                String solooption= "0";
                String mateoption= "0";
                String capoption= "0";

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)){
                    Toast.makeText(SignUpActivity.this, "Login Fields cannot be empty", Toast.LENGTH_SHORT).show();
                }else {
                    if(password.equals(repassword)){
                        auth.createUserWithEmailAndPassword(email,password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            CollectionReference playerRef = db.collection(Constants.USER_COLLECTION);
                                            Query query = playerRef.whereEqualTo("phone",phone);
                                            query.get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                for(DocumentSnapshot documentSnapshot : task.getResult()){
                                                                    String phone = documentSnapshot.getString("phone");

                                                                    if(phone.equals(phone)){
                                                                        Toast.makeText(SignUpActivity.this, "This number is alredy registered", Toast.LENGTH_SHORT).show();
                                                                    }else {
                                                                        Map<String, Object> players = new HashMap<>();
                                                                        players.put(Constants.DocumentFields.USERNAME, username);
                                                                        players.put(Constants.DocumentFields.EMAIL,email);
                                                                        players.put(Constants.DocumentFields.PHONE, phone);
                                                                        players.put(Constants.DocumentFields.PASSWORD, password);
                                                                        players.put(Constants.DocumentFields.SOLOOPTION,solooption);
                                                                        players.put(Constants.DocumentFields.MATEOPTION, mateoption);
                                                                        players.put(Constants.DocumentFields.CAPOPTION, capoption);

                                                                        db.collection("PLAYERS")
                                                                                .add(players)
                                                                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                                    @Override
                                                                                    public void onSuccess(DocumentReference documentReference) {
                                                                                        Toast.makeText(SignUpActivity.this,"Success",Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                });
                                                                        Toast.makeText(SignUpActivity.this,"User Added Successfully", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                        finish();

                                                                    }
                                                                }
                                                            }
                                                            Toast.makeText(SignUpActivity.this, "Some Error ",Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }else {
                                            Toast.makeText(SignUpActivity.this, "Error "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }else {
                        Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
