package edu.rit.simulatingclassroom;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.VideoView;


public class GlassesActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glasses);

        startAsServer();
        getWindow().getDecorView().findViewById(android.R.id.content).setOnTouchListener(new OnSwipeTouchListener(this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_glasses, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String getNumbersFromString(String s){
        String returnedString = "";
        for(char c : s.toCharArray()){
            if(Character.isDigit(c)){
                returnedString += c + "";
            }
        }
        return returnedString;
    }
    private void startAsServer() {
        new AcceptThread(mHandler).start();
    }

    private String data = "";
    private ConnectionThread mBluetoothConnection = null;

    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1: {
                    mBluetoothConnection = (ConnectionThread) msg.obj;
                    break;
                }
                case 2: {
                    data = (String) msg.obj;
                    Log.d("SOME TAG", "DATA: " + data);
                    //The pause/play messages start with P::
                    if(data.startsWith("P::")){
                        if(VideoUtil.isPlaying){
                            VideoUtil.pauseVideo();
                        }
                        else{
                            Log.d("GLASS", "Resumes the video");
                            VideoUtil.resumeVideo();
                        }
                    }
                    //The play video 1 , 2 ,3 ... etc. messages start with V:
                    else if(data.startsWith("V:")){
                        //DO VIDEO
                        ((VideoView)findViewById(R.id.someVideo)).setVisibility(View.VISIBLE);
                        TextView glassesCaptions = (TextView) findViewById(R.id.glassesCaptions);
                        glassesCaptions.setVisibility(View.GONE);

                        VideoUtil.playVideo(GlassesActivity.this,Integer.parseInt(getNumbersFromString(data)));
                    }
                    //If the things are neither P:: or V:, then it is a caption
                    else {
                        //It's a caption
                        TextView glassesCaptions = (TextView) findViewById(R.id.glassesCaptions);
                        glassesCaptions.setVisibility(View.VISIBLE);
                        ((VideoView)findViewById(R.id.someVideo)).setVisibility(View.GONE);
                        glassesCaptions.setText(data);
                    }
                }
                default:
                    break;
            }
        }
    };
}
