package com.example.e_gov;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

public class MsgAdapter extends ArrayAdapter<Message> {

    private int resourceID;
    public MsgAdapter(Context context, int textViewSourceID, List<Message> objects)
    {
        super(context, textViewSourceID, objects);
        resourceID = textViewSourceID;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Message msg = getItem(position);
        View view;
        ViewHolder viewHolder;

        if (convertView == null)
        {
            view = LayoutInflater.from(getContext()).inflate(resourceID, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = view.findViewById(R.id.right_msg);
            view.setTag(viewHolder);
        }
        else
        {
            view = convertView;
            viewHolder = (ViewHolder)view.getTag();
        }

        if (msg.getType() == Message.RECEIVED)
        {
            // if it is received msg, then show left layout hind right
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
        }
        else if(msg.getType() == Message.SENT)
        {
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.rightMsg.setText(msg.getContent());
        }
        return view;


    }
}
