package edu.rit.simulatingclassroom;

import android.app.Activity;
import android.content.Context;
import android.media.session.MediaController;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

public class VideoUtil {
    //Here is where you put video names!
    private static String[] videoNames = {"video_caption.mp4","video_asl.mp4"};
    public static boolean isPlaying = false;
    private static int position = 0;
    private static VideoView videoView;
    public static int CURRENT_VIDEO_CODE = 0;
    /**
     *
     * @param videoCode
     * The code of the video.
     */
    public static void playVideo(Activity context, int videoCode){
        isPlaying = true;
        if(videoCode < 0){
            videoCode = videoNames.length;
        }
        if(videoCode > videoNames.length){
            videoCode = 0;
        }
        CURRENT_VIDEO_CODE = videoCode;

        final File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "Download/" + videoNames[videoCode]);
        videoView = ((VideoView)context.findViewById(R.id.someVideo));
        videoView.setVideoPath(file.getAbsolutePath());
        //FIXME: we have to make it synch with all the other three videos being played: videoView.seekTo(THE_CORRECT_POSITION);
        videoView.start();

    }

    public static void disposeOfVideo(){
       videoView.stopPlayback();
    }
    public static void pauseVideo(){
        isPlaying = false;
        position = videoView.getCurrentPosition();
        videoView.pause();
    }

    public static void resumeVideo(){
        isPlaying = true;
        videoView.seekTo(position);
        videoView.start();
    }
}
