package com.example.Adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.Model.OptionMenu;
import com.example.Model.Post;
import com.example.Model.User;
import com.example.chatroom.CommentActivity;
import com.example.chatroom.Home;
import com.example.chatroom.Profile_Activity;
import com.example.chatroom.R;
import com.example.chatroom.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class OptionMenuAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<OptionMenu> lstOptions;
    private String myID;
    private ArrayList<Post> my_list_post;

    PostCreatorAdapter postCreatorAdapter;
    ListView list_my_post;

    public OptionMenuAdapter(Context context, ArrayList<OptionMenu> lstOptions) {
        this.context=context;
        this.lstOptions=lstOptions;
        this.myID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.my_list_post= new ArrayList<>();
    }

    public ArrayList<Post> getMy_list_post() {
        return my_list_post;
    }

    public void setMy_list_post(ArrayList<Post> my_list_post) {
        this.my_list_post = my_list_post;
    }

    public PostCreatorAdapter getPostCreatorAdapter() {
        return postCreatorAdapter;
    }

    public void setPostCreatorAdapter(PostCreatorAdapter postCreatorAdapter) {
        this.postCreatorAdapter = postCreatorAdapter;
    }

    @Override
    public int getCount() {
        return lstOptions.size();
    }

    @Override
    public Object getItem(int position) {
        return lstOptions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_option_menu, parent, false);
        }

        ImageView icOption= convertView.findViewById(R.id.ic_option);

        TextView txt_optionName= convertView.findViewById(R.id.txt_optionName);

        OptionMenu optionMenu= lstOptions.get(position);

        icOption.setImageResource(optionMenu.getIcResource());
        txt_optionName.setText(optionMenu.getOptionName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position==0) {
                    System.out.println(myID);
                    Intent intent= new Intent(context, Profile_Activity.class);
                    intent.putExtra("userID", myID);
                    ((Activity)context).startActivity(intent);
                }
                if(position==1) {
                    openDialog();
                }
                if(position==2) {

                }
                if (position==3) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent( (Activity)context, SignIn.class);
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    public void openDialog() {
        final Dialog dialog= new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_list_post_created);
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

        ImageView close= dialog.findViewById(R.id.btn_close);
        list_my_post= dialog.findViewById(R.id.list_my_post);
        postCreatorAdapter= new PostCreatorAdapter(context, my_list_post);
        list_my_post.setAdapter(postCreatorAdapter);

        FirebaseFirestore.getInstance().collection("Post").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null) return;
                my_list_post.clear();
                for(DocumentSnapshot d : value.getDocuments()) {
                    if (d.getString("fromUser").equals(myID)) {
                        my_list_post.add(d.toObject(Post.class));
                    }
                }
                postCreatorAdapter.notifyDataSetChanged();
            }


//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()) {
//                    my_list_post.clear();
//                    for(DocumentSnapshot d: task.getResult().getDocuments()) {
//                        my_list_post.add(d.toObject(Post.class));
//                    }
//                    postCreatorAdapter.notifyDataSetChanged();
//                }
//            }


        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
