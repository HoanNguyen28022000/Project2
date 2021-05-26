package com.example.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.Model.Comment;
import com.example.chatroom.Profile_Activity;
import com.example.chatroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    Context context;
    ArrayList<Comment> lstComments;
    FirebaseFirestore db= FirebaseFirestore.getInstance();

    public CommentAdapter(Context context, ArrayList<Comment> lstComments) {
        this.context=context;
        this.lstComments=lstComments;
    }

    @Override
    public int getCount() {
        return lstComments.size();
    }

    @Override
    public Object getItem(int position) {
        return lstComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_comment, parent, false);
        }

        ImageView img_avt= convertView.findViewById(R.id.img_avt);
         TextView txt_username= convertView.findViewById(R.id.txt_username);
        TextView txt_comment= convertView.findViewById(R.id.txt_comment);
        TextView txt_timeSent= convertView.findViewById(R.id.txt_timeSent);
        ImageView img_message= convertView.findViewById(R.id.img_message);

        Picasso.get().load(lstComments.get(position).getAvtResource()).into(img_avt);
        txt_username.setText(lstComments.get(position).getUsername());
        txt_comment.setText(lstComments.get(position).getMessage());
        txt_timeSent.setText(lstComments.get(position).getTimeSent());
        Picasso.get().load(lstComments.get(position).getImg_message()).into(img_message);

        img_avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, Profile_Activity.class);
                intent.putExtra("userID", lstComments.get(position).getFromUser());
                ((Activity)context).startActivity(intent);
            }
        });

        return convertView;
    }
}
