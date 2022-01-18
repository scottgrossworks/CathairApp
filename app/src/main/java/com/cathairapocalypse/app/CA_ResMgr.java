package com.cathairapocalypse.app;

        import android.app.Activity;

        import android.content.ContentResolver;
        import android.content.ContentUris;
        import android.content.Intent;
        import android.content.res.Resources;
        import android.database.Cursor;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.graphics.Canvas;

        import android.graphics.drawable.BitmapDrawable;
        import android.graphics.drawable.Drawable;
        import android.content.Context;
        import android.media.MediaScannerConnection;
        import android.os.AsyncTask;
        import android.os.Build;
        import android.os.Environment;
        import android.provider.DocumentsContract;
        import android.provider.MediaStore;
        import android.provider.OpenableColumns;
        import android.util.DisplayMetrics;
        import android.util.Log;
        import android.view.View;
        import android.view.ViewGroup;
        import android.text.SpannableString;
        import android.text.Spannable;
        import android.text.style.ImageSpan;
        import android.net.Uri;
        import android.widget.Toast;

        import org.xmlpull.v1.XmlPullParser;
        import org.xmlpull.v1.XmlPullParserFactory;

        import java.io.File;
        import java.io.FileOutputStream;

        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.List;


public final class CA_ResMgr {



    public static boolean IS_READY = false;
    public static int COMP_WIDTH, COMP_HEIGHT;

    public static int COLOR_0;
    public static int COLOR_1;
    public static int COLOR_2;
    public static int COLOR_3;

    public static List<CathairResource> mCathairs;
    public static String mCathairDirPath;
    public static String MOD;  // Message of the Day


    private static Drawable mLogo;
    private static Drawable mFrame;
    private static Drawable mMadeWith;

    private static Drawable button_0_ON;
    private static Drawable button_0_OFF;

    private static Drawable button_1_ON;
    private static Drawable button_1_OFF;

    private static Drawable button_2_ON;
    private static Drawable button_2_OFF;


    private static Context mContext;
    private static Activity mActivity;
    private static ContentResolver mResolver;
    private static Resources mResources;



    /**
     * Private CTOR because it is a static utility
     */
    private CA_ResMgr() {
    }


    public static void initialize(Activity activity, Context context, ContentResolver resolver) {

        mActivity = activity;
        mContext = context;
        mResources = context.getResources();
        mResolver = resolver;

        mLogo = loadImage(R.drawable.logo);

        button_0_ON = loadImage(R.drawable.button_on_0);
        button_0_OFF = loadImage(R.drawable.button_off_0);

        button_1_ON = loadImage(R.drawable.button_on_1);
        button_1_OFF = loadImage(R.drawable.button_off_1);

        button_2_ON = loadImage(R.drawable.button_on_2);
        button_2_OFF = loadImage(R.drawable.button_off_2);

        COLOR_0 = mResources.getColor(R.color.colorPrimary);
        COLOR_1 = mResources.getColor(R.color.colorSecondary);
        COLOR_2 = mResources.getColor(R.color.colorTertiary);
        COLOR_3 = mResources.getColor(R.color.colorMedium);


        /** contains references to all cathairs, remote or local */
        mCathairs = new ArrayList<CathairResource>();


        /** Create Downloads/CATHAIR and load bundled cathairs */
        new CathairIOThread(0).execute();

        /**
         *  Query website XML
         *  download new free cathair
         *  */
        new CathairIOThread(1).execute();

        /** Load mFrame and mMadeWith */
        mFrame = loadImage(R.drawable.frame);
        mMadeWith = loadImage(R.drawable.madewith);
        COMP_WIDTH = mFrame.getIntrinsicWidth();
        COMP_HEIGHT = mFrame.getIntrinsicHeight();



        IS_READY = true;
    }


    public static Drawable getLogo() {

        return mLogo;
    }

    public static BitmapDrawable getFrame() {

        return (BitmapDrawable) mFrame;
    }


    public static BitmapDrawable getMadeWith() {

        return (BitmapDrawable) mMadeWith;
    }


