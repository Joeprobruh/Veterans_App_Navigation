package com.Senior_Proj_Fall_2015.Veterans_App_Navigation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.animation.*;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class MenuPage extends Activity implements View.OnClickListener {

    protected static File[] listOfFiles = listOfFiles();
    private static final int IMAGE_ROTATION_TIME = 2000;
    private static final int DELAY_PROGRESS_BAR = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu_page);

        ImageView icon = (ImageView) findViewById(R.id.load_icon);
        icon.setImageResource(R.drawable.web_hi_res_512);
        ObjectAnimator animator = new ObjectAnimator().ofFloat(icon, "RotationY", 0.0f, 360.0f);
        animator.setDuration(IMAGE_ROTATION_TIME);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.start();

        final com.beardedhen.androidbootstrap.BootstrapProgressBar progressBar =
            (com.beardedhen.androidbootstrap.BootstrapProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        ObjectAnimator progressBarAnimator = new ObjectAnimator().ofInt(progressBar, "progress", 0, 100);
        progressBarAnimator.setInterpolator(new LinearInterpolator());
        progressBarAnimator.setDuration(DELAY_PROGRESS_BAR);
        progressBarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {}

            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator progressBarFade = new ObjectAnimator().ofFloat(progressBar, "alpha", 1, 0);
                progressBarFade.setInterpolator(new AccelerateInterpolator());
                progressBarFade.setStartDelay(250);
                progressBarFade.setDuration(700);
                progressBarFade.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        progressBarAnimator.start();

        com.beardedhen.androidbootstrap.BootstrapButton button_create_route =
            (com.beardedhen.androidbootstrap.BootstrapButton) findViewById(R.id.button_create_route);
        com.beardedhen.androidbootstrap.BootstrapButton button_load_route =
            (com.beardedhen.androidbootstrap.BootstrapButton) findViewById(R.id.button_load_route);

        ObjectAnimator createRoute = new ObjectAnimator().ofFloat(button_create_route, "alpha", 0, 1);
        createRoute.setDuration(700);
        createRoute.setStartDelay(3500);
        createRoute.setInterpolator(new DecelerateInterpolator());
        createRoute.start();

        ObjectAnimator loadRoute = new ObjectAnimator().ofFloat(button_load_route, "alpha", 0, 1);
        loadRoute.setDuration(700);
        loadRoute.setStartDelay(3900);
        loadRoute.setInterpolator(new DecelerateInterpolator());
        loadRoute.start();

        button_load_route.setOnClickListener(this);
        button_create_route.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_load_route:
                try {
                    listOfFiles = listOfFiles();
                    if (listOfFiles.length == 0) {
                        Toast.makeText(getApplicationContext(), R.string.no_maps_load_error,
                            Toast.LENGTH_LONG).show();
                    }
                    else if (listOfFiles.length != 0) {
                        Intent i = new Intent(
                            MenuPage.this, LoadRoute.class);
                        startActivity(i);
                    }
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), R.string.no_maps_load_error,
                        Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.button_create_route:
                Intent j = new Intent(
                    MenuPage.this, MapPane.class);
                startActivity(j);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public static File[] listOfFiles() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_maps");
        File[] file = myDir.listFiles();
        return file;
    }


}
