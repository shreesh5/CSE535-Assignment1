package com.example.mc_assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telecom.InCallService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        //New Intent to get the gesture name from the previous intents
        Intent i1 = getIntent();
        final String vname = i1.getStringExtra("videoname");

        File directory = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        final String path = directory + "/my_folder/" + vname + ".mp4";
        //Toast.makeText(getApplicationContext(), path, Toast.LENGTH_LONG).show();
        //Log.i("path", "path");

        //Button to play the video
        Button b2 = (Button) findViewById(R.id.buttonb2);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoView video = (VideoView) findViewById(R.id.videoView);
                video.setVideoPath(path);
                video.start();
            }
        });

        //Button to take user to the second screen
        Button b3 = (Button) findViewById(R.id.buttonb3);
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent2 = new Intent(Main2Activity.this,Main3Activity.class);
                intent2.putExtra("videoname",vname);//Name of the gesture is passed into the intent for naming the video
                startActivity(intent2);
            }
        });


    }
}
