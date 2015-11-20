package com.Senior_Proj_Fall_2015.Veterans_App_Navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

public class MenuPage extends Activity implements View.OnClickListener {

    protected static File[] listOfFiles = listOfFiles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_menu_page);

        Button button_load_route =
            (Button) findViewById(R.id.button_load_route);
        button_load_route.setOnClickListener(this);

        Button button_create_route =
            (Button) findViewById(R.id.button_create_route);
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
                    if (listOfFiles.length != 0) {
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
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    public static File[] listOfFiles() {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_maps");
        File[] file = myDir.listFiles();
        return file;
    }


}
