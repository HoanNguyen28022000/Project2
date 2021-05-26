package com.example.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.Model.Follow;
import com.example.Model.Notification;
import com.example.Model.Post;
import com.example.chatroom.CommentActivity;
import com.example.chatroom.LargeImageActivity;
import com.example.chatroom.PostActivity;
import com.example.chatroom.Profile_Activity;
import com.example.chatroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class PostCardAdapter extends BaseAdapter {
    public static final int COMMENT=100;
    public  final  static  int  UPDATE_POST=112;

    Context context;
    ArrayList<Post> lstPost;

    String myID;

    FirebaseStorage storage;
    FirebaseFirestore db;

    public PostCardAdapter(Context context, ArrayList<Post> lstPost) {
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
//        boolean newLoad=true;
        if(convertView==null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_post_card, parent, false);
        }

        ImageView img_avt= convertView.findViewById(R.id.img_avt);
        TextView txt_username= convertView.findViewById(R.id.txt_username);
        TextView txt_timePosted= convertView.findViewById(R.id.txt_timePosted);
        TextView txt_itemPrice= convertView.findViewById(R.id.txt_itemPrice);
        TextView txt_itemName= convertView.findViewById(R.id.txt_itemName);
        TextView txt_itemStatus= convertView.findViewById(R.id.txt_itemStatus);
        TextView txt_itemDetail= convertView.findViewById(R.id.txt_itemDetail);
        ImageView img_item= convertView.findViewById(R.id.img_item);
        CheckBox chk_follow= (CheckBox) convertView.findViewById(R.id.chk_follow);
        CheckBox chk_comment= convertView.findViewById(R.id.chk_comment);

        db= FirebaseFirestore.getInstance();

        String userID= lstPost.get(position).getFromUser();
        myID =FirebaseAuth.getInstance().getCurrentUser().getUid();

        Picasso.get().load(lstPost.get(position).getAvtResource()).into(img_avt);
        txt_username.setText(lstPost.get(position).getUsername());
        txt_timePosted.setText(lstPost.get(position).getTimePosted().toString());
        txt_itemPrice.setText("Giá :"+lstPost.get(position).getPrice()+ " VND");
        txt_itemName.setText("Tên sản phẩm:" +lstPost.get(position).getItemName());
        if (lstPost.get(position).isStatus()) {
            txt_itemStatus.setText("Trạng thái : chưa bán");
        } else  txt_itemStatus.setText("Trạng thái : đã bán");

        Picasso.get().load(lstPost.get(position).getImgItemResource()).into(img_item);
        txt_itemDetail.setText("Mô tả :" +lstPost.get(position).getDetail());
        chk_follow.setText(lstPost.get(position).getCountFollows() + " theo dõi");
        chk_comment.setText(lstPost.get(position).getCountCmt() + " bình luận");

        chk_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_comment.isChecked()) {
                    chk_comment.setChecked(false);
                }
                Intent intent= new Intent(context, CommentActivity.class);
                intent.putExtra("postID", lstPost.get(position).getPostID());
                intent.putExtra("fromUser", lstPost.get(position).getFromUser());
                ((Activity)context).startActivityForResult(intent, COMMENT);
            }
        });

        img_avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("TAG", "avatar clicked");

                Intent intent= new Intent(context, Profile_Activity.class);
                intent.putExtra("userID", lstPost.get(position).getFromUser());
                ((Activity)context).startActivity(intent);
            }
        });

        chk_follow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chk_follow.setButtonDrawable(R.drawable.ic_baseline_favorite_24);
                } else chk_follow.setButtonDrawable(R.drawable.ic_baseline_favorite_border_24);
            }
        });

        if (lstPost.get(position).isFollowed()) {
            chk_follow.setChecked(true);
        } else {
            chk_follow.setChecked(false);
        }

        chk_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_follow.isChecked()) {
                    DocumentReference df = db.collection("Post").document(lstPost.get(position).getPostID());
                    df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                int f= task.getResult().getLong("countFollows").intValue() + 1;
                                lstPost.get(position).setCountFollows(f);
                                chk_follow.setText(String.valueOf(f) + " theo dõi");
                                df.set(lstPost.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        lstPost.get(position).setFollowed(true);
                                        chk_follow.setText(String.valueOf(f) + " theo dõi");
                                        db.collection("Follow").add(new Follow(myID, lstPost.get(position).getPostID())).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                System.out.println("add follow into db success");
                                            }
                                        });
                                        Post post= lstPost.get(position);
                                        if(post.getFromUser().equals(myID)) {
                                            db.collection("Notification").add(new Notification(post.getPostID(), 2, post.getFromUser(),
                                                    post.getAvtResource(), post.getUsername(), "theo dõi", myID, false, getTimeNow())).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                                    System.out.println("add notification into db success");
                                                }
                                            });
                                        } else {
                                            db.collection("User").document(myID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        db.collection("Notification").add(new Notification(post.getPostID(), 1, post.getFromUser(),
                                                                task.getResult().getString("avtResource"), task.getResult().getString("username"), "theo dõi", post.getFromUser(), false, getTimeNow())).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                                System.out.println("add notification into db success");
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            } else System.out.println("get cnt follow failed");
                        }
                    });
                } else {
                    DocumentReference df = db.collection("Post").document(lstPost.get(position).getPostID());
                    df.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                int f= task.getResult().getLong("countFollows").intValue() - 1;
                                lstPost.get(position).setCountFollows(f);
                                chk_follow.setText(String.valueOf(f) + " theo dõi");
                                lstPost.get(position).setFollowed(false);
                                df.set(lstPost.get(position)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
//                                        chk_follow.setButtonDrawable(R.drawable.ic_baseline_favorite_border_24);
                                        db.collection("Follow").whereEqualTo("userID", myID).whereEqualTo("postID", lstPost.get(position).getPostID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    WriteBatch writeBatch= db.batch();
                                                    writeBatch.delete(db.collection("Follow").document(task.getResult().getDocuments().get(0).getId())).commit().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                        }
                                                    });
                                                }
                                            }
                                        });
                                    }
                                });
                            } else System.out.println("get cnt follow failed");
                        }
                    });
                }
            }
        });

        img_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, LargeImageActivity.class);
                intent.putExtra("imageResource", lstPost.get(position).getImgItemResource());
                ((Activity)context).startActivity(intent);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, PostActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("isFollow", lstPost.get(position).isFollowed());
                intent.putExtra("postID", lstPost.get(position).getPostID());
                ((Activity)context).startActivityForResult(intent, UPDATE_POST);
            }
        });

        return convertView;

    }

    public String getTimeNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

}
