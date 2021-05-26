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

import androidx.annotation.NonNull;

import com.example.Model.Notification;
import com.example.Model.OptionMenu;
import com.example.Model.Post;
import com.example.chatroom.PostActivity;
import com.example.chatroom.Profile_Activity;
import com.example.chatroom.R;
import com.example.chatroom.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotificationAdapter extends BaseAdapter {
    public final static int UPDATE_POST_NOTIFICATION= 115;

    Context context;
    ArrayList<Notification> lst_Notifications;

    public NotificationAdapter(Context context, ArrayList<Notification> lst_Notifications) {
        this.context = context;
        this.lst_Notifications = lst_Notifications;
    }

    @Override
    public int getCount() {
        return lst_Notifications.size();
    }

    @Override
    public Object getItem(int position) {
        return lst_Notifications.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_item_notification, parent, false);
        }

        ImageView img_avt= convertView.findViewById(R.id.img_avt);
        TextView username= convertView.findViewById(R.id.txt_username);
        TextView txt_action= convertView.findViewById(R.id.txt_action);
        TextView txt_time= convertView.findViewById(R.id.txt_time);

        Notification notification= lst_Notifications.get(position);

        if (notification.isReaded()) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        } else convertView.setBackgroundColor(context.getResources().getColor(R.color.white));

        Picasso.get().load(notification.getAvtResource()).into(img_avt);
        username.setText(notification.getUsername());
        txt_action.setText(notification.getAction() + " bài viết của bạn");
        txt_time.setText(notification.getTime());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore db= FirebaseFirestore.getInstance();
                db.collection("Notification").whereEqualTo("postID", notification.getPostID())
                        .whereEqualTo("toUser", notification.getToUser()).whereEqualTo("action", notification.getAction()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            WriteBatch writeBatch= db.batch();
                            writeBatch.update(db.collection("Notification").document(task.getResult().getDocuments().get(0).getId()), "readed", true).commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
//                            writeBatch.update(db.collection("Notification").document(task.getResult().getDocuments().get(0).getId()), "new", false).commit().addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task) {
//
//                                }
//                            });
                        }
                    }
                });
                Intent intent= new Intent(context, PostActivity.class);
                intent.putExtra("position", position);
                if (notification.getToUserType()==1) intent.putExtra("isFollow", false);
                else intent.putExtra("isFollow", true);
                intent.putExtra("postID", notification.getPostID());
                ((Activity)context).startActivityForResult(intent,UPDATE_POST_NOTIFICATION);
            }
        });

        return convertView;
    }
}
