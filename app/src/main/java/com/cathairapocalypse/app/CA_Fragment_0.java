package com.cathairapocalypse.app;



import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;



public class CA_Fragment_0 extends CA_Fragment {


    public static CA_Fragment_0 newInstance(CA_PagerAdapter adapter) {


        CA_Fragment_0 fragment = new CA_Fragment_0();

        /**
         Bundle args = new Bundle();
         args.putInt(ARG_PAGE, page);
         fragment.setArguments(args);
         */


        fragment.setAdapter(adapter);
        fragment.mInitialized = true;
        return fragment;
    }



    protected void setErrorView(String msg) {

        View view = getView();
        if ( view == null) {
            android.util.Log.d("ERROR setErrorView", "getView() returns null");
            return;
        }

        ViewGroup vg = (ViewGroup) view;

        if (mInitialized) {
            // viewgroup contains the default view
            vg.removeViewAt(0);
        }

        TextView textView = new TextView(mContext);
        textView.setGravity(Gravity.CENTER);

        Resources res = getResources();

        textView.setText( msg );
        textView.setText( msg );
        textView.setTextColor( res.getColor(R.color.colorAccent) );
        textView.setBackgroundColor( res.getColor(R.color.colorMedium ));

        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        // textView.setAllCaps(true);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        textView.setPadding(10, 10, 10, 10);
        //textView.setBackgroundResource(outValue.resourceId);

        textView.invalidate();
        vg.addView(textView, 0);
        vg.invalidate();
    }



    @Override
    public void onResume() {
        super.onResume();

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        if (dm != null) {
            mHeight = dm.heightPixels;
            mWidth = dm.widthPixels;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.ca_state_0,container,false);
        view.setOnClickListener(new CA_OpenPicListener());

        return view;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        // android.util.Log.d("CA_Fragment 0 activityResult", Integer.toString(resultCode));

        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == IMAGE_OPEN_SUCCESS) {

            Uri uri = data.getData();
            if (uri == null) return;
            mAdapter.changeState_1( uri );
        }
    }




} // The End.