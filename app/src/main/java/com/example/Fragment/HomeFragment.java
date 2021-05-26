package com.example.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.Adapter.PostCardAdapter;
import com.example.Model.Comment;
import com.example.Model.Follow;
import com.example.Model.Post;
import com.example.Model.User;
import com.example.chatroom.AddPostActivity;
import com.example.chatroom.R;
import com.example.chatroom.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FirebaseFirestore db;

    ArrayList<Post> lstPost;
    LinearLayout layout_search;
    ImageView btn_add_post;
    ListView listPosts;

    PostCardAdapter postAdapter;

    public HomeFragment() {
        super();
    }

    public HomeFragment(ArrayList<Post> lstPost) {
        this.lstPost=lstPost;
    }

    public ArrayList<Post> getLstPost() {
        return lstPost;
    }

    public void setLstPost(ArrayList<Post> lstPost) {
        this.lstPost = lstPost;
    }

    public PostCardAdapter getPostAdapter() {
        return postAdapter;
    }

    public void setPostAdapter(PostCardAdapter postAdapter) {
        this.postAdapter = postAdapter;
    }

    public ListView getListPosts() {
        return listPosts;
    }

    public void setListPosts(ListView listPosts) {
        this.listPosts = listPosts;
    }

    public void getListPost() {
        lstPost.clear();
        String myID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db= FirebaseFirestore.getInstance();
        db.collection("Follow").document(myID).collection("List_Follow").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> taskPostFollow) {
                if (taskPostFollow.isSuccessful()) {
                    ArrayList<String> myFollow= new ArrayList<>();
                    for (DocumentSnapshot df: taskPostFollow.getResult()) {
                        myFollow.add(df.getId());
                    }

                    db.collection("Post").orderBy("timePosted", Query.Direction.DESCENDING).limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot d: task.getResult()) {
                                    Post p= d.toObject(Post.class);
                                    if(myFollow.contains(p.getPostID())) {
                                        System.out.println("this post is followed by me");
                                        p.setFollowed(true);
                                    }
                                    lstPost.add(p);
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout relativeLayout= (RelativeLayout) inflater.inflate(R.layout.home_fragment, null);

        listPosts= (ListView) relativeLayout.findViewById(R.id.listPosts);
        layout_search= (LinearLayout) relativeLayout.findViewById(R.id.search_);
        btn_add_post= (ImageView) relativeLayout.findViewById(R.id.img_add_post);

        lstPost= new ArrayList<>();
        postAdapter = new PostCardAdapter(getActivity(), lstPost);
        listPosts.setAdapter(postAdapter);

        layout_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_add_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), AddPostActivity.class);
                startActivity(intent);
            }
        });

        layout_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        db=FirebaseFirestore.getInstance();
        String myID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("Follow").whereEqualTo("userID", myID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> taskPostFollow) {
                if (taskPostFollow.isSuccessful()) {
                    ArrayList<String> myFollow= new ArrayList<>();
                    for (DocumentSnapshot df: taskPostFollow.getResult()) {
                        myFollow.add(df.getString("postID"));
                    }
                    db.collection("Post").orderBy("timePosted", Query.Direction.DESCENDING).limit(3).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot d: task.getResult()) {
                                    Post p= d.toObject(Post.class);
                                    if(myFollow.contains(p.getPostID())) {
                                        System.out.println("is follow by me "+ p.getPostID());
                                        p.setFollowed(true);
                                    }
                                    lstPost.add(p);
                                }
                                postAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }

            }
        });

        return relativeLayout;
    }

}
