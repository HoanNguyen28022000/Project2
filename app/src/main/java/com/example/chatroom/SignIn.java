package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignIn extends AppCompatActivity {

    private Button btn_signIn;
    private TextView txt_SignUp;
    private EditText etxt_email;
    private EditText etxt_password;
    private FirebaseAuth myAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btn_signIn = findViewById(R.id.btn_signIn);
        etxt_email = findViewById(R.id.etxt_email);
        etxt_password = findViewById(R.id.etxt_password);
        txt_SignUp = findViewById(R.id.txt_signUp);
        progressBar= findViewById(R.id.progressBar);
        myAuth=FirebaseAuth.getInstance();

        etxt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= etxt_email.getText().toString();
                String password= etxt_password.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
                    progressBar.setVisibility(View.VISIBLE);

                    myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.e("TAG0", "-----------------");
                            if(task.isSuccessful()) {
                                goToMain();
                            } else {
                                Toast.makeText(getApplicationContext(), "Email or Password incorrect", Toast.LENGTH_LONG).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Email or Password wasn't filled", Toast.LENGTH_LONG).show();
                }
            }
        });

        txt_SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignIn.this, Register.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currUser= FirebaseAuth.getInstance().getCurrentUser();
        if(currUser != null) {
            Intent intent = new Intent(SignIn.this, Home.class);
            startActivity(intent);
            finish();
        }
    }

    private void goToMain() {
//        System.out.println(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        Intent intent = new Intent(SignIn.this, Home.class);
        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
        finish();
    }
}