package com.whatsappclone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity
{
    private String messageReceiverID, messageReceiverName, messagereceiverImage, messagesenderID;
    
    private TextView userName;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;
    private ImageButton SendMessageButton;
    private EditText MessageInputText;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdapter messageAdapter;
    private RecyclerView userMessagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth= FirebaseAuth.getInstance();
        messagesenderID= mAuth.getCurrentUser().getUid();
        RootRef= FirebaseDatabase.getInstance().getReference();

        messageReceiverID= getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName= getIntent().getExtras().get("visit_user_name").toString();
        messagereceiverImage= getIntent().getExtras().get("visit_user_image").toString();


        
        InitializeControllers();

        //Toast.makeText(this, messageReceiverName, Toast.LENGTH_SHORT).show();

        userName.setText(messageReceiverName);
        Picasso.get().load(messagereceiverImage).placeholder(R.drawable.profile_image).into(userImage);

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             SendMessage();
            }
        });
    }

    private void InitializeControllers()
    {


        ChatToolBar= (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(ChatToolBar);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView =layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);

        userImage= findViewById(R.id.custom_profile_image);
        userName= (TextView)findViewById(R.id.custom_profile_name);

        SendMessageButton= findViewById(R.id.send_image_btn);
        MessageInputText= findViewById(R.id.input_message);

        messageAdapter= new MessageAdapter(messagesList);
        userMessagesList= (RecyclerView) findViewById(R.id.private_messages_list_of_users);
        linearLayoutManager=new LinearLayoutManager(this);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messageAdapter);


    }

    @Override
    protected void onStart()
    {
        super.onStart();

        RootRef.child("Messages").child(messagesenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                    {
                        Messages messages=dataSnapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage()
    {
        String messageText= MessageInputText.getText().toString();

        if(TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef="Messages/" + messagesenderID+"/"+ messageReceiverID;
            String messageReceiverRef= "Messages/" + messageReceiverID +"/"+messagesenderID;

            DatabaseReference userMessageKeyRef = RootRef.child("Messages")
                    .child(messagesenderID).child(messageReceiverID).push();

            String messagePushID= userMessageKeyRef.getKey();

            Map messageTextBody= new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messagesenderID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSenderRef +"/"+ messagePushID,messageTextBody);
            messageBodyDetails.put(messageReceiverRef +"/"+ messagePushID,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ChatActivity.this, "Message Sent Successfully...", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });


        }
    }


}
