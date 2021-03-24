package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.chatroom.R;

import java.util.List;
import java.util.zip.Inflater;

import Model.Message;



public class RecentChatAdapter extends BaseAdapter {
    private Context context;
    private List<Message> lst_lastMess;

    public RecentChatAdapter(Context context, List<Message> lst_lastMess) {
        this.context = context;
        this.lst_lastMess = lst_lastMess;
    }


    @Override
    public int getCount() {
        return lst_lastMess.size();
    }

    @Override
    public Object getItem(int position) {
        return lst_lastMess.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_recent_chat, parent, false);
        }

        ImageView img_avt= convertView.findViewById(R.id.img_avt);
        TextView txt_username= convertView.findViewById(R.id.txt_username);
        TextView txt_timeSent= convertView.findViewById(R.id.txt_timeSent);
        TextView txt_recent_message= convertView.findViewById(R.id.txt_recent_message);

        Message last_mess= lst_lastMess.get(position);

        img_avt.setImageResource(last_mess.getUser().getAvtResource());
        txt_username.setText(last_mess.getUser().getUserName());
        txt_recent_message.setText(last_mess.getMessage());
        txt_timeSent.setText(last_mess.getTimeSent());

        return convertView;
    }
}
