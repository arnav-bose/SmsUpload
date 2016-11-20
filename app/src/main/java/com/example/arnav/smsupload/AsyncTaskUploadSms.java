package com.example.arnav.smsupload;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.android.internal.http.multipart.MultipartEntity;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by Arnav on 20/11/2016.
 */
public class AsyncTaskUploadSms extends AsyncTask<Void, Void, Void> {

    private Context contextUploadSms;
    private Activity activityUploadSms;
    private ProgressDialog progressDialogUploadSms;

    public AsyncTaskUploadSms(Context context) {
        this.contextUploadSms = context;
        this.activityUploadSms = (Activity) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialogUploadSms = new ProgressDialog(activityUploadSms);
        progressDialogUploadSms.setTitle("SMSUpload");
        progressDialogUploadSms.setMessage("Uploading to Server...");
        progressDialogUploadSms.setIndeterminate(false);
        progressDialogUploadSms.show();
    }

    @Override
    protected Void doInBackground(Void... voids) {


        //URL url = new URL("http://192.168.137.1/upload_sms/upload_all_sms.php");
        String url = "http://192.168.137.1/upload_sms/upload_all_sms.php";

        Storage storage = null;
        if (SimpleStorage.isExternalStorageWritable()) {
            storage = SimpleStorage.getExternalStorage();
        }
        else {
            storage = SimpleStorage.getInternalStorage(contextUploadSms);
        }

        File file = storage.getFile("/sms_upload/sms.txt");
        String absolutePath = file.getAbsolutePath();
        long size = file.getTotalSpace();
        try {
            String uploadId =
                    new MultipartUploadRequest(contextUploadSms, "http://192.168.137.1/upload_sms/upload_all_sms.php")
                            .addFileToUpload(absolutePath, "sms")
                            .setNotificationConfig(new UploadNotificationConfig())
                            .setMaxRetries(2)
                            .startUpload();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        progressDialogUploadSms.dismiss();
        Toast.makeText(contextUploadSms, "File Uploaded", Toast.LENGTH_SHORT).show();
    }
}
