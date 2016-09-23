package com.sptulsian;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.List;
import java.util.Map;

/**
 * Created by earth on 9/9/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    String link,title,desc,type;
    public static final String ACTION_FCM_NOTIFICATION = "fcm-notification";
    // Intent keys
    public static final String INTENT_FCM_NOTIFICATION_FROM = "from";
    public static final String INTENT_FCM_NOTIFICATION_DATA = "data";

    public static String BROADCAST_ACTION = "com.sptulsian.android.broadcasttest.SHOWTOAST";
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        System.out.println("From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
       // if (remoteMessage.getData().size() > 0) {
        Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("click_action")) {
            ClickActionHelper.startActivity(data.get("click_action"), null, this);
        }
            System.out.println("Message data payload: " + remoteMessage.getData());
            JSONObject reader = new JSONObject(remoteMessage.getData());
            JSONObject msg  = null;
            try {
                //msg = reader.getJSONObject("Message");
                link = reader.getString("link");
                title = reader.getString("title");
                desc = reader.getString("body");
                desc = html2text(desc);
                type = reader.getString("alert_type");
                System.out.println("Link : " + link);
                System.out.println("Desc : " + desc);
                System.out.println("Title : " + title);
                System.out.println("Type : " + type);
                Long ts = System.currentTimeMillis()/1000;
                Integer last = (int)(long)ts;
                String redLink;
                if(type.equals("Member")){
                    redLink= "member-zone"+link;
                }else{
                    redLink= "free-zone"+link;
                }
                if (isForeground(this)) {
                    // broadcast notification
                    displayNotification(title, last, remoteMessage.getFrom(),redLink);

                } else {
                    displayNotification(title, last, remoteMessage.getFrom(), redLink);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        if (remoteMessage.getNotification() != null) {
            System.out.println("Message Notification Body: " + remoteMessage.getData());

        }
            
        //}

        /*// Check if message contains a notification payload.
        /*if (remoteMessage.getNotification() != null) {
            System.out.println("Message Notification Body: " + remoteMessage.getData());

        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void broadcast(final String from, final String link) {
        Intent intent = new Intent(ACTION_FCM_NOTIFICATION);
        intent.putExtra(INTENT_FCM_NOTIFICATION_FROM, from);
        intent.putExtra("RedirectLink", link);
        intent.putExtra("Body", desc);
        intent.putExtra("Title", title);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
/*
        Intent broadcast = new Intent();
        broadcast.setAction(BROADCAST_ACTION);
        sendBroadcast(broadcast);*/


    }
    public void displayNotification(String msg, Integer id, String from, String redlink){
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.putExtra("SearchText", link);
        System.out.println("Clicked Link : "+link);
        notificationIntent.putExtra("Type", type);

        /*Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("Type", type); //Optional parameters
        myIntent.putExtra("SearchText", link); //Optional parameters
        this.startActivity(myIntent);*/
       /* if(type.equals("Member")){
            MainActivity ma = new MainActivity();
            ma.redirectWV("member-zone"+link);
        }else{
            MainActivity ma = new MainActivity();
            ma.redirectWV("free-zone"+link);
        }*/

        broadcast(from,redlink);

       /* notificationIntent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE );

        notificationManager.notify(id, builder.build());*/
    }

    public static String html2text(String html) {
        return Jsoup.parse(html).text();
    }

    private static boolean isForeground(Context context) {
        // Gets a list of running processes.
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();

        // On some versions of android the first item in the list is what runs in the foreground,
        // but this is not true on all versions.  Check the process importance to see if the app
        // is in the foreground.
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : tasks) {
            if (ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == appProcess.importance
                    && packageName.equals(appProcess.processName)) {
                return true;
            }
        }
        return false;
    }
}
