package com.example.laxman.bussuvidha;

import android.widget.ArrayAdapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Belal on 2/26/2017.
 */

public class BusList extends ArrayAdapter<UserInformation> {
    private Activity context;
    List<UserInformation> users;

    public BusList(Activity context, List<UserInformation> users) {
        super(context, R.layout.buslist, users);
        this.context = context;
        this.users = users;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.buslist, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewAddress = (TextView) listViewItem.findViewById(R.id.textViewAddress);
        TextView textViewPhone = (TextView) listViewItem.findViewById(R.id.textViewPhone);
        Button btnBusInfo = (Button) listViewItem.findViewById(R.id.btnBusInfo);
        Button btnBusLocation = (Button) listViewItem.findViewById(R.id.btnBusLocation);

        UserInformation user = users.get(position);
        textViewName.setText(user.getUserName());
        textViewAddress.setText(user.getUserAddress());
        textViewPhone.setText(user.getUserPhone());
        return listViewItem;
    }
}