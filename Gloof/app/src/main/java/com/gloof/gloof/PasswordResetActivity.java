package com.gloof.gloof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetActivity extends AppCompatActivity {

    private Button buttonResetPassword;
    private EditText editTextEmail;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_reset);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        buttonResetPassword = (Button) findViewById(R.id.buttonResetPassword);
        editTextEmail = (AutoCompleteTextView) findViewById(R.id.emailReset);
        editTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE ) {
                    forgotPassword();
                    return true;
                }
                return false;
            }
        });

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void forgotPassword(){
        String email = editTextEmail.getText().toString().trim();

        editTextEmail.setError(null);

        boolean cancel = false;
        View focusView = null;
        if (TextUtils.isEmpty(email)){
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
        progressDialog.setMessage("Reseting password... ");
        progressDialog.show();

        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            Intent mainIntent = new Intent(PasswordResetActivity.this, LoginActivity.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(mainIntent);

                            Toast.makeText(PasswordResetActivity.this, "An Email With Your Password Reset Has Been Sent!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(PasswordResetActivity.this, "Could Not Reset Your Password! Try Again Later.", Toast.LENGTH_SHORT).show();
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
