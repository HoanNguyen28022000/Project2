package com.example.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.Adapter.FollowPostCardAdapter;
import com.example.Adapter.PostCardAdapter;
import com.example.Model.Post;
import com.example.chatroom.AddPostActivity;
import com.example.chatroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;

public class FollowFragment extends Fragment {
    ArrayList<Post> lstPost;
    ArrayList<String> myFollow= new ArrayList<>();
    ListView listPosts;
    TextView txt_notify;

    FollowPostCardAdapter followPostAdapter;
    FirebaseFirestore db;



    public FollowFragment() {
        super();
    }

    public FollowFragment(ArrayList<Post> lstPost) {
        this.lstPost=lstPost;
    }

    public ArrayList<Post> getLstPost() {
        return lstPost;
    }

    public void setLstPost(ArrayList<Post> lstPost) {
        this.lstPost = lstPost;
    }

    public FollowPostCardAdapter getPostAdapter() {
        return followPostAdapter;
    }

    public void setPostAdapter(FollowPostCardAdapter postAdapter) {
        this.followPostAdapter = postAdapter;
    }

    public ListView getListPosts() {
        return listPosts;
    }

    public void setListPosts(ListView listPosts) {
        this.listPosts = listPosts;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout= (LinearLayout) inflater.inflate(R.layout.layout_follow_fragment, null);

        listPosts= linearLayout.findViewById(R.id.listPosts);
        txt_notify= linearLayout.findViewById(R.id.txt_notify);

        lstPost= new ArrayList<>();
        followPostAdapter = new FollowPostCardAdapter(getActivity(), lstPost);
        listPosts.setAdapter(followPostAdapter);

        db=FirebaseFirestore.getInstance();
        String myID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Follow").addSnapshotListener(new EventListener<QuerySnapshot>() {

            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null) {
                    return;
                }
                myFollow.clear();
                for (DocumentSnapshot df: value.getDocuments()) {
                    System.out.print(df.getString("userID"));
                    if(df.getString("userID").equals(myID)) myFollow.add(df.getString("postID"));
                }
                db.collection("Post").orderBy("timePosted", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            lstPost.clear();
                            for (DocumentSnapshot d: task.getResult()) {
                                Post p= d.toObject(Post.class);
                                if(myFollow.contains(p.getPostID())) {
                                    System.out.println("add post followed by me "+ p.getPostID());
                                    p.setFollowed(true);
                                    lstPost.add(p);
                                }
                            }
                            followPostAdapter.notifyDataSetChanged();
                            if (lstPost.size()==0) txt_notify.setVisibility(View.VISIBLE);
                            else txt_notify.setVisibility(View.GONE);

                        }
                    }
                });

            }
        });



        return linearLayout;
    }

}
