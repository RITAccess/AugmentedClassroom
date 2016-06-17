package com.mywebground.android.glassvideoplayer;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.os.Handler;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.mywebground.android.glassvideotest.R;

public class MainActivity extends AppCompatActivity {

    // These 2 lines are for the Log button.
    final Context context = this;
    private Button button;

    private View mDecorView;
    private CustomVideoView videoView;

    private android.widget.RelativeLayout.LayoutParams layoutParams;

    static final int MIN_WIDTH = 300;
    private ScaleGestureDetector mScaleGestureDetector;

    private int TOUCH_GESTURE_MODE;

    private static int GESTURE_MODE_NONE = 0;
    private static int GESTURE_MODE_DRAG = 1;
    private static int GESTURE_MODE_SCALE = 2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mDecorView = getWindow().getDecorView();
        hideSystemUI();
        setContentView(R.layout.activity_main);

        TOUCH_GESTURE_MODE = GESTURE_MODE_DRAG;

        layoutParams = (RelativeLayout.LayoutParams) findViewById(R.id.video_frame).getLayoutParams();
        videoView = (CustomVideoView) this.findViewById(R.id.videoView);

        String uriPath = "android.resource://" + getPackageName() + "/" + R.raw.presentations;
        Uri uri = Uri.parse(uriPath);
        videoView.setVideoURI(uri);

        mScaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());

        // log button, initialize variable
        button = (Button) findViewById(R.id.logInfo);


            videoView.setOnTouchListener(new View.OnTouchListener()

            {

                @Override
                public boolean onTouch (View v, MotionEvent mEvent){

                int pointersCount = mEvent.getPointerCount();

                System.out.println("pointersCount-->" + pointersCount);

                if (TOUCH_GESTURE_MODE == GESTURE_MODE_DRAG) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(videoView);
                    videoView.startDrag(null, shadowBuilder, videoView, 0);
                    return true;
                } else if (TOUCH_GESTURE_MODE == GESTURE_MODE_SCALE) {
                    mScaleGestureDetector.onTouchEvent(mEvent);
                    return true;
                } else {
                    return true;
                }
            }
            }

            );

            videoView.setOnDragListener(new View.OnDragListener()

            {
                @Override
                public boolean onDrag (View v, DragEvent event){
                int xCord;
                int yCord;
                int newLeftMargin;
                int newTopMargin;

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (RelativeLayout.LayoutParams) v.getLayoutParams();
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                    case DragEvent.ACTION_DRAG_EXITED:
                        xCord = (int) event.getX();
                        yCord = (int) event.getY();
                        newLeftMargin = xCord - (videoView.getWidth() / 2);
                        newTopMargin = yCord - (videoView.getHeight() / 2);

                        if (newLeftMargin > 0 && newTopMargin > 0) {
                            layoutParams.setMargins(newLeftMargin, newTopMargin, 0, 0);
                            v.setLayoutParams(layoutParams);
                            v.invalidate();
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        // Log.d(msg, "Action is DragEvent.ACTION_DRAG_LOCATION");
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        // Log.d(msg, "Action is DragEvent.ACTION_DRAG_ENDED");

                        return true;

                    case DragEvent.ACTION_DROP:
                        //Log.d(msg, "ACTION_DROP event");
                        break;

                    default:
                        break;
                }

                return true;
            }
            }

            );

        // add button listener
        button.setOnClickListener(new OnClickListener()

          {

              // Buttons
              final RadioButton moveButton = (RadioButton) findViewById(R.id.radio_move);
              final RadioButton resizeButton = (RadioButton) findViewById(R.id.radio_resize);

              public void startPres() {
                  moveButton.setVisibility(View.INVISIBLE);
                  resizeButton.setVisibility(View.INVISIBLE);
                  button.setVisibility(View.INVISIBLE);
                  TOUCH_GESTURE_MODE = GESTURE_MODE_NONE;
                  videoView.start();
              }

              @Override
              public void onClick(View arg0) {
                  String posInfo = "Please wait for researchers to log these values.\n";
                  posInfo += "(If not ready, tap outside window.)\n";
                  posInfo += "Top: " + String.valueOf(layoutParams.topMargin) + "\n";
                  posInfo += "Left: " + String.valueOf(layoutParams.leftMargin) + "\n";
                  posInfo += "Width: " + String.valueOf(videoView.getWidth()) + "\n";
                  posInfo += "Height: " + String.valueOf(videoView.getHeight());

                  AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                          context);
                  
                  // set title
                  alertDialogBuilder.setTitle("Position Stats");

                  // For photos video
                  String uriPathp = "android.resource://" + getPackageName() + "/" + R.raw.presentationp;
                  final Uri urip = Uri.parse(uriPathp);

                  // set dialog message
                  alertDialogBuilder
                          .setMessage(posInfo)
                          .setCancelable(true) // allow tapping outside window to close
                          .setPositiveButton("Stars", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {
                                  startPres();
                                  dialog.cancel();
                              }
                          })
                          .setNegativeButton("Photos", new DialogInterface.OnClickListener() {
                              public void onClick(DialogInterface dialog, int id) {
                                  videoView.setVideoURI(urip);
                                  startPres();
                                  dialog.cancel();
                              }
                          });

                  // create alert dialog
                  AlertDialog alertDialog = alertDialogBuilder.create();

                  // show it
                  alertDialog.show();
              }
          }
        );

        // play video and pause so the user can set preferences
        videoView.start();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                videoView.pause();
            }
        }, 1000);


        // note to move before resizing due to a bug
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Note");
  
        // set dialog message
        alertDialogBuilder
                .setMessage("Please move the video before resizing to avoid glitches.")
                .setCancelable(true); // allow tapping outside window to close
  
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
  
        // show it
        alertDialog.show();


        }

    public void onRadioButtonClicked(View view) {
            // Is the button now checked?
            boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_move:
                if (checked)
                    TOUCH_GESTURE_MODE = GESTURE_MODE_DRAG;
                break;
            case R.id.radio_resize:
                if (checked)
                    TOUCH_GESTURE_MODE = GESTURE_MODE_SCALE;
                break;
        }
    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        private int mW, mH;

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            System.out.println("videoView.getWidth()-->" + videoView.getWidth());
            System.out.println("videoView.getHeight()-->" + videoView.getHeight());

            float scaleFactor = detector.getScaleFactor();
            mW *= scaleFactor;
            mH *= scaleFactor;

            int videoWidth = videoView.getWidth();
            int videoHeight = videoView.getHeight();

            int oldLeftMargin = layoutParams.leftMargin;
            int oldTopMargin = layoutParams.topMargin;

            int newLeftMargin;
            int newTopMargin;

            if (mW > videoWidth) {
                newLeftMargin = oldLeftMargin - Math.round(mW - videoWidth);
            } else {
                newLeftMargin = oldLeftMargin + Math.round(mW - videoWidth);
            }

            if (mH > videoHeight) {
                newTopMargin = oldTopMargin - Math.round(mH - videoHeight);
            } else {
                newTopMargin = oldTopMargin + Math.round(mH - videoHeight);
            }

            Log.d("onScale", "newLeftMargin=" + newLeftMargin + ", newTopMargin=" + newTopMargin);

            if (mW < MIN_WIDTH) { // limits width
                mW = videoWidth;
                mH = videoHeight;
            }
            Log.d("onScale", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            layoutParams.width = mW;
            layoutParams.height = mH;
            if (newLeftMargin > 0 && newTopMargin > 0) {
                layoutParams.setMargins(newLeftMargin, newTopMargin, 0, 0);
            }

            videoView.setLayoutParams(layoutParams);
            videoView.invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mW = videoView.getWidth();
            mH = videoView.getHeight();
            Log.d("onScaleBegin", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            Log.d("onScaleEnd", "scale=" + detector.getScaleFactor() + ", w=" + mW + ", h=" + mH);
        }

    }

    private void hideSystemUI() {
        mDecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }
}
