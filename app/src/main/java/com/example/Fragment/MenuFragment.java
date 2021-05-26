package com.example.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.Adapter.OptionMenuAdapter;
import com.example.Model.OptionMenu;
import com.example.chatroom.Home;
import com.example.chatroom.R;
import com.example.chatroom.SignIn;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MenuFragment extends Fragment {

    ArrayList<OptionMenu> lstOptions;
    OptionMenuAdapter optionMenuAdapter;

    public MenuFragment() {

        lstOptions= new ArrayList<>();
        lstOptions.add(new OptionMenu(R.drawable.ic_round_perm_identity_24, "Trang cá nhân"));
        lstOptions.add(new OptionMenu(R.drawable.ic_list, "Đồ của tôi"));
        lstOptions.add(new OptionMenu(R.drawable.ic_baseline_settings_24, "Cài đặt"));
        lstOptions.add(new OptionMenu(R.drawable.ic_baseline_logout_24, "Đăng xuất"));
    }

    public ArrayList<OptionMenu> getLstOptions() {
        return lstOptions;
    }

    public void setLstOptions(ArrayList<OptionMenu> lstOptions) {
        this.lstOptions = lstOptions;
    }

    public OptionMenuAdapter getOptionMenuAdapter() {
        return optionMenuAdapter;
    }

    public void setOptionMenuAdapter(OptionMenuAdapter optionMenuAdapter) {
        this.optionMenuAdapter = optionMenuAdapter;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout= (LinearLayout) inflater.inflate(R.layout.layout_menu, container, false);

        ListView menu= linearLayout.findViewById(R.id.lst_option);
        optionMenuAdapter= new OptionMenuAdapter(getActivity(), lstOptions);
        menu.setAdapter(optionMenuAdapter);

        return linearLayout;
    }



}
