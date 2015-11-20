package com.Senior_Proj_Fall_2015.Veterans_App_Navigation;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageFragment extends Fragment {

    @Override
    @SuppressWarnings("deprecation")
    public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.layout_image_view, parent, false);
        ImageView imageView = (ImageView) v.findViewById(R.id.location_image);
        imageView.setImageBitmap(MapPane.rotatedImage);
        return v;
    }

}