    public static String getCathairDirPath() {

        return mCathairDirPath;
    }
    
    public static Drawable getButton(int position, boolean isSelected) {

        return getButton(position, 0, 0, isSelected);
    }


    public static Drawable getButton(int position, int width, int height, boolean isSelected) {

        Drawable image;

        switch (position) {

            case 0:
                image = (isSelected) ? button_0_ON : button_0_OFF;
                break;

            case 1:
                image = (isSelected) ? button_1_ON : button_1_OFF;
                break;

            case 2:
            default:
                image = (isSelected) ? button_2_ON : button_2_OFF;
                break;

        }

        if (width == 0 || height == 0) {
            image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        } else {
            image.setBounds(0, 0, width, height);
        }

        return image;
    }


    protected static SpannableString charsFromDrawable(Drawable image) {
        SpannableString sb = new SpannableString("  ");
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }



    protected static Drawable loadImage(int id) {

        DisplayMetrics dm = mResources.getDisplayMetrics();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = dm.densityDpi;
        options.inScreenDensity = dm.densityDpi;
        options.inTargetDensity = dm.densityDpi;

        Bitmap bitmap = BitmapFactory.decodeResource(mResources, id, options);
        BitmapDrawable image = new BitmapDrawable(mResources, bitmap);

        return image;
    }




    protected static BitmapDrawable loadImage(Uri uri) {

        Bitmap bitmap = loadImageFromStream(uri); // might be null
        BitmapDrawable image = new BitmapDrawable(mResources, bitmap);
        return image;
    }


        /**
        try {
            String fullpath = getPathFromURI(uri);

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(fullpath, options);



            // Calculate inSampleSize
            DisplayMetrics dm = mResources.getDisplayMetrics();
            options.inSampleSize = calculateInSampleSize(options, dm.widthPixels, dm.heightPixels);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(fullpath, options);
            BitmapDrawable image = new BitmapDrawable(mResources, bitmap);

            return image;

        } catch (Exception e) {
            //Log.d("ERROR in addImage()", e.toString());
            if (uri != null)
                Toast.makeText(mContext, "Error loading: " + uri.toString(), Toast.LENGTH_SHORT).show();
        }

        return null;
    }
*/





    protected static int calculateInSampleSize( BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }






    protected static Bitmap loadImageFromStream(Uri uri) {

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 200000; // 0.2MP
            in = mResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;                    //request only the dimension
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }

