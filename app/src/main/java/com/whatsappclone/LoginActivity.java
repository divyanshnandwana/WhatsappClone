package com.whatsappclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private ProgressDialog loadingbar;


    private Button LoginButton, PhoneLoginButton;
    private EditText UserEmail, UserPassword;
    private TextView CreateNewLoginAccountLink, ForgotPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth= FirebaseAuth.getInstance();


        InitializeFields();

        CreateNewLoginAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendUserToRegisterActivity();

            }
        });


        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowUserToLogin();
            }
        });



    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            SendUserToMainActivity();
        }
    }

    private void AllowUserToLogin()
    {
        String email= UserEmail.getText().toString();
        String password=UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please Enter The Email ", Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please Enter the passWord", Toast.LENGTH_SHORT).show();
        }
        else
            {

                loadingbar.setTitle("Loging In");
                loadingbar.setMessage("Please Wait, while we Login for you");
                loadingbar.setCanceledOnTouchOutside(true);
                loadingbar.show();

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                      if(task.isSuccessful())
                      {
                          SendUserToMainActivity();
                          Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                          loadingbar.dismiss();
                      }
                          else
                          {
                              String message= task.getException().toString();
                              Toast.makeText(LoginActivity.this, "Error : "+ message, Toast.LENGTH_SHORT).show();
                              loadingbar.dismiss();
                          }

                    }
                });



        }
    }

    private void InitializeFields()
    {

        LoginButton= findViewById(R.id.login_Button);
        PhoneLoginButton= findViewById(R.id.phone_login_button);
        UserEmail= findViewById(R.id.login_email);
        UserPassword= findViewById(R.id.login_password);
        CreateNewLoginAccountLink= findViewById(R.id.need_new_account);
        ForgotPasswordLink= findViewById(R.id.forgot_password);

        loadingbar= new ProgressDialog(this);
    }



    private void SendUserToMainActivity()
    {
        Intent mainIntent=new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);

    }

    private void SendUserToRegisterActivity() {
        Intent registerIntent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(registerIntent);
    }

}
