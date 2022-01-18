package com.cathairapocalypse.app;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;




/**
 *
 */
public class CA_PagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {


    public static final int NUM_STATES = 3;
    public static int CURRENT_STATE = 0;
    public static Project project;


    public static boolean IN_ERROR_STATE = false;
    public static CA_Fragment_0 FRAGMENT_0;
    public static CA_Fragment_1 FRAGMENT_1;
    public static CA_Fragment_2 FRAGMENT_2;

    protected FragmentManager mFragManager;
    protected Context mContext;
    protected ViewPager mPager;
    protected Toolbar mFooter;
    protected CA_SlidingTabLayout mTabs;

    protected boolean mInitialized = false;



    public CA_PagerAdapter(Context context, CA_SlidingTabLayout tabs, FragmentManager fm, ViewPager viewPager, Toolbar footer) {

        super(fm);

        mContext = context;

        mTabs = tabs;
        mFragManager = fm;
        mPager = viewPager;

        mFooter = footer;

        if (project == null)
            project = new Project();
    }



    @Override
    public void onPageSelected(int position) {
        // android.util.Log.d("PAGER ADAPTER page selected=", Integer.toString(position));

        if (position == CURRENT_STATE) return;

        switch (position) {

            case 0:
                changeState_0();
                break;

            case 1:
                changeState_1(project.mUri);
                break;

            default:
            case 2:
                changeState_2();
                break;
        }
    }



    @Override
    public void onPageScrollStateChanged(int state) {
        // android.util.Log.d("STATE CHANGED", Integer.toString(state));
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }


    @Override
    public final int getCount() {
        return NUM_STATES;
    }


    @Override
    public int getItemPosition(Object object) {

        int result = POSITION_NONE;

        if (object instanceof Fragment) {

            java.util.List list = mFragManager.getFragments();
            if (list != null) result = list.indexOf(object);
        }

        return result;
    }


    @Override
    public Fragment getItem(int position) {

        if (!mInitialized) {
            FRAGMENT_0 = CA_Fragment_0.newInstance(this);
            FRAGMENT_1 = CA_Fragment_1.newInstance(this);
            FRAGMENT_2 = CA_Fragment_2.newInstance(this);
            mInitialized = true;
        }

        Fragment frag;
        switch (position)  {
            case 0:
                frag = FRAGMENT_0;
                break;

            case 1:
                frag = FRAGMENT_1;
                break;

            case 2:default:
                // frag = CA_Fragment_2.newInstance(this);
                frag = FRAGMENT_2;
                break;

        }
        return frag;
    }







    @Override
    public CharSequence getPageTitle(int position) {

        Drawable image = CA_ResMgr.getButton(position, position == CURRENT_STATE);

        SpannableString sb = CA_ResMgr.charsFromDrawable(image);

        return sb;
    }





    /**
     *
     * Change State ERROR
     *
     */
    protected void changeState_E(String msg) {

        IN_ERROR_STATE = true;
        android.util.Log.d("ERROR: ", msg);

        FRAGMENT_0.setErrorView(msg);

        CURRENT_STATE = 0;
        mPager.setCurrentItem( CURRENT_STATE );
        mPager.invalidate();
        mPager.setEnabled(false);
        mTabs.enableTabs(false, false, false);

        changeFooter(0);
    }




    /**
     *
     * Change State  ---> 0
     *
     */
    protected void changeState_0() {


        CURRENT_STATE = 0;

        if (mPager.getCurrentItem() != CURRENT_STATE) {
            mPager.setCurrentItem(CURRENT_STATE);
        }

        project.recycle();

        mPager.setEnabled(false);

        if (mInitialized) {
            FRAGMENT_1.clear();
            FRAGMENT_2.clear();
            changeFooter(0); // will only work when everything is visible
        }

        mTabs.enableTabs(true, false, false);
    }


    /**
     *
     * Change State from 0 ---> 1
     *
     */
    protected void changeState_1(Uri uri) {

        if (uri == null) {
            //android.util.Log.d("CHANGE STATE ERROR", "uri == null");
            changeState_E("changeState_1(null)");
            return;
        }

        boolean success = true;

        if (CURRENT_STATE == 0) {
            project.mUri = uri;
            success = FRAGMENT_1.addImage(uri); // calls changeFooter(1)
        } else {
            changeFooter(1);
        }

        if (success) {
            CURRENT_STATE = 1;

            mPager.setCurrentItem(CURRENT_STATE);
            mPager.setEnabled(false);

            mTabs.enableTabs(true, true, false);

        } else {
            changeState_E("Error loading image: " + uri.toString());
        }


    }




