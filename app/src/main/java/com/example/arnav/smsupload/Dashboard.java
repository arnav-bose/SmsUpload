package com.example.arnav.smsupload;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends AppCompatActivity {

    private HashMap<String, String> hashMapSms;
    private ArrayList<String> arrayListSms;
    private ArrayAdapter<String> arrayAdapterSms;
    private ListView listViewSms;
    private Toolbar toolbarDashboard;
    private Button buttonUploadSms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        listViewSms = (ListView)findViewById(R.id.listViewSms);
        toolbarDashboard = (Toolbar)findViewById(R.id.toolbarDashboard);
        buttonUploadSms = (Button)findViewById(R.id.buttonUploadSms);

        setSupportActionBar(toolbarDashboard);

        //hashMapSms = getAllSmsHashmap();

        arrayListSms = getAllSmsArrayList();

        arrayAdapterSms = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListSms);
        listViewSms.setAdapter(arrayAdapterSms);

        buttonUploadSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AsyncTaskUploadSms asyncTaskUploadSms = new AsyncTaskUploadSms(Dashboard.this);
                asyncTaskUploadSms.execute();
            }
        });

    }

    public HashMap<String, String> getAllSmsHashmap(){
        HashMap<String, String> hashMapSms = new HashMap<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            hashMapSms.put("Sender: " + address, "\n\nMessage:\n" + body + "\n");

        }
        return hashMapSms;
    }

    public ArrayList<String> getAllSmsArrayList(){
        ArrayList<String> arrayListSms = new ArrayList<>();
        StringBuilder stringBuilderSms = new StringBuilder();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndex("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            String entry = "Sender: " + address + "\nMessage:\n" + body + "\n\n";
            stringBuilderSms.append(entry);
            arrayListSms.add(entry);
        }

        writeToFile(stringBuilderSms.toString());

        return arrayListSms;
    }

    private void writeToFile(String data) {
        Storage storage = null;
        if (SimpleStorage.isExternalStorageWritable()) {
            storage = SimpleStorage.getExternalStorage();
        }
        else {
            storage = SimpleStorage.getInternalStorage(Dashboard.this);
        }

        storage.createDirectory("sms_upload");
        storage.createFile("sms_upload", "sms.txt", data);
        Toast.makeText(Dashboard.this, "File Written!", Toast.LENGTH_SHORT).show();
    }
}
