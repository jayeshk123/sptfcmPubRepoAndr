package com.sptulsian;

import android.database.Cursor;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by earth on 9/9/16.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    DBHelper myDB;
    String token;
    String deviceId;
    final String HomeURL = "https://dev.sptulsian.com/";

    /*MyFirebaseInstanceIDService(){
        myDB = new DBHelper(this);
        String currentToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Refreshed token: " + currentToken);
        myDB.updateDBToken(1,currentToken,"");
    }*/
    @Override
    public void onTokenRefresh() {
        //Get updated InstanceID token.
        myDB = new DBHelper(getApplicationContext());
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println("Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken,android_id);

    }

    private void sendRegistrationToServer(String refreshedToken, String android_id) {
        Cursor c = myDB.getDBToken(1);
        c.moveToFirst();
        token = c.getString(c.getColumnIndex(DBHelper.User_COLUMN_token));

        Cursor c3 = myDB.getDBDeviceId(1);
        c3.moveToFirst();
        deviceId = c3.getString(c3.getColumnIndex(DBHelper.User_COLUMN_deviceId));

        c.close();
        c3.close();

        if(deviceId == null){
            deviceId = android_id;
        }else if(!deviceId.equals(android_id)){
            deviceId = android_id;
        }

        if(token == null){
            token = "NULL";
        }

        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();


            RequestBody postData = new FormBody.Builder()
                    .add("token",token)
                    .add("newtoken", refreshedToken)
                    .add("deviceId", deviceId)
                    .build();

            Request request = new Request.Builder()
                    .url(HomeURL+"/ws_update_token")
                    //.header("x-api-key","t1zYkDOmba1kIbJOBXtgO6qC1qSPgWpp4oMKsK9I")
                    .post(postData)
                    .build();

            Response response = null;
              /*  Response httpResponse = client.newCall(request).execute();
                httpResponse.code();*/
            response = client.newCall(request).execute();
            myDB.updateDBToken(1,refreshedToken,deviceId);
            System.out.println(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