    /**
     *
     * Change State  ---> 2
     *
     */
    protected void changeState_2() {

        BitmapDrawable comp = null;
        try {
            comp = FRAGMENT_1.getCompBitmap();
            if (comp == null)
                throw new Exception("getCompBitmap() = null");
        } catch (Exception e) {
            changeState_E("Cannot create comp: " + e.toString());
            return;
        }

        project.mComp = comp;
        FRAGMENT_2.addImage( project.mComp );
        CURRENT_STATE = 2;

        if (mPager.getCurrentItem() != CURRENT_STATE) {
            mPager.setCurrentItem(CURRENT_STATE);
        }

       mPager.setEnabled(true);
       mTabs.enableTabs(true, true, true);

       changeFooter(2);
    }





    protected void changeFooter(int state) {

        Menu footerMenu = mFooter.getMenu();
        MenuItem action_cat = (MenuItem) footerMenu.findItem(R.id.action_cat);
        MenuItem action_add = (MenuItem) footerMenu.findItem(R.id.action_add);
        MenuItem action_rem = (MenuItem) footerMenu.findItem(R.id.action_remove);
        MenuItem action_flip = (MenuItem) footerMenu.findItem(R.id.action_flip);
        MenuItem action_mirror = (MenuItem) footerMenu.findItem(R.id.action_mirror);
        MenuItem action_rotate90 = (MenuItem) footerMenu.findItem(R.id.action_rotate90);


        switch (state) {

            case 0:

                if (IN_ERROR_STATE) {

                    mFooter.setTitle("Cat-scratched!");

                    try {
                        action_cat.setVisible(false);
                        action_add.setVisible(false);
                        action_rem.setVisible(false);
                        action_mirror.setVisible(false);
                        action_flip.setVisible(false);
                        action_rotate90.setVisible(false);
                    } catch (Exception e) {
                        String s = e.toString();
                        mFooter.setTitle(s);
                    }

                } else {

                    mFooter.setTitle("Open a Cat Pic");
                    action_cat.setVisible(true);
                    action_add.setVisible(false);
                    action_rem.setVisible(false);
                    action_mirror.setVisible(false);
                    action_flip.setVisible(false);
                    action_rotate90.setVisible(false);
                }
                break;



            case 1:
                boolean hasLayers = FRAGMENT_1.hasCathair();

                if (hasLayers){
                    mFooter.setTitle("");
                 } else{

                    /**
                    if (CURRENT_STATE == 0) {
                        String dir = CA_ResMgr.getCathairDirPath();
                        Toast.makeText(mContext, "Working Folder: " + dir, Toast.LENGTH_LONG).show();
                    }
                     */

                    mFooter.setTitle("Add #Cathair");
                }

                action_cat.setVisible(false);
                action_add.setVisible(true);
                action_mirror.setVisible(true);
                action_rem.setVisible(hasLayers);
                action_flip.setVisible(hasLayers);
                action_rotate90.setVisible(true); /** allow rotation of cat image */

                break;



            case 2: default:
                mFooter.setTitle("Save and Share");
                action_cat.setVisible(false);
                action_add.setVisible(false);
                action_rem.setVisible(false);
                action_mirror.setVisible(false);
                action_flip.setVisible(false);
                action_rotate90.setVisible(false);

                break;
        }

        mFooter.invalidate();
    }


    protected void clearProject() {
        try {
            project.recycle();
        } catch (Exception e) {
            project = new Project();
        }
    }


    /**
     * Represents the current state of the Cathair
     */

    protected final class Project {

        protected Uri mUri;
        protected BitmapDrawable mStartImage;
        protected BitmapDrawable mComp;

        protected String mFilename;
        protected String mFullpath;

        protected Boolean DONE = false;

        public Project() {
        }


        public boolean hasComp() {
            return (mComp != null);
        }

        public void setStartImage(Uri uri, BitmapDrawable d) {

            mUri = uri;
            mStartImage = d;
        }

        public void recycle() {

        /** don't recycle the frame */
            try {
                if (mStartImage != null) mStartImage.getBitmap().recycle();
                if (mComp != null) mComp.getBitmap().recycle();
            } catch (Exception e) {

            }

            mUri = null;
            mStartImage = null;
            mComp = null;
            mFilename = null;
            mFullpath = null;

            System.gc();
        }

    }



} // The End.