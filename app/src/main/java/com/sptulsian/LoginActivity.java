package com.sptulsian;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static final String HomeURL = "https://dev.sptulsian.com/";

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;
    private SkipLoginTask mskipLoginTask = null;
    ProgressDialog pd;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    DBHelper myDB;
    String token;
    String deviceId;
    Boolean responseOk;
    boolean doubleBackToExitPressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        responseOk = false;
        myDB = new DBHelper(LoginActivity.this);
         pd = ProgressDialog.show(this, "", "Loading...",false);
        pd.dismiss();
        doubleBackToExitPressedOnce = false;
        ////////////////////////////////////////////////////////////////////////////////
        ///////Fetch current token from firebase and get current android device ID//////
        ///////////////////////////////////////////////////////////////////////////////
        String currentToken = FirebaseInstanceId.getInstance().getToken();
        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        System.out.println("Refreshed token: " + currentToken);
        myDB.updateDBToken(1,currentToken,android_id);
        Cursor c = myDB.getDBToken(1);
        c.moveToFirst();
        token = c.getString(c.getColumnIndex(DBHelper.User_COLUMN_token));
        c.close();
        ////////////////////////////////////////////////////////////////////////////////
        ///////Compare token from database and current token//////
        ///////////////////////////////////////////////////////////////////////////////

        if(token ==null || !token.equals(currentToken)){
            Toast.makeText(this, "Updating token", Toast.LENGTH_SHORT).show();
            myDB.updateDBToken(1,currentToken,android_id);
            c = myDB.getDBToken(1);
            c.moveToFirst();
            token = c.getString(c.getColumnIndex(DBHelper.User_COLUMN_token));
            c.close();
//            Toast.makeText(this, "Token is : "+currentToken, Toast.LENGTH_SHORT).show();
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            Button skipSignInButton = (Button) findViewById(R.id.skip_sign_in_button);
            skipSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    skipLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }else{
//            Toast.makeText(this, "Token is : "+token, Toast.LENGTH_SHORT).show();
            mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

            mPasswordView = (EditText) findViewById(R.id.password);
            mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                    if (id == R.id.login || id == EditorInfo.IME_NULL) {
                        attemptLogin();
                        return true;
                    }
                    return false;
                }
            });

            Button mEmailSignInButton = (Button) findViewById(R.id.sign_in_button);
            mEmailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });

            Button skipSignInButton = (Button) findViewById(R.id.skip_sign_in_button);
            skipSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    skipLogin();
                }
            });

            mLoginFormView = findViewById(R.id.login_form);
            mProgressView = findViewById(R.id.login_progress);
        }

    }

    public void showProgressLoader(Boolean value){
        if(value){
            pd.show();
        }else{
            pd.dismiss();
        }
    }

    private void skipLogin(){
        Toast.makeText(this, "Skip login clicked. Getting token and deviceId from DB.", Toast.LENGTH_SHORT).show();
        showProgressLoader(true);
        Cursor c = myDB.getDBToken(1);
        c.moveToFirst();
        String token = c.getString(c.getColumnIndex(DBHelper.User_COLUMN_token));
        //Toast.makeText(this, "Token : "+token, Toast.LENGTH_SHORT).show();
        c.close();
        Cursor c1 = myDB.getDBDeviceId(1);
        c1.moveToFirst();
        String deviceId = c1.getString(c1.getColumnIndex(DBHelper.User_COLUMN_deviceId));
        //Toast.makeText(this, "DeviceID : "+deviceId, Toast.LENGTH_SHORT).show();
        c1.close();
        responseOk = false;
        mskipLoginTask = new SkipLoginTask(token, deviceId);
        mskipLoginTask.execute();
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
                    Button btn = (Button)findViewById(R.id.skip_sign_in_button);
                    btn.setEnabled(true);

                }
            }
        }, 15000);
        Button btn = (Button)findViewById(R.id.skip_sign_in_button);
        btn.setEnabled(false);
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
                        Button btn = (Button)findViewById(R.id.sign_in_button);
                        btn.setEnabled(true);

                    }
                }
            }, 15000);
            Button btn = (Button)findViewById(R.id.sign_in_button);
            btn.setEnabled(false);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 5;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
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
                    Button btn = (Button)findViewById(R.id.sign_in_button);
                    btn.setEnabled(true);
                    System.out.println(pd);
                    //pd.dismiss();
                    showProgressLoader(false);
                    showProgress(false);
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
                    myDB.updateDBUser(1,mEmail,mPassword);
                    LinearLayout mainLayout;
                    mainLayout = (LinearLayout)findViewById(R.id.email_login_form);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    LoginActivity.this.startActivity(myIntent);
                    finish();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    public class SkipLoginTask extends AsyncTask<String, Void, String> {

        private final String mToken;
        private final String mDeviceId;

        SkipLoginTask(String token, String deviceid) {
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
                        .add("token", mToken)
                        .add("deviceId", mDeviceId)
                        .add("creation_method","android-app-signup")
                        .build();

                Request request = new Request.Builder()
                        .url(HomeURL+"/ws_skip_sign_in")
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
                    Button btn = (Button)findViewById(R.id.sign_in_button);
                    btn.setEnabled(true);
                    System.out.println(pd);
                    //pd.dismiss();
                    showProgressLoader(false);
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                    responseOk = true;

                } else{
                    responseOk = true;
                    String newUSername = msg.getString("Username");
                    LinearLayout mainLayout;
                    Toast.makeText(LoginActivity.this, "Response received \n Username :" +
                            " "+newUSername+" Password"+ newUSername+" \nSaving...", Toast.LENGTH_SHORT).show();
                    myDB.updateDBUser(1,newUSername,newUSername);
                    // Get your layout set up, this is just an example
                    mainLayout = (LinearLayout)findViewById(R.id.email_login_form);
                    // Then just use the following:
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainLayout.getWindowToken(), 0);
                    Intent myIntent = new Intent(LoginActivity.this, MainActivity.class);
                    //myIntent.putExtra("key", value); //Optional parameters
                    LoginActivity.this.startActivity(myIntent);
                    finish();
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





                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}

