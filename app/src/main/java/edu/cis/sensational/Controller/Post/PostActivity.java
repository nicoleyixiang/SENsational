package edu.cis.sensational.Controller.Post;

import androidx.appcompat.app.AppCompatActivity;

import edu.cis.sensational.Controller.Home.HomeActivity;
import edu.cis.sensational.Controller.Login.RegisterActivity;
import edu.cis.sensational.Model.Post;
import edu.cis.sensational.Model.Utils.FirebaseMethods;
import edu.cis.sensational.R;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";

    private EditText mTitle, mDescription, mTag;

    private String title, description, tag;
    private boolean privatePost;

    private Button postButton, backButton;

    private Switch privateSwitch;

    private Context mContext;

    private String userID;

    private FirebaseAuth mAuth;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }

        initWidgets();
        init();
    }

    private void initWidgets(){
        Log.d(TAG, "initWidgets: Initializing Widgets.");
        mTitle = (EditText) findViewById(R.id.titleInput);
        mDescription = (EditText) findViewById(R.id.descriptionInput);
        mTag = (EditText) findViewById(R.id.tagInput);

        postButton = (Button) findViewById(R.id.newPostButton);
        backButton = (Button) findViewById(R.id.backButton);

        privateSwitch = (Switch) findViewById(R.id.privateSwitch);
        privateSwitch.setChecked(false);

        mContext = PostActivity.this;
    }

    private void init(){
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = mTitle.getText().toString();
                description = mDescription.getText().toString();
                tag = mTag.getText().toString();

                if(privateSwitch.isChecked()){
                    privatePost = true;
                }
                else{
                    privatePost = false;
                }

                if(checkInputs(title, description, tag)){

                    FirebaseMethods firebaseMethods = new FirebaseMethods(PostActivity.this);
                    if(firebaseMethods.createNewPost(title, description, tag, privatePost)){
                        Toast.makeText(mContext, "Successfully posted.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(context,
                                HomeActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(mContext, "Posting unsuccessful. Try again.", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(mContext, "Inputs are invalid. Try again.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkInputs(String title, String description, String tag){
        Log.d(TAG, "checkInputs: checking inputs for null values.");
        if(title.equals("") || description.equals("") || tag.equals("")){
            Toast.makeText(mContext, "All fields must be filled out.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}