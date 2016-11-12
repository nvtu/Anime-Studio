package com.example.tuvanninh.anime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import info.hoang8f.widget.FButton;

/**
 * Created by Tu Van Ninh on 11/11/2016.
 */
public class EpisodeAdapter extends ArrayAdapter<String> {

    Context context;
    int resource;
    ArrayList<String> listEps;

    public EpisodeAdapter(Context context, int resource, ArrayList<String> listEps) {
        super(context, resource, listEps);
        this.context = context;
        this.resource = resource;
        this.listEps = listEps;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }
        TextView vEps = (TextView) convertView.findViewById(R.id.griditem);
        vEps.setText(String.valueOf(position+1));
        if (position == 0){
            vEps.setBackgroundColor(R.color.colorYellow);
        }
        return convertView;
    }
}
