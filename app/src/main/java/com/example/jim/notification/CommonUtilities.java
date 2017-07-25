package com.example.jim.notification;

import android.content.Context;
import android.content.Intent;

public class CommonUtilities {

    /**
     * Base URL of the Demo Server (such as http://my_host:8080/gcm-demo)
     */
    static final String SERVER_URL = "http://140.130.36.246/php";
    //static final String SENDER_ID = "581236977487";
    static final String SENDER_ID = "890914030479";
    static final String DISPLAY_MESSAGE_ACTION = "com.example.jim.DISPLAY_MESSAGE";
    /**
     * Google API project id registered to use GCM.
     */

    /**
     * Tag used on log messages.
     */
    static final String TAG = "GCMDemo";

    /**
     * Intent used to display a message in the screen.
     */


    /**
     * Intent's extra that contains the message to be displayed.
     */
    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
