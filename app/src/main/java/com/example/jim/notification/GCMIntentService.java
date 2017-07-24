package com.example.jim.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.user1.notification.R;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import static com.example.jim.notification.CommonUtilities.displayMessage;
import static com.example.jim.notification.CommonUtilities.SENDER_ID;

public class GCMIntentService extends GCMBaseIntentService{
   @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        displayMessage(context, getString(R.string.gcm_registered));
        ServerUtilities.register(context, registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        displayMessage(context, getString(R.string.gcm_unregistered));
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    /*@Override
    protected void onMessage(Context context, Intent intent) {
        Log.i(TAG, "Received message");
        String message = getString(R.string.gcm_message);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }*/

    @Override
    protected void onMessage(Context context, Intent intent)
    {
        Log.i(TAG, "Received message");
        // 接收 GCM server 傳來的訊息
        Bundle bData = intent.getExtras();

        // 處理 bData 內含的訊息
        // 在本例中, 我的 server 端程式 gcm_send.php 傳來了 message, campaigndate, title, description 四項資料
        /*String message = bData.getString("message");
        String campaigndate = bData.getString("campaigndate");
        String title = bData.getString("title");
        String description = bData.getString("description");
        // 通知 user
        generateNotification(context, bData);*/

        //處理bData內含的訊息
        //在本例中，我的server端程式gcm_send.php傳來了 message, title, number 三項資料
        String message=bData.getString("message");
        String title=bData.getString("title");
        String number=bData.getString("number");
        Log.i(TAG, "Received message: "+message+","+title+","+number);
        displayMessage(context, "Received message: "+ message + "," + title);

        //通知 user
        generateNotification(context, bData);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
        displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new Notification(icon, message, when);
        String title = context.getString(R.string.app_name);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        // set intent so it does not start a new activity
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        /*notification.setLatestEventInfo(context, title, message, intent);*/
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    // 注意這裡我不是直接改寫原範例的generateNotification()
    // 範例的 generateNotification() 傳入的參數是 Context, String
    // 利用 Java 的 函數重載 特性, 我保留了原 generateNotification(), 說不定以後會用到
    // 另外增加了一個傳入參數為 Context, Bundle 的 generateNotification()
    private static void generateNotification(Context context, Bundle bData)
    {
        int icon = R.drawable.ic_launcher;
        long when = System.currentTimeMillis();
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent ni = new Intent(context, MainActivity.class);
        ni.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, ni, 0);
        // 如果您想讓通知的內容有動態的變化
        // 就可以運用傳進來的參數 -- Bundle 型別 data
        // 取出您要的欄位填入 setContentTitle() 和 setContentText()
        // ;-)

        String title=bData.getString("title");
        String message=bData.getString("message");
        //String DecodeUrl= URLDecoder.decode("message");
        //String number=bData.getString("number");
        Notification noti = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setContentText(message)
                //.setContentIntent(intent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(icon)
                .setWhen(when)
                .build();
        nm.notify(0, noti);
    }

}
