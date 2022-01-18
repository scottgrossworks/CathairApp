package com.cathairapocalypse.app;

        import android.app.Activity;

        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentActivity;

        import android.util.DisplayMetrics;
        import android.view.View;
        import android.content.Intent;


/**
 *
 */
public class CA_Fragment extends Fragment {

    protected static boolean mDialogOpen = false;


    protected CA_PagerAdapter mAdapter;
    protected FragmentActivity mContext;
    protected int mWidth, mHeight;
    protected boolean mInitialized = false;


    protected static final int REQUEST_IMAGE_OPEN = 1;
    protected static int IMAGE_OPEN_SUCCESS = -1;






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
            mDialogOpen = false;
    }



        @Override
    public void onAttach(Activity activity) {

        mContext = (FragmentActivity) activity;
        mAdapter = ((MainActivity) activity).getAdapter();

        super.onAttach(activity);
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



    protected void setAdapter( CA_PagerAdapter adapter ) {
        mAdapter = adapter;
    }



    protected void openImageDialog() {
        // android.util.Log.d("opening an image dialog: ", Integer.toString(mCount));

        if (! mInitialized) return;

        try {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_OPEN);


         } catch (Exception e) {
            android.util.Log.d("openImageDialog ERROR", e.toString());
            mAdapter.changeState_E( "Cathair Error: Cannot access your phone's image gallery.  Napping...");
        }
    }



    protected class CA_OpenPicListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            if (mAdapter.IN_ERROR_STATE) return;

            if (mDialogOpen) return;

            mDialogOpen = true;
            openImageDialog();
        }
    }


} // The End.

