package com.gloof.gloof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DeleteAccountActivity extends AppCompatActivity {
    private Button mDeleteAcc;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private DatabaseReference mDatabaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);

        mDeleteAcc = (Button) findViewById(R.id.btnDeleteAccount);
        editTextEmail = (EditText) findViewById(R.id.emaildelete);
        editTextPassword = (EditText) findViewById(R.id.passworddelete);
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseUsers.keepSynced(true);
        progressDialog = new ProgressDialog(this);
        mDeleteAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteaccount();
            }
        });

    }

    private void deleteaccount() {
        final String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Reset errors.
        editTextEmail.setError(null);
        editTextPassword.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Your Email and Password ", Toast.LENGTH_SHORT).show();
            return;
            // Check for a valid password, if the user entered one.
        }
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            editTextPassword.setError(getString(R.string.error_invalid_password));
            focusView = editTextPassword;
            cancel = true;

        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Enter Your Password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter Your Email", Toast.LENGTH_SHORT).show();
            return;


        }
        if (!isEmailValid(email)) {
            editTextEmail.setError(getString(R.string.error_invalid_email));
            focusView = editTextEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();


        }
        progressDialog.setMessage("Deleting Account in... ");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            checkUserExist();
                            firebaseAuth.getCurrentUser().delete();
                            finish();
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            Toast.makeText(DeleteAccountActivity.this, "Account Deleted Successfully!", Toast.LENGTH_SHORT).show();
                        } else {


                            Toast.makeText(DeleteAccountActivity.this, "Could not Delete your Account! Confirm your email/password and Try Again.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private void checkUserExist() {
        final String user_id = firebaseAuth.getCurrentUser().getUid();

        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user_id)) {
                    Intent mainIntent = new Intent(DeleteAccountActivity.this, LoginActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(mainIntent);

                } else {

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
