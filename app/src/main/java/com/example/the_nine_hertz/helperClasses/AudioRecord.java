package com.example.the_nine_hertz.helperClasses;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class AudioRecord {

    String outputFile;
    MediaRecorder myAudioRecorder;
    Context context;
    MediaPlayer mediaPlayer = new MediaPlayer();
    private static final String TAG = "AudioRecord";


    public AudioRecord(Context context) {
        try {
            this.context = context;
            outputFile = context.getExternalCacheDir().getAbsolutePath() + "/recording.3gp";
            mediaPlayer = new MediaPlayer();
        }
        catch (Exception e)
        {
            Log.e(TAG, "AudioRecord : " + e.toString());
        }
    }

    public boolean init()
    {
        if(checkPlaying())
            stopPlaying();
        try {
            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(AudioSource.MIC);
            myAudioRecorder.setOutputFormat(OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(outputFile);
            myAudioRecorder.setMaxDuration(60000);
            myAudioRecorder.prepare();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "init: IO Error: " + e.toString() );
            e.printStackTrace();
        }
        return false;
    }


    public boolean checkPlaying()
    {
        return mediaPlayer.isPlaying();
    }

    public boolean stopPlaying()
    {
        try {
                mediaPlayer.stop();
                //mediaPlayer.release();
                Toast.makeText(context, "Audio Playing Stopped ", Toast.LENGTH_SHORT).show();
                return true;
        }
        catch (Exception e)
        {
            Log.e(TAG, "init: Error while checking media player" + e.toString());
        }
        return false;
    }

    public void recordStart()
    {
        Log.d(TAG, "recordStart: Starting recording...");
        myAudioRecorder.start();
        Toast.makeText(context, "Audio Started Recording ", Toast.LENGTH_SHORT).show();
    }


    public void recordStop()
    {
        try {
            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
            Toast.makeText(context, "Audio Stopped Recording", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            myAudioRecorder = null;
            Log.e(TAG, "recordStop: Illegal state exception: " + e.toString());
        }
    }

    public void play()
    {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.d(TAG, "play: Playing audio...");
            Toast.makeText(context, "Playing Audio", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Error while playing Audio", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "play: Error while playing " + e.toString());
            // make something
        }
    }

    public MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }


}
