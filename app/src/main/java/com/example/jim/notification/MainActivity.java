package com.example.jim.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.TextView;

import com.example.user1.notification.R;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import static com.example.jim.notification.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.example.jim.notification.CommonUtilities.EXTRA_MESSAGE;
import static com.example.jim.notification.CommonUtilities.SENDER_ID;
import static com.example.jim.notification.CommonUtilities.TAG;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    GoogleCloudMessaging gcm;
    private Context context;
    private TextView tvRegisterMsg;
    private String strRegId;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GCMTask().execute();

        tvRegisterMsg = (TextView)findViewById(R.id.action_settings);
        context = getApplicationContext();
        gcm = GoogleCloudMessaging.getInstance(this);
        setGCM_RegID();
    }

    // get GCM Reg ID and Save to our server (use ServerUtilities.java)
    public void setGCM_RegID() {

        registerReceiver(mHandleMessageReceiver, new IntentFilter(
                DISPLAY_MESSAGE_ACTION));
        // register with Google.
        new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    strRegId = gcm.register(SENDER_ID);
                    msg = "Device registered, registration id=" + strRegId;
                    // send id to our server
                    boolean registered = ServerUtilities.register(context,
                            strRegId);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                // tvRegisterMsg.append(msg + "\n");
            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHandleMessageReceiver); // 在Activity
        // 消滅時才unregister
    }

    // Create a broadcast receiver to get message and show on screen
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        private final static String MY_MESSAGE = "1111";
        //com.stu.phonebook.DISPLAY_MESSAGE

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MY_MESSAGE.equals(intent.getAction())) {
                final String newMessage = intent.getExtras().getString(
                        EXTRA_MESSAGE);
                tvRegisterMsg.setText(newMessage);

            }
        }
    };

    private class GCMTask extends AsyncTask<Void, Void, Void>
    {
        protected Void doInBackground(Void... params)
        {
            Log.d(TAG, "檢查裝置是否支援 GCM");
            // 檢查裝置是否支援 GCM
            GCMRegistrar.checkDevice(MainActivity.this);
            GCMRegistrar.checkManifest(MainActivity.this);
            final String regId = GCMRegistrar.getRegistrationId(MainActivity.this);
            if (regId.equals(""))
            {
                Log.d(TAG, "尚未註冊 Google GCM, 進行註冊");
                GCMRegistrar.register(MainActivity.this, SENDER_ID);
            }
            return null;
        }
    }


}
