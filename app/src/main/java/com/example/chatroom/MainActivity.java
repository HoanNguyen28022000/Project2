package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import Adapter.MessageAdapter;
import Model.Message;
import Model.User;

public class MainActivity extends AppCompatActivity {

    List<Message> lst_mess;
    List<User> lst_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lst_mess = new ArrayList<>();
        lst_user = new ArrayList<>();

        lst_user.add(new User(R.drawable.avata, "me"));
        lst_user.add(new User(R.drawable.avata, "Lê Xuân Phúc"));

        lst_mess.add(new Message(lst_user.get(0), "12:17 11/3/2021", "Hello world"));
        lst_mess.add(new Message(lst_user.get(1), "12:18 11/3/2021", "hi, my name is Phúc"));
        lst_mess.add(new Message(lst_user.get(0), "12:19 11/3/2021", "How are you"));
        lst_mess.add(new Message(lst_user.get(1), "12:20 11/3/2021", "I'm fine thanks you!"));

        MessageAdapter messageAdapter = new MessageAdapter(lst_mess, this);
        ((ListView) findViewById(R.id.lst_Message)).setAdapter(messageAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currUser= FirebaseAuth.getInstance().getCurrentUser();
        if (currUser == null) {
            Intent intent = new Intent(MainActivity.this, SignIn.class);
            startActivity(intent);
            finish();
        }
    }
}