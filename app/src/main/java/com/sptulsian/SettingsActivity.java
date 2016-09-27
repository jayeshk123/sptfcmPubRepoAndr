package com.sptulsian;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {
    DBHelper myDB;
    CardView cardView, cardView2,cardView3,cardView4,cardView5;
    TextView appupdate,credsNote;
    ImageView appInfoStat;
    AutoCompleteTextView usernameTxt,passwordTxt;

    String username;
    String password;
    String deviceId;
    String token;
    Button ResetButton;

    Integer minHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        myDB = new DBHelper(SettingsActivity.this);
        minHeight = 0;

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
        
        if(version.equals(latestVersion)){
            String message = "App Version : "+latestVersion+"\nAll set here. You have the latest app installed. If you are facing issues, try resetting app below.";
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
        myDB.close();

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
        if(!username.equals(null) && !password.equals(null) && !token.equals(null) && !deviceId.equals(null) && !username.contains("app_")){
            // All set
            usernameTxt = (AutoCompleteTextView)findViewById(R.id.usernameTextView);
            passwordTxt = (AutoCompleteTextView)findViewById(R.id.passwordTextView);
            usernameTxt.setText(username);
            passwordTxt.setText(password);
//            usernameTxt.setEnabled(false);
//            passwordTxt.setEnabled(false);
            String message = "All set here!";
            credsNote = (TextView)findViewById(R.id.credsNote);
            credsNote.setText(message);



        }else if(token.equals(null)){
            //Panic!! Update token if not working even for second time ask to reset app.
        }else if(username.equals(null)) {
            //update Credentials
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
                    Toast.makeText(getApplicationContext(),
                            "Application will open playstore...", Toast.LENGTH_LONG).show();
                    Intent myIntent = new Intent(SettingsActivity.this, MainActivity.class);
                    myIntent.putExtra("SearchText", "https://dev.sptulsian.com/register/step1");
                    myIntent.putExtra("Type","Member");//Optional parameters
                    SettingsActivity.this.startActivity(myIntent);


                }
            });

        }
        ///////////////////////////////////////////////////////////////////
        //////////////////////////////////////////////////////////////////
        ///////////////////////CREDENTIALS Tab ENDED///////////////////////
        //////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////

        ResetButton = (Button) findViewById(R.id.ResetBtn);
        ResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.updateDBToken(1,null,null);
                myDB.updateDBUser(1,null,null);
                Toast.makeText(getApplicationContext(),
                        "Application will restart now...", Toast.LENGTH_LONG).show();

                Intent i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
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


        cardView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                cardView.getViewTreeObserver().removeOnPreDrawListener(this);
                minHeight = cardView.getHeight();
                ViewGroup.LayoutParams layoutParams = cardView.getLayoutParams();
                layoutParams.height = minHeight;
                cardView.setLayoutParams(layoutParams);

                return true;
            }
        });

        final ImageView btnExpand = (ImageView) findViewById(R.id.cv1Btn);
        final ImageView btn2Expand = (ImageView) findViewById(R.id.cv2Btn);
        //final ImageView btn3Expand = (ImageView) findViewById(R.id.cv3Btn);
        final ImageView btn4Expand = (ImageView) findViewById(R.id.cv4Btn);
        //final ImageView btn5Expand = (ImageView) findViewById(R.id.cv5Btn);
        btnExpand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView,200,btnExpand);

            }
        });

        btn2Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView2,200,btn2Expand);

            }
        });
       /* btn3Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView3,200,btn3Expand);

            }
        });*/
        btn4Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView4,200,btn4Expand);

            }
        });
        /*btn5Expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toggleCardViewnHeight(cardView5,200,btn5Expand);

            }
        });*/


    }

    private void toggleCardViewnHeight(CardView cardView, int height, ImageView btn) {

        if (cardView.getHeight() == minHeight) {
            // expand
            Drawable res = getResources().getDrawable(R.drawable.arrow_down_float);
            btn.setImageDrawable(res);
            expandView(cardView,height); //'height' is the height of screen which we have measured already.

        } else {
            // collapse
            Drawable res = getResources().getDrawable(R.drawable.cancel);
            btn.setImageDrawable(res);
            collapseView(cardView);

        }
    }


    public void collapseView(CardView card) {
        appupdate.setEnabled(true);
        ResetButton.setEnabled(true);
        ValueAnimator anim = ValueAnimator.ofInt(card.getHeight(),
                minHeight);
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
    public void expandView(CardView card,int height) {
        appupdate.setEnabled(false);
        ResetButton.setEnabled(false);
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

}
