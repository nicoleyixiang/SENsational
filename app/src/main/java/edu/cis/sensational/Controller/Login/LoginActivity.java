package edu.cis.sensational.Controller.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.cis.sensational.Controller.Home.HomeActivity;
import edu.cis.sensational.R;

public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private EditText mEmail, mPassword;

    final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        Log.d(TAG, "onCreate: started.");

        setupFirebaseAuth();
        init();

    }

    private boolean isStringNull(String string){
        Log.d(TAG, "isStringNull: checking string if null.");

        if(string.equals("")){
            return true;
        }
        else{
            return false;
        }
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    private void init(){

        //initialize the button for logging in
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                /***TODO 1a: Get email and password strings from mEmail and mPassword EditText object. Research the EditText class documentation to find out which method(s) will help. ***/
                String emailString = mEmail.getText().toString();
                String pwString = mPassword.getText().toString();

                /*** CODE for 1b inside if statement parenthesis, should not be boolean "true" ***/
                if( emailString.isEmpty() || pwString.isEmpty()){ /*** TODO 1b. check whether the user gave a blank email or password, research which method(s) you can use from the EditText class ***/
                    Toast.makeText(mContext, "Invalid input", Toast.LENGTH_SHORT).show(); /*** TODO 1c: if true this line uses Toast.makeText to inform the user of an error, give any error message you want ***/
                }else{

                    mAuth.signInWithEmailAndPassword(emailString, pwString) /*** TODO 1d. If email and password are present, use mAuth.signInWithEmailAndPassword to send it to firebase, change the parameters here ***/
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    //get current user from mAuth, store it in variable
                                    FirebaseUser currentUser = mAuth.getCurrentUser();

                                    // If sign in fails, display a message to the user. If sign in succeeds
                                    // the auth state listener will be notified and logic to handle the
                                    // signed in user can be handled in the listener.

                                    /*** CODE for 1e inside if statement parenthesis, should not be boolean "true" ***/
                                    if (!task.isSuccessful()) { /*** TODO 1e: check if task was not successful, research the Android Task Class ***/
                                        Log.w(TAG, "signInWithEmail:failed", task.getException());

                                        /*** TODO 1f: inform the user with a Toast that something went wrong ***/
                                        Toast.makeText(mContext, "something went wrong.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    else{ //if task was successful
                                        try{
                                            /*** CODE for 1g inside if statement parenthesis, should not be boolean "true" ***/
                                            if(currentUser.isEmailVerified()){ /*** TODO 1g: Check if the user's email has been verified, change true, research the FirebaseUser class documentation for helpful method(s) ***/
                                                Log.d(TAG, "onComplete: success. email is verified.");
                                                /*** TODO 1h: create code to navigate from LoginActivity to HomeActivity, you'll have to research the Android Class used for navigating from one screen to another ***/
                                                Intent intent = new Intent(context,
                                                        HomeActivity.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                Toast.makeText(mContext, "Email is not verified \n check your email inbox.", Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }

                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: " + e.getMessage() );
                                        }
                                    }

                                    // ...
                                }
                            });
                }

            }
        });

        TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
        if(mAuth.getCurrentUser() != null){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        //TODO figure out why this crashes the system
//        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}