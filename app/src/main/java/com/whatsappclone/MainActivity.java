package com.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAccessorAdaptor mytabsAccessorAdaptor;

    private FirebaseUser currentuser;
    private FirebaseAuth mAuth;

    private DatabaseReference Rootref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth=FirebaseAuth.getInstance();
        currentuser= mAuth.getCurrentUser();
        Rootref= FirebaseDatabase.getInstance().getReference();

        mtoolbar=(Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Whatsapp");

        myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        mytabsAccessorAdaptor=new TabsAccessorAdaptor(getSupportFragmentManager());
        myViewPager.setAdapter(mytabsAccessorAdaptor);

        myTabLayout = (TabLayout)findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);




    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentuser==null)
        {
            SendUserToLoginActivity();
        }
        else {
            VerifyUserExistence();
        }
    }

    private void VerifyUserExistence()
    {
        String currentuserid= mAuth.getCurrentUser().getUid();
        Rootref.child("Users").child(currentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.child("name").exists()))
                {
                    Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
         super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout_option)
        {
            Log.d("error 1", String.valueOf(item.getTitle()));
            mAuth.signOut();
            SendUserToLoginActivity();
        }
        if(item.getItemId() == R.id.find_settings_option)
        {
            Log.d("error 2", String.valueOf(item.getTitle()));
            SendUserToSettingsActivity();

        }
        if(item.getItemId() == R.id.create_group_option)
        {
            Log.d("error 3", String.valueOf(item.getTitle()));
           RequestNewGroup();

        }
        if(item.getItemId() == R.id.find_friends_option)
        {
            Log.d("error 4", String.valueOf(item.getTitle()));
            SendUserToFindFriendsActivity();
        }

        return true;
    }

    private void RequestNewGroup()
    {

        //final AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        builder.setCancelable(true);


        final EditText groupNameField= new EditText(MainActivity.this);
        groupNameField.setHint("E.G Friends");
       builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String groupName= groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName))
                {

                    Toast.makeText(MainActivity.this, "Please Enter the group name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    CreateNewGroup(groupName);
                }

            }
        });
       builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        });

       AlertDialog alertDialog= builder.create();
       alertDialog.show();

       //builder.show();


    }

    private void CreateNewGroup(final String groupName)
    {
        Rootref.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(MainActivity.this, groupName+" is created successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void SendUserToLoginActivity() {

        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);

        startActivity(loginIntent);



    }

    private void SendUserToSettingsActivity() {

        Intent settingsIntent=new Intent(MainActivity.this,SettingActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();

    }

    private void SendUserToFindFriendsActivity() {

        Intent FindFriendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(FindFriendsIntent);


    }
}
