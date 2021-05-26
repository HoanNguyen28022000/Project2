package com.example.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.Adapter.NotificationAdapter;
import com.example.Model.Notification;
import com.example.chatroom.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    boolean loaded ;

    ArrayList<Notification> lst_Notification;
    NotificationAdapter notificationAdapter;
    FirebaseFirestore db;

    public NotificationFragment() {
        super();
        this.loaded= false;
    }


    public ArrayList<Notification> getLst_Notification() {
        return lst_Notification;
    }

    public void setLst_Notification(ArrayList<Notification> lst_Notification) {
        this.lst_Notification = lst_Notification;
        if (loaded) {
            notificationAdapter.notifyDataSetChanged();
        }
    }

    public NotificationAdapter getNotificationAdapter() {
        return notificationAdapter;
    }

    public void setNotificationAdapter(NotificationAdapter notificationAdapter) {
        this.notificationAdapter = notificationAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout relativeLayout= (RelativeLayout) inflater.inflate(R.layout.layout_notifications, container, false);

        ListView lvNotification= relativeLayout.findViewById(R.id.list_notifications);
        notificationAdapter= new NotificationAdapter(getActivity(), lst_Notification);
        lvNotification.setAdapter(notificationAdapter);
        loaded= true;

        String myID= FirebaseAuth.getInstance().getCurrentUser().getUid();
        db=FirebaseFirestore.getInstance();

        return relativeLayout;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        notificationAdapter.notifyDataSetChanged();
//    }


}
