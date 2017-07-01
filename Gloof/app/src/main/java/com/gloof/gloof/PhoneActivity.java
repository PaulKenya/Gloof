package com.gloof.gloof;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.InputQueue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {

    private EditText editTextPhone;
    private Button mSavePhone;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changephone);

        progressDialog = new ProgressDialog(this);
        editTextPhone = (EditText) findViewById(R.id.editTextPhone);
        mSavePhone = (Button) findViewById(R.id.buttonSavePhone);
        mSavePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savePhone();
            }
        });

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value2 = mCurrentUser.getUid();
        DatabaseReference mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users");
        final TextView post_desc3 = (EditText) findViewById(R.id.editTextPhone);
        mDatabaseusername.child(value2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String navusername = (String) dataSnapshot.child("phone").getValue();
                post_desc3.setText(navusername);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private void savePhone() {

        editTextPhone.setError(null);
        final String phone = editTextPhone.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(phone)){
            Toast.makeText(this, "Enter Your Phone ", Toast.LENGTH_SHORT).show();
        }
        else if (!isPhoneValid2(phone)){
            Toast.makeText(this, "Include plus sign ", Toast.LENGTH_SHORT).show();
        }
        else if (isPhoneValid2(phone) && !isPhoneValid(phone)){
            Toast.makeText(this, "The phone you have entered is too short", Toast.LENGTH_SHORT).show();
        }

        else if (!TextUtils.isEmpty(phone) && !isPhoneValid2(phone) && !isPhoneValid(phone)){
            editTextPhone.setError("This phone is invalid");
            focusView = editTextPhone;
            cancel = true;
        }
        else if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }

        else if (!TextUtils.isEmpty(phone) && isPhoneValid2(phone) && isPhoneValid(phone)){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            FirebaseUser mCurrentUser = mAuth.getCurrentUser();
            String value2 = mCurrentUser.getUid();
            DatabaseReference mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users").child(value2);
            progressDialog.setMessage("Saving Phone...");
            progressDialog.show();




            mDatabaseusername.child("phone").setValue(phone).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.show();
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(PhoneActivity.this, "Phone Saved!", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(PhoneActivity.this, "Could Not Save Your Phone! Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            progressDialog.dismiss();
        }


    }

    private boolean isPhoneValid(String phone) {
        //TODO: Replace this with your own logic
        return phone.length() == 13;
    }

    private boolean isPhoneValid2(String phone) {
        //TODO: Replace this with your own logic
        return phone.startsWith("+") ;
    }
}
