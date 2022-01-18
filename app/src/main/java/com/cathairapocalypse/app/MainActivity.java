package com.cathairapocalypse.app;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity implements MenuItem.OnMenuItemClickListener {

    CA_PagerAdapter mAdapter;
    Toolbar mHeader, mFooter;
    CA_ViewPager mPager;

    CA_SlidingTabLayout mTabs;



    protected boolean mHeaderReady, mFooterReady;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Context context = getApplicationContext();
        ContentResolver resolver = getContentResolver();
        CA_ResMgr.initialize( this, context, resolver );

        mHeader = (Toolbar) findViewById(R.id.ca_header); // Attaching the layout to the toolbar object
        mFooter = (Toolbar) findViewById(R.id.ca_footer); // Attaching the layout to the toolbar object

        setSupportActionBar(mFooter);  // Setting toolbar as the ActionBar with setSupportActionBar() call

        mHeaderReady = mFooterReady = false;



        // Assiging the Sliding Tab Layout View
        mTabs = (CA_SlidingTabLayout) findViewById(R.id.ca_tabs);
        mTabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width

        // Setting Custom Color for the Scroll bar indicator of the Tab View

        mTabs.setCustomTabColorizer(new CA_SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                int c = getResources().getColor(R.color.colorAccent);
                return c;
            }
        });


        // Assigning ViewPager View and setting the adapter
        mPager = (CA_ViewPager) findViewById(R.id.ca_pager);
        // Creating The ViewPagerAdapter and Passing Fragment Manager, Titles fot the Tabs and Number Of Tabs.
        mAdapter = new CA_PagerAdapter( context, mTabs, getSupportFragmentManager(), mPager, mFooter);
        mPager.setAdapter(mAdapter);
        mTabs.setAdapter(mAdapter);

        // Setting the ViewPager For the SlidingTabsLayout
        mTabs.setViewPager(mPager);


        mAdapter.changeState_0();
    }


    @Override
    protected void onResume() {
        super.onResume();

      //  mTabs.invalidate();

        if(! mHeaderReady) configureHeader();

        if(! mFooterReady) configureFooter();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (! mFooterReady) configureFooter();
        return true;
    }


    @Override
    public boolean onMenuItemClick(MenuItem item) {

        boolean success = false;

        int id = item.getItemId();
        if (id == R.id.action_web) {

            try {
                String url = getResources().getString(R.string.goto_url);
                Uri uri = Uri.parse(url);
                startActivity(new Intent(Intent.ACTION_VIEW, uri));

            } catch (Exception e) {
                android.util.Log.d("Error going to website", e.toString());
                Toast.makeText(this, "Cannot find a web browser.",  Toast.LENGTH_LONG).show();
                //mAdapter.changeState_E("Cannot load web browser.");
            }

            success = true;
        }

        return success;
    }



    /**
     *
     *
     * Addcat and Cathair buttons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       super.onOptionsItemSelected(item);
        boolean success = false;
        int id = item.getItemId();

        if (id == R.id.action_cat) {

            CA_PagerAdapter.FRAGMENT_0.openImageDialog();
            success = true;



        } else if (id == R.id.action_add) {

            CA_PagerAdapter.FRAGMENT_1.openImageDialog();
            success = true;


        } else if (id == R.id.action_remove) {

            CA_PagerAdapter.FRAGMENT_1.clearTop();
            if (!CA_PagerAdapter.FRAGMENT_1.hasCathair())
                mAdapter.changeFooter(1);
            success = true;


        } else if (id == R.id.action_mirror) {

            CA_PagerAdapter.FRAGMENT_1.mirror();
            success = true;



        } else if (id == R.id.action_flip) {

            CA_PagerAdapter.FRAGMENT_1.flip();
            success = true;


        } else if (id == R.id.action_rotate90) {

                CA_PagerAdapter.FRAGMENT_1.rotate90();
                success = true;


        } else {
            success = false;
        }
        return success;
    }


protected CA_PagerAdapter getAdapter() {
    return mAdapter;
}



protected void configureHeader() {

    Drawable logo = CA_ResMgr.getLogo();
    mHeader.setLogo( logo );
    mHeader.setLogoDescription("@strings/logo_description");


    mHeader.inflateMenu(R.menu.menu_top);
    Menu menu = mHeader.getMenu();
    MenuItem webButton = menu.getItem(0);
    webButton.setOnMenuItemClickListener(this);
    mHeaderReady = true;
}


    protected void configureFooter() {

        int views = mFooter.getChildCount();
        if (views != 2) return; // not ready yet
        Menu footerMenu = mFooter.getMenu();
        if (footerMenu == null || ! footerMenu.hasVisibleItems()) return; // not ready yet

        mAdapter.changeFooter(0);
        mFooterReady = true;
    }









} // The End.