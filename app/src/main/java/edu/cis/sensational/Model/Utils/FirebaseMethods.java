package edu.cis.sensational.Model.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

//import edu.cis.instagramclone.Controller.Home.HomeActivity;
//import edu.cis.instagramclone.Controller.Home.HomeFragment;
//import edu.cis.instagramclone.Controller.Profile.AccountSettingsActivity;
import edu.cis.sensational.R;
//import edu.cis.instagramclone.View.materialcamera.MaterialCamera;
//import edu.cis.instagramclone.Model.Comment;
//import edu.cis.instagramclone.Model.Like;
//import edu.cis.instagramclone.Model.Photo;
//import edu.cis.instagramclone.Model.Story;
import edu.cis.sensational.Model.User;
//import edu.cis.instagramclone.Model.UserAccountSettings;
//import edu.cis.instagramclone.Model.UserSettings;

/**
 * Created by User on 6/26/2017.
 */

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageReference;
    private String userID;

    //vars
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    public void updateUserAccountSettings(String displayName, String website, String description, long phoneNumber){ //TODO finish updateUserSettings

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if(displayName != null){
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }

        if(website != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_website))
                    .setValue(website);
        }

        if(description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if(phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }

    /**
     * update username in the 'users' node and 'user_account_settings' node
     * @param username
     */
    public void updateUsername(String username){
        Log.d(TAG, "updateUsername: upadting username to: " + username);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * update the email in the 'user's' node
     * @param email
     */
    public void updateEmail(String email){
        Log.d(TAG, "updateEmail: upadting email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);

    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(task.isSuccessful()){
                            //send verificaton email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }

    public void sendVerificationEmail(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){

                            }else{
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Add information to the users nodes
     * Add information to the user_account_settings node
     * @param email
     * @param username
     * @param location
     */
    public void addNewUser(String email, String username, String location){

        User user = new User( userID,  StringManipulation.condenseUsername(username) , email, location);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        UserAccountSettings settings = new UserAccountSettings(
                StringManipulation.condenseUsername(username),
                email,
                location,
                userID
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);
    }


    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_acount_Settings node
     * @param dataSnapshot
     * @return
     */
//    public UserSettings getUserSettings(DataSnapshot dataSnapshot){
//        Log.d(TAG, "getUserSettings: retrieving user account settings from firebase.");
//
//
//        UserAccountSettings settings  = new UserAccountSettings();
//        User user = new User();
//
//        for(DataSnapshot ds: dataSnapshot.getChildren()){
//
//            // user_account_settings node
//            if(ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
//                Log.d(TAG, "getUserSettings: user account settings node datasnapshot: " + ds);
//
//                try {
//
//                    settings.setDisplay_name(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getDisplay_name()
//                    );
//                    settings.setUsername(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getUsername()
//                    );
//                    settings.setWebsite(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getWebsite()
//                    );
//                    settings.setDescription(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getDescription()
//                    );
//                    settings.setProfile_photo(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getProfile_photo()
//                    );
//                    settings.setPosts(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getPosts()
//                    );
//                    settings.setPhone(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getPhone()
//                    );
//                    settings.setFollowing(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getFollowing()
//                    );
//                    settings.setFollowers(
//                            ds.child(userID)
//                                    .getValue(UserAccountSettings.class)
//                                    .getFollowers()
//                    );
//
//                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());
//                } catch (NullPointerException e) {
//                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
//                }
//            }
//
//            // users node
//            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
//            if(ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
//                Log.d(TAG, "getUserAccountSettings: users node datasnapshot: " + ds);
//
//                user.setUsername(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getUsername()
//                );
//                user.setEmail(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getEmail()
//                );
//                user.setUser_id(
//                        ds.child(userID)
//                                .getValue(User.class)
//                                .getUser_id()
//                );
//
//                Log.d(TAG, "getUserAccountSettings: retrieved users information: " + user.toString());
//            }
//        }
//        return new UserSettings(user, settings);
//
//    }

}