            Bitmap b = null;
            in = mResolver.openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);
                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, (int) y, true);
                b.recycle();
                b = scaledBitmap;
                System.gc();

            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            return b;

        } catch (Exception e) {
            if (uri != null)
                Toast.makeText(mContext, "Error loading: " + uri.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }





    protected static Bitmap loadBitmapFromView(View v) {

        v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int width = v.getMeasuredWidth();
        int height = v.getMeasuredHeight();

        Bitmap b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);

        v.layout(0, 0, width, height);
        v.draw(c);

        return b;
    }






    protected static void saveToPhone(BitmapDrawable drawable, Uri uri) throws Exception {

        Bitmap bitmap = drawable.getBitmap();
        new CathairIOThread(2, uri, bitmap).execute();
    }





    protected static void launchShareActivity() throws Exception {

        String type = "image/jpg";

        String mediaPath = CA_PagerAdapter.project.mFullpath;
        String caption = mResources.getString(R.string.share_txt);

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media
        File media = new File(mediaPath);
        Uri uri = Uri.fromFile(media);

        // Add the URI and the caption to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.putExtra(Intent.EXTRA_TEXT, caption);

        // Broadcast the Intent.
        Intent intent = Intent.createChooser(share, "Share Cathair to");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity( intent );
    }


    protected static String getFilenameFromURI(Uri uri) {

        FileMetaData metaData = FileMetaData.getFileMetaData(mContext, uri);

        String filename = metaData.displayName;
        return filename;
    }


    /**
     * The RULES for saving CATHAIRS
     *
     * 5.2015
     *
     */
    protected static String makeCathairFilename(String original) {

        int lastDot = original.length();
        if (original.contains("."))
            lastDot = original.lastIndexOf("."); // trim original file ext

        String filename = original.substring(0, lastDot);

        if (filename.endsWith("_CATHAIR")) {
            return filename + ".jpg";
        }

        filename += "_CATHAIR.jpg";
        return filename;
    }






    protected static String getFilenameFromPath(String path, boolean fullpath) {

        int lastDot = path.lastIndexOf(".");
        String sub = path.substring(0, lastDot);
        if (fullpath) {
            return sub;
        } else {
            int slash = sub.lastIndexOf("/");
            sub = sub.substring(slash + 1);
            return sub;
        }
    }


    /**
     * Helper for ALL I/O functions
     * downloading
     * saving to filesystem
     */

    protected static class CathairIOThread extends AsyncTask<Void, Void, Void> {

        protected Uri mUri;
        protected Bitmap mBitmap;
        protected int mState;


        /**
         * state 2 -- save a composite bitmap to ~/DOWNLOAD/CATHAIR
         */
        public CathairIOThread(int state, Uri uri, Bitmap bitmap) {

            mState = state; // better == 2!!
            mUri = uri;
            mBitmap = bitmap;
        }

        public CathairIOThread(int state) {

            mState = state;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... params) {

            switch (mState) {
                /**
                 * make ~/DOWNLOAD/CATHAIR
                 * save bundled Cathairs to CATHAIR
                 */
                case 0:
                    if (!makeCathairDir()) return null;
                    if (!saveStubsToCathair()) return null;
                    break;

                /**
                 * DOWNLOAD Cathairs from URL
                 *
                 */
                case 1:
                    String url = mResources.getString(R.string.cathair_url);
                    if (url == null) {
                        //Log.d("I/O ERROR", "URI == null");
                        makeToast("I/O Error: Cannot find cathair_url");
                    } else {
                        //Log.d("downloadCathairs()", url);
                        downloadCathairs(url);
                    }
                    break;

                /**
                 * SAVE a composite bitmap
                 * to the CATHAIR directory
                 */
                case 2:

                    if (mUri == null || mBitmap == null) {
                        //android.util.Log.d("I/O ERROR", "I/O Error: no Uri or Bitmap to Save");
                        makeToast("I/O Error: no Uri or Bitmap to Save");
                    } else {
                        try {
                            saveUserCathair(mBitmap, mUri);
                        } catch (Exception e) {
                            //android.util.Log.d("I/O ERROR", e.toString());
                            makeToast("I/O Error: " + e.toString());
                        }
                    }
                    break;


                /**
                 * LOAD mFrame and mMadeWith
                 * before user gets to state 1
                 *
                 */
                case 3:

                    if (mFrame == null || mMadeWith == null) {

                        mFrame = loadImage(R.drawable.frame);
                        mMadeWith = loadImage(R.drawable.madewith);
                        COMP_WIDTH = mFrame.getIntrinsicWidth();
                        COMP_HEIGHT = mFrame.getIntrinsicHeight();
                    }

                    break;

                default:
                    //android.util.Log.d("I/O ERROR", "Called I/O Thread with wrong CODE: " + Integer.toString(mState));
                    break;
            }
            return null;
        }


        @Override
        public void onPostExecute(Void results) {
            super.onPostExecute(results);
            //Toast.makeText(mContext, "Cathair Setup Complete", Toast.LENGTH_SHORT).show();
        }


        /**
         * Used for saving completed images
         * appends _CATHAIR.jpg
         */
        protected static void saveUserCathair(Bitmap bitmap, Uri uri) throws Exception {

            String filename = getFilenameFromURI(uri);

            String cathair = makeCathairFilename(filename);

            String localpath = mCathairDirPath + cathair;

            CA_PagerAdapter.project.mFilename = filename;
            CA_PagerAdapter.project.mFullpath = localpath;

            saveCathair(bitmap, localpath, false);

            CA_PagerAdapter.project.DONE = true;

            // eliminate this Toast to speed transition to Fragment2 --> Share
            // makeToast("Cathair Saved: " + cathair);
        }


        /**
         * Save  ca_cathair_# overlay to the CATHAIR directory
         */
        protected static void saveCathair(Bitmap bitmap, String fullpath, boolean png) throws Exception {

            FileOutputStream out = new FileOutputStream( new File(fullpath) );
            String mime;
            if (png) {
                mime = "image/png";
                bitmap.setHasAlpha(true);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            } else {
                mime = "image/jpg";
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            out.flush();
            out.close();

            MediaScannerConnection.scanFile(mContext, new String[]{fullpath}, new String[]{mime}, null);
            MediaScannerConnection.scanFile(mContext, new String[]{fullpath}, new String[]{mime}, null);

            //  android.util.Log.d("I/O SUCCESS writing: ", fullpath);
        }




        protected static boolean downloadCathairs(String cathairUrl) {

            int downloaded = 0;
            try {
                if (cathairUrl == null) throw new Exception("Cathair http directory is null");
                if (!cathairUrl.endsWith("/"))
                    cathairUrl += "/";

                String cathairXML = mResources.getString(R.string.cathair_xml);
                String xmlUrl = cathairUrl + cathairXML; //append the xml filename

                /**
                 * adds new CathairResources to m_cathairs
                 */
                //Log.d("downloadXML", xmlUrl);
                downloadXML(xmlUrl);

                if (!hasCathairDir())
                    makeCathairDir();


                /**
                 * Download each remote cathair individually
                 */
                String mime = "image/png";
                CathairResource cathair;
                String remotePath, localPath;

                int count = mCathairs.size();
                for (int i = 0; i < count; i++) {

                    cathair = mCathairs.get(i);

                    if (! cathair.ready) { // has not been downloaded
                        if (cathair.error == null) {  // is not an error

                            try {
                                remotePath = cathairUrl + cathair.filename;
                                URL url = new URL(remotePath);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.connect();

                                // require HTTP 200 OK
                                int code = connection.getResponseCode();
                                if (code != HttpURLConnection.HTTP_OK) {
                                    String error = "Bad Response Code: " + Integer.toString(code);
                                    //Log.d("ERROR httpConnection", error);
                                    throw new Exception(error);
                                }


                                /** download this remote cathair to its local equivalent */
                                localPath = mCathairDirPath + cathair.filename;
                                boolean success = downloadHelper(connection, cathair, localPath);
                                if (success) {
                                    MediaScannerConnection.scanFile(mContext, new String[]{localPath}, new String[]{mime}, null);
                                    cathair.ready = true;
                                    downloaded++;
                                }

                            } catch (Exception e) {
                                String error = e.toString();
                                //Log.d("ERROR downloadCathairs() inner", error);
                                //e.printStackTrace();
                                if (cathair.error == null)
                                    cathair.error = error;
                                // keep looping
                            }
                        }
                    }
                }


            } catch (Exception e) {
                //String msg = e.toString();
                //Log.d("ERROR downloadCathairs() outer", msg);
                //e.printStackTrace();
                return false;
            }

            if (downloaded > 0) {
                makeToast("Downloaded " + Integer.toString(downloaded) + " new Cathairs!");
            }

            return true;
        }


        private static boolean downloadHelper(HttpURLConnection connection, CathairResource cathair, String localPath) throws Exception {

            int fileLength = connection.getContentLength();
            if (fileLength < 1)
                fileLength = 4096;

            InputStream input = null;
            FileOutputStream output = null;

            File localFile = new File(localPath);
            if ( localFile.exists()) {
                /**
                 * Cathair is ready but do NOT
                 * overwrite existing files on downloads
                 * */
                cathair.ready = true;
                return false;

            } else {
                try {
                    input = connection.getInputStream();
                    output = new FileOutputStream(localFile);

                    byte data[] = new byte[fileLength];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        output.write(data, 0, count);
                    }

                } catch (Exception e) {
                    //e.printStackTrace();
                    String error = e.toString();
                    //Log.d("ERROR writing file", error);
                    cathair.ready = false;
                    cathair.error = error;

                } finally {

                    try {
                        if (output != null)
                            output.close();

                        if (input != null)
                            input.close();

                        if (connection != null)
                            connection.disconnect();

                    } catch (Exception e) {
                        String error = e.toString();
                        //Log.d("ERROR closing streams", error);
                        cathair.ready = false;
                        cathair.error = error;
                    }
                }
            }


            System.gc();
            return true;
        }



        protected static void downloadXML(String httpUrl) throws Exception {

            try {
                URL url = new URL(httpUrl);

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                // We will get the XML from an input stream
                InputStream iStream = url.openConnection().getInputStream();
                xpp.setInput(iStream, "UTF_8");

                String name = null;
                String text = null;
                CathairResource cathair = new CathairResource();

                // Log.d("XMLparser", "START");

                // Returns the type of current event: START_TAG, END_TAG, etc..
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    try {
                        name = xpp.getName();
                        switch (eventType) {
                            case XmlPullParser.START_TAG:

                                if (name.equalsIgnoreCase("free")) {
                                    cathair = new CathairResource();

                                /**
                                } else if (name.equalsIgnoreCase("pro")) {
                                    cathair = new CathairResource();
                                    cathair.free = false;
                                */

                                } else if (name.equalsIgnoreCase("mod")) {
                                    /** reset the MOD and grab the text below */
                                    MOD = null;

                                } else {
                                    // ignore error case
                                }
                                break;


                            case XmlPullParser.TEXT:
                                text = xpp.getText();
                                break;


                            case XmlPullParser.END_TAG:

                                if (name.equalsIgnoreCase("free")) {
                                    cathair.free = true;
                                    mCathairs.add(cathair);
                                    //Log.d("Added Cathair", cathair.filename);


                                } else if (name.equalsIgnoreCase("filename")) {
                                    cathair.filename = text;


                                } else if (name.equalsIgnoreCase("mod")) {

                                    /** DISPLAY the message of the day */
                                    makeLongToast(MOD = text);


                                    /**
                                } else if (name.equalsIgnoreCase("pro")) {
                                     cathair.filename = text;
                                     cathair.free = false;

                                } else if (name.equalsIgnoreCase("info")) {
                                    cathair.info = text;

                                } else if (name.equalsIgnoreCase("code")) {

                                    cathair.code = Integer.parseInt(text);
                                    */


                                } else {
                                    // unrecognized tag
                                }
                                break;
                        }

                    } catch (Exception e) {
                        String error = e.toString();
                        //Log.d("ERROR parsingXML inner", error);
                        cathair.error = error;
                        cathair.ready = false;
                        cathair = new CathairResource();
                        // keep looping
                    }

                    eventType = xpp.next();
                }


            } catch (Exception e) {
                String error = e.toString();
                //Log.d("ERROR parsingXML outer", error);
                //e.printStackTrace();
                makeLongToast(error);
            }
        }




        protected static boolean saveStubsToCathair() {

            int id[] = new int[] {  R.drawable.ca_cathair_0,
                                    R.drawable.ca_cathair_1,
                                    R.drawable.ca_cathair_2,
                                    R.drawable.ca_cathair_3,
                                    R.drawable.ca_cathair_4,
                                    R.drawable.ca_cathair_5,
                                    R.drawable.ca_cathair_6,
                                    R.drawable.ca_cathair_7,
                                    R.drawable.ca_cathair_8,
                                    R.drawable.ca_cathair_9,
                                    R.drawable.ca_cathair_10,
                    R.drawable.ca_cathair_11,
                    R.drawable.ca_cathair_12,
                    R.drawable.ca_cathair_13,
                    R.drawable.ca_cathair_14,
                    R.drawable.ca_cathair_15,
                    R.drawable.ca_cathair_16,
                    R.drawable.ca_cathair_17,
                    R.drawable.ca_cathair_18
            };

            CathairResource cathair = null;
            String filename;
            File existing;
            Bitmap bitmap;
            for (int i = 0; i < id.length; i++) {

                try {
                    filename = "ca_cathair_" + Integer.toString(i) + ".png";
                    String fullpath = mCathairDirPath + filename;

                    cathair = new CathairResource();
                    cathair.filename = filename;
                    cathair.free = true;

                    /** don't overwrite existing cathairs every time app starts */
                    existing = new File(fullpath);
                    if (existing.exists()) {
                        cathair.info = "existing";
                    } else {
                        bitmap = BitmapFactory.decodeResource(mResources, id[i]);
                        saveCathair(bitmap, fullpath, true);
                        cathair.info = "installed";
                        // Log.d("SAVED", fullpath);
                    }

                    cathair.ready = true;
                    mCathairs.add(cathair);

                } catch (Exception e) {
                    makeToast("I/O Error: " + e.toString());
                    if (cathair != null) {
                        cathair.ready = false;
                        cathair.error = e.toString();
                    }
                }
            }


            return true;
        }


        protected static boolean hasCathairDir() {
            return (mCathairDirPath != null);
        }

        protected static boolean makeCathairDir() {

            if (mCathairDirPath != null) return true;

            try {
                String state = Environment.getExternalStorageState();
                if (!Environment.MEDIA_MOUNTED.equals(state)) {
                    throw new Exception("External storage not avail/mounted");
                }

                File cathairDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "CATHAIR");

                if (! cathairDir.exists()) {
                    boolean result = cathairDir.mkdirs();
                    if (!result) {
                        String s = Environment.DIRECTORY_PICTURES + "/CATHAIR";
                        throw new Exception("Could not create: " + s);
                    }
                }

                mCathairDirPath = cathairDir.getPath();
                if (! mCathairDirPath.endsWith("/")) {
                    mCathairDirPath += "/";
                }

                if (!cathairDir.canWrite()) {
                    throw new Exception("Cannot write to: " + mCathairDirPath);
                }

            } catch (Exception e) {
                makeToast("I/O Error: " + e.toString());
                return false;
            }

            return true;
        }


    } // end Cathair I/O thread




    /**
     * 12/2015
     * NullPointerException pops up in file handling on device, can't get filename from URI
     *
     */
    protected static class FileMetaData {

        public String displayName;
        public long size;
        public String mimeType;
        public String path;

        @Override
        public String toString() {

            return "name : " + displayName + " ; size : " + size + " ; path : " + path + " ; mime : " + mimeType;
        }

    public static FileMetaData getFileMetaData(Context context, Uri uri) {
        FileMetaData fileMetaData = new FileMetaData();

        if ("file".equalsIgnoreCase(uri.getScheme())) {
            File file = new File(uri.getPath());
            fileMetaData.displayName = file.getName();
            fileMetaData.size = file.length();
            fileMetaData.path = file.getPath();

            return fileMetaData;
        } else {
            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            fileMetaData.mimeType = contentResolver.getType(uri);

            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    fileMetaData.displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));

                    if (!cursor.isNull(sizeIndex))
                        fileMetaData.size = cursor.getLong(sizeIndex);
                    else
                        fileMetaData.size = -1;

                    try {
                        fileMetaData.path = cursor.getString(cursor.getColumnIndexOrThrow("_data"));
                    } catch (Exception e) {
                        // DO NOTHING, _data does not exist
                    }

                    return fileMetaData;
                }
            } catch (Exception e) {
                makeToast("I/O Error: " + e.toString());
                return fileMetaData;
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
            return null;
    }


    } // end FileMetaData















    private static class CathairResource {

        private String filename;
        private boolean free = true;
        private int code;
        private boolean ready = false;
        private String info;
        private String error;


    } // End CathairResource




    protected static void makeToast(final String msg) {

        try {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            // android.util.Log.d("I/O ERROR:", e.toString());
        }
    }



    protected static void makeLongToast(final String msg) {

        try {
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            // android.util.Log.d("I/O ERROR:", e.toString());
        }
    }

} // The End.