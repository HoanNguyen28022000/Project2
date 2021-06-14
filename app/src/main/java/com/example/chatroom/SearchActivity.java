package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.Adapter.FollowPostCardAdapter;
import com.example.Model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ImageView btn_close;
    EditText etxt_search;
    ListView suggestions;
    ListView list_post_result;

    ArrayList<String> itemTypes;
    ArrayList<Post> result;
    ArrayList<String> itemTypesSuggestions;

    ArrayAdapter<String> itemTypesSearchAdapter;
    FollowPostCardAdapter listPostResultAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        btn_close= findViewById(R.id.btn_close);
        etxt_search= findViewById(R.id.etxt_search);
        suggestions= findViewById(R.id.suggestions);
        list_post_result= findViewById(R.id.list_post_result);

        itemTypes= new ArrayList<>();
        itemTypesSuggestions= new ArrayList<>();
        result= new ArrayList<>();

        itemTypesSearchAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemTypesSuggestions);
        suggestions.setAdapter(itemTypesSearchAdapter);

        listPostResultAdapter = new FollowPostCardAdapter(this, result);
        list_post_result.setAdapter(listPostResultAdapter);

        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        etxt_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    itemTypesSuggestions.clear();
                    itemTypesSearchAdapter.notifyDataSetChanged();
                }
            }
        });

        etxt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.equals("")) {
                    itemTypesSuggestions.clear();
                    String str= s.toString().trim().toLowerCase();
                    for (String type: itemTypes) {
                        if (type.contains(str)) itemTypesSuggestions.add(type);
                    }
                    itemTypesSearchAdapter.notifyDataSetChanged();
                }
                else {
                    itemTypesSuggestions.clear();
                    for (String type: itemTypes) {
                        itemTypesSuggestions.add(type);
                    }
                    itemTypesSearchAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

        });

        suggestions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FirebaseFirestore.getInstance().collection("Post").whereEqualTo("itemType", itemTypesSuggestions.get(position)).get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    etxt_search.setText("");
                                    itemTypesSuggestions.clear();
                                    result.clear();
                                    for (DocumentSnapshot d : task.getResult().getDocuments()) {
                                        result.add(d.toObject(Post.class));
                                    }
                                    itemTypesSearchAdapter.notifyDataSetChanged();
                                    listPostResultAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
        });

        FirebaseFirestore.getInstance().collection("ItemType").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) return;
                for (DocumentChange d: value.getDocumentChanges()) {
                    itemTypes.add(d.getDocument().getString("nameType"));
                    System.out.println("itemTypeChange :"+ d.getDocument().getString("nameType").toLowerCase());
                }
            }
        });
    }
}