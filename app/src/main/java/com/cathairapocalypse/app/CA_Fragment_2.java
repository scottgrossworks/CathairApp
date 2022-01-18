package com.cathairapocalypse.app;

import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;



/**
 *
 */
public class CA_Fragment_2 extends CA_Fragment implements View.OnClickListener {


    public static CA_Fragment_2 newInstance(CA_PagerAdapter adapter) {


        CA_Fragment_2 fragment = new CA_Fragment_2();

        /**
         Bundle args = new Bundle();
         args.putInt(ARG_PAGE, page);
         fragment.setArguments(args);
         */


        fragment.setAdapter(adapter);
        return fragment;
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.ca_state_2, container, false);


        try {
            if (view == null) throw new Exception("cannot inflate ca_state_2");


            ViewGroup vg = (ViewGroup) view;
            ImageButton ib = (ImageButton) vg.findViewById(R.id.ca_tatbutton);
            ib.setOnClickListener(this);
            disableTouchTheft(ib);

            ib = (ImageButton) vg.findViewById(R.id.ca_sharebutton);
            ib.setOnClickListener(this);
            disableTouchTheft(ib);

            TextView tv = (TextView) vg.findViewById(R.id.ca_tatbutton_txt);
            tv.setOnClickListener(this);
            disableTouchTheft(tv);

            tv = (TextView) vg.findViewById(R.id.ca_sharebutton_txt);
            tv.setOnClickListener(this);
            disableTouchTheft(tv);


        } catch (Exception e) {

            mAdapter.changeState_E("Error in Fragment2.createView(): " + e.toString());
            return null;
        }

        return view;
    }



    protected void disableTouchTheft(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                onClick(view);
                return false;
            }
        });
    }


    /**
     * 9/2016
     * Instead of a Save Button -- automatically save on entry and show two buttons
     *
     * Share
     * Make a Tat
     *
     */
    @Override
    public void onClick(View view) {

        if (view == null) return;

        int id = view.getId();

        /**
         *  9/2016
         *  Send the user to the website to make a TAT
         */
         if (id == R.id.ca_tatbutton || id == R.id.ca_tatbutton_txt) {

             try {

                 try {
                     String url = getResources().getString(R.string.goto_url);
                     Uri uri = Uri.parse(url);
                     startActivity(new Intent(Intent.ACTION_VIEW, uri));

                 } catch (Exception e) {
                     android.util.Log.d("Error going to website", e.toString());
                     Toast.makeText(mContext, "Cannot find a web browser.",  Toast.LENGTH_LONG).show();
                     //mAdapter.changeState_E("Cannot load web browser.");
                 }



            } catch (Exception e) {
                Toast.makeText(mContext, "Error launching browser: " + e.toString(), Toast.LENGTH_LONG).show();
            }


        } else if ( id == R.id.ca_sharebutton  || id == R.id.ca_sharebutton_txt ) {

             /**
              * The Save activity may not be finished here
              * temporary fix -- user will tap again
              */
            if ( ! CA_PagerAdapter.project.DONE ) {
                // Toast.makeText(mContext, "Still Saving...", Toast.LENGTH_SHORT).show();

                final Toast toast = Toast.makeText(mContext, "Still Saving...", Toast.LENGTH_SHORT);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 250);



                return;
            }

            try {
                CA_ResMgr.launchShareActivity();
            } catch (Exception e) {
                Toast.makeText(mContext, "Error Sharing: " + e.toString(), Toast.LENGTH_LONG).show();
            }



        } else {

            Toast.makeText(mContext, "ERROR: Unknown Command id=" + Integer.toString(id), Toast.LENGTH_LONG).show();
            // unknown
        }
    }







    protected void clear() {

        try {
            View view = getView();
            if (view == null) return;
            ViewGroup vg = (ViewGroup) view;
            if (vg == null) return;
            vg.removeAllViews();
            vg.invalidate();
        } catch (Exception e) {
            android.util.Log.d("Fragment 2 clear() ", e.toString());
        }
    }





    protected void addImage(BitmapDrawable bitmap) {

        if ( bitmap == null) {
            android.util.Log.d("Fragment_2 addImage()", "bitmap is null");
            return;
        }

        View view = getView();
        if ( view == null) {
            android.util.Log.d("Fragment_2 addImage()", "View is null");
            return;
        }

        ViewGroup vg = (ViewGroup) view;
        // viewgroup contains the default view or a previous cathair

        try {
            ImageView thumb;
            thumb = (ImageView) vg.findViewById(R.id.ca_thumbnail);
            if (thumb == null) throw new Exception("Cannot find ca_thumbnail");

            int w = thumb.getWidth();
            int h = thumb.getHeight();
            Bitmap background = Bitmap.createScaledBitmap(bitmap.getBitmap(), w, h, true);
            thumb.setImageBitmap(background);
            vg.invalidate();

        } catch (Exception e) {
            mAdapter.changeState_E("Error Fragment2.addImage(): " + e.toString());
        }
    }







} // The End.
