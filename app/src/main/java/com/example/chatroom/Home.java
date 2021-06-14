package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.Adapter.FollowPostCardAdapter;
import com.example.Adapter.FragmentAdapter;
import com.example.Adapter.NotificationAdapter;
import com.example.Adapter.PostCardAdapter;
import com.example.Adapter.PostCreatorAdapter;
import com.example.Fragment.FollowFragment;
import com.example.Fragment.HomeFragment;
import com.example.Fragment.MenuFragment;
import com.example.Fragment.NotificationFragment;
import com.example.Model.Notification;
import com.example.Model.Post;
import com.example.SendNotificationPack.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Home extends AppCompatActivity implements Serializable {

    TabLayout tabLayout;
    ViewPager viewPager;
    ArrayList<Fragment> lst_Fragments;
    FragmentAdapter fragmentHomeAdapter;
    TextView txt_newNotifications;

    ArrayList<Notification> lstNotifications;
    ArrayList<String> listNotificationID;
    int newNotifications;

    FirebaseFirestore db;
    String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseUser currUser= FirebaseAuth.getInstance().getCurrentUser();
        if (currUser == null) {
            Intent intent = new Intent(Home.this, SignIn.class);
            startActivity(intent);
            finish();
        } else {
            myID = currUser.getUid();
            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    String refreshToken = task.getResult();
                    Token token = new Token(refreshToken);
                    FirebaseFirestore.getInstance().collection("token").document(myID).set(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                        }
                    });
                }
            });
        }

        viewPager= (ViewPager)findViewById(R.id.viewPager);
        tabLayout= (TabLayout)findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_outline_home_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_favorite_border_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_baseline_notifications_none_24));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_menu));

        tabLayout.getTabAt(2).setCustomView(LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_tab_notifications, null));
        txt_newNotifications= tabLayout.getTabAt(2).getCustomView().findViewById(R.id.number_notifications);

        lst_Fragments= new ArrayList<>();
        lst_Fragments.add(new HomeFragment());
        lst_Fragments.add(new FollowFragment());
        lst_Fragments.add(new NotificationFragment());
        lst_Fragments.add(new MenuFragment());

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        fragmentHomeAdapter= new FragmentAdapter(this, getSupportFragmentManager(), lst_Fragments);
        viewPager.setAdapter(fragmentHomeAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition()==2) {
                    txt_newNotifications.setVisibility(View.INVISIBLE);
                    db.collection("Notification").whereEqualTo("toUser", myID).whereEqualTo("new", true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()) {
                                WriteBatch writeBatch= db.batch();
                                for (DocumentSnapshot d: task.getResult().getDocuments()) {
                                    writeBatch.update(db.collection("Notification").document(d.getId()), "new", false);
                                }
                                writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                    }
                                });
                            }
                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                if (tab.getPosition()==0) {
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( requestCode == PostCardAdapter.COMMENT && resultCode == RESULT_OK && data != null) {
            ArrayList<Post> lp=((HomeFragment)fragmentHomeAdapter.getItem(0)).getLstPost();
            PostCardAdapter postAdapter= ((HomeFragment)fragmentHomeAdapter.getItem(0)).getPostAdapter();
            FirebaseFirestore.getInstance().collection("Comment").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                        return;
                    }
                    for (DocumentChange doc : value.getDocumentChanges()) {
                        System.out.println("**************1");
                        for (Post post: lp) {
                            if(doc.getDocument().getId().equals(post.getPostID())) {
                                post.setCountCmt(doc.getDocument().getLong("countCmt").intValue());
                                System.out.println("**************2");
                            }
                        }
                    }
                    postAdapter.notifyDataSetChanged();
                }
            });
        }

        if( requestCode == PostCardAdapter.UPDATE_POST && resultCode == RESULT_OK && data != null) {
            int position= data.getIntExtra("position", -1);
            String itemName= data.getStringExtra("itemName");
            String itemType= data.getStringExtra("itemType");
            int itemPrice= data.getIntExtra("itemPrice", 0);
            boolean itemStatus= data.getBooleanExtra("itemStatus", true);
            String itemDetail= data.getStringExtra("itemDetail");
            String itemImage= data.getStringExtra("itemImage");
            int countFollows= data.getIntExtra("countFollows", 0);
            int countCmt= data.getIntExtra("countCmt", 0);
            boolean isFollow = data.getBooleanExtra("isFollow", false);

            HomeFragment homeFragment=(HomeFragment)fragmentHomeAdapter.getItem(0);
            Post post= homeFragment.getLstPost().get(position);
            if (!post.getItemName().equals(itemName)) post.setItemName(itemName);
            if (!post.getItemType().equals(itemType)) post.setItemType(itemType);
            if (!(post.getPrice()==itemPrice)) post.setPrice(itemPrice);
            if (!(post.isStatus()==(itemStatus))) post.setStatus(itemStatus);
            post.setDetail(itemDetail);
            if (!post.getImgItemResource().equals(itemImage)) post.setImgItemResource(itemImage);
            post.setCountFollows(countFollows);
            post.setCountCmt(countCmt);
            post.setFollowed(isFollow);

            homeFragment.getPostAdapter().notifyDataSetChanged();
            viewPager.setCurrentItem(0);
        }

        if (requestCode == FollowPostCardAdapter.UPDATE_FOLLOW_POST && resultCode == RESULT_OK && data != null) {
            onResultBack(1, data);
        }

        if (requestCode == PostCreatorAdapter.UPDATE_FOLLOW_MY_POST && resultCode == RESULT_OK && data != null) {
            onResultBack(3, data);
        }

        if (requestCode == NotificationAdapter.UPDATE_POST_NOTIFICATION && resultCode == RESULT_OK && data != null) {
            int position= data.getIntExtra("position", -1);
            ArrayList<Notification> lstNotifications= ((NotificationFragment)fragmentHomeAdapter.getItem(2)).getLst_Notification();
            NotificationAdapter notificationAdapter= ((NotificationFragment)fragmentHomeAdapter.getItem(2)).getNotificationAdapter();
            lstNotifications.get(position).setReaded(true);
            notificationAdapter.notifyDataSetChanged();

            onResultBack(2, data);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        lstNotifications= new ArrayList<>();
        listNotificationID= new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        db.collection("Notification").orderBy("time", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!=null) {
                    return;
                }
                newNotifications=0;
                for (DocumentChange d: value.getDocumentChanges()) {
                    if (d.getDocument().getString("toUser").equals(myID) && !listNotificationID.contains(d.getDocument().getId())) {
                        Notification notification= d.getDocument().toObject(Notification.class);
                        if(notification.isNew()) {
                            newNotifications++;
                        }
                        lstNotifications.add(notification);
                        listNotificationID.add(d.getDocument().getId());
                    }
                }
                if (newNotifications == 0) {
                    txt_newNotifications.setVisibility(View.INVISIBLE);
                } else {
                    txt_newNotifications.setVisibility(View.VISIBLE);
                    txt_newNotifications.setText(String.valueOf(newNotifications));
                }
                ((NotificationFragment)fragmentHomeAdapter.getItem(2)).setLst_Notification(lstNotifications);
//                fragmentHomeAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onResultBack(int itemViewPaper, Intent data) {
        String postID= data.getStringExtra("postID");
        String itemName= data.getStringExtra("itemName");
        String itemType= data.getStringExtra("itemType");
        int itemPrice= data.getIntExtra("itemPrice", 0);
        boolean itemStatus= data.getBooleanExtra("itemStatus", true);
        String itemDetail= data.getStringExtra("itemDetail");
        String itemImage= data.getStringExtra("itemImage");
        int countFollows= data.getIntExtra("countFollows", 0);
        int countCmt= data.getIntExtra("countCmt", 0);
        boolean isFollow = data.getBooleanExtra("isFollow", false);

        HomeFragment homeFragment=(HomeFragment)fragmentHomeAdapter.getItem(0);
        ArrayList<Post> lstPosts= homeFragment.getLstPost();
        Post post = null;
        for ( int i=0; i<lstPosts.size(); i++) {
            if(lstPosts.get(i).getPostID().equals(postID)) {
                post= lstPosts.get(i);
                break;
            }
        }
        if (post != null) {
            if (!post.getItemName().equals(itemName)) post.setItemName(itemName);
            if (!post.getItemType().equals(itemType)) post.setItemType(itemType);
            if (!(post.getPrice() == itemPrice)) post.setPrice(itemPrice);
            if (!(post.isStatus() == (itemStatus))) post.setStatus(itemStatus);
            post.setDetail(itemDetail);
            if (!post.getImgItemResource().equals(itemImage))
                post.setImgItemResource(itemImage);
            post.setCountFollows(countFollows);
            post.setCountCmt(countCmt);
            post.setFollowed(isFollow);

            homeFragment.getPostAdapter().notifyDataSetChanged();
            viewPager.setCurrentItem(itemViewPaper);
        }
    }


}