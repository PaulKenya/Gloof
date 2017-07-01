package com.gloof.gloof;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

public class VerifyemailActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private ProgressDialog progressDialog;
    private DatabaseReference mDatabaseusername;
    private TextView mtxtnavusername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_verify_email);

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        if (mCurrentUser != null) {
            String value= mCurrentUser.getEmail();
            final TextView Email = (TextView) findViewById(R.id.email_activityVerifyEmail);
            Email.setText(value);
        }


        FirebaseUser FirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseUser != null) {
            String uid = FirebaseUser.getUid();
            final TextView Name = (TextView) findViewById(R.id.name_activityVerifyEmail);
            mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users");
            mDatabaseusername.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String navusername = (String) dataSnapshot.child("username").getValue();
                    Name.setText(navusername);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        progressDialog = new ProgressDialog(this);

        Button mbtnVerifyEmail = (Button) findViewById(R.id.btnVerify);
        mbtnVerifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyEmail();
            }
        });

        Button buttonLogOff = (Button) findViewById(R.id.signoff);
        buttonLogOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    firebaseAuth.signOut();
                    finish();
                    startActivity(new Intent(VerifyemailActivity.this, LoginActivity.class));
                }
            }
        });

    }

    private void verifyEmail() {
        progressDialog.setMessage("Verifying email... ");
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(VerifyemailActivity.this, "A Verification Email has been Sent", Toast.LENGTH_LONG).show();


                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(VerifyemailActivity.this, "Could Not Verify Your Account! Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            progressDialog.dismiss();
        }
    }
}
