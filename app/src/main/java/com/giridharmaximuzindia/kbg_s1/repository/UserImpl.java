package com.giridharmaximuzindia.kbg_s1.repository;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;


import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.giridharmaximuzindia.kbg_s1.FirestoreUserModel;
import com.giridharmaximuzindia.kbg_s1.utils.Constants;
import com.giridharmaximuzindia.kbg_s1.CustomApplication;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class UserImpl implements IUserRepository{

    private static final String TAG = UserImpl.class.getSimpleName();
    private Context context;
    private FirebaseFirestore db;
    private CustomApplication app;


    public UserImpl(Context context){
        this.context = context;
        app = CustomApplication.getInstance();

    }

    @Override
    public void doesUserEmailExist(String email) {
        Task<DocumentSnapshot> docSnapShot = db.collection(Constants.USER_COLLECTION).document(email).get();
        docSnapShot.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    //User already exists
                }else {
                    //User does not exist in Database
                }
            }
        });
    }

    @Override
    public void addNewRegisteredUser(FirestoreUserModel firestoreUserModel) {
        Map<String, Object> user = new HashMap<>();
        user.put(Constants.DocumentFields.EMAIL, firestoreUserModel.getEmail());
        user.put(Constants.DocumentFields.PASSWORD, firestoreUserModel.getPassword());

        Task<Void> newUser = db.collection(Constants.USER_COLLECTION).document(firestoreUserModel.getPhone()).set(user);
        newUser.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User was added Successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error occured "+ e.getMessage() );
            }
        });
    }

}
