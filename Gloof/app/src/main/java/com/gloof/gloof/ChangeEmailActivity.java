package com.gloof.gloof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChangeEmailActivity extends AppCompatActivity {
    private Button mbtnEmailChange;
    private EditText editTextEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value= mCurrentUser.getEmail();
        final TextView post_desc3 = (EditText) findViewById(R.id.editTextEmailChange);

        post_desc3.setText(value);





        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        mbtnEmailChange = (Button) findViewById(R.id.btnEmailChange);
        editTextEmail = (AutoCompleteTextView) findViewById(R.id.editTextEmailChange);

        mbtnEmailChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEmail();
            }
        });
    }

    private void changeEmail() {
        String email = editTextEmail.getText().toString().trim();

        editTextEmail.setError(null);

        boolean cancel = false;
        View focusView = null;
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

        progressDialog.setMessage("Changing email... ");
        progressDialog.show();

        firebaseAuth.getCurrentUser().updateEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            finish();
                            firebaseAuth.signOut();

                            Toast.makeText(ChangeEmailActivity.this, "Email Updated! You Can Now Login With Your New Email.", Toast.LENGTH_LONG).show();

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(ChangeEmailActivity.this, "Could Not Change Your Email! Try Again.", Toast.LENGTH_LONG).show();
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
}
