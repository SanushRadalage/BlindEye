package com.mad.calculator;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfLine;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Delayed;

public class Quiz extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener
{
    ConstraintLayout quizTouch;
    EditText teacherAns, studentAns;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    //private static final String TAG = "";
    private static final int  STORAGE_CODE = 1000;

    TextToSpeech myTTS;
    String number;
    int i = 1;
    String initialText = "I'm Quiz, touch the screen and give your answers, If you need to know the key words in this activity, say information";
    String a;
    int g = 0;

    ArrayList<String> sAnswers = new ArrayList<String>();
    ArrayList<String> tAnswers = new ArrayList<String>();

    int seekForwardTime = 5000;
    int seekBackwardTime = 5000;
    private  MediaPlayer mp;
    private SongsManager songManager;
    private int currentSongIndex = 0;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        quizTouch = findViewById(R.id.quizT);
        teacherAns = findViewById(R.id.tans);
        studentAns = findViewById(R.id.sans);

        mp = new MediaPlayer();
        songManager = new SongsManager();

        mp.setOnCompletionListener((MediaPlayer.OnCompletionListener) this); // Important

        songsList = songManager.getPlayList();

        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = myTTS.setLanguage(Locale.US);

                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Log.e("TTS", "Language not supported");
                    }
                    else
                    {

                    }
                }
                else
                {
                    Log.e("TTS", "Initialization Failed");
                }
            }
        });


        quizTouch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speak();
            }
        });

        initializeTextToSpeech();

    }

    private void initializeTextToSpeech() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (myTTS.getEngines().size() == 0) {
                    Toast.makeText(Quiz.this, "There is no TTS engine on your device",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    myTTS.setLanguage(Locale.UK);
                    myTTS.setSpeechRate(1);
                    aiVoice(initialText);

                }
            }

            private void aiVoice(String s)
            {
                if(Build.VERSION.SDK_INT >= 21)
                {
                    myTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
                }
                else
                {
                    myTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
    }

    private void speak()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, 1);

        try
        {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        }
        catch (Exception e)
        {

        }
    }

    public void  playSong(int songIndex){

        // Play song
        initialText = "Question number" + String.valueOf(i);

        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            String songTitle = songsList.get(songIndex).get("songTitle");

            if(isRepeat)
            {
                initialText = "Repeat again";
                initializeTextToSpeech();

            }
            else
            {
                initializeTextToSpeech();
                i++;
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        myTTS.shutdown();
    }

    @Override
    protected void onDestroy() {
        if(myTTS != null)
        {
            myTTS.stop();
            myTTS.shutdown();
        }
        super.onDestroy();
        mp.release();
    }

    private void savePDF()
    {

        com.itextpdf.text.Document mDoc = new com.itextpdf.text.Document();

        String mFileName = new SimpleDateFormat("YYYY-MM-DD-HH-MM-SS", Locale.getDefault()).format(System.currentTimeMillis());

        String mFilePath = Environment.getExternalStorageDirectory() + "/" + mFileName  + ".pdf";


        try
        {
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));

            mDoc.open();

            for(int d = 0; d < g; d++)
            {
                String mtext = sAnswers.get(d);

                //mDoc.addAuthor("sanush");

                mDoc.add(new Paragraph(mtext));

            }

            mDoc.close();

        }
        catch (Exception e)
        {

        }

    }


    @Override
    public void onCompletion(MediaPlayer mp)
    {
        if(isRepeat){
            // repeat is on play same song again
            playSong(currentSongIndex);

        }
        else {
            // no repeat or shuffle ON - play next song
            if(currentSongIndex < (songsList.size())){
                playSong(currentSongIndex);
                currentSongIndex = currentSongIndex + 1;
            }
            else if(currentSongIndex == (songsList.size()))
            {
                mp.stop();
                mp.reset();
                initialText = "Quiz is end. Thank you for the participation";
                initializeTextToSpeech();
            }
            else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String text1 = result.get(0);

                    if(text1.equals("play"))
                    {

                        if(mp.isPlaying()){
                            if(mp!=null){
                                mp.pause();
                                // Changing button image to play button
                                //btnPlay.setImageResource(R.drawable.btn_play);
                            }
                        }
                        else
                        {
                            // Resume song
                            if(mp!=null)
                            {
                                mp.start();
                            }
                        }

                    }
                    else if(text1.equals("pause"))
                    {
                        if(mp.isPlaying())
                        {
                            if(mp!=null)
                            {
                                mp.pause();
                            }
                        }
                    }
                    else if(text1.equals("previous"))
                    {

                        if(currentSongIndex > 0){

                            playSong(currentSongIndex - 1);
                            //currentSongIndex = currentSongIndex - 1;
                        }else{
                            // play last song
                            playSong(songsList.size());
                            //currentSongIndex = songsList.size() - 1;
                        }
                    }

                    else if(text1.equals("next"))
                    {
                        if(currentSongIndex < (songsList.size() - 1)){
                            playSong(currentSongIndex + 1);
                            currentSongIndex = currentSongIndex + 1;
                        }else{
                            // play first song
                            playSong(0);
                            currentSongIndex = 0;
                        }
                    }
                    else if(text1.equals("repeat"))
                    {
                        if(isRepeat)
                        {
                            isRepeat = false;
                        }else
                            {
                            isRepeat = true;
                            }
                    }
                    else if(text1.equals("calculator"))
                    {
                        Intent intent = new Intent(this, Calculator.class);
                        startActivity(intent);
                        break;
                    }
                    else if(text1.equals("home"))
                    {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        break;
                    }
                    else if(text1.equals("commit"))
                    {
                        sAnswers.add(studentAns.getText().toString());
                        Toast.makeText(Quiz.this, sAnswers.get(g), Toast.LENGTH_SHORT).show();
                        g++;
                    }
                    else if(text1.equals("save"))
                    {
                        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
                        {
                            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED)
                            {
                                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                                requestPermissions(permissions, STORAGE_CODE);
                            }
                            else
                            {
                                savePDF();
                            }
                        }
                    }
                    else if(text1.equals("information"))
                    {
                        initialText = "play, pause, previous, next, repeat, calculator, home, commit, save";
                        initializeTextToSpeech();
                    }
                }
            }
        }

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:
            {
                if (resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                    String text = res.get(0);
                    studentAns.setText(text);
                }
                break;
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode)
        {
            case STORAGE_CODE:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    //Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    savePDF();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
}


