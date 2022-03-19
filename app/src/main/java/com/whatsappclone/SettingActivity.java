package com.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity
{

    private Button UpdateAccountSettings;
    private EditText username, userstatus;
    private CircleImageView userProfileImage;
    String currentuserid;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);





        RootRef= FirebaseDatabase.getInstance().getReference();

        InitializeFields();

        UpdateAccountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();

    }


    void demo()
    {

      mAuth= FirebaseAuth.getInstance();
//        Toast.makeText(this, "Demooo 1", Toast.LENGTH_SHORT).show();
       currentuserid=mAuth.getCurrentUser().getUid();

        Log.d("err", String.valueOf(mAuth));
//      Toast.makeText(this, "Id:"+currentuserid, Toast.LENGTH_SHORT).show();


    }
    private void InitializeFields()
    {
        UpdateAccountSettings= findViewById(R.id.update_settings_button);
        username=findViewById(R.id.set_user_name);
        userstatus= findViewById(R.id.set_user_status);
        userProfileImage=findViewById(R.id.set_profile_image);
    }


    private void UpdateSettings()
    {
        demo();
        String setUserName= username.getText().toString();
        String setUserStatus= userstatus.getText().toString();

        if(TextUtils.isEmpty(setUserName))
        {
            Toast.makeText(this, "Please Enter The Name", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(setUserStatus))
        {
            Toast.makeText(this, "Please type the status", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap<String, String> profilemap= new HashMap<>();
            profilemap.put("uid", currentuserid);
            profilemap.put("name", setUserName);
            profilemap.put("status", setUserStatus);

            RootRef.child("Users").child(currentuserid).setValue(profilemap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();
                                Toast.makeText(SettingActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                String error=task.getException().toString();
                                Toast.makeText(SettingActivity.this, "Error " +error, Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }

    private void RetrieveUserInfo()
    {

       demo();

        RootRef.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")) && (dataSnapshot.hasChild("image")))
                {
                     String retriveUserName= dataSnapshot.child("name").getValue().toString();
                    String retriveUserStatus= dataSnapshot.child("status").getValue().toString();
                    String profileImage= dataSnapshot.child("image").getValue().toString();

                    username.setText(retriveUserName);
                    userstatus.setText(retriveUserStatus);
                }
                else if((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                {
                    String retriveUserName= dataSnapshot.child("name").getValue().toString();
                    String retriveUserStatus= dataSnapshot.child("status").getValue().toString();


                    username.setText(retriveUserName);
                    userstatus.setText(retriveUserStatus);

                }
                else
                {
                    Toast.makeText(SettingActivity.this, "Please Set and Update your profile information", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(SettingActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


}
