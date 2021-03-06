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

import edu.cis.sensational.Controller.MainActivity;
import edu.cis.sensational.R;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private EditText mEmail, mPassword;

    final Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.d(TAG, "onCreate: started.");
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        mContext = LoginActivity.this;
        setupFirebaseAuth();
        init();
    }

     /*
    ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Initialize the widgets on the page
     */
    private void init(){
        //initialize the button for logging in
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");
                String emailString = mEmail.getText().toString();
                String pwString = mPassword.getText().toString();
                // If the inputs were null, show error message.
                if(emailString.isEmpty() || pwString.isEmpty())
                {
                    Toast.makeText(mContext, R.string.login_request, Toast.LENGTH_SHORT).show();
                }
                // If the inputs were valid, sign in user
                else{
                    mAuth.signInWithEmailAndPassword(emailString, pwString)
                            .addOnCompleteListener(LoginActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task)
                                {
                                    Log.d(TAG, "signInWithEmail:onComplete:"
                                            + task.isSuccessful());
                                    //get current user from mAuth, store it in variable
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    // If sign in fails, display a message to the user.
                                    // If sign in succeeds the auth state listener will be notified
                                    // and logic to handle the signed in user can be
                                    // handled in the listener.
                                    if (!task.isSuccessful())
                                    {
                                        Log.w(TAG, "signInWithEmail:failed"
                                                , task.getException());
                                        Toast.makeText(mContext, R.string.login_error,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                    // if task was successful
                                    else
                                    {
                                        try
                                        {
                                            if(currentUser.isEmailVerified())
                                            {
                                                Log.d(TAG, "onComplete: success. " +
                                                        "email is verified.");
                                                Intent intent = new Intent(context,
                                                        MainActivity.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                Toast.makeText(mContext, R.string.email_not_verified
                                                        , Toast.LENGTH_SHORT).show();
                                                mAuth.signOut();
                                            }
                                        }
                                        catch (NullPointerException e)
                                        {
                                            Log.e(TAG, "onComplete: NullPointerException: "
                                                    + e.getMessage() );
                                        }
                                    }
                                }
                            });
                }
            }
        });

        // Button to open signup page
        TextView linkSignUp = (TextView) findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "onClick: navigating to register screen");
                Intent intent = new Intent(LoginActivity.this
                        , RegisterActivity.class);
                startActivity(intent);
            }
        });

         /*
         If the user is logged in then navigate to HomeActivity and call 'finish()'
          */
        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(context, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                }
                else
                {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}