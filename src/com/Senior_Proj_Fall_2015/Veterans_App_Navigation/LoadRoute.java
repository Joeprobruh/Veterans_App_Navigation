package com.Senior_Proj_Fall_2015.Veterans_App_Navigation;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class LoadRoute extends Activity implements View.OnClickListener {

    private static String fileToLoad;
    private File[] listOfFiles = MenuPage.listOfFiles();
    private Button[] buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_dialog);

        buttonList = new Button[] {(Button) findViewById(R.id.button1), (Button) findViewById(R.id.button2),
            (Button) findViewById(R.id.button3), (Button) findViewById(R.id.button4),
            (Button) findViewById(R.id.button5), (Button) findViewById(R.id.button6),
            (Button) findViewById(R.id.button7), (Button) findViewById(R.id.button8),
            (Button) findViewById(R.id.button9), (Button) findViewById(R.id.button10)};

        for (int i = 0; i < buttonList.length; i++) {
            buttonList[i].setOnClickListener(this);
        }
        for (int i = 0; i < buttonList.length; i++) {
            while (i < listOfFiles.length) {
                buttonList[i].setText(listOfFiles[i].getName().replace(".txt", ""));
                i++;
            }
            buttonList[i].setEnabled(false);
            buttonList[i].setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < listOfFiles.length; i++) {
            if (((Button) view).getText().toString().concat(".txt").toLowerCase().equals(listOfFiles[i].getName().toLowerCase())) {
                fileToLoad = listOfFiles[i].getName().toLowerCase();
                int j = 0;
                break;
            }
        }
        Intent i = new Intent(
            LoadRoute.this, MapPane.class);
        startActivity(i);
        finish();
    }

    public static String getFileToLoad () {
        return fileToLoad;
    }
}
