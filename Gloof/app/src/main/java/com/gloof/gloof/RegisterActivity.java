package com.gloof.gloof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonRegister;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText mUsername;
    private TextView textViewSignin;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabase2;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(this, LoginActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setContentView(R.layout.activity_register);



        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }


        progressDialog = new ProgressDialog(this);

        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("GloofUsers");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsername = (EditText) findViewById(R.id.username);

        buttonRegister = (Button) findViewById(R.id.buttonRegister);

        editTextEmail = (EditText) findViewById(R.id.email);

        editTextPassword = (EditText) findViewById(R.id.password);
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ) {
                    registerUser();
                    return true;
                }
                return false;
            }
        });

        textViewSignin = (TextView) findViewById(R.id.textViewSignin);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser() {

        // Reset errors.
        editTextEmail.setError(null);
        editTextPassword.setError(null);

        final String username = mUsername.getText().toString().trim();
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username, if the user entered one.
        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password) && TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Enter Your Username, Email and Password ", Toast.LENGTH_SHORT).show();
            return;

        }if (!TextUtils.isEmpty(username) && !isUsernameValid(username)) {
            mUsername.setError(getString(R.string.error_invalid_username));
            focusView = mUsername;
            cancel = true;


        } if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Enter Your Username ", Toast.LENGTH_SHORT).show();
            return;

        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Your Email ", Toast.LENGTH_SHORT).show();
            return;
        }  if (!isEmailValid(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = editTextPassword;
            cancel = true;

        }  if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Your Password ", Toast.LENGTH_SHORT).show();
            return;
        }

        checkusernameExist();



        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();


        }
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task){
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            firebaseAuth.getCurrentUser().sendEmailVerification();


                            String uid = firebaseAuth.getCurrentUser().getUid();
                            DatabaseReference current_user_db = mDatabase.child(uid);
                            DatabaseReference displayusers = mDatabase2;
                            //DatabaseReference current_user_image = mDatabase2.child(username);

                            current_user_db.child("username").setValue(username);
                            displayusers.child(username).setValue(username);
                            //current_user_image.child("image").setValue("default");


                            Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);
                            finish();
                            Toast.makeText(RegisterActivity.this, "Registered Successfully! Confirmation Email Sent.", Toast.LENGTH_SHORT).show();



                        }else  {


                            Toast.makeText(RegisterActivity.this, "Could Not Register! Try Again.", Toast.LENGTH_SHORT).show();
                        }



                    }
                });
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isUsernameValid(String username) {
        //TODO: Replace this with your own logic
        return username.length() > 4;}

    private void checkusernameExist() {

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("username")) {
                    Toast.makeText(RegisterActivity.this, "Username already exists!", Toast.LENGTH_LONG).show();


                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onClick(View v) {

        if (v == buttonRegister){
            registerUser();
        }

        if (v == textViewSignin){
            startActivity(new Intent(this, LoginActivity.class));

        }

    }
}

