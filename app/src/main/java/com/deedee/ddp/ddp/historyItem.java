package com.deedee.ddp.ddp;

/**
 * Created by cake on 2/27/16 AD.
 */
public class historyItem {
    String dateTime;
    String fromTo;
    String price;
    String driverName;
    String duration;
    String driverImage;
    String licensePlateNumber;

    public historyItem(String dateTime,
            String fromTo,
            String price,
            String driverName,
            String duration,
            String driverImage,
            String licensePlateNumber){

        this.dateTime = dateTime;
        this.fromTo = fromTo;
        this.price = price;
        this.driverName = driverName;
        this.duration = duration;
        this.driverImage = driverImage;
        this.licensePlateNumber = licensePlateNumber;
    }
}
