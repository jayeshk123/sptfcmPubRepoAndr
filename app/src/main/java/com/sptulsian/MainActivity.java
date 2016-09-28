package com.sptulsian;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.util.EncodingUtils;

public class MainActivity extends Activity {

    WebView webView;
    final String HomeURL = "https://dev.sptulsian.com/";
    boolean doubleBackToExitPressedOnce;
    String username;
    String password;
    String deviceId;
    String token;
    DBHelper myDB;
    String cookies;
    String reclink;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        myDB = new DBHelper(MainActivity.this);
        doubleBackToExitPressedOnce = false;
        pd = ProgressDialog.show(this, "", "Loading...",false);

        Cursor c = myDB.getDBToken(1);
        c.moveToFirst();
        token = c.getString(c.getColumnIndex(DBHelper.User_COLUMN_token));

        Cursor c1 = myDB.getDBUsername(1);
        c1.moveToFirst();
        username = c1.getString(c1.getColumnIndex(DBHelper.User_COLUMN_username));

        Cursor c2 = myDB.getDBPassword(1);
        c2.moveToFirst();
        password = c2.getString(c2.getColumnIndex(DBHelper.User_COLUMN_password));

        Cursor c3 = myDB.getDBDeviceId(1);
        c3.moveToFirst();
        deviceId = c3.getString(c3.getColumnIndex(DBHelper.User_COLUMN_deviceId));

        c.close();
        c1.close();
        c2.close();
        c3.close();



       /* webView = (WebView)findViewById(R.id.MainWebView);
        webView.setWebViewClient(new WebViewClient());

        CookieManager cookieManager = CookieManager.getInstance();
        CookieSyncManager.createInstance(this);
        cookieManager.setAcceptCookie(true);
        cookieManager.acceptCookie();

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAppCacheEnabled(true);*/

        Intent intent = getIntent();
        if(intent.getExtras()!= null) {
            Bundle bundle = intent.getExtras();
            System.out.println("Extra"+intent.getExtras());
            if (bundle.getString("SearchText") != null) {
                reclink = bundle.getString("SearchText");
                System.out.println("Link :" +reclink);
                if (bundle.getString("Type") != null) {
                    String type = bundle.getString("Type");
                    if (type.equals("Member")) {
                        System.out.println("MemberZone");
                        redirectWebView(reclink);
                    } else {
                        redirectWebView(reclink);
                    }
                } else {
                    System.out.println("No type bt has extra");
                    redirectWebView(reclink);
                }
                //redirectWebView("home");
            }else if(bundle.getString("link")!=null){
                reclink = bundle.getString("link");
                System.out.println("No Serachtext bt has extra "+bundle.getString("link"));
                if(bundle.getString("alert_type")!=null){
                   String type = bundle.getString("alert_type");
                    if(type.equals("Member")){
                        redirectWebView(reclink);
                    }else{
                        redirectWebView(reclink);
                    }
                }else{
                    redirectWebView(reclink);
                }

            }else{
                System.out.println("Link is not available in data component.");
            }
        }else{
            System.out.println("No extras");
            redirectWebView("home");
        }



        /*if(username.length()>0 && password.length()>0){
            String url = "/my-alerts";
            String postData = "&username="+username+"&password="+password+"&redirecturl="+url;
            webView.postUrl(HomeURL+"ws_user_login", EncodingUtils.getBytes(postData, "BASE64"));
            cookies = CookieManager.getInstance().getCookie(HomeURL+"user_profiles/mobile_login");


            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    pd.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(pd!=null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                }
            });
        }else{
            webView.loadUrl(HomeURL);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    pd.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(pd!=null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                }
            });
        }*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                System.out.println("Refreshed token: " + refreshedToken);


                String Message = "Token : "+token+"\n\nUsername : "+username+"\n\nPassword : "+password+"\n\nDeviceId : "+deviceId;
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setTitle("Token Management");
                alertDialogBuilder.setMessage(Message);
                alertDialogBuilder.show();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(refreshedToken, refreshedToken);
                clipboard.setPrimaryClip(clip);
                Snackbar.make(view, "Copied to clipboard", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Intent myIntent = new Intent(MainActivity.this, SettingsActivity.class);
                //myIntent.putExtra("key", value); //Optional parameters
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("extra")) {
            System.out.println("Extra"+intent.getExtras());
            ClickActionHelper.startActivity(intent.getStringExtra("click_action"), intent.getExtras(), this);
        }

    }

    public void redirectWebView(String link) {
        webView = (WebView)findViewById(R.id.MainWebView);
        webView.setWebViewClient(new WebViewClient());

        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
//        webView.setInitialScale(250);
        System.out.println("Link" +link);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // wv2.loadUrl(link);
        Cursor c1 = myDB.getDBUsername(1);
        c1.moveToFirst();
        username = c1.getString(c1.getColumnIndex(DBHelper.User_COLUMN_username));

        Cursor c2 = myDB.getDBPassword(1);
        c2.moveToFirst();
        password = c2.getString(c2.getColumnIndex(DBHelper.User_COLUMN_password));

        if(username != null && password != null){

            String postData = "&username="+username+"&password="+password+"&redirecturl="+link;
            webView.postUrl(HomeURL+"ws_redirect_user", EncodingUtils.getBytes(postData, "BASE64"));
            cookies = CookieManager.getInstance().getCookie(HomeURL+"ws_redirect_user");
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    pd.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(pd!=null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                }
            });
        }else{
            webView.loadUrl(HomeURL);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon)
                {
                    pd.show();
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    if(pd!=null && pd.isShowing())
                    {
                        pd.dismiss();
                    }
                }
            });
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        // unregister notification receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(notificationReceiver);
    }

    @Override
    protected void onResume() {

        super.onResume();

        // register notification receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(notificationReceiver,
                new IntentFilter(MyFirebaseMessagingService.ACTION_FCM_NOTIFICATION));

    }


    private final BroadcastReceiver notificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            Log.d(LOG_TAG, "Received notification from local broadcast. Display it in a dialog.");
            Bundle bundle = intent.getExtras();
            System.out.println("Extra"+bundle.getString("RedirectLink"));
            showDialog(MainActivity.this,bundle.getString("Title"),bundle.getString("Body"),bundle.getString("RedirectLink"));
        }
    };


    public void showDialog(Activity activity, String title, CharSequence message, String link) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String url = link;
        if (title != null) builder.setTitle(title);

        builder.setMessage(message);
        builder.setPositiveButton("Open Page", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                redirectWebView(url);
            }
        });
        builder.setNegativeButton("Close", null);
        builder.show();
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                   if(webView.canGoBack()) {
                       webView.goBack();
                    } else {
                      /*  String currurl = wv2.getUrl();
                        if(!currurl.equals("http://52.73.112.105/")){
                            wv2.loadUrl("http://52.73.112.105/");
                        }*/
//                        else{
                        if (doubleBackToExitPressedOnce) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                finishAffinity();
                            }
                            System.exit(0);
                        }

                        doubleBackToExitPressedOnce = true;
                        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                doubleBackToExitPressedOnce = false;
                            }
                        }, 2000);
//                        }




                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
