package com.example.Chat;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ImageView circleImage;
    private TextInputEditText username_to_update;
    private Button update_btn;

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseStorage storage;
    StorageReference reference;

    Uri imageUri;
    boolean imageControl =false;
    ProgressDialog progressDialog;
    String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        circleImage=findViewById(R.id.update_profile_pic);
        username_to_update=findViewById(R.id.update_user);
        update_btn=findViewById(R.id.btn_update);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        firebaseUser=auth.getCurrentUser();
        storage=FirebaseStorage.getInstance();
        reference=storage.getReference();

        getUserInfo();

        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
                uploadImage();
            }
        });
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }
    public void updateProfile(){
        String username=username_to_update.getText().toString();
        databaseReference.child("Users").child(auth.getUid()).child("Username").setValue(username);
        if(imageControl){
            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    String filepath=uri.toString();
                    databaseReference.child("Users").child(auth.getUid()).child("Image").setValue(filepath+".jpg").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(ProfileActivity.this, "write to database is successful.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ProfileActivity.this, "write to database is not successful.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }else{
            databaseReference.child("Users").child(auth.getUid()).child("Image").setValue(image);
        }
        Intent intent=new Intent(ProfileActivity.this,LoginActivity.class);
        intent.putExtra("Username",username);
        startActivity(intent);
        finish();

    }

    public void getUserInfo(){
        databaseReference.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name=snapshot.child("Username").getValue().toString();
                image=snapshot.child("Image").getValue().toString();

                username_to_update.setText(name);

                if(image.equals("null")){
                    circleImage.setImageResource(R.drawable.account);
                }else{
                    Picasso.get().load(image).into(circleImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    ActivityResultLauncher<String> mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result!=null){
                circleImage.setImageURI(result);
                imageUri=result;
                imageControl=true;
            }else{
                imageControl=false;
            }
        }
    });

    private void uploadImage() {

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image..");
        progressDialog.show();

        if(imageUri!=null){
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            Date now=new Date();
            String filename=formatter.format(now);
            String filenameExtend=username_to_update.getText().toString();
            reference=storage.getReference().child("images/"+filenameExtend+"_"+ filename);//this will creat ref. in fStorage.
            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(ProfileActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }else{
                        Toast.makeText(ProfileActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }
    }
}