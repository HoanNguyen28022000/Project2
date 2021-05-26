package com.example.chatroom;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.ImageView;

public class LargeImageActivity extends AppCompatActivity {

    ImageView btn_close;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_large_image);

        String imageUrl= getIntent().getStringExtra("imageResource");

        btn_close= findViewById(R.id.btn_close);
        imageView = findViewById(R.id.imageView);

        Picasso.get().load(imageUrl).into(imageView);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}