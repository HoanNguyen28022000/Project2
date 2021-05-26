package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Adapter.PostCardAdapter;
import com.example.Model.Follow;
import com.example.Model.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    ImageView img_avt;
    TextView txt_username;
    TextView txt_timePosted;
    TextView itemPrice;
    TextView itemName;
    TextView itemStatus;
    TextView itemDetail;
    ImageView img_item;
    CheckBox chk_follow;
    CheckBox chk_comment;
    ImageView close;
    ImageView deletePost;
    ImageView edit;

    FirebaseFirestore db;

    String postID;
    String itemType;
    boolean bool_status;
    String itemImage;
    String fromUser;

    Boolean isFollow;
    String myID;
    int position;

    Intent previousIntent;

    public void openDialog() {
        final Dialog dialog= new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_delete);
        dialog.setCancelable(true);

        Window window= dialog.getWindow();
        if (window== null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttr= window.getAttributes();
        windowAttr.gravity= Gravity.CENTER;
        window.setAttributes(windowAttr);

        Button cancel= dialog.findViewById(R.id.btn_cancel);
        Button delete= dialog.findViewById(R.id.btn_delete);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                db.collection("Comment").document(postID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("Comment of this post deleted");
                    }
                });

                db.collection("Post").document(postID).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("post deleted");
                    }
                });
                Toast.makeText(getApplicationContext(), "Xóa bài thành công", Toast.LENGTH_LONG).show();
                finish();
            }
        });

        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        previousIntent= getIntent();
        position= previousIntent.getIntExtra("position", -1);
        postID= previousIntent.getStringExtra("postID");
        isFollow = previousIntent.getBooleanExtra("isFollow", false);
        myID= FirebaseAuth.getInstance().getCurrentUser().getUid();

        img_avt= findViewById(R.id.img_avt);
        txt_username= findViewById(R.id.txt_username);
        txt_timePosted= findViewById(R.id.txt_timePosted);
        itemPrice= findViewById(R.id.itemPrice);
        itemName= findViewById(R.id.itemName);
        itemStatus= findViewById(R.id.itemStatus);
        itemDetail= findViewById(R.id.itemDetail);
        img_item= findViewById(R.id.img_item);
        chk_follow= findViewById(R.id.chk_follow);
        chk_comment= findViewById(R.id.chk_comment);
        close= findViewById(R.id.btn_close);
        edit= findViewById(R.id.btn_edit);
        deletePost= findViewById(R.id.btn_dialogDelete);

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getApplicationContext(), EditPostActivity.class);
                intent.putExtra("postID", postID);
                intent.putExtra("fromUser", fromUser);
                intent.putExtra("itemName", itemName.getText().toString());
                intent.putExtra("itemType", itemType);
                intent.putExtra("itemPrice", itemPrice.getText().toString().substring(0,itemPrice.getText().toString().indexOf('V')).trim());
                intent.putExtra("itemStatus", bool_status);
                intent.putExtra("itemDetail", itemDetail.getText().toString());
                intent.putExtra("itemImage", itemImage);
                startActivity(intent);
            }
        });

        deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        db= FirebaseFirestore.getInstance();

        chk_follow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isFollow= true;
                    chk_follow.setButtonDrawable(R.drawable.ic_baseline_favorite_24);
                } else {
                    isFollow=false;
                    chk_follow.setButtonDrawable(R.drawable.ic_baseline_favorite_border_24);
                }
            }
        });

        chk_follow.setChecked(isFollow);

        chk_follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_follow.isChecked()) {
                    db.collection("Post").document(postID).update("countFollows", Integer.parseInt(chk_follow.getText().toString()) +1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            System.out.println("follow thanh cong");
                        }
                    });

                    db.collection("Follow").add(new Follow(myID, postID)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {

                        }
                    });
                    db.collection("User").document(myID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                db.collection("Notification").add(new Notification(postID, 1, myID,
                                        task.getResult().getString("avtResource"), task.getResult().getString("username"), "theo dõi", fromUser, false, getTimeNow())).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        System.out.println("add notification into db success");
                                    }
                                });
                            }
                        }
                    });
                } else {
                    db.collection("Post").document(postID).update("countFollows", Integer.parseInt(chk_follow.getText().toString()) -1).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            System.out.println("unfollow thanh cong");
                        }
                    });

                    db.collection("Follow").whereEqualTo("userID", myID).whereEqualTo("postID", postID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
            }
        });

        chk_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chk_comment.isChecked()) {
                    chk_comment.setChecked(false);
                }
                Intent intent= new Intent(getApplicationContext(), CommentActivity.class);
                intent.putExtra("postID", postID);
                intent.putExtra("fromUser", fromUser);
                startActivity(intent);
            }
        });


        db.collection("Post").document(postID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                if (value != null && value.exists()) {
                    if(!myID.equals(value.getString("fromUser"))) {
                        edit.setVisibility(View.INVISIBLE);
                        deletePost.setVisibility(View.INVISIBLE);
                    }

                    Picasso.get().load(value.getString("avtResource")).into(img_avt);
                    txt_username.setText(value.getString("username"));
                    itemPrice.setText(String.valueOf(value.getLong("price").intValue())+ " VND");
                    itemName.setText(value.getString("itemName"));
                    if (value.getBoolean("status")) {
                        itemStatus.setText("chưa bán");
                        bool_status=true;
                    } else  {
                        itemStatus.setText("đã bán");
                    }
                    if (itemImage==null || !itemImage.equals(value.getString("imgItemResource"))) Picasso.get().load(value.getString("imgItemResource")).into(img_item);
                    itemDetail.setText(value.getString("detail"));
                    chk_follow.setText(String.valueOf(value.getLong("countFollows").intValue()));
                    chk_comment.setText(String.valueOf(value.getLong("countCmt").intValue()));

                    fromUser= value.getString("fromUser");
                    itemType= value.getString("itemType");
                    itemImage= value.getString("imgItemResource");

                    previousIntent.putExtra("position", position);
                    previousIntent.putExtra("postID", postID);
                    previousIntent.putExtra("itemName", value.getString("username"));
                    previousIntent.putExtra("itemType", value.getString("itemType"));
                    previousIntent.putExtra("itemPrice", value.getLong("price").intValue());
                    previousIntent.putExtra("itemStatus", value.getBoolean("status"));
                    previousIntent.putExtra("itemDetail", value.getString("detail"));
                    previousIntent.putExtra("itemImage", value.getString("imgItemResource"));
                    previousIntent.putExtra("countFollows", value.getLong("countFollows").intValue());
                    previousIntent.putExtra("countCmt", value.getLong("countCmt").intValue());
                    previousIntent.putExtra("isFollow", isFollow);

                    setResult(Activity.RESULT_OK, previousIntent);
                }
            };
        });


    }

    public String getTimeNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

}