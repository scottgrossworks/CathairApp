package com.cathairapocalypse.app;


import android.graphics.Bitmap;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;



/**
 *
 */
public class CA_Fragment_1 extends CA_Fragment implements GestureDetector.OnDoubleTapListener {



    public static CA_Fragment_1 newInstance(CA_PagerAdapter adapter) {


        CA_Fragment_1 fragment = new CA_Fragment_1();
        fragment.mInitialized = false;
        fragment.setAdapter(adapter);

        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();

        View view = getView();
        if ( view == null) {
            android.util.Log.d("ERROR onResume()", "view is null");
            return;
        }
        view.setBackgroundColor(getResources().getColor(R.color.colorMedium));
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.ca_state_1, container, false);
        return view;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //android.util.Log.d("CA_Fragment 1 activityResult", Integer.toString(resultCode));
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_OPEN && resultCode == IMAGE_OPEN_SUCCESS) {

            Uri uri = data.getData();
            if (uri == null) return;
            addImage( uri );
            mAdapter.changeFooter(1);
        }
    }






        @Override
        public boolean onSingleTapConfirmed(MotionEvent e)
        {
            return true;
        }



        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mAdapter.CURRENT_STATE != 2) {
                mAdapter.changeState_2();

                try {
                    if ((mAdapter.project.mComp != null) && (mAdapter.project.mUri != null))
                        CA_ResMgr.saveToPhone(mAdapter.project.mComp, mAdapter.project.mUri);

                } catch (Exception ex) {
                    Toast.makeText(mContext, "Error Saving: " + ex.toString(), Toast.LENGTH_SHORT).show();
                }



            }

            return true;
        }


        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            if (mAdapter.CURRENT_STATE != 2) {
                mAdapter.changeState_2();


                try {
                    if ((mAdapter.project.mComp != null) && (mAdapter.project.mUri != null))
                        CA_ResMgr.saveToPhone(mAdapter.project.mComp, mAdapter.project.mUri);

                } catch (Exception ex) {
                    Toast.makeText(mContext, "Error Saving: " + ex.toString(), Toast.LENGTH_SHORT).show();
                }
            }


            return true;
    }














    protected boolean addImage(Uri uri) {

        View view = getView();
        if ( view == null) {
            return false;
        }

        BitmapDrawable bitmap = CA_ResMgr.loadImage(uri);
        if (bitmap == null) {
            //mAdapter.changeState_E("Cannot loadImage(): " + uri.toString());
            return false;
        }

        ViewGroup vg = (ViewGroup) view;
        if (! mInitialized) { /** First time adding an image */

            vg.removeAllViews();
            BitmapDrawable drawable = CA_ResMgr.getFrame();
            if (drawable == null) {
                String error = "I/O Thread did not load frame";
                android.util.Log.d("ERROR addImage()", error);
                mAdapter.changeState_E(error);
                return false;

            } else {
                ImageView iv = new ImageView(mContext);
                iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
                drawable.setFilterBitmap(true);
                drawable.setAlpha(255);
                iv.setImageDrawable(drawable);
                vg.addView(iv);
            }
            mAdapter.project.setStartImage(uri, bitmap);
        }


        /** add the new view to the group */
        CA_ImageView civ = new CA_ImageView(mContext);
        bitmap.setFilterBitmap(true);
        civ.setScaleType(ImageView.ScaleType.FIT_CENTER);
        civ.setOnDoubleTapListener(this);
        civ.setImageDrawable(bitmap);
        vg.addView(civ);
        vg.invalidate();

        /** 11/2016 -- only show the instructions once */
        if (! mInitialized) {
            Toast.makeText(mContext, "Double-Tap when done.", Toast.LENGTH_SHORT).show();
            mInitialized = true;
        }

        mAdapter.changeFooter(1);

        return true;
    }



    /**
     * Flatten the layers of the ViewGroup into the comp bitmap
     *
     */
    protected BitmapDrawable getCompBitmap() {

        /**
         * create full-size blank bitmap representing entire view
         * draw all layers to it just like on screen
         * then crop it down to frame (layer 0) dimensions at the end
         */
        ImageView view;
        Drawable drawable;
        ViewGroup vg = (ViewGroup) getView();
        int width = (int) vg.getMeasuredWidth();
        int height = (int) vg.getMeasuredHeight();


        /**
         * add the 'made with' footer
         */
        drawable = CA_ResMgr.getMadeWith();
        drawable.setFilterBitmap(true);
        view = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        view.setImageDrawable(drawable);
        vg.addView(view);


        view.measure(View.MeasureSpec.EXACTLY, View.MeasureSpec.EXACTLY);
        view.layout(0, 0, width, height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        int layers = vg.getChildCount();
        Matrix m, startMatrix = null;
        for (int i = 0; i < layers; i++) {

            view = (ImageView) vg.getChildAt(i);
            m = view.getImageMatrix();

            /** don't draw the frame, just capture the startMatrix */
            if (i == 0) {
                startMatrix = m;

                /** draw all other layers */
            } else {

                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache(true);
                canvas.setMatrix(m);
                canvas.save();
                drawable = view.getDrawable();
                drawable.setAlpha(255);
                drawable.draw(canvas);
                view.setDrawingCacheEnabled(false);
                canvas.restore();
            }
        }

        /**
         * remove the 'madeWith' layer
         */
        vg.removeViewAt(layers - 1);


        float[] n = new float[9];
        startMatrix.getValues(n);
        int frameX = (int) n[Matrix.MTRANS_X];
        int frameY = (int) n[Matrix.MTRANS_Y];
        width = (int) (CA_ResMgr.COMP_WIDTH * n[Matrix.MSCALE_X]);
        height = (int) (CA_ResMgr.COMP_HEIGHT * n[Matrix.MSCALE_Y]);

        Bitmap cropped = Bitmap.createBitmap(bitmap, frameX, frameY, width, height);
        BitmapDrawable image = new BitmapDrawable(mContext.getResources(), cropped);
        return image;
    }



    private void printMatrixInfo(int i, Matrix matrix) {
        float[] n = new float[9];
        matrix.getValues(n);
        Log.d("MATRIX for " + Integer.toString(i), "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " TransY: " + n[Matrix.MTRANS_Y]);
    }



    protected void clear() {

        mAdapter.clearProject();
        mInitialized = false;

        ViewGroup vg= (ViewGroup) getView();

        vg.removeAllViews();
        vg.invalidate();
    }


    protected void clearTop() {

        View view = getView();
        ViewGroup vg = (ViewGroup) view;
        int num = vg.getChildCount();

        /** Layer 0 is the frame */
        if (num > 1) {
            vg.removeViewAt( num - 1 );
        }
    }


    /**
     * take the top image and flip it horizontal (mirror image)
     *
     */
    protected void mirror() {

        ViewGroup vg = (ViewGroup) getView();
        int num = vg.getChildCount();

        if (num < 2)
            return; // ERROR

        ImageView top = (ImageView) vg.getChildAt(num - 1);
        BitmapDrawable drawable = (BitmapDrawable) top.getDrawable();
        Bitmap topBitmap = drawable.getBitmap();
        Matrix topMatrix = top.getImageMatrix();



        // For horizontal flipping: [ x = x * -1, y = y ]
        topMatrix.postScale(-1.0f, 1.0f);


        Bitmap newBitmap = Bitmap.createBitmap(topBitmap, 0, 0, topBitmap.getWidth(),
                topBitmap.getHeight(), topMatrix, true);

        //top.setImageMatrix(topMatrix);
        top.setImageDrawable(new BitmapDrawable(mContext.getResources(), newBitmap) );

        vg.invalidate();

        newBitmap = topBitmap = null;
        System.gc();
    }






    private void printMatrixInfo(Matrix m) {
        float[] n = new float[9];
        m.getValues(n);
        Log.d("MATRIX", "Scale: " + n[Matrix.MSCALE_X] + " TransX: " + n[Matrix.MTRANS_X] + " TransY: " + n[Matrix.MTRANS_Y]);
    }


    protected void flip() {

        ViewGroup vg = (ViewGroup) getView();
        int num = vg.getChildCount();

        /**
         * flip the top two layers
         * Layer 0 is the frame
         *
         * pos0 -- pos1 -- pos2   where 1 and 2 can be anywhere -->
         */
        if (num > 2){
            View v1, v2;
            Matrix m1, m2;
            int pos2 = num - 1;
            int pos1 = num - 2;

            v1 = vg.getChildAt( pos1 );
            m1 = ((ImageView) v1).getImageMatrix();

            //printMatrixInfo(m1);

            //v2 = vg.getChildAt( pos2 );
            //m2 = ((ImageView) v2).getImageMatrix();

            vg.removeViewAt( pos1 );

            // v2 = vg.getChildAt( pos1 ); // v2 slides down to pos1
            //((ImageView) v2).setImageMatrix(m2);

            vg.addView(v1, pos2);
            ((ImageView) v1).setImageMatrix(m1);
            vg.invalidate();
        }

        System.gc();
    }


    /**
     * Rotate the bitmap 90 degrees clockwise
     *
     */
    protected void rotate90() {

        ViewGroup vg = (ViewGroup) getView();
        int num = vg.getChildCount();

        if (num < 2)
            return; // ERROR

        ImageView top = (ImageView) vg.getChildAt(num - 1);
        BitmapDrawable drawable = (BitmapDrawable) top.getDrawable();
        Bitmap topBitmap = drawable.getBitmap();
        Matrix topMatrix = top.getImageMatrix();

        Matrix matrix = new Matrix(topMatrix);
        matrix.postRotate((float) 90);
        Bitmap newBitmap = Bitmap.createBitmap(topBitmap, 0, 0, topBitmap.getWidth(), topBitmap.getHeight(), matrix, true);

        //top.setImageMatrix(topMatrix);
        top.setImageDrawable(new BitmapDrawable(mContext.getResources(), newBitmap) );

        vg.invalidate();

        newBitmap = topBitmap = null;
        System.gc();
    }









    /** has ADDITIONAL layers -- CathairLayers */
    protected boolean hasCathair() {

        View view = getView();
        ViewGroup vg = (ViewGroup) view;
        int num = vg.getChildCount();

        /**
         *  Layer 0: Frame
         *  Layer 1: Start image
         *  Layer 2: !!!!!!
         */
        if (num > 2) {
            return true;
        } else {
            return false;
        }
    }


} // The End.

