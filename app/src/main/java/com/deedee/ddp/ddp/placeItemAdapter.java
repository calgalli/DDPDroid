package com.deedee.ddp.ddp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cake on 1/23/16 AD.
 */
public class placeItemAdapter extends ArrayAdapter<placeItem> {
    // declaring our ArrayList of items
    private ArrayList<placeItem> objects;


    Context context;
    /* here we must override the constructor for ArrayAdapter
    * the only variable we care about now is ArrayList<Item> objects,
    * because it is the list of objects we want to display.
    */
    public placeItemAdapter(Context context, int textViewResourceId, ArrayList<placeItem> objects) {
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
            v = inflater.inflate(R.layout.place_cell, null);
        }

		/*
		 * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list. (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
        placeItem i = objects.get(position);
        // ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();


        if (i != null) {

            // This is how you obtain a reference to the TextViews.
            // These TextViews are created in the XML files we defined.

            TextView tt = (TextView) v.findViewById(R.id.textView1);
            tt.setTextColor(Color.BLACK);
            ImageView ttd = (ImageView) v.findViewById(R.id.imageView1);

            // check to see if each individual textview is null.
            // if not, assign some text!
            if (tt != null){
                tt.setText(i.pname);
            }

           // var placeImage : [String] = ["places.png", "beach.png", "hotel.png", "shopping.png", "foodAndDrink.png","transportation.png","emergency.png", "places.png","rentCarOrBike.png","niteSpot.png","emergency.png","places.png","bookmarkIcon.png"]


            if (ttd != null){

                switch (i.imageID) {
                    case 1:  ttd.setImageResource(R.drawable.places);
                        break;
                    case 2:  ttd.setImageResource(R.drawable.beach);
                        break;
                    case 3:  ttd.setImageResource(R.drawable.hotel);
                        break;
                    case 4: ttd.setImageResource(R.drawable.shopping);
                        break;
                    case 5: ttd.setImageResource(R.drawable.food_and_drink);
                        break;
                    case 6: ttd.setImageResource(R.drawable.transportation);
                        break;
                    case 7: ttd.setImageResource(R.drawable.emergency);
                        break;
                    case 8: ttd.setImageResource(R.drawable.places);
                        break;
                    case 9: ttd.setImageResource(R.drawable.rent_car_bike);
                        break;
                    case 10: ttd.setImageResource(R.drawable.nite_spot);
                        break;
                    case 11: ttd.setImageResource(R.drawable.emergency);
                        break;
                    case 12: ttd.setImageResource(R.drawable.places);
                        break;
                    case 13: ttd.setImageResource(R.drawable.bookmark_icon);
                        break;
                    default: ttd.setImageResource(R.drawable.bookmark_icon);
                        break;
                }



            }


        }

        // the view must be returned to our activity
        return v;

    }
}
