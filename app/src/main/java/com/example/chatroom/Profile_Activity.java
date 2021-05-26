package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;

public class Profile_Activity extends AppCompatActivity {

    public static final int PICK_IMAGE= 111;

    User user;
    ImageView img_back;
    ImageView img_avt;
    TextView username;
    TextView phone;
    TextView email;
    TextView address;

    ImageView btn_updateAvt;
    ImageView btn_saveAvt;
    ImageView btn_edit;
    ImageView btn_save;
    ImageView btn_cancel;
    EditText etxt_username;
    EditText etxt_phone;
    EditText etxt_email;
    EditText etxt_address;
//    LinearLayout line1;

    Uri selectedImage;
    FirebaseFirestore db;

    String avtResource;

    public void getUser(String userID) {
        db= FirebaseFirestore.getInstance();
        System.out.println("************* userID");
        DocumentReference myProfile= db.collection("User").document(userID);
        myProfile.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user= documentSnapshot.toObject(User.class);
                if(user.getAvtResource() != null) {
                    avtResource = user.getAvtResource();
                    Picasso.get().load(user.getAvtResource()).into(img_avt);
                }
                username.setText(user.getUserName());
                phone.setText(user.getPhone());
                email.setText(user.getEmail());
                address.setText(user.getAddress());

                etxt_username.setText(user.getUserName());
                etxt_phone.setText(user.getPhone());
                etxt_email.setText(user.getEmail());
                etxt_address.setText(user.getAddress());
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("*************");
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);

        img_back= findViewById(R.id.img_back);
        img_avt= findViewById(R.id.img_avt);
        username= findViewById(R.id.txt_username);
        phone= findViewById(R.id.txt_phone);
        email= findViewById(R.id.txt_email);
        address= findViewById(R.id.txt_address);

        btn_updateAvt= findViewById(R.id.btn_updateAvt);
        btn_saveAvt= findViewById(R.id.btn_saveAvt);
        btn_save= findViewById(R.id.btn_save);
        btn_cancel= findViewById(R.id.btn_cancel);
        btn_edit= findViewById(R.id.btn_edit);
        etxt_username= findViewById(R.id.etxt_username);
        etxt_phone= findViewById(R.id.etxt_phone);
        etxt_email= findViewById(R.id.etxt_email);
        etxt_address= findViewById(R.id.etxt_address);
//        line1= findViewById(R.id.layout_line1);

        String userID= getIntent().getStringExtra("userID");
        String myID= FirebaseAuth.getInstance().getUid();
        if (!userID.equals(myID)) {
            btn_updateAvt.setVisibility(View.GONE);
            btn_edit.setVisibility(View.GONE);
        }
        System.out.println(myID + "-" +userID);
        getUser(userID);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_updateAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE);
            }
        });

        btn_saveAvt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                StorageReference imgRef = storageRef.child("avatar/"+ userID);
                UploadTask uploadImg = imgRef.putFile(selectedImage);
                uploadImg.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        btn_updateAvt.setVisibility(View.VISIBLE);
                        btn_saveAvt.setVisibility(View.GONE);
                        img_avt.setImageResource(R.drawable.ic_round_perm_identity_24);
                        Toast.makeText(getApplicationContext(), "cập nhật ảnh đại diện tất bại", Toast.LENGTH_LONG).show();
                    }
                });

                uploadImg.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        btn_updateAvt.setVisibility(View.VISIBLE);
                        btn_saveAvt.setVisibility(View.GONE);
                        imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                db= FirebaseFirestore.getInstance();
                                db.collection("User").document(userID).update("avtResource", uri.toString());
                            }
                        });
                        Toast.makeText(getApplicationContext(), "cập nhật ảnh đại diện thành công", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertViewProfile();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db= FirebaseFirestore.getInstance();
                DocumentReference myProfile= db.collection("User").document(userID);
                if (username.getText().toString() != etxt_username.getText().toString()) {
                    myProfile.update("username", etxt_username.getText().toString());
                    username.setText(etxt_username.getText().toString());
                }
                if (phone.getText().toString() != etxt_phone.getText().toString()) {
                    myProfile.update("phone", etxt_phone.getText().toString());
                    phone.setText(etxt_phone.getText().toString());
                }
                if (email.getText().toString() != etxt_email.getText().toString()) {
                    myProfile.update("email", etxt_email.getText().toString());
                    email.setText(etxt_email.getText().toString());
                }
                if (address.getText().toString() != etxt_address.getText().toString()) {
                    myProfile.update("address", etxt_address.getText().toString());
                    address.setText(etxt_address.getText().toString());
                }

                convertViewProfile();
                Toast.makeText(getApplicationContext(), "cập nhật thông tin thành công", Toast.LENGTH_LONG).show();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertViewProfile();

                etxt_username.setText(username.getText().toString());
                etxt_phone.setText(phone.getText().toString());
                etxt_email.setText(email.getText().toString());
                etxt_address.setText(address.getText().toString());
            }
        });

        img_avt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (avtResource!= null) {
                    Intent intent = new Intent(getApplicationContext(), LargeImageActivity.class);
                    intent.putExtra("imageResource", avtResource);
                    startActivity(intent);
                } else Toast.makeText(getApplicationContext(), "Không tồn tại ảnh đại diện", Toast.LENGTH_LONG).show();
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
            img_avt.setImageBitmap(bitmap);

            btn_updateAvt.setVisibility(View.GONE);
            btn_saveAvt.setVisibility(View.VISIBLE);
        }
    }

    public void convertViewProfile() {
        if (btn_edit.getVisibility() == View.GONE) {
            btn_edit.setVisibility(View.VISIBLE);
            btn_save.setVisibility(View.GONE);
            btn_cancel.setVisibility(View.GONE);

            username.setVisibility(View.VISIBLE);
            etxt_username.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.VISIBLE);
            etxt_phone.setVisibility(View.INVISIBLE);
            email.setVisibility(View.VISIBLE);
            etxt_email.setVisibility(View.INVISIBLE);
            address.setVisibility(View.VISIBLE);
            etxt_address.setVisibility(View.INVISIBLE);
        } else {
            btn_edit.setVisibility(View.GONE);
            btn_save.setVisibility(View.VISIBLE);
            btn_cancel.setVisibility(View.VISIBLE);

            username.setVisibility(View.INVISIBLE);
            etxt_username.setVisibility(View.VISIBLE);
            phone.setVisibility(View.INVISIBLE);
            etxt_phone.setVisibility(View.VISIBLE);
            email.setVisibility(View.INVISIBLE);
            etxt_email.setVisibility(View.VISIBLE);
            address.setVisibility(View.INVISIBLE);
            etxt_address.setVisibility(View.VISIBLE);
        }

    }
}