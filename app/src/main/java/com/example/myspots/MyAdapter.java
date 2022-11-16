package com.example.myspots;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class MyAdapter extends ArrayAdapter<Landmarks> {
    public MyAdapter(@NonNull Context context, ArrayList<Landmarks> landmarksArrayList) {
        super(context, 0, landmarksArrayList);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    public View getView (int position, View convertView, ViewGroup parent){
        View myView = convertView;
        if (myView == null)
        {
            myView = LayoutInflater.from(getContext()).inflate(R.layout.landmark_list_item, parent, false);
        }
        Landmarks myLandmark= getItem(position);
        TextView txtNameView = myView.findViewById(R.id.txtLandmarkName);
        TextView txtTypeView = myView.findViewById(R.id.txtLandmarkType);
        txtNameView.setText(myLandmark.getLandMarkName());
        txtTypeView.setText(myLandmark.getLandmarkType());

        return myView;
    }
}
