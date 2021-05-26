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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Model.ItemType;
import com.example.Model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddPostActivity extends AppCompatActivity {
    public static final int PICK_IMAGE= 111;
    int sizeTypeList;

    ImageView btn_close;
    TextView btn_post;
    EditText etxt_itemName;
    EditText etxt_itemPrice;
    EditText etxt_itemDetail;
    ImageView btn_chosePicture;
    ImageView img_item;
    Spinner spinner_itemType;
    ImageView addItemType;
    EditText etxt_newType;
    ImageView done;
    ImageView cancel;
    FirebaseFirestore db;

    ArrayList<String> listType=new ArrayList();
    ArrayAdapter<String> adapter;

    Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        btn_close= findViewById(R.id.btn_close);
        btn_post = findViewById(R.id.btn_post);
        etxt_itemName= findViewById(R.id.etxt_itemName);
        etxt_itemPrice= findViewById(R.id.etxt_itemPrice);
        etxt_itemDetail= findViewById(R.id.etxt_itemDetail);
        btn_chosePicture= findViewById(R.id.btn_chose_picture);
        img_item= findViewById(R.id.img_item);
        addItemType= findViewById(R.id.btn_addItemType);
        spinner_itemType= findViewById(R.id.spinner_itemType);
        etxt_newType= findViewById(R.id.etxt_newType);
        done= findViewById(R.id.btn_done);
        cancel= findViewById(R.id.btn_cancel);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listType);
        spinner_itemType.setAdapter(adapter);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addItemType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertAddType();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etxt_newType.setText("");

                convertAddType();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newType= etxt_newType.getText().toString();
                if(!TextUtils.isEmpty(newType)) {
                    listType.add(newType);
                    adapter.notifyDataSetChanged();
                    spinner_itemType.setSelection(listType.size()-1);
                    convertAddType();
                }
            }
        });

        btn_chosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        btn_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName= etxt_itemName.getText().toString();
                String itemPrice= etxt_itemPrice.getText().toString();
                String itemDetail= etxt_itemDetail.getText().toString();
                String itemType= spinner_itemType.getSelectedItem().toString();


                if(!TextUtils.isEmpty(itemName) && !TextUtils.isEmpty(itemPrice) && !TextUtils.isEmpty(itemPrice) && img_item.getDrawable() != null) {
                    String userID= FirebaseAuth.getInstance().getCurrentUser().getUid();

                    db.collection("User").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> taskUser) {
                            if (taskUser.isSuccessful()) {
                                String avtResource= taskUser.getResult().getString("avtResource");
                                String username= taskUser.getResult().getString("username");
                                Post newPost= new Post(userID, avtResource, username, getTimeNow(), Long.parseLong(itemPrice), itemName, itemType, true, itemDetail, 0,0);
                                db.collection("Post").add(newPost).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        // Create a storage reference from our app
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                        StorageReference imgRef = storageRef.child("item_image/"+task.getResult().getId());
                                        UploadTask uploadImg = imgRef.putFile(selectedImage);
                                        uploadImg.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                    @Override
                                                    public void onSuccess(Uri uri) {
                                                        task.getResult().update("imgItemResource", uri.toString());
                                                        task.getResult().update("postID", task.getResult().getId());
                                                        System.out.println("post id : " + task.getResult().getId());
                                                    }
                                                });

                                                if(spinner_itemType.getSelectedItemPosition()==sizeTypeList) {
                                                    db.collection("ItemType").add(new ItemType(itemType)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                                            System.out.println("new type : " + itemType);
                                                        }
                                                    });
                                                }
                                                Toast.makeText(getApplicationContext(), "đăng bài thành công", Toast.LENGTH_LONG).show();
                                                finish();
                                            }
                                        });
                                        uploadImg.addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "đăng bài thất bại", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });

                            }
                        }
                    });


                } else {
                    if (TextUtils.isEmpty(itemName)) {
                        Toast.makeText(getApplicationContext(), "Tên sản phẩm không đươc để trống", Toast.LENGTH_LONG).show();
                    } else if (TextUtils.isEmpty(itemPrice)) {
                        Toast.makeText(getApplicationContext(), "Gía sản phẩm không đươc để trống", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Ảnh sản phẩm cần được cung cấp", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        db= FirebaseFirestore.getInstance();
        CollectionReference Types= db.collection("ItemType");
        Types.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot dc : task.getResult()) {
                        System.out.println(dc.getData().toString());
                        listType.add(dc.getString("nameType"));
                    }
                    sizeTypeList= listType.size();
                    adapter.notifyDataSetChanged();
                }
            }
        });


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
            img_item.setImageBitmap(bitmap);

        }
    }

    public String getTimeNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return formatter.format(date);
    }

    public void convertAddType() {
        if(addItemType.getVisibility()==View.VISIBLE) {
            spinner_itemType.setVisibility(View.INVISIBLE);
            addItemType.setVisibility(View.INVISIBLE);
            done.setVisibility(View.VISIBLE);
            cancel.setVisibility(View.VISIBLE);
            etxt_newType.setVisibility(View.VISIBLE);
        } else {
            spinner_itemType.setVisibility(View.VISIBLE);
            addItemType.setVisibility(View.VISIBLE);
            done.setVisibility(View.INVISIBLE);
            cancel.setVisibility(View.INVISIBLE);
            etxt_newType.setVisibility(View.INVISIBLE);
        }
    }
}