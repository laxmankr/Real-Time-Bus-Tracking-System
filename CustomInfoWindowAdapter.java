package com.example.laxman.bussuvidha;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private  final View mWindow;
    private Context mContext;

    public CustomInfoWindowAdapter(Context mContext) {
        this.mWindow = LayoutInflater.from(mContext).inflate(R.layout.custom_info_window,null);
        this.mContext = mContext;
    }
    private void rendowWindowText(Marker marker, View view){
        String title = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        if(!title.equals("")){
            tvTitle.setText(title);
        }
        else{
            return;
        }
        String snippet = marker.getSnippet();
         TextView tvSnippet = (TextView) view.findViewById(R.id.snippet);
        if(!snippet.equals("")){
            tvSnippet.setText(snippet);
        }
        else{
            return;
        }
    }

    @Override

    public View getInfoWindow(Marker marker) {
        rendowWindowText(marker,mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        rendowWindowText(marker,mWindow);
        return mWindow;
    }
}
