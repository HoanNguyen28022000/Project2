package com.example.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Model.Post;
import com.example.chatroom.PostActivity;
import com.example.chatroom.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostCreatorAdapter extends BaseAdapter {
    public static  final int UPDATE_FOLLOW_MY_POST= 114;

    Context context;
    ArrayList<Post> lstPost;

    FirebaseStorage storage;
    FirebaseFirestore db;

    public PostCreatorAdapter(Context context, ArrayList<Post> lstPost) {
        this.context=context;
        this.lstPost= lstPost;
    }


    @Override
    public int getCount() {
        return lstPost.size();
    }

    @Override
    public Object getItem(int position) {
        return lstPost.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_my_post, parent, false);
        }

        TextView txt_timePosted= convertView.findViewById(R.id.txt_timePosted);
        TextView txt_itemName= convertView.findViewById(R.id.txt_itemName);
        TextView txt_itemStatus= convertView.findViewById(R.id.txt_itemStatus);

        txt_timePosted.setText(lstPost.get(position).getTimePosted().toString());
        txt_itemName.setText("Tên sản phẩm:" +lstPost.get(position).getItemName());
        if (lstPost.get(position).isStatus()) {
            txt_itemStatus.setText("chưa bán");
            txt_itemStatus.setBackgroundResource(R.drawable.bg_conner_green);
        } else  {
            txt_itemStatus.setText("đã bán");
            txt_itemStatus.setBackgroundResource(R.drawable.bg_conner_red);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, PostActivity.class);
                intent.putExtra("isFollow", lstPost.get(position).isFollowed());
                intent.putExtra("postID", lstPost.get(position).getPostID());
                ((Activity)context).startActivityForResult(intent, UPDATE_FOLLOW_MY_POST);
            }
        });

        return convertView;
    }
}
