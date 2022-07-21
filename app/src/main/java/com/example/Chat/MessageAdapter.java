package com.example.Chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    List<ModelClass> list;
    String Username;
    //String otherName;
    //Context mContext;
    FirebaseDatabase database;
    DatabaseReference reference;

    boolean status;
    int send;
    int receive;

    public MessageAdapter(List<ModelClass> list, String Username/*,String otherName, Context mContext*/) {
        this.list = list;
        this.Username = Username;
        //this.otherName=otherName;
        //this.mContext=mContext;

        database=FirebaseDatabase.getInstance();
        reference=database.getReference();

        status=false;
        send=1;
        receive=2;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==send){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_send,parent,false);
        }else{
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.card_received,parent,false);
        }
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

        /*reference.child("Messages").child(Username).child(otherName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String key=snapshot.getKey();
                if(!key.equals("")){
                    reference.child("Messages").child(Username).child(otherName).child(key).child("from").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String key1= snapshot.getKey();
                            if(!key1.equals(otherName)){
                                reference.child("Messages").child(Username).child(otherName).child(key).child("message").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String msg1= snapshot.getKey();
                                        if(!msg1.equals("")){
                                            list.add(msg1);
                                            list.add("adapterWorking fine");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        holder.textView.setText(list.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            if(status){
                textView=itemView.findViewById(R.id.textViewSend);
                //list.get(send).setMessage(textView.getText().toString());
            }else{
                textView=itemView.findViewById(R.id.textViewReceive);
                //list.get(receive).setFrom(textView.getText().toString());
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(list.get(position).getFrom().equals(Username)){
            status=true;
            return send;
        }else{
            status=false;
            return receive;
        }
    }
}
