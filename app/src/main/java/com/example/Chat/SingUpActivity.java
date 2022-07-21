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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SingUpActivity extends AppCompatActivity {
    private ImageView imageView;
    private Button singUp;
    private TextInputEditText emailId,PassCode,UserName;
    private TextView login_btn;
    boolean imageControl =false;

    FirebaseAuth auth;
    FirebaseStorage storage;
    StorageReference reference;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Uri imageUri;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        imageView=findViewById(R.id.update_profile_pic);
        singUp=findViewById(R.id.btn_update);
        emailId=findViewById(R.id.edit_email);
        PassCode=findViewById(R.id.Edit_password);
        login_btn=findViewById(R.id.txt_goto_login);
        UserName=findViewById(R.id.update_user);

        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        reference=storage.getReference();
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
                uploadImage();
            }
        });
        singUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emailId.getText().toString();
                String password=PassCode.getText().toString();
                String Username=UserName.getText().toString();
                if(!email.equals("")&&!password.equals("")&&!Username.equals("")){
                    singupmethod(email,password,Username);
                }else{
                    Toast.makeText(SingUpActivity.this, "please enter user credential.", Toast.LENGTH_SHORT).show();
                }
                //uploadImage();
            }
        });
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SingUpActivity.this,LoginActivity.class));
            }
        });
    }

    private void singupmethod(String email, String password, String Username) {
        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Singing up..");
        progressDialog.show();

        HashMap<String,String> UserInfo=new HashMap<>();
        UserInfo.put("Email",email);
        UserInfo.put("Password",password);
        UserInfo.put("Username",Username);

        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    databaseReference.child("Users").child(auth.getUid()).setValue(UserInfo);
                    if(imageControl){
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String filepath=uri.toString();
                                databaseReference.child("Users").child(auth.getUid()).child("Image").setValue(filepath+".jpg").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SingUpActivity.this, "write to database is successful.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SingUpActivity.this, "write to database is not successful.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }else{
                        databaseReference.child("Users").child(auth.getUid()).child("Image").setValue("null");
                    }
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }                    Toast.makeText(SingUpActivity.this, "Sing Up Success", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(SingUpActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    if(progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }                    Toast.makeText(SingUpActivity.this, "Sing Up Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void uploadImage() {

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading Image..");
        progressDialog.show();

        if(imageUri!=null){
            SimpleDateFormat formatter=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
            Date now=new Date();
            String filename=formatter.format(now);
            String filenameExtend=UserName.getText().toString();
            reference=storage.getReference().child("images/"+filenameExtend+"_"+ filename);//this will creat ref. in fStorage.
            reference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(SingUpActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }else{
                        Toast.makeText(SingUpActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }
                }
            });
        }
    }

    ActivityResultLauncher<String> mGetContent=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            if(result!=null){
                imageView.setImageURI(result);
                imageUri=result;
                imageControl=true;
            }else{
                imageControl=false;
            }
        }
    });
}