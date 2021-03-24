package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom.R;

import java.util.List;

import Model.User;

public class UserOnlineAdapter extends RecyclerView.Adapter<UserOnlineAdapter.MyView> {
    List<User> lstFriend;

    public UserOnlineAdapter(List<User> lstFriend) {
        this.lstFriend = lstFriend;
    }

    public class MyView extends RecyclerView.ViewHolder {
        private ImageView avtResource;
        private TextView username;

        public MyView(View view) {
            super(view);
            avtResource = (ImageView)view.findViewById(R.id.img_avt);
            username =(TextView)view.findViewById(R.id.txt_user_adapter);

        }

    }

    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userAdapterView= LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_online, parent, false);
        return new MyView(userAdapterView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        holder.avtResource.setImageResource(lstFriend.get(position).getAvtResource());
        holder.username.setText(lstFriend.get(position).getUserName());
    }

    @Override
    public int getItemCount() {
        return lstFriend.size();
    }
}
