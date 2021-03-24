package com.example.chatroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import Adapter.RecentChatAdapter;
import Adapter.UserOnlineAdapter;

import Model.Message;
import Model.User;

public class List_chat_friend extends AppCompatActivity {

    List<User> lstFriend;
    List<Message> lstRecentChat;

    RecyclerView.LayoutManager recyclerViewLayoutManager;
    RecyclerView recyclerView;
    LinearLayoutManager HorizontalLayout;

    ListView lst_RecentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_chat_friend);

        lstFriend= new ArrayList<>();
        lstRecentChat = new ArrayList<>();

        lstFriend.add(new User(R.drawable.avata, "Lê Xuân Phúc"));
        lstFriend.add(new User(R.drawable.avata, "Lê Hải Anh"));
        lstFriend.add(new User(R.drawable.avata, "Lê Nhật Nam"));
        lstFriend.add(new User(R.drawable.avata, "Bùi Thùy Dung"));
        lstFriend.add(new User(R.drawable.avata, "Nguyễn Huy Hoàn"));
        lstFriend.add(new User(R.drawable.avata, "Trần Đức Long"));
        lstFriend.add(new User(R.drawable.avata, "Nguyễn Anh Đức"));
        lstFriend.add(new User(R.drawable.avata, "Phạm Ngũ Lão"));
        lstFriend.add(new User(R.drawable.avata, "Nguyễn Trần Trung Quân"));


        lstRecentChat.add(new Message(lstFriend.get(0), "12:17 11/3/2021", "Hello world"));
        lstRecentChat.add(new Message(lstFriend.get(1), "12:18 11/3/2021", "hi, my name is Phúc"));
        lstRecentChat.add(new Message(lstFriend.get(2), "12:19 11/3/2021", "How are you"));
        lstRecentChat.add(new Message(lstFriend.get(3), "12:20 11/3/2021", "I'm fine thanks you!"));
        lstRecentChat.add(new Message(lstFriend.get(4), "12:20 11/3/2021", "I'm fine thanks you!"));
        lstRecentChat.add(new Message(lstFriend.get(5), "12:20 11/3/2021", "I'm fine thanks you!"));
        lstRecentChat.add(new Message(lstFriend.get(6), "12:20 11/3/2021", "I'm fine thanks you!"));
        lstRecentChat.add(new Message(lstFriend.get(7), "12:20 11/3/2021", "I'm fine thanks you!"));

        ////////////
        recyclerView= findViewById(R.id.lstFriendChat);
        recyclerViewLayoutManager= new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        HorizontalLayout = new LinearLayoutManager(List_chat_friend.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(HorizontalLayout);

        UserOnlineAdapter userOnlineAdapter= new UserOnlineAdapter(lstFriend);
        recyclerView.setAdapter(userOnlineAdapter);

        ////////////
        lst_RecentChat= findViewById(R.id.lst_RecentChat);
        RecentChatAdapter recentChatAdapter= new RecentChatAdapter(this, lstRecentChat);
        lst_RecentChat.setAdapter(recentChatAdapter);
    }
}