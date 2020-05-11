package com.example.mc_assignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;


public class MainActivity extends AppCompatActivity {
    private String vurl;
    private String vname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //HashMap for links of all the videos
        final HashMap<String, String> map = new HashMap<>(20);
        map.put("buy", "https://www.signingsavvy.com/media/mp4-ld/6/6442.mp4");
        map.put("house", "https://www.signingsavvy.com/media/mp4-ld/23/23234.mp4");
        map.put("fun", "https://www.signingsavvy.com/media/mp4-ld/22/22976.mp4");
        map.put("hope", "https://www.signingsavvy.com/media/mp4-ld/22/22197.mp4");
        map.put("arrive", "https://www.signingsavvy.com/media/mp4-ld/14/14210.mp4");
        map.put("really", "https://www.signingsavvy.com/media/mp4-ld/24/24977.mp4");
        map.put("read", "https://www.signingsavvy.com/media/mp4-ld/7/7042.mp4");
        map.put("lip", "https://www.signingsavvy.com/media/mp4-ld/26/26085.mp4");
        map.put("mouth", "https://www.signingsavvy.com/media/mp4-ld/22/22188.mp4");
        map.put("some", "https://www.signingsavvy.com/media/mp4-ld/23/23931.mp4");
        map.put("communicate", "https://www.signingsavvy.com/media/mp4-ld/22/22897.mp4");
        map.put("write", "https://www.signingsavvy.com/media/mp4-ld/8/8441.mp4");
        map.put("create", "https://www.signingsavvy.com/media/mp4-ld/22/22337.mp4");
        map.put("pretend", "https://www.signingsavvy.com/media/mp4-ld/25/25901.mp4");
        map.put("sister", "https://www.signingsavvy.com/media/mp4-ld/21/21587.mp4");
        map.put("man", "https://www.signingsavvy.com/media/mp4-ld/21/21568.mp4");
        map.put("one", "https://www.signingsavvy.com/media/mp4-ld/11/11001.mp4");
        map.put("drive", "https://www.signingsavvy.com/media/mp4-ld/23/23918.mp4");
        map.put("perfect", "https://www.signingsavvy.com/media/mp4-ld/24/24791.mp4");
        map.put("mother", "https://www.signingsavvy.com/media/mp4-ld/21/21571.mp4");

        //Dropdowwn list of signs
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adpt = ArrayAdapter.createFromResource(this,
                R.array.signs, android.R.layout.simple_spinner_item);
        adpt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adpt);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString().toLowerCase();
                vname = text;
                vurl = map.get(text);
                //Toast.makeText(adapterView.getContext(), vidurl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        //Button to download the video
        Button b1 = (Button) findViewById(R.id.buttonb1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadTask dw1 = new DownloadTask();
                Toast.makeText(getApplicationContext(), "Running Background Task", Toast.LENGTH_LONG).show();
                dw1.execute();
                //Toast.makeText(getApplicationContext(), vurl, Toast.LENGTH_LONG).show();
            }
        });
    }

    public class DownloadTask extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute(){
            Toast.makeText(getApplicationContext(), "Starting to Execute Background Task", Toast.LENGTH_SHORT).show();
        }

        //Video download takes place here
        @Override
        protected String doInBackground(String... text) {
            File SDCardRoot = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File directory = new File(SDCardRoot, "/my_folder/"); //create directory to keep your downloaded file
            if (!directory.exists())
            {
                directory.mkdir();
            }
            String fileName = vname + ".mp4";
            try
            {
                InputStream input = null;
                try{
                    URL url = new URL(vurl);
                    HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setReadTimeout(95 * 1000);
                    urlConnection.setConnectTimeout(95 * 1000);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setRequestProperty("X-Environment", "android");
                    urlConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            /** if it necessarry get url verfication */
                            //return HttpsURLConnection.getDefaultHostnameVerifier().verify("your_domain.com", session);
                            return true;
                        }
                    });
                    urlConnection.setSSLSocketFactory((SSLSocketFactory) SSLSocketFactory.getDefault());
                    urlConnection.connect();
                    input = urlConnection.getInputStream();
                    OutputStream output = new FileOutputStream(new File(directory, fileName));
                    try {
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0)
                        {
                            output.write(buffer, 0, bytesRead);

                        }
                        output.close();
                    }
                    catch (Exception exception)
                    {
                        Log.d("Error1", String.valueOf(exception));
                        publishProgress(String.valueOf(exception));
                        output.close();
                    }
                }
                catch (Exception exception)
                {
                    Log.d("Error2", String.valueOf(exception));
                    publishProgress(String.valueOf(exception));
                }
                finally
                {
                    input.close();
                }
            }
            catch (Exception exception)
            {
                Log.d("Error3", String.valueOf(exception));
                publishProgress(String.valueOf(exception));
            }
            return "true";
        }

        @Override
        protected void onProgressUpdate(String... text) {
            Toast.makeText(getApplicationContext(), "In Background Task" + text[0], Toast.LENGTH_LONG).show();
        }

        //After the video has been downloaded, the user will be taken back to the first screen
        @Override
        protected void onPostExecute(String text){
            Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT);
            Intent intent1 = new Intent(MainActivity.this, Main2Activity.class);
            intent1.putExtra("videoname",vname);
            startActivity(intent1);
        }
    }

}

