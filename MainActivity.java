package com.example.ftp_test;

import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPListParseEngine;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new FTPUpload().execute(touch(Environment.getExternalStorageDirectory()+"/LOG","test.txt"),"LOG/test.txt");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //-------------------------------------
    // TEST CODE
    //-------------------------------------
    public void logcat(){
        // <uses-permission android:name="android.permission.READ_LOGS" />

        //       try {
        //           ArrayList<String> commandLine = new ArrayList<String>();
        //           // コマンドの作成
        //           commandLine.add( "logcat");
        //           commandLine.add( "-d");
        //           commandLine.add( "-v");
        //           commandLine.add( "time");
        //           commandLine.add( "-s");
        //           commandLine.add( "tag:W");
        //           Process process = Runtime.getRuntime().exec( commandLine.toArray( new String[commandLine.size()]));
        //           BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()), 1024);
        //           String line;
        //           while ((line = bufferedReader.readLine()) != null) {
        //               log.append(line);
        //               log.append("\n");
        //           }
        //       } catch ( IOException e) {
        //           // 例外処理
        //       }
    }

    public String touch(String path, String name){
        // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
        // <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
        try {
            File dir = new File(path);
            if (!dir.exists()) dir.mkdir();
            FileWriter writer = new FileWriter(new File(dir, name));
            writer.append("test:"+ LocalDateTime.now());
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return path + "/"+name;
    }

    private class FTPUpload extends AsyncTask <String, Void, Long>{

        String TAG = "FTPTask";
        @Override
        protected Long doInBackground(String... paths) {
            // https://mvnrepository.com/artifact/commons-net/commons-net
            // implementation group: 'commons-net', name: 'commons-net', version: '3.6'
            //     <uses-permission android:name="android.permission.INTERNET" />
            //     <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
            String localpath = paths[0];
            String rempotepath = paths[1];
            FTPClient client = new FTPClient();
            FileInputStream fis = null;
            try{
                String server = "172.22.2.60";
                int port = 2121;
                client.connect(server,port);
                Log.d(TAG, String.format("connect: %d",client.getReplyCode()));

                String username = "user";
                String password = "12345";
                client.login(username,password);
                Log.d(TAG, String.format("login: %d",client.getReplyCode()));

                // Create an InputStream of the file to be upload
                fis = new FileInputStream(localpath);
                // Store file to server
                client.storeFile(rempotepath,fis);
                Log.d(TAG, String.format("storeFile: %d",client.getReplyCode()));

                client.logout();
                Log.d(TAG, String.format("logout: %d",client.getReplyCode()));
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }finally {
                try {
                    if (fis != null) fis.close();
                    client.disconnect();
                    Log.d(TAG, String.format("disconnect: %d",client.getReplyCode()));
                } catch (Exception e){
                    Log.d(TAG, e.getMessage());
                }
            }
            return null;
        }
    }

}
