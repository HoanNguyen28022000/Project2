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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.core.ApiFuture;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class Register extends AppCompatActivity {

    private ImageView img_back;
    private Button btn_register;
    private EditText etxt_email;
    private EditText etxt_username;
    private EditText etxt_phone;
    private EditText etxt_address;
    private EditText etxt_password;
    private EditText etxt_confirm_password;
    private FirebaseAuth myAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        img_back=findViewById(R.id.img_back);
        etxt_email= findViewById(R.id.etxt_email);
        etxt_username= findViewById(R.id.etxt_username);
        etxt_phone= findViewById(R.id.etxt_phone);
        etxt_address= findViewById(R.id.etxt_address);
        etxt_password= findViewById(R.id.etxt_password);
        etxt_confirm_password=findViewById(R.id.etxt_passConfirm);
        btn_register=findViewById(R.id.btn_resgister);
        progressBar=findViewById(R.id.progressBar);
        myAuth= FirebaseAuth.getInstance();

        etxt_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
        etxt_confirm_password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Register.this, SignIn.class);
                startActivity(intent);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= etxt_email.getText().toString();
                String password= etxt_password.getText().toString();
                String confirmPassword= etxt_confirm_password.getText().toString();
                String username = etxt_username.getText().toString();
                String phone = etxt_phone.getText().toString();
                String address = etxt_address.getText().toString();

                if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(confirmPassword)
                        && !TextUtils.isEmpty(username) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(address)) {
                    if(password.equals(confirmPassword)) {
                        progressBar.setVisibility(View.VISIBLE);
                        myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    myAuth= FirebaseAuth.getInstance();
                                    String userID= myAuth.getCurrentUser().getUid();
                                    FirebaseFirestore db= FirebaseFirestore.getInstance();
                                    db.collection("User").document(userID).set(new User(username, phone, email, address));
                                    Log.e("TAG", "success");
                                    goToMain();
                                } else {
                                    String error= task.getException().getMessage();
                                    Log.e("TAG", error);
                                    Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_LONG);
                                }
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        });

                    } else {
                        Log.e("register Field", password+"-"+confirmPassword);
                        Toast.makeText(getApplicationContext(), "Password and ConfirmPassword not match, try again", Toast.LENGTH_LONG);
                    }

                } else {
                    Log.e("register", email+"-"+password+"-"+confirmPassword);
                    Toast.makeText(getApplicationContext(), "Fill all the form", Toast.LENGTH_LONG);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currUser= FirebaseAuth.getInstance().getCurrentUser();
        if (currUser!= null) {
            goToMain();
        }
    }

    private void goToMain() {
        Intent intent= new Intent(Register.this, Home.class);
        intent.putExtra("userID", FirebaseAuth.getInstance().getCurrentUser().getUid());
        startActivity(intent);
        finish();
    }
}