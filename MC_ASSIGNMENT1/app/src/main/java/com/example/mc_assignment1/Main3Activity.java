package com.example.mc_assignment1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.toolbox.HttpResponse;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.HashMap;

public class Main3Activity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int VIDEO_CAPTURE = 101;
    private Uri fileUri;
    private String name;
    private String finalname;
    private String pno;
    private String vidname;
    private String asu_id;
    private String acc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        //New Intent to get the gesture name from the previous intents
        Intent i2 = getIntent();
        final String vname = i2.getStringExtra("videoname").toUpperCase();
        vidname = vname;

        //HashMap for the List of Students
        final HashMap<String, String> map2 = new HashMap<>(20);
        map2.put("A", "A");
        map2.put("B", "B");
        map2.put("C", "C");
        map2.put("D", "D");

        //HashMap for match the Students to their IDs
        final HashMap<String, String> map3 = new HashMap<>(20);
        map3.put("A", "1");
        map3.put("B", "2");
        map3.put("C", "3");
        map3.put("D", "4");

        //Dropdown list of students
        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        ArrayAdapter<CharSequence> adpt2 = ArrayAdapter.createFromResource(this,
                R.array.names, android.R.layout.simple_spinner_item);
        adpt2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adpt2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String text = adapterView.getItemAtPosition(i).toString();
                name = text;
                finalname = map2.get(text);
                asu_id = map3.get(text);
                //Toast.makeText(adapterView.getContext(), vidurl, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //If Camera Permission isn't already granted, then it is requested
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        //Button to Start Recording Video
        Button b4 = (Button) findViewById(R.id.buttonb4);
        //Checks if the phone has a camera
        if(!hasCamera()){
            Toast.makeText(getApplicationContext(), "No Camera", Toast.LENGTH_LONG);
            b4.setEnabled(false);
        }
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });

        //Button to Upload Video
        Button b5 = (Button) findViewById(R.id.buttonb5);
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadTask up1 = new UploadTask();
                //Toast.makeText(getApplicationContext(), "Starting to upload", Toast.LENGTH_SHORT).show();
                up1.execute();
            }
        });
    }

    //Method to Connect to the camera and start recording a video
    public void startRecording()
    {
        EditText practiceno = (EditText) findViewById(R.id.editText);
        pno = practiceno.getText().toString();
        EditText accno = (EditText) findViewById(R.id.editText2);
        acc = accno.getText().toString();
        File SDCardRoot = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        File directory = new File(SDCardRoot, "/my_output_folder/"); //create directory to keep your downloaded file
        if (!directory.exists())
        {
            directory.mkdir();
        }
        ///Name of the Video in the format: GESTURE_PRACTICE_(Practice Number)_USERLASTNAME.mp4
        File mediaFile = new File(directory.getAbsolutePath() + "/" + vidname + "_PRACTICE_" + pno +"_" + finalname + ".mp4");
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,5);
        fileUri = FileProvider.getUriForFile(getApplicationContext() , "com.package.name.fileprovider", mediaFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, VIDEO_CAPTURE);
    }

    private boolean hasCamera() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
            return true;
        }
        else {
            return false;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIDEO_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Video has been saved to:\n" +
                        data.getData(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                        Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Failed to record video",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    public class UploadTask extends AsyncTask<String, String, String>{

        //Video Upload takes place here
        @Override
        protected String doInBackground(String... strings) {

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            File SDCardRoot = getExternalFilesDir(Environment.DIRECTORY_MOVIES);
            File directory = new File(SDCardRoot, "/my_output_folder/"); //create directory to find the recorded video
            //final String fileName = "buy" + ".mp4";
            final String fileName = vidname + "_PRACTICE_" + pno +"_" + finalname + ".mp4";
            final String group_id = "21";
            final String id = asu_id;
            final String accept = acc;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1024 * 1024;
            try {
                FileInputStream input = new FileInputStream(new File(directory, fileName));
                try {
                    URL url = new URL("http://192.168.0.8:8080/api/upload_files/index.php"); //URL to php upload file goes here
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoInput(true); // Allow Inputs
                    urlConnection.setDoOutput(true); // Allow Outputs
                    urlConnection.setUseCaches(false); // Don't use a Cached Copy
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Connection", "Keep-Alive");
                    urlConnection.setRequestProperty("ENCTYPE", "multipart/form-data");
                    urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    urlConnection.setRequestProperty("uploaded_file", fileName);
                    urlConnection.setRequestProperty("group_id", "21");
                    urlConnection.setRequestProperty("id",id);
                    urlConnection.setRequestProperty("accept",accept);

                    DataOutputStream dos = new DataOutputStream(urlConnection.getOutputStream());

                    //group_id
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"group_id\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(group_id);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    //id
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(id);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);


                    //accept
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"accept\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(accept);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    //video
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + fileName +  lineEnd);
                    dos.writeBytes(lineEnd);

                    bytesAvailable = input.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    bytesRead = input.read(buffer, 0, bufferSize);
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = input.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = input.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    int serverResponseCode = urlConnection.getResponseCode();
                    String serverResponseMessage = urlConnection.getResponseMessage();
                    Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + " : " + serverResponseCode);
                    if (serverResponseCode == 200) {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("uploadFile2","File upload complete");
                                String msg = "File Upload Completed.\n\n See uploaded file here : \n\n" + " http://www.androidexample.com/media/uploads/" + fileName;
                                Toast.makeText(getApplicationContext(), "File Upload Complete", Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    input.close();
                    dos.flush();
                    dos.close();

                }
                catch (Exception exception)
                {
                    Log.d("Error1", String.valueOf(exception));
                    publishProgress(String.valueOf(exception));
                }
            }
            catch (Exception exception)
            {
                Log.d("Error2", String.valueOf(exception));
                publishProgress(String.valueOf(exception));
            }
            return "null";
        }

        @Override
        protected void onProgressUpdate(String... text){
            Toast.makeText(getApplicationContext(), "In Background Task" + text[0], Toast.LENGTH_SHORT).show();
        }

        //After the video has been uploaded, the user will be taken back to the first screen
        @Override
        protected void onPostExecute(String text){
            //Toast.makeText(getApplicationContext(), "done", Toast.LENGTH_SHORT).show();
            Intent intent3 = new Intent(Main3Activity.this, MainActivity.class);
            startActivity(intent3);
        }
    }
}
