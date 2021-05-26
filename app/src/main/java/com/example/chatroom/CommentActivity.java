package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.Adapter.CommentAdapter;
import com.example.Model.Comment;
import com.example.Model.ItemType;
import com.example.Model.Notification;
import com.example.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentActivity extends AppCompatActivity {
    public static final int PICK_IMAGE= 101;

    String postID;
    String fromUser;

    ListView lst_comments;
    ImageView btn_send;
    ImageView btn_choseImg;
    ImageView btn_closeImg;
    ImageView img_message;
    EditText etxt_message;
    ImageView img_back;
    ArrayList<Comment> lstComments;
    CommentAdapter commentAdapter;

    FirebaseFirestore db;
    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        lst_comments = (ListView)findViewById(R.id.lst_comments);
        btn_send= (ImageView) findViewById(R.id.btn_send);
        btn_choseImg= findViewById(R.id.btn_choseImg);
        btn_closeImg= findViewById(R.id.btn_closeImg);
        img_message= findViewById(R.id.img_message);
        etxt_message =(EditText)findViewById(R.id.etxt_message);
        img_back= (ImageView)findViewById(R.id.img_back);


        postID= getIntent().getStringExtra("postID");
        fromUser= getIntent().getStringExtra("fromUser");

        lstComments= new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        commentAdapter= new CommentAdapter(this, lstComments);
        lst_comments.setAdapter(commentAdapter);

        btn_choseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        btn_closeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_message.setImageDrawable(null);
                img_message.getLayoutParams().height=0;
                img_message.getLayoutParams().width=0;
                img_message.requestLayout();
                btn_choseImg.setVisibility(View.INVISIBLE);
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = etxt_message.getText().toString();
                if(!TextUtils.isEmpty(message)) {
                    img_message.setImageDrawable(null);
                    img_message.getLayoutParams().height=0;
                    img_message.getLayoutParams().width=0;
                    img_message.requestLayout();
                    etxt_message.setText(null);
                    btn_choseImg.setVisibility(View.INVISIBLE);

                    String myID= FirebaseAuth.getInstance().getCurrentUser().getUid();
                    db.collection("User").document(myID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> taskUser) {
                            if (taskUser.isSuccessful()) {
                                Comment comment= new Comment(myID, taskUser.getResult().getString("avtResource"), taskUser.getResult().getString("username"), postID, message, getTimeNow());
                                db.collection("Comment").document(postID).collection("List_Comment").add(comment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        comment.setCommentID(task.getResult().getId());
//                                        task.getResult().update("commentID", task.getResult().getId());
//                                        System.out.println("in add comment success : 1 -  " + img_message.getDrawable());
                                        if(selectedImage!=null) {
                                            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                            StorageReference imgRef = storageRef.child("comment_image/" + task.getResult().getId());
                                            UploadTask uploadImg = imgRef.putFile(selectedImage);
                                            uploadImg.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                                    System.out.println("in add comment success : 2");
                                                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri uri) {
//                                                            System.out.println("in add comment success : 3");
                                                            comment.setImg_message(uri.toString());
                                                        }
                                                    });
                                                }
                                            });
                                            uploadImg.addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getApplicationContext(), "Comment thất bại", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }

                                        task.getResult().set(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                    System.out.println("in add comment success : 4");
                                                img_message.setImageDrawable(null);
                                                img_message.getLayoutParams().height=0;
                                                img_message.getLayoutParams().width=0;
                                                img_message.requestLayout();
                                            }
                                        });

                                        DocumentReference post = db.collection("Post").document(postID);
                                        post.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    post.update("countCmt", task.getResult().getLong("countCmt").intValue()+1).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            System.out.println("increase cmt");
                                                        }
                                                    });
                                                }
                                            }
                                        });

                                        db.collection("Notification").add(new Notification(postID, 1, myID, taskUser.getResult().getString("avtResource"),
                                                taskUser.getResult().getString("username"), "bình luận", fromUser, false, getTimeNow()));

                                        Toast.makeText(getApplicationContext(), "đăng bài thành công", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Bình luận không thể để trống", Toast.LENGTH_LONG);
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public String getTimeNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            selectedImage = data.getData();
            Bitmap bitmap= null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            img_message.setImageBitmap(bitmap);
            img_message.getLayoutParams().height=400;
            img_message.getLayoutParams().width=400;
            img_message.requestLayout();
            System.out.println("img in comment is selected : "+img_message.getDrawable());
            btn_closeImg.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection("Comment").document(postID).collection("List_Comment").orderBy("timeSent").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.e("TAG", "Listen failed.", e);
                    return;
                }
                lstComments.clear();
                for (DocumentSnapshot d: value.getDocuments()) {
                    lstComments.add(d.toObject(Comment.class));
                    System.out.println("data change :"+d.getString("message"));
                }
                commentAdapter.notifyDataSetChanged();
            }
        });
    }
}