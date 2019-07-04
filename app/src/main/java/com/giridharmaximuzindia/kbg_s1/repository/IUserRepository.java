package com.giridharmaximuzindia.kbg_s1.repository;

import com.giridharmaximuzindia.kbg_s1.FirestoreUserModel;

public  interface IUserRepository {

     void doesUserEmailExist(String email);
     void addNewRegisteredUser(FirestoreUserModel firestoreUserModel);
}
