package com.gloof.gloof;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

public class ChangeUsernameActivity extends AppCompatActivity {
    private EditText mUsername;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_username);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value2 = mCurrentUser.getUid();
        DatabaseReference mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users");
        final TextView post_desc3 = (EditText) findViewById(R.id.editTextUsernameChange);
        mUsername = (EditText) findViewById(R.id.editTextUsernameChange);
        progressDialog = new ProgressDialog(this);

        Button mChangeUsername = (Button)findViewById(R.id.btnUsernameChange);
        mChangeUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUsername();
            }
        });

        mDatabaseusername.child(value2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String navusername = (String) dataSnapshot.child("username").getValue();
                post_desc3.setText(navusername);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void changeUsername() {
        final String username = mUsername.getText().toString().trim();
        if (TextUtils.isEmpty(username)){
            Toast.makeText(this, "Enter Your Username ", Toast.LENGTH_SHORT).show();
            return;

        }
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value2 = mCurrentUser.getUid();
        DatabaseReference mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users").child(value2);
        progressDialog.setMessage("Changing Username...");
        mDatabaseusername.child("username").setValue(username).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.show();
                    progressDialog.dismiss();
                    Toast.makeText(ChangeUsernameActivity.this, "Username Changed!", Toast.LENGTH_SHORT).show();
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(ChangeUsernameActivity.this, "Could Not Change Your Username! Try Again.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        progressDialog.dismiss();

    }


}
