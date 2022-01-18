package com.cathairapocalypse.app;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;

import android.graphics.Paint;
import android.graphics.Path;

import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import android.util.AttributeSet;
import android.util.DisplayMetrics;

import android.util.TypedValue;
import android.view.Gravity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * To be used with ViewPager to provide a tab indicator component which give constant feedback as to
 * the user's scroll progress.
 * <p>
 * To use the component, simply add it to your view hierarchy. Then in your
 * {@link android.app.Activity} or {@link android.support.v4.app.Fragment} call
 * {@link #setViewPager(ViewPager)} providing it the ViewPager this layout is being used for.
 * <p>
 * The colors can be customized in two ways. The first and simplest is to provide an array of colors
 * via {@link #setSelectedIndicatorColors(int...)}. The
 * alternative is via the {@link TabColorizer} interface which provides you complete control over
 * which color is used for any individual position.

 */
public class CA_SlidingTabLayout extends HorizontalScrollView {
    /**
     * Allows complete control over the colors drawn in the tab layout. Set with
     * {@link #setCustomTabColorizer(TabColorizer)}.
     */
    public interface TabColorizer {

        /**
         * @return return the color of the indicator used when {@code position} is selected.
         */
        int getIndicatorColor(int position);

    }

    protected static final int NUGGET_RATIO = 6;
    protected static final int TITLE_OFFSET_DIPS = 24;

    private int mTitleOffset;
    private boolean mDistributeEvenly;

    private LinearLayout.LayoutParams DEFAULT_LAYOUT_PARAMS;

    private ViewPager mViewPager;
    private Resources mResources;
    private Context mContext;

    private ViewPager.OnPageChangeListener mViewPagerPageChangeListener;  // CA_PagerAdapter
    // private CA_PagerAdapter mAdapter;

    private CA_SlidingTabStrip mTabStrip;

    public CA_SlidingTabLayout(Context context) {
        this(context, null);
    }

    public CA_SlidingTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CA_SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Disable the Scroll Bar
        setHorizontalScrollBarEnabled(false);
        // Make sure that the Tab Strips fills this View
        setFillViewport(true);

        mContext = context;
        mResources = getResources();
        mTitleOffset = (int) (TITLE_OFFSET_DIPS * mResources.getDisplayMetrics().density);


        DEFAULT_LAYOUT_PARAMS = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                                                              ViewGroup.LayoutParams.WRAP_CONTENT);

        mTabStrip = new CA_SlidingTabStrip(context);
        addView(mTabStrip, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    }

    /**
     * Set the custom {@link TabColorizer} to be used.
     *
     * If you only require simple custmisation then you can use
     * {@link #setSelectedIndicatorColors(int...)} to achieve
     * similar effects.
     */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }

    public void setDistributeEvenly(boolean distributeEvenly) {
        mDistributeEvenly = distributeEvenly;
    }

    /**
     * Sets the colors to be used for indicating the selected tab. These colors are treated as a
     * circular array. Providing one color will mean that all tabs are indicated with the same color.
     */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }





    protected void setAdapter(CA_PagerAdapter cap) {
        setOnPageChangeListener(cap);
    }


    /**
     * Set the {@link ViewPager.OnPageChangeListener}. When using  SlidingTabLayout} you are
     * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
     * that the layout can update it's scroll position correctly.
     *
     * @see ViewPager#setOnPageChangeListener(ViewPager.OnPageChangeListener)
     */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mViewPagerPageChangeListener = listener;
        //mAdapter
      }


    /**
     * Sets the associated view pager. Note that the assumption here is that the pager content
     * (number of tabs and tab titles) does not change after this call has been made.
     */
    public void setViewPager(ViewPager viewPager) {
        mTabStrip.removeAllViews();

        mViewPager = viewPager;
        if (viewPager != null) {

            InternalViewPagerListener pageChangeListener = new InternalViewPagerListener();
            viewPager.setOnPageChangeListener( pageChangeListener );

            populateTabStrip();
        }
    }


    /**
     * Create a default view to be used for tabs. This is called if a custom tab view is not set via

    protected TextView createDefaultTabView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TypedValue outValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground,
                outValue, true);
        textView.setBackgroundResource(outValue.resourceId);

        //textView.setBackgroundResource( R.color.colorPrimaryDark );
        textView.setAllCaps(true);



        float dps = mResources.getDimension(R.dimen.tabs_padding);
        int padding = (int) (dps * mResources.getDisplayMetrics().density);
        textView.setPadding(padding, padding, padding, padding);

        return textView;
    }
*/



    private void populateTabStrip() {
        final PagerAdapter adapter = mViewPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();


        View tabView = null;
        int count = adapter.getCount();


        for (int i = 0; i < count; i++) {

            tabView = new TabView(mContext, i);

            if (mDistributeEvenly) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                lp.width = 0;
                lp.weight = 1;
            }

            tabView.setOnClickListener(tabClickListener);
            tabView.setClickable( i == CA_PagerAdapter.CURRENT_STATE );

            mTabStrip.addView(tabView);
        }
    }


    protected void enableTabs(boolean tab0, boolean tab1, boolean tab2) {

        View tab;

        tab = mTabStrip.getChildAt(0);
        tab.setClickable(tab0);

        tab = mTabStrip.getChildAt(1);
        tab.setClickable(tab1);

        tab = mTabStrip.getChildAt(2);
        tab.setClickable(tab2);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (mViewPager != null) {
            scrollToTab(mViewPager.getCurrentItem(), 0);
        }
    }

    protected void scrollToTab(int tabIndex, int positionOffset) {
        final int tabStripChildCount = mTabStrip.getChildCount();
        if (tabStripChildCount == 0 || tabIndex < 0 || tabIndex >= tabStripChildCount) {
            return;
        }

        View tab;
        for (int i = 0; i < tabStripChildCount; i++) {

            tab = mTabStrip.getChildAt(i);
            if (tab == null) return;

            if (i == tabIndex) {
                int targetScrollX = tab.getLeft() + positionOffset;

                if (tabIndex > 0 || positionOffset > 0) {
                    // If we're not at the first child and are mid-scroll, make sure we obey the offset
                    targetScrollX -= mTitleOffset;
                }

                scrollTo(targetScrollX, 0);
            }
            tab.invalidate();
        }
    }








    protected class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            int tabStripChildCount = mTabStrip.getChildCount();
            if ((tabStripChildCount == 0) || (position < 0) || (position >= tabStripChildCount)) {
                return;
            }

            mTabStrip.onViewPagerPageChanged(position, positionOffset);

            View selectedTitle = mTabStrip.getChildAt(position);
            int extraOffset = (selectedTitle != null)
                    ? (int) (positionOffset * selectedTitle.getWidth())
                    : 0;
            scrollToTab(position, extraOffset);

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            mScrollState = state;

            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        /**

        @Override
        public void onPageSelected(int position) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(position, 0f);
                scrollToTab(position, 0);
            }
            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                mTabStrip.getChildAt(i).setSelected(position == i);
            }
            if (mViewPagerPageChangeListener != null) {
                mViewPagerPageChangeListener.onPageSelected(position);
            }
        }

    }

*/

    @Override
    public void onPageSelected(int position) {

     if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
            mTabStrip.onViewPagerPageChanged(position, 0f);
            scrollToTab(position, 0);
        }

        // notify the viewPager that page(position) has been selected
        if (mViewPagerPageChangeListener != null) {
            mViewPagerPageChangeListener.onPageSelected(position);
            }
        }
    }





        private class TabClickListener implements View.OnClickListener {


            @Override
            public void onClick(View clicked) {
                int n = mTabStrip.getChildCount();
                for (int i = 0; i < n; i++) {

                    View child = mTabStrip.getChildAt(i);

                    if (child == clicked) {
                        mViewPager.setCurrentItem(i);
                        child.setSelected(true);
                    } else {
                        child.setSelected(false);
                    }
                }
            }
        }




    protected class TabView extends TextView {

         private int myPos;
        private float marginTop;

        public TabView(Context context, int pos) {
            super(context);

            myPos = pos;
            DisplayMetrics metrics = mResources.getDisplayMetrics();
            marginTop = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                    CA_SlidingTabStrip.SELECTED_INDICATOR_THICKNESS_DIPS,
                                                        metrics);

            float pad = mResources.getDimension(R.dimen.tabs_padding);
            int padding = (int) ((float) pad * metrics.density);
            setPadding(padding, padding, padding, padding);
            setGravity(Gravity.CENTER);

            setLayoutParams(DEFAULT_LAYOUT_PARAMS);
        }




        @Override
        public void setOnClickListener(View.OnClickListener listener) {

            super.setOnClickListener(listener);
        }

        @Override
        public void setSelected(boolean b) {
            super.setSelected(b);
            invalidate();
        }



        @Override
        protected void onDraw(Canvas canvas) {

            float height = getHeight();
            float width = getWidth();

            boolean selected = myPos == CA_PagerAdapter.CURRENT_STATE;
            Drawable image = CA_ResMgr.getButton(myPos, 0, 0, selected);
            image.setAlpha(55);

            int color = CA_ResMgr.COLOR_0;


            if (selected) {

                color = CA_ResMgr.COLOR_3;
                image.setAlpha(255);


            } else if (myPos == 0) {

                if (CA_PagerAdapter.CURRENT_STATE ==1) {

                    if (CA_PagerAdapter.project.hasComp()) {
                        color = CA_ResMgr.COLOR_1;
                        image.setAlpha(75);

                    } else {
                        color = CA_ResMgr.COLOR_2;
                        image.setAlpha(150);
                    }

                } else {
                    color = CA_ResMgr.COLOR_1;
                    image.setAlpha(75);
                }



            } else if (myPos == 1) {

                if (CA_PagerAdapter.CURRENT_STATE > 0) {
                    color = CA_ResMgr.COLOR_2;
                    image.setAlpha(150);
                }

            } else { // if (myPos == 2) {

                if (CA_PagerAdapter.CURRENT_STATE > 0) {
                    if (CA_PagerAdapter.project.hasComp()) {
                        color = CA_ResMgr.COLOR_2;
                        image.setAlpha(150);
                    }
                }

            }

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(color);
                paint.setAntiAlias(true);

            float  w = width / NUGGET_RATIO;
            canvas.drawRect(w, marginTop, width - w + 1, height, paint);

            if (myPos < 2) {
                Path path = new Path();
                path.setFillType(Path.FillType.EVEN_ODD);
                path.moveTo(width - w, marginTop);
                path.lineTo(width, (height - marginTop) / 2);
                path.lineTo(width - w, height);
                path.close();
                canvas.drawPath(path, paint);
            }


            int imgW = image.getIntrinsicWidth();
            int imgH = image.getIntrinsicHeight();
            float left = (width / 2) - (imgW / 2);
            float top = (height / 2) - (imgH / 2);

            canvas.translate(left, top);
            image.draw(canvas);
            canvas.restore();
        }
    }





} // The End