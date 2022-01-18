package com.cathairapocalypse.app;


import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.InputDevice;
import android.view.MotionEvent;


public class CA_ViewPager extends ViewPager {

    public CA_ViewPager(Context context) {
        super(context);
    }

     public CA_ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String toString() { return "CA_ViewPager"; }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isEnabled()) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        try {
            if (isEnabled()) {
               /**  if (CA_PagerAdapter.CURRENT_STATE == 2) { */
                return super.onInterceptTouchEvent(event);
            }

        } catch (Exception e) {
            return false;
        }
        return false;
    }


} // The End.
