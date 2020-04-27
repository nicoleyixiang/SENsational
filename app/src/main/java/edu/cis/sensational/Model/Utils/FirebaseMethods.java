package edu.cis.sensational.Model.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import edu.cis.sensational.Controller.Home.HomeActivity;
import edu.cis.sensational.Controller.Profile.AccountSettingsActivity;
import edu.cis.sensational.Model.Post;
import edu.cis.sensational.Model.User;
import edu.cis.sensational.Model.UserAccountSettings;
import edu.cis.sensational.Model.UserSettings;
import edu.cis.sensational.R;
//import edu.cis.instagramclone.View.materialcamera.MaterialCamera;
//import edu.cis.instagramclone.Model.Comment;
//import edu.cis.instagramclone.Model.Like;
//import edu.cis.instagramclone.Model.Photo;
//import edu.cis.instagramclone.Model.Story;

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

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("Canada/Pacific"));
        return sdf.format(new Date());
    }

    public void createNewPost(String title, String description, String tag){

        // retrieve a key for the postID
        String newPostKey = myRef.child(mContext.getString(R.string.dbname_posts)).push().getKey();

        // create a new Post object and set the values given
        Post post = new Post();
        post.setTitle(title);
        post.setDescription(description);
        post.setTags(tag);
        post.setUser_id(userID);
        post.setDate_created(getTimestamp());
        post.setComments(post.getComments());
        post.setPostID(newPostKey);

        // upload the post to the database
        uploadNewPost(post);
    }

    public void uploadNewPost(Post post)
    {
        Log.d(TAG, "uploadNewPost: attempting to post.");

        myRef.child("user_posts").child(post.getUser_id())
                .child(post.getPostID()).setValue(post);
        myRef.child("posts").child(post.getPostID()).setValue(post);
    }

    public void updateUserAccountSettings(String location, String age, String information) {

        Log.d(TAG, "updateUserAccountSettings: updating user account settings.");

        if (location != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_location))
                    .setValue(location);
        }

        if (age != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_age))
                    .setValue(age);
        }

        if (information != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_information))
                    .setValue(information);
        }
    }

    /**
     * update username in the 'users' node and 'user_account_settings' node
     *
     * @param username
     */
    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to: " + username);

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
     *
     * @param email
     */

    public void updateEmail(String email) {
        Log.d(TAG, "updateEmail: updating email to: " + email);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

    public void updatePassword(String password) {
        Log.d(TAG, "updatePassword: updating password to: " + password);

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_password))
                .setValue(password);
    }

    /**
     * Register a new email and password to Firebase Authentication
     *
     * @param email
     * @param password
     * @param username
     */

    public void registerNewEmail(final String email, String password, final String username) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) { // Check if user creation is successful
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());
                        // If the task fails, display a message to the user.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        // If task succeeds, this happens
                        } else if (task.isSuccessful()) {
                            //sends verification email to the registration email inbox
                            sendVerificationEmail();
                            //the auth state listener will be notified and signed in user can be handled in the listener.
                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }
                    }
                });
    }

    public void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "couldn't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    /**
     * Add information to the users nodes
     * Add information to the user_account_settings node
     *
     * @param email
     * @param username
     * @param location
     * @param child_age
     * @param child_profile
     */

    public void addNewUser(String email, String username, String location, String child_age, String child_profile) {

        // creates a new User object with the parameters given
        User user = new User(userID, StringManipulation.condenseUsername(username), email, location);

        // saves the user to the database
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);

        // creates a new UserAccountSettings for this user
        UserAccountSettings settings = new UserAccountSettings(
                StringManipulation.condenseUsername(username),
                email,
                location,
                child_age,
                child_profile,
                0,
                userID
        );

        // saves the settins to the databse
        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);
    }


    /**
     * Retrieves the account settings for teh user currently logged in
     * Database: user_acount_Settings node
     *
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot) {
        Log.d(TAG, "getUserSettings: retrieving user account settings from firebase.");


        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            // user_account_settings node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings))) {
                Log.d(TAG, "getUserSettings: user account settings node datasnapshot: " + ds);

                try {

                    settings.setUsername(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getUsername()
                    );

                    settings.setLocation(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getLocation()
                    );

                    settings.setChild_age(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getChild_age()
                    );

                    settings.setChild_gender(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getChild_gender()
                    );

                    settings.setPosts(
                            ds.child(userID)
                                    .getValue(UserAccountSettings.class)
                                    .getPosts()
                    );

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information: " + settings.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getUserAccountSettings: NullPointerException: " + e.getMessage());
                }
            }

            // users node
            Log.d(TAG, "getUserSettings: snapshot key: " + ds.getKey());
            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: users node datasnapshot: " + ds);

                user.setUsername(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUsername()
                );
                user.setEmail(
                        ds.child(userID)
                                .getValue(User.class)
                                .getEmail()
                );
                user.setUser_id(
                        ds.child(userID)
                                .getValue(User.class)
                                .getUser_id()
                );

                Log.d(TAG, "getUserAccountSettings: retrieved users information: " + user.toString());
            }
        }
        return new UserSettings(user, settings);

    }

    public Post getPost(DataSnapshot dataSnapshot){
        Log.d(TAG, "getPost: retrieving post information from firebase.");

        Post post = new Post();

        for (DataSnapshot ds : dataSnapshot.getChildren()) {

            // posts node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_posts))) {
                Log.d(TAG, "getUserSettings: posts node datasnapshot: " + ds);

                try {

                    post.setTitle(
                            ds.child(userID)
                                    .getValue(Post.class)
                                    .getTitle()
                    );

                    post.setDescription(
                            ds.child(userID)
                                    .getValue(Post.class)
                                    .getDescription()
                    );

                    post.setTags(
                            ds.child(userID)
                                    .getValue(Post.class)
                                    .getTags()
                    );

                    Log.d(TAG, "getPost: retrieved post information: " + post.toString());
                } catch (NullPointerException e) {
                    Log.e(TAG, "getPost: NullPointerException: " + e.getMessage());
                }
            }
        }
        return post;
    }
}