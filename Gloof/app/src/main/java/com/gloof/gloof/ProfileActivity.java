package com.gloof.gloof;

        import android.annotation.SuppressLint;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.os.Build;
        import android.provider.MediaStore;
        import android.support.annotation.NonNull;
        import android.support.annotation.RequiresApi;
        import android.support.design.widget.BottomNavigationView;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.Snackbar;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.PopupMenu;
        import android.text.TextUtils;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.ImageButton;
        import android.widget.ImageView;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.tasks.OnCompleteListener;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.android.gms.tasks.Task;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.OnProgressListener;
        import com.google.firebase.storage.StorageReference;
        import com.google.firebase.storage.UploadTask;
        import com.squareup.picasso.NetworkPolicy;
        import com.squareup.picasso.Picasso;
        import com.theartofdev.edmodo.cropper.CropImage;
        import com.theartofdev.edmodo.cropper.CropImageView;

        import java.util.Random;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImageBtn;
    private FloatingActionButton mUpdateProfileBtn;
    private Uri mImageUri;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabasProfilePic;
    private static final int CAMERA_REQUEST_CODE =1;
    private static final int GALLERY_REQUEST = 1;
    private DatabaseReference mDatabase2;
    private DatabaseReference mDatabase3;
    private DatabaseReference mDatabase4;
    private ImageView mProfPic;
    private Button mtxtemail;
    private Button mtxtusername;
    private Button mtxtpassword;
    private Button mtxtdeleteaccount;
    private Button mtxtphone;
    private Button mtxtverifyaccount;
    private DatabaseReference mDatabaseusername;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Profile_Photo");
        mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabase2 = FirebaseDatabase.getInstance().getReference().child("UsersProfile");
        mDatabase3 = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabase4 = FirebaseDatabase.getInstance().getReference().child("Blog");

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = mAuth.getCurrentUser();
        String value= mCurrentUser.getEmail();
        String value2 = mCurrentUser.getUid();
        boolean  value3 = mCurrentUser.isEmailVerified();
        mDatabaseusername = FirebaseDatabase.getInstance().getReference("Users");


        mtxtphone = (Button) findViewById(R.id.btnPhone);
        mtxtphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PhoneActivity.class));
            }
        });

        mtxtdeleteaccount = (Button) findViewById(R.id.btnGotoDeleteAccount);
        mtxtdeleteaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, DeleteAccountActivity.class));
            }
        });

        mtxtverifyaccount = (Button) findViewById(R.id.btnGotoVerifyAccount);
        mtxtverifyaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser FirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (FirebaseUser != null) {
                    boolean emailVerified= FirebaseUser.isEmailVerified();
                    if(emailVerified==true){
                        Snackbar.make(v, "Account Verified", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        startActivity(new Intent(ProfileActivity.this, VerifyemailActivity.class));
                    }
                }
            }
        });

        if (value3==false){
            mtxtverifyaccount.setText("\t Account Not Verified" );
            ImageView mError = (ImageView) findViewById(R.id.imageView4);
            mError.setVisibility(View.VISIBLE);
            mError.setImageResource(R.drawable.ic_error);
        }
        if (value3==true){
            mtxtverifyaccount.setText("\t Account Verified" );
        }

        mtxtemail = (Button) findViewById(R.id.btnChangeEmail);
        mtxtemail.setText("\t" +value +"\n  Change Email");
        mtxtemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangeEmailActivity.class));
            }
        });

        mtxtpassword = (Button) findViewById(R.id.btnChangePassword);
        mtxtpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        mtxtusername = (Button) findViewById(R.id.btnChangeUsername);
        mtxtusername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ChangeUsernameActivity.class));
            }
        });


        mDatabaseusername.child(value2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String navusername = (String) dataSnapshot.child("username").getValue();
                mtxtusername.setText("\t" +navusername+"\n  Change Username");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabaseusername.child(value2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String navusername = (String) dataSnapshot.child("phone").getValue();
                if (!TextUtils.isEmpty(navusername)) {
                    mtxtphone.setText("\t" + navusername + "\n  Change Phone");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mDatabasProfilePic = FirebaseDatabase.getInstance().getReference("UsersProfile");

        mProfileImageBtn = (ImageView) findViewById(R.id.CropProfileimageView);
        mUpdateProfileBtn = (FloatingActionButton) findViewById(R.id.fabSavePic);
        mProfPic = (ImageView) findViewById(R.id.CropProfileimageView);

        mProgress = new ProgressDialog(this);
        mProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });
        //change


        mUpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this,mUpdateProfileBtn);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.selectgallery:
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                                break;
                            case R.id.pop_camera:
                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                startActivityForResult(intent, GALLERY_REQUEST);
                                //return false;
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });

        /*mUpdateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });*/

        //DISPLAY PROF PIC FROM DB TO APP
        mDatabasProfilePic.child(value2).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String post_image = (String) dataSnapshot.child("image").getValue();
                if (!TextUtils.isEmpty(post_image)) {
                    Picasso.with(ProfileActivity.this).load(post_image).networkPolicy(NetworkPolicy.OFFLINE).into(mProfPic);


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final ImageButton imageButton = (ImageButton) findViewById(R.id.imageButtonexp);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(ProfileActivity.this,imageButton);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.selectgallery:
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, GALLERY_REQUEST);
                                break;

                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setMultiTouchEnabled(true)
                    .setAspectRatio(1, 1)
                    .start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                ((ImageView) findViewById(R.id.CropProfileimageView)).setImageURI(mImageUri);
                startPosting();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

            }
        }
    }





    private void startPosting() {


        mProgress.show();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser2 = mAuth.getCurrentUser();
        String value= mCurrentUser2.getUid();

        final StorageReference filepath = mStorage.child("Profile_Images").child(value + ".jpg");


        filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {



                final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                final DatabaseReference newPost = mDatabase.push();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                FirebaseUser mCurrentUser2 = mAuth.getCurrentUser();
                final String value2 = mCurrentUser2.getUid();

                mDatabaseUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newPost.child("image").setValue(downloadUrl.toString());
                        mDatabase2.child(value2).child("image").setValue(downloadUrl.toString());
                        mDatabase3.child(value2).child("profimage").setValue(downloadUrl.toString());
                        newPost.child("uid").setValue(mCurrentUser.getUid());
                        newPost.child("username").setValue(dataSnapshot.child("username").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    mProgress.dismiss();
                                    Toast.makeText(ProfileActivity.this, "Profile Photo Updated Successfully!", Toast.LENGTH_SHORT).show();
                                    Intent homeIntent = new Intent(ProfileActivity.this, MainActivity.class);
                                    startActivity(homeIntent);
                                    finish();
                                }else {
                                    Toast.makeText(ProfileActivity.this, "Could Not Update Profile Photo!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }


                });
            }


        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( ProfileActivity.this, e.getMessage() , Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100* taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                mProgress.setMessage("Uploading Profile Photo \t" +(int)progress+"% ...");
            }
        });


    }

    public static String random(){
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i <randomLength;i++){
            tempChar = (char) (generator.nextInt(96)+32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}






