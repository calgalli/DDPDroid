package com.deedee.ddp.ddp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class alarmReceiver extends BroadcastReceiver
{
    public static final String NOTIFICATION = "com.cake.android.alarm.receiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent service1 = new Intent(context, MyAlarmService.class);
        context.startService(service1);
        String alarmOrderID = intent.getStringExtra("orderID");
        //Toast.makeText(context, "Alarm Triggered and SMS Sent", Toast.LENGTH_LONG).show();

        //Toast.makeText(context, "Alarm Triggered in alarmReveiver " + alarmOrderID, Toast.LENGTH_LONG).show();
        Log.d("HTTP", "Alarm Triggered in alarmReveiver " + alarmOrderID);

      /*  Intent intent2 = new Intent(NOTIFICATION);
        intent2.putExtra("orderID", alarmOrderID);
        //intent.putExtra("driverCarType", "bbb");



        context.sendBroadcast(intent2);*/



        Intent intent1 = new Intent();
        intent1.setClassName("com.example.ddp.ddp", "com.example.ddp.ddp.MainActivity");
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra("adv", "yes");

        /*

        //Intent intent1 = new Intent(context, MapsActivity.class);
        intent1.putExtra("acceptedCustomerId", acceptedCustommerID);
        intent1.putExtra("driverID", driverID);
        intent1.putExtra("order", pickupedOrder.toString());
        intent1.putExtra("orderID", alarmOrderID);
        intent1.putExtra("driverCarType", Integer.toString(driverCarType));
        intent1.putExtra("isAdvBooking", "Yes");

        //intent1.putExtra("customerTelNumber", customerTelNumber);
        //intent1.putExtra("noteToDriver", noteToDriver);

        Log.d("HTTP", "Picked up order = " + pickupedOrder);*/

        context.startActivity(intent1);


    }
}
