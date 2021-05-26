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
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class EditPostActivity extends AppCompatActivity {
    public static final int PICK_IMAGE= 112;
    int sizeTypeList;
    int itemTypePosition;

    ImageView btn_close;
    TextView btn_save;
    EditText etxt_itemName;
    EditText etxt_itemPrice;
    RadioGroup itemStatus;
    RadioButton notSoldYet;
    RadioButton sold;
    EditText etxt_itemDetail;
    ImageView btn_chosePicture;
    ImageView img_item;
    Spinner spinner_itemType;
    ImageView addItemType;
    EditText etxt_newType;
    ImageView done;
    ImageView cancel;

    ArrayList<String> listType=new ArrayList();
    ArrayAdapter<String> adapter;

    Uri selectedImage;

    FirebaseFirestore db;

    String postID;
    String fromUser;
    String itemName;
    String itemType;
    String itemPrice;
    boolean status;
    String itemDetail;
    String itemImage;
    boolean isChangeItemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        String myID= FirebaseAuth.getInstance().getCurrentUser().getUid();

        postID=getIntent().getStringExtra("postID");
        fromUser= getIntent().getStringExtra("fromUser");
        itemName= getIntent().getStringExtra("itemName");
        itemType= getIntent().getStringExtra("itemType");
        itemPrice= getIntent().getStringExtra("itemPrice");
        status= getIntent().getBooleanExtra("itemStatus", true);
        itemDetail= getIntent().getStringExtra("itemDetail");
        itemImage= getIntent().getStringExtra("itemImage");

        btn_close= findViewById(R.id.btn_close);
        btn_save = findViewById(R.id.btn_save);
        etxt_itemName= findViewById(R.id.etxt_itemName);
        etxt_itemPrice= findViewById(R.id.etxt_itemPrice);
        itemStatus= findViewById(R.id.itemStatus);
        notSoldYet= findViewById(R.id.rd_notSoldYet);
        sold=findViewById(R.id.rd_sold);
        etxt_itemDetail= findViewById(R.id.etxt_itemDetail);
        btn_chosePicture= findViewById(R.id.btn_chose_picture);
        img_item= findViewById(R.id.img_item);
        addItemType= findViewById(R.id.btn_addItemType);
        spinner_itemType= findViewById(R.id.spinner_itemType);
        etxt_newType= findViewById(R.id.etxt_newType);
        done= findViewById(R.id.btn_done);
        cancel= findViewById(R.id.btn_cancel);

        etxt_itemName.setText(itemName);
        etxt_itemPrice.setText(itemPrice);
        etxt_itemDetail.setText(itemDetail);

        Picasso.get().load(itemImage).into(img_item);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, listType);
        spinner_itemType.setAdapter(adapter);

        notSoldYet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    status = true;
                    System.out.println("change status :" + status);
                }
            }
        });

        sold.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    status=false;
                    System.out.println("change status :" + status);
                }
            }
        });

        if(status) {
            notSoldYet.setSelected(true);
        } else sold.setSelected(true);

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

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemName= etxt_itemName.getText().toString();
                itemPrice= etxt_itemPrice.getText().toString();
                itemDetail= etxt_itemDetail.getText().toString();
                itemType= spinner_itemType.getSelectedItem().toString();

                db.collection("Post").document(postID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            Post post=task.getResult().toObject(Post.class);
                            post.setItemName(itemName);
                            post.setItemType(itemType);
                            post.setPrice(Long.parseLong(itemPrice));
                            post.setStatus(status);
                            post.setDetail(itemDetail);

                            if (isChangeItemImage) {
                                StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                StorageReference imgRef = storageRef.child("item_image/"+task.getResult().getId());
                                UploadTask uploadImg = imgRef.putFile(selectedImage);
                                uploadImg.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        System.out.println("post image updated thanh cong");
                                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                post.setImgItemResource(uri.toString());
                                                System.out.println("post updated id : " + task.getResult().getId());
                                                db.collection("Post").document(postID).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(), "cap nhat post thanh cong", Toast.LENGTH_LONG).show();
                                                        finish();
                                                    }
                                                });

                                            }
                                        });
                                    }
                                });
                                uploadImg.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("post image updated that bai");
                                    }
                                });
                            } else {
                                db.collection("Post").document(postID).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getApplicationContext(), "cap nhat post thanh cong", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                });
                            }

                            if(spinner_itemType.getSelectedItemPosition()==sizeTypeList) {
                                db.collection("ItemType").add(new ItemType(itemType)).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        System.out.println("new type : " + itemType);
                                    }
                                });
                            }


                        }
                    }
                });
            }
        });

        db= FirebaseFirestore.getInstance();
        CollectionReference Types= db.collection("ItemType");
        Types.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot dc : task.getResult()) {
                        listType.add(dc.getString("nameType"));
                        if(dc.getString("nameType").equals(itemType)) {
                            itemTypePosition= listType.size()-1;
                        }
                    }
                    sizeTypeList= listType.size();
                    adapter.notifyDataSetChanged();
                    spinner_itemType.setSelection(itemTypePosition);
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
            isChangeItemImage= true;
        }
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