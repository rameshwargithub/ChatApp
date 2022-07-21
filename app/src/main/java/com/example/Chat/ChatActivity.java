package com.example.Chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private ImageView imageViesBackBtn;
    private TextView textViewChatWithUser;
    private EditText editTextMessage;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerViewChat;

    String Username,otherName;
    //String msg;

    FirebaseDatabase database;
    DatabaseReference reference;

    MessageAdapter adapter;
    List<ModelClass> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        imageViesBackBtn=findViewById(R.id.imageViewBack);
        textViewChatWithUser=findViewById(R.id.textViewChat);
        editTextMessage=findViewById(R.id.editTextMessage);
        floatingActionButton=findViewById(R.id.fab);
        recyclerViewChat=findViewById(R.id.rvChat);

        recyclerViewChat.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerViewChat.setHasFixedSize(true);
        //msg="";
        list=new ArrayList<>();

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        Username=getIntent().getStringExtra("Username");
        otherName=getIntent().getStringExtra("otherName");
        textViewChatWithUser.setText(otherName);

        imageViesBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(ChatActivity.this,MainActivity.class);
                startActivity(i);
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message=editTextMessage.getText().toString();
                if(!message.equals("")){
                    sendMessage(message);
                    editTextMessage.setText("");
                }
            }
        });
        getMessage();

        adapter=new MessageAdapter(list,Username/*,otherName,ChatActivity.this*/);
        recyclerViewChat.setAdapter(adapter);


    }

    public void getMessage() {
        reference.child("Messages").child(Username).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ModelClass modelClass=snapshot.getValue(ModelClass.class);
                list.add(modelClass);
                adapter.notifyDataSetChanged();
                recyclerViewChat.scrollToPosition(list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        adapter=new MessageAdapter(list,Username);
        recyclerViewChat.setAdapter(adapter);
        /*reference.child("Messages").child(Username).child(otherName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String key =snapshot.getKey();
                //Toast.makeText(ChatActivity.this, "Chat keys are ="+key, Toast.LENGTH_LONG).show();
                if(!key.equals("")){

                    reference.child("Messages").child(Username).child(otherName).child(key).child("message").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String msg=snapshot.getValue().toString();
                            reference.child("Messages").child(Username).child(otherName).child(key).child("from").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String key2=snapshot.getValue().toString();
                                    if(key2.equals(otherName)){
                                        //String msg=;
                                        Toast.makeText(ChatActivity.this , "msg from "+key2+" "+msg, Toast.LENGTH_SHORT).show();
                                        list.add(msg);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            //String key1=snapshot.getKey();

                            //Toast.makeText(ChatActivity.this, "Chat keys are ="+msg, Toast.LENGTH_LONG).show();

                            //list.add(msg);
                            //adapter.notifyDataSetChanged();
                            //recyclerViewChat.scrollToPosition(list.size()-1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    list.add(msg);
                    adapter.notifyDataSetChanged();
                    recyclerViewChat.scrollToPosition(list.size()-1);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/

        //adapter=new MessageAdapter(list,Username,ChatActivity.this);
        //recyclerViewChat.setAdapter(adapter);

    }

    public void sendMessage(String message) {
        final String key=reference.child("Messages").child(Username).child(otherName).push().getKey();
        final Map<String,Object> messageMap=new HashMap<>();
        messageMap.put("message",message);
        messageMap.put("from",Username);
        reference.child("Messages").child(Username).child(otherName).child(key).setValue(messageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    reference.child("Messages").child(otherName).child(Username).child(key).setValue(messageMap);
                }
            }
        });
    }
}