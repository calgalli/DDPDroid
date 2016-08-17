package com.deedee.ddp.ddp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by cake on 1/31/16 AD.
 */
public class PushNotificationService extends GcmListenerService {

    public static final String ACTION="mycustomactionstring";

    int mType;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("HTTP",data.toString());
        //createNotification(mTitle, push_msg);

        mType = data.getInt("MessageType");
       // data.getString("MessageType");

        mType = Integer.parseInt(data.getString("MessageType"));
        Log.d("HTTP","type = "+ mType + " " + data.getString("MessageType"));


        switch(mType) {
            case 2:
                // Confirm

                String DriverPicture = data.getString("DriverPicture");
                String DriverName = data.getString("DriverName");
                String DriverLicensePlate = data.getString("DriverLicensePlate");
                String DriverMobile = data.getString("DriverMobile");

                Log.d("HTTP","Confirm");

                Intent mData = new Intent(ACTION);
                // add data
                mData.putExtra("DriverPicture", DriverPicture);
                mData.putExtra("DriverName", DriverName);
                mData.putExtra("DriverLicensePlate", DriverLicensePlate);
                mData.putExtra("DriverMobile", DriverMobile);
                mData.putExtra("mType",2);
                LocalBroadcastManager.getInstance(this).sendBroadcast(mData);

                break;
            case 3:
                // Pick up
                Intent data2 = new Intent(ACTION);
                // add data
                Log.d("HTTP", "Pick up");
                data2.putExtra("mType",3);
                LocalBroadcastManager.getInstance(this).sendBroadcast(data2);
                break;
            case 4:
                // Drop off
                Intent data3 = new Intent(ACTION);
                // add data
                Log.d("HTTP", "Drop off");
                data3.putExtra("mType",4);
                LocalBroadcastManager.getInstance(this).sendBroadcast(data3);
                break;
            default:
        }



    }


}
