package com.deedee.ddp.ddp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by cake on 2/27/16 AD.
 */
public class historyItemAdapter  extends ArrayAdapter<historyItem> {
    // declaring our ArrayList of items
    private ArrayList<historyItem> objects;

    constants cc = new constants();
    public final String address = cc.address;
    Context context;
    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public historyItemAdapter(Context context, int textViewResourceId, ArrayList<historyItem> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        this.context = context;
    }


    /*
* we are overriding the getView method here - this is what defines how each
* list item will look.
*/
    public View getView(int position, View convertView, ViewGroup parent){

        // assign the view we are converting to a local variable
        View v = convertView;

        // first check to see if the view is null. if so, we have to inflate it.
        // to inflate it basically means to render, or show, the view.
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.history_cell, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        historyItem i = objects.get(position);
        // ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();


        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView histDateTime = (TextView) v.findViewById(R.id.histDateTimeText);
            histDateTime.setTextColor(Color.BLACK);
            TextView fromTo = (TextView) v.findViewById(R.id.histFromToText);
            fromTo.setTextColor(Color.BLACK);
            TextView price = (TextView) v.findViewById(R.id.histFromToPrice);
            price.setTextColor(Color.BLACK);
            TextView driverName = (TextView) v.findViewById(R.id.histDriverName);
            driverName.setTextColor(Color.GRAY);
            TextView duration = (TextView) v.findViewById(R.id.histDurationText);
            duration.setTextColor(Color.BLACK);

            ImageView driverImage = (ImageView) v.findViewById(R.id.histDriverImage);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (histDateTime != null){
                histDateTime.setText(i.dateTime);
            }

            if (fromTo != null){
                fromTo.setText(i.fromTo);
            }

            if(price != null) {
                price.setText(i.price);
            }

            if(driverName != null){
                driverName.setText(i.driverName);
            }

            if(duration != null) {
                duration.setText(i.duration);
            }

            if (driverImage != null){
                String dImage = "http://" + address + ":8080/" +  i.driverImage+ ".png";
               // Log.d("HTTP", dImage);
                new DownloadImageTask(driverImage)
                        .execute(dImage);

            }


        }

        // the view must be returned to our activity
        return v;

    }



    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
