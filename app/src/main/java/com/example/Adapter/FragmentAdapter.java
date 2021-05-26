package com.example.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.Fragment.HomeFragment;
import com.example.Fragment.MenuFragment;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentPagerAdapter {

    Context context;
    ArrayList<Fragment> lst_Fragments;

    public FragmentAdapter(Context context, FragmentManager fragmentManager, ArrayList<Fragment> lst_Fragments) {
        super(fragmentManager);
        this.context= context;
        this.lst_Fragments= lst_Fragments;
    }


    public ArrayList<Fragment> getLst_Fragments() {
        return lst_Fragments;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return lst_Fragments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return lst_Fragments.size();
    }

}
