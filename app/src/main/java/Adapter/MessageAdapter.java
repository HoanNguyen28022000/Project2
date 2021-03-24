package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.chatroom.R;

import java.util.List;

import Model.Message;

public class MessageAdapter extends BaseAdapter {
    List<Message> messAdapter;
    Context context;

    public MessageAdapter(List<Message> messAdapter, Context context) {
        this.messAdapter = messAdapter;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messAdapter.size();
    }

    @Override
    public Object getItem(int i) {
        return messAdapter.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (messAdapter.get(position).getUser().getUserName().equals("me"))
            return 1;
        else
            return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            if (getItemViewType(position) == 1) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_right, parent, false);
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_message_left, parent, false);
            }
        }

        ImageView img_avt = convertView.findViewById(R.id.img_avatar);
        TextView txt_timeSent = convertView.findViewById(R.id.txt_timeSent);
        TextView txt_message = convertView.findViewById(R.id.txt_message);

        Message message= (Message) getItem(position);

        img_avt.setImageResource(message.getUser().getAvtResource());
        txt_message.setText(message.getMessage());
        txt_timeSent.setText(message.getTimeSent());

        return convertView;
    }
}
