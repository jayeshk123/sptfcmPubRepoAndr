package com.sptulsian;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsActivity extends AppCompatActivity {
    DBHelper myDB;
    CardView cardView, cardView2,cardView3,cardView4,cardView5;
    TextView appupdate,credsNote;
    ImageView appInfoStat, credStat;
    AutoCompleteTextView usernameTxt,passwordTxt;
    public static final String HomeURL = "https://dev.sptulsian.com/";

    String username;
    String password;
    String deviceId;
    String token;
    Button ResetButton, UpdateButton, DebugReport;
    ProgressDialog pd;
    Integer minHeight1,minHeight2,minHeight3,minHeight4,minHeight5;
    private UserLoginTask mAuthTask = null;
    private UserLoginTask1 mAuthTask1 = null;
    private switchUser mSwitchTask = null;
    private adminDebugReport mDebugReportTask = null;
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mPasswordView;
    Boolean responseOk;

    public void showProgressLoader(Boolean value){
        if(value){
            pd = ProgressDialog.show(this, "", "Loading...",false);
        }else{
            pd.dismiss();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDB = new DBHelper(SettingsActivity.this);
        responseOk = false;


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



        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////App Information Tab///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        String version = null,latestVersion = null;
        try{
            PackageManager manager = getApplicationContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
            version = info.versionName;
            System.out.println("Current version is :"+version);
        }catch (Exception e){
            System.out.println(e);
        }
        
        VersionChecker versionChecker = new VersionChecker();
        try {
            latestVersion = versionChecker.execute().get();
            System.out.println("Play Store version is :"+latestVersion);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String versionStr = version.replace(".", "");
        String latestVersionStr = latestVersion.replace(".","");

        int versionInt = Integer.parseInt(versionStr);
        int latestVersionInt = Integer.parseInt(latestVersionStr);


        if(latestVersionInt <= versionInt){
            String message = "App Version : "+version+"\nAll set here. You have the latest app installed. If you are facing issues, try resetting app below.";
            Drawable res = getResources().getDrawable(R.drawable.tick);
            appInfoStat = (ImageView)findViewById(R.id.cv1ImgHazard);
            appInfoStat.setImageDrawable(res);
            appupdate = (TextView)findViewById(R.id.AppUpdate);
            appupdate.setText(message);
        }else{
            String message = "App Version : "+version+"\nLatest Version : "+latestVersion+"\nClick here to goto Playstore. Update app to get the latest features.";
            Drawable res = getResources().getDrawable(R.drawable.hazard_yellow);
            appInfoStat = (ImageView)findViewById(R.id.cv1ImgHazard);
            appInfoStat.setImageDrawable(res);
            appupdate = (TextView)findViewById(R.id.AppUpdate);
            appupdate.setText(message);
            appupdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),
                            "Application will open playstore...", Toast.LENGTH_LONG).show();

                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sptulsian"));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } catch (android.content.ActivityNotFoundException anfe) {
                    }
                }
            });
        }

        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////App Information Tab ENDED///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////Debugging Tab Started///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////

        ResetButton = (Button) findViewById(R.id.ResetBtn);
        DebugReport = (Button) findViewById(R.id.DebugReport);
        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call Switch User Web-service
                new AlertDialog.Builder(SettingsActivity.this)
                        .setTitle("Reset App")
                        .setMessage("Do you really want to reset application?\nIt will delete all data and restart app.")
                        .setIcon(R.drawable.hazard_yellow)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                mSwitchTask = new switchUser(username, password);
                                responseOk = false;
                                mSwitchTask.execute();
                                new Handler().postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (responseOk == false) {
                        /*if(pd!=null && pd.isShowing())
                        {
                            pd.dismiss();
                        }*/
                                            showProgressLoader(false);
                                            Toast.makeText(getApplicationContext(),
                                                    "Problem with network. Please try again", Toast.LENGTH_LONG).show();
                                            Button btn = (Button)findViewById(R.id.ResetBtn);
                                            btn.setEnabled(true);

                                        }
                                    }
                                }, 15000);
                            }})
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });
        DebugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Call Debug Report Web-service

                if(!username.equals(null) && !password.equals(null)){
                    mDebugReportTask = new adminDebugReport(username, password,token,deviceId);
                    responseOk = false;
                    mDebugReportTask.execute();
                    showProgressLoader(true);
                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (responseOk == false) {
                                showProgressLoader(false);
                                Toast.makeText(getApplicationContext(),
                                        "Problem with network. Please try again", Toast.LENGTH_LONG).show();
                                Button btn = (Button)findViewById(R.id.DebugReport);
                                btn.setEnabled(true);

                            }
                        }
                    }, 15000);
                    Toast.makeText(getApplicationContext(),
                            "Application will send Debug-Report to admin", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),
                            "Please enter valid credentials or reset app and choose skip-login to autogenerate.", Toast.LENGTH_LONG).show();
                }


            }
        });

        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////Debugging Tab ENDED///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////CREDENTIALS Tab StARTED///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////
        mEmailView = (AutoCompleteTextView)findViewById(R.id.usernameTextView);
        mPasswordView = (AutoCompleteTextView)findViewById(R.id.passwordTextView);


        UpdateButton = (Button)findViewById(R.id.updateBtn);

        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Web service to login is called", Toast.LENGTH_SHORT).show();
                attemptLogin();

            }
        });

        mAuthTask1 = new UserLoginTask1(username, password,token,deviceId);
        responseOk = false;
        mAuthTask1.execute();
        showProgressLoader(true);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (responseOk == false) {
                    showProgressLoader(false);
                    Toast.makeText(getApplicationContext(),
                            "Problem with network. Please try again", Toast.LENGTH_LONG).show();
                    Button btn = (Button)findViewById(R.id.updateBtn);
                    btn.setEnabled(true);

                }
            }
        }, 15000);

        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////CREDENTIALS Tab ENDED///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*WindowManager windowmanager = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimension = new DisplayMetrics();
        windowmanager.getDefaultDisplay().getMetrics(dimension);
        final int height = dimension.heightPixels;*/

        cardView = (CardView)findViewById(R.id.cv);
        cardView2 = (CardView)findViewById(R.id.cv2);
        //cardView3 = (CardView)findViewById(R.id.cv3);
        cardView4 = (CardView)findViewById(R.id.cv4);
        //cardView5 = (CardView)findViewById(R.id.cv5);
        final LinearLayout cvlin1 = (LinearLayout)findViewById(R.id.cvLin1);
        final LinearLayout cvlin2 = (LinearLayout)findViewById(R.id.cvLin2);
        final LinearLayout cvlin4 = (LinearLayout)findViewById(R.id.cvLin4);


        cardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cardView.getViewTreeObserver().removeOnPreDrawListener(this);
                minHeight1 = cardView.getHeight();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = minHeight1;
                cardView.setLayoutParams(layoutParams);

                return true;
            }
        });
        cardView2.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cardView2.getViewTreeObserver().removeOnPreDrawListener(this);
                minHeight2 = cardView2.getHeight();
                ViewGroup.LayoutParams layoutParams = cardView2.getLayoutParams();
                layoutParams.height = minHeight2;
                cardView2.setLayoutParams(layoutParams);

                return true;
            }
        });
        /*cardView3.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cardView3.getViewTreeObserver().removeOnPreDrawListener(this);
                minHeight3 = cardView3.getHeight();
                ViewGroup.LayoutParams layoutParams = cardView3.getLayoutParams();
                layoutParams.height = minHeight3;
                cardView3.setLayoutParams(layoutParams);

                return true;
            }
        });*/
        cardView4.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cardView4.getViewTreeObserver().removeOnPreDrawListener(this);
                minHeight4 = cardView4.getHeight();
                ViewGroup.LayoutParams layoutParams = cardView4.getLayoutParams();
                layoutParams.height = minHeight4;
                cardView4.setLayoutParams(layoutParams);

                return true;
            }
        });
        /*cardView5.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cardView5.getViewTreeObserver().removeOnPreDrawListener(this);
                minHeight5 = cardView5.getHeight();
                ViewGroup.LayoutParams layoutParams = cardView5.getLayoutParams();
                layoutParams.height = minHeight5;
                cardView5.setLayoutParams(layoutParams);

                return true;
            }
        });*/


        final ImageView btnExpand = (ImageView) findViewById(R.id.cv1Btn);
        final ImageView btn2Expand = (ImageView) findViewById(R.id.cv2Btn);
        //final ImageView btn3Expand = (ImageView) findViewById(R.id.cv3Btn);
        final ImageView btn4Expand = (ImageView) findViewById(R.id.cv4Btn);
        //final ImageView btn5Expand = (ImageView) findViewById(R.id.cv5Btn);
        btnExpand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView,cvlin1.getHeight(),btnExpand,1);

            }
        });
        cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView,cvlin1.getHeight(),btnExpand,1);

            }
        });

        btn2Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView2,cvlin2.getHeight(),btn2Expand,2);

            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView2,cvlin2.getHeight(),btn2Expand,2);

            }
        });
       /* btn3Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView3,200,btn3Expand,3);

            }
        });*/
        /* cardView3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView3,200,btn3Expand,3);

            }
        });*/

        btn4Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView4,cvlin4.getHeight(),btn4Expand,4);

            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView4,cvlin4.getHeight(),btn4Expand,4);

            }
        });
        /*btn5Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView5,200,btn5Expand,5);

            }
        });*/
        /*cardView5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView5,200,btn5Expand,5);

            }
        });*/

     /*   Drawable res = getResources().getDrawable(R.drawable.cancel);
        btnExpand.setImageDrawable(res);
        collapseView(cardView,1);
        btn2Expand.setImageDrawable(res);
        collapseView(cardView2,2);
        *//*btn3Expand.setImageDrawable(res);
        collapseView(cardView3,3);*//*
        btn4Expand.setImageDrawable(res);
        collapseView(cardView4,4);
        *//*btn5Expand.setImageDrawable(res);
        collapseView(cardView5,5);*//*
*/

        /*new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                   Drawable res = getResources().getDrawable(R.drawable.cancel);
        btnExpand.setImageDrawable(res);
        expandView(cardView,cardView.getHeight(),1);
        btn2Expand.setImageDrawable(res);
                expandView(cardView2,cardView2.getHeight(),2);
//        btn3Expand.setImageDrawable(res);
//        collapseView(cardView3,3);
        btn4Expand.setImageDrawable(res);
                expandView(cardView4,cardView4.getHeight(),4);
//        btn5Expand.setImageDrawable(res);
//        collapseView(cardView5,5);
            }
        }, 1000);*/



    }

    private void toggleCardViewnHeight(CardView cardView, int height, ImageView btn, int tab) {

        if(tab == 1){
            if (cardView.getHeight() == minHeight1) {
                // expand
                Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
                btn.setImageDrawable(res);
                expandView(cardView,height,tab); //'height' is the height of screen which we have measured already.
                /*RelativeLayout lin1 = (RelativeLayout) findViewById(R.id.Lin1);
                lin1.setVisibility(View.VISIBLE);*/
            } else {
                // collapse
                Drawable res = getResources().getDrawable(R.drawable.cancel);
                btn.setImageDrawable(res);
                collapseView(cardView,tab);
                /*RelativeLayout lin1 = (RelativeLayout) findViewById(R.id.Lin1);
                lin1.setVisibility(View.VISIBLE);*/


            }
        }else if(tab == 2){
            if (cardView.getHeight() == minHeight2) {
                // expand
                Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
                btn.setImageDrawable(res);
                expandView(cardView,height,tab); //'height' is the height of screen which we have measured already.

            } else {
                // collapse
                Drawable res = getResources().getDrawable(R.drawable.cancel);
                btn.setImageDrawable(res);
                collapseView(cardView,tab);

            }
        }else if(tab == 3){
            if (cardView.getHeight() == minHeight3) {
                // expand
                Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
                btn.setImageDrawable(res);
                expandView(cardView,height,tab); //'height' is the height of screen which we have measured already.

            } else {
                // collapse
                Drawable res = getResources().getDrawable(R.drawable.cancel);
                btn.setImageDrawable(res);
                collapseView(cardView,tab);

            }
        }else if(tab == 4){
            if (cardView.getHeight() == minHeight4) {
                // expand
                Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
                btn.setImageDrawable(res);
                expandView(cardView,height,tab); //'height' is the height of screen which we have measured already.

            } else {
                // collapse
                Drawable res = getResources().getDrawable(R.drawable.cancel);
                btn.setImageDrawable(res);
                collapseView(cardView,tab);

            }
        }else if(tab == 5){
            if (cardView.getHeight() == minHeight5) {
                // expand
                Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
                btn.setImageDrawable(res);
                expandView(cardView,height,tab); //'height' is the height of screen which we have measured already.

            } else {
                // collapse
                Drawable res = getResources().getDrawable(R.drawable.cancel);
                btn.setImageDrawable(res);
                collapseView(cardView,tab);

            }
        }else{
            if (cardView.getHeight() == minHeight1) {
                // expand
                Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
                btn.setImageDrawable(res);
                expandView(cardView,height,tab); //'height' is the height of screen which we have measured already.

            } else {
                // collapse
                Drawable res = getResources().getDrawable(R.drawable.cancel);
                btn.setImageDrawable(res);
                collapseView(cardView,tab);

            }
        }

    }


    public void collapseView(CardView card,int tab) {

        if(tab == 1){
            appupdate.setEnabled(true);
            ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                    minHeight1);
            System.out.println(card.getHeight());
            final CardView card1 = card;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                    layoutParams.height = val;
                    card1.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }else if(tab == 2){
            ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                    minHeight2);
            System.out.println(card.getHeight());
            final CardView card1 = card;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                    layoutParams.height = val;
                    card1.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }else if(tab == 3){
            ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                    minHeight3);
            System.out.println(card.getHeight());
            final CardView card1 = card;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                    layoutParams.height = val;
                    card1.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }else if(tab == 4){
            ResetButton.setEnabled(true);
            DebugReport.setEnabled(true);
            ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                    minHeight4);
            System.out.println(card.getHeight());
            final CardView card1 = card;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                    layoutParams.height = val;
                    card1.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }else if(tab == 5){
            ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                    minHeight5);
            System.out.println(card.getHeight());
            final CardView card1 = card;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                    layoutParams.height = val;
                    card1.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }else{
            ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                    minHeight1);
            System.out.println(card.getHeight());
            final CardView card1 = card;
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                    layoutParams.height = val;
                    card1.setLayoutParams(layoutParams);

                }
            });
            anim.start();
        }

    }
    public void expandView(CardView card,int height,int tab) {


        if(tab == 1){
            appupdate.setEnabled(false);
        }else if(tab == 2){

        }else if(tab == 3){

        }else if(tab == 4){
            ResetButton.setEnabled(false);
            DebugReport.setEnabled(false);
        }else if(tab == 5){

        }else{

        }
        ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                height);
        final CardView card1 = card;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = card1.getLayoutParams();
                layoutParams.height = val;
                card1.setLayoutParams(layoutParams);
            }
        });
        anim.start();

    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_empty_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            //showProgress(true);
            //pd.show(this, "", "Loading...",false);
            showProgressLoader(true);
            Cursor c = myDB.getDBToken(1);
            c.moveToFirst();
            token = c.getString(c.getColumnIndex(DBHelper.User_COLUMN_token));
            Cursor c3 = myDB.getDBDeviceId(1);
            c3.moveToFirst();
            deviceId = c3.getString(c3.getColumnIndex(DBHelper.User_COLUMN_deviceId));
            mAuthTask = new UserLoginTask(email, password,token,deviceId);
            responseOk = false;
            mAuthTask.execute();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (responseOk == false) {
                        /*if(pd!=null && pd.isShowing())
                        {
                            pd.dismiss();
                        }*/
                        showProgressLoader(false);
                        Toast.makeText(getApplicationContext(),
                                "Problem with network. Please try again", Toast.LENGTH_LONG).show();
                        Button btn = (Button)findViewById(R.id.updateBtn);
                        btn.setEnabled(true);

                    }
                }
            }, 15000);
            Button btn = (Button)findViewById(R.id.updateBtn);
            btn.setEnabled(false);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    public class UserLoginTask extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mToken;
        private final String mDeviceId;

        UserLoginTask(String email, String password, String token, String deviceid) {
            mEmail = email;
            mPassword = password;
            mToken = token;
            mDeviceId = deviceid;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                RequestBody postData = new FormBody.Builder()
                        .add("username",mEmail)
                        .add("password",mPassword)
                        .add("token", mToken)
                        .add("deviceId", mDeviceId)
                        .build();

                Request request = new Request.Builder()
                        .url(HomeURL+"/ws_sign_in")
                        //.header("x-api-key","t1zYkDOmba1kIbJOBXtgO6qC1qSPgWpp4oMKsK9I")
                        .post(postData)
                        .build();

                Response response = null;
              /*  Response httpResponse = client.newCall(request).execute();
                httpResponse.code();*/
                response = client.newCall(request).execute();
                System.out.println(response);
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mAuthTask = null;

            try{
                super.onPostExecute(s);
                JSONObject reader = new JSONObject(s);
                JSONObject msg  = reader.getJSONObject("Response");
                /*Toast.makeText(getApplicationContext(),
                        String.valueOf(msg), Toast.LENGTH_LONG).show();*/
                Integer status_code = msg.getInt("Status_code");
                String alert = msg.getString("Message");
                if (status_code == 500){
                    Toast.makeText(getApplicationContext(),
                            alert, Toast.LENGTH_LONG).show();
                    Button btn = (Button)findViewById(R.id.updateBtn);
                    btn.setEnabled(true);
                    System.out.println(pd);
                    //pd.dismiss();
                    showProgressLoader(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    responseOk = true;

                } else{
                    /*if(pd!=null && pd.isShowing())
                    {
                        pd.dismiss();
                    }*/
                    showProgressLoader(false);
                    responseOk =true;
                    Toast.makeText(getApplicationContext(),
                            alert, Toast.LENGTH_LONG).show();
                    username = mEmail;
                    password = mPassword;
                    myDB.updateDBUser(1,mEmail,mPassword);
                    LinearLayout mainLayout;
                    mainLayout = (LinearLayout)findViewById(R.id.Lin4);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    Button btn = (Button)findViewById(R.id.updateBtn);
                    btn.setEnabled(true);
                    Toast.makeText(getApplicationContext(),
                            "Credentials Updated Successfully", Toast.LENGTH_LONG).show();

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class UserLoginTask1 extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mToken;
        private final String mDeviceId;

        UserLoginTask1(String email, String password, String token, String deviceid) {
            mEmail = email;
            mPassword = password;
            mToken = token;
            mDeviceId = deviceid;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                RequestBody postData = new FormBody.Builder()
                        .add("username",mEmail)
                        .add("password",mPassword)
                        .add("token", mToken)
                        .add("deviceId", mDeviceId)
                        .build();

                Request request = new Request.Builder()
                        .url(HomeURL+"/ws_sign_in")
                        //.header("x-api-key","t1zYkDOmba1kIbJOBXtgO6qC1qSPgWpp4oMKsK9I")
                        .post(postData)
                        .build();

                Response response = null;
              /*  Response httpResponse = client.newCall(request).execute();
                httpResponse.code();*/
                response = client.newCall(request).execute();
                System.out.println(response);
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mAuthTask = null;

            final LinearLayout cvlin1 = (LinearLayout)findViewById(R.id.cvLin1);
            final LinearLayout cvlin2 = (LinearLayout)findViewById(R.id.cvLin2);
            final LinearLayout cvlin4 = (LinearLayout)findViewById(R.id.cvLin4);

            final ImageView btnExpand = (ImageView) findViewById(R.id.cv1Btn);
            final ImageView btn2Expand = (ImageView) findViewById(R.id.cv2Btn);
            //final ImageView btn3Expand = (ImageView) findViewById(R.id.cv3Btn);
            final ImageView btn4Expand = (ImageView) findViewById(R.id.cv4Btn);
            //final ImageView btn5Expand = (ImageView) findViewById(R.id.cv5Btn);

            toggleCardViewnHeight(cardView,cvlin1.getHeight(),btnExpand,1);
            toggleCardViewnHeight(cardView2,cvlin2.getHeight(),btn2Expand,2);
            toggleCardViewnHeight(cardView4,cvlin4.getHeight(),btn4Expand,4);

            try{
                super.onPostExecute(s);

                JSONObject reader = new JSONObject(s);
                JSONObject msg  = reader.getJSONObject("Response");
                /*Toast.makeText(getApplicationContext(),
                        String.valueOf(msg), Toast.LENGTH_LONG).show();*/
                Integer status_code = msg.getInt("Status_code");
                String alert = msg.getString("Message");
                if (status_code == 500){
                    //Invalid
                    Toast.makeText(getApplicationContext(),
                            alert, Toast.LENGTH_LONG).show();
                    Button btn = (Button)findViewById(R.id.updateBtn);
                    btn.setEnabled(true);
                    System.out.println(pd);
                    //pd.dismiss();
                    showProgressLoader(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    responseOk = true;

                    String message = "Credentials are wrong! \nPlease update valid credentials to continue using website and application.";
                    credsNote = (TextView)findViewById(R.id.credsNote);
                    credsNote.setText(message);
                    Drawable res = getResources().getDrawable(R.drawable.hazard_red);
                    credStat = (ImageView)findViewById(R.id.cv2ImgHazard);
                    credStat.setImageDrawable(res);


                } else{
                    //Valid




                    showProgressLoader(false);
                    responseOk =true;
                    if(!username.equals(null) && !password.equals(null) && !token.equals(null) && !deviceId.equals(null) && !username.contains("app_")){
                        // All set
                        usernameTxt = (AutoCompleteTextView)findViewById(R.id.usernameTextView);
                        passwordTxt = (AutoCompleteTextView)findViewById(R.id.passwordTextView);
                        usernameTxt.setText(username);
                        passwordTxt.setText(password);
                        String message = "All set here!";
                        credsNote = (TextView)findViewById(R.id.credsNote);
                        credsNote.setText(message);
                        Drawable res = getResources().getDrawable(R.drawable.tick);
                        credStat = (ImageView)findViewById(R.id.cv2ImgHazard);
                        credStat.setImageDrawable(res);





                    }else if(token.equals(null)){
                        //Panic!! Update token if not working even for second time ask to reset app.
                        String message = "Something went wrong! \nPlease reset application using debugging section below to get notifications.";
                        credsNote = (TextView)findViewById(R.id.credsNote);
                        credsNote.setText(message);
                        Drawable res = getResources().getDrawable(R.drawable.hazard_red);
                        credStat = (ImageView)findViewById(R.id.cv2ImgHazard);
                        credStat.setImageDrawable(res);

                    }else if(username.equals(null)) {
                        //update Credentials
                        String message = "Please enter credentials to continue using website and app services.";
                        credsNote = (TextView)findViewById(R.id.credsNote);
                        credsNote.setText(message);
                        Drawable res = getResources().getDrawable(R.drawable.hazard_red);
                        credStat = (ImageView)findViewById(R.id.cv2ImgHazard);
                        credStat.setImageDrawable(res);


                    }else if(username.contains("app_")){
                        //Display as free user
                        usernameTxt = (AutoCompleteTextView)findViewById(R.id.usernameTextView);
                        passwordTxt = (AutoCompleteTextView)findViewById(R.id.passwordTextView);
                        usernameTxt.setText(username);
                        passwordTxt.setText(password);
//            usernameTxt.setEnabled(false);
//            passwordTxt.setEnabled(false);
                        String message = "Your credentials are valid but your account does not have any valid subscription. Hence you are getting only free alerts.\nClick here to activate membership.";
                        credsNote = (TextView)findViewById(R.id.credsNote);
                        credsNote.setText(message);
                        credsNote.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
//                    Toast.makeText(getApplicationContext(),
//                            "Application will open playstore...", Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                                myIntent.putExtra("SearchText", "https://dev.sptulsian.com/register/step1");
                                myIntent.putExtra("Type","Member");//Optional parameters
                                SettingsActivity.this.startActivity(myIntent);
                            }
                        });
                        Drawable res = getResources().getDrawable(R.drawable.hazard_yellow);
                        credStat = (ImageView)findViewById(R.id.cv2ImgHazard);
                        credStat.setImageDrawable(res);

                    }
                    LinearLayout mainLayout;
                    mainLayout = (LinearLayout)findViewById(R.id.Lin4);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    Button btn = (Button)findViewById(R.id.updateBtn);
                    btn.setEnabled(true);


                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class switchUser extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;

        switchUser(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                RequestBody postData = new FormBody.Builder()
                        .add("username",mEmail)
                        .add("password",mPassword)
                        .add("deviceType","Android")
                        .build();

                Request request = new Request.Builder()
                        .url(HomeURL+"/ws_switch_user")
                        //.header("x-api-key","t1zYkDOmba1kIbJOBXtgO6qC1qSPgWpp4oMKsK9I")
                        .post(postData)
                        .build();

                Response response = null;
              /*  Response httpResponse = client.newCall(request).execute();
                httpResponse.code();*/
                response = client.newCall(request).execute();
                System.out.println(response);
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mSwitchTask = null;
            responseOk = true;
            try{
                super.onPostExecute(s);
                JSONObject reader = new JSONObject(s);
                JSONObject msg  = reader.getJSONObject("Response");
                Integer status_code = msg.getInt("Status_code");
                String alert = msg.getString("Message");

                if(status_code == 200){
                    //success

                    myDB.updateDBToken(1,null,null);
                    myDB.updateDBUser(1,null,null);
                    Toast.makeText(getApplicationContext(),
                            "Application will restart now...", Toast.LENGTH_LONG).show();

                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    //abort
                    Toast.makeText(getApplicationContext(),
                            alert, Toast.LENGTH_LONG).show();

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }




    public class adminDebugReport extends AsyncTask<String, Void, String> {

        private final String mEmail;
        private final String mPassword;
        private final String mToken;
        private final String mDeviceId;

        adminDebugReport(String email, String password, String token, String deviceid) {
            mEmail = email;
            mPassword = password;
            mToken = token;
            mDeviceId = deviceid;
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                RequestBody postData = new FormBody.Builder()
                        .add("username",mEmail)
                        .add("password",mPassword)
                        .add("token", mToken)
                        .add("deviceId", mDeviceId)
                        .add("deviceType","Android")
                        .build();

                Request request = new Request.Builder()
                        .url(HomeURL+"/ws_admin_debug_report")
                        //.header("x-api-key","t1zYkDOmba1kIbJOBXtgO6qC1qSPgWpp4oMKsK9I")
                        .post(postData)
                        .build();

                Response response = null;
                response = client.newCall(request).execute();
                System.out.println(response);
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mDebugReportTask = null;
            System.out.println("Response :"+s);
            try{
                super.onPostExecute(s);
                JSONObject reader = new JSONObject(s);
                JSONObject msg  = reader.getJSONObject("Response");
                /*Toast.makeText(getApplicationContext(),
                        String.valueOf(msg), Toast.LENGTH_LONG).show();*/
                Integer status_code = msg.getInt("Status_code");
                String alert = msg.getString("Message");
                if (status_code == 500){
                    Toast.makeText(getApplicationContext(),
                            alert, Toast.LENGTH_LONG).show();
                    Button btn = (Button)findViewById(R.id.DebugReport);
                    btn.setEnabled(true);
                    showProgressLoader(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    responseOk = true;

                } else{
                    showProgressLoader(false);
                    responseOk =true;
                    Toast.makeText(getApplicationContext(),
                            alert, Toast.LENGTH_LONG).show();
                    Button btn = (Button)findViewById(R.id.DebugReport);
                    btn.setEnabled(true);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                    SettingsActivity.this.startActivity(myIntent);
                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }



}
