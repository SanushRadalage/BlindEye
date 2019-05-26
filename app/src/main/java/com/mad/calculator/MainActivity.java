package com.mad.calculator;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout n;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private TextToSpeech myTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        n = findViewById(R.id.touch);
        final Vibrator vibe = (Vibrator) MainActivity.this.getSystemService(Context.VIBRATOR_SERVICE);

        Activity act = MainActivity.this;
        checkPermission(act);
        createFolder();

        n.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibe.vibrate(200);
                speak();
            }
        });

        initializeTextToSpeech();
    }

    private void createFolder(){
        Toast.makeText(this, "ON METHOD", Toast.LENGTH_SHORT).show();
        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Calcu");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
        } else {
            Toast.makeText(this, "FAILED LOADING FILE......", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPermission(Activity act){
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        1);
            }
        } else {
            Toast.makeText(act, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(act,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(act,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }
        } else {
            Toast.makeText(act, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH_INPUT:
            {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();

                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;

            case 1:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            break;
        }

    }


    private void initializeTextToSpeech() {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (myTTS.getEngines().size() == 0) {
                    Toast.makeText(MainActivity.this, "There is no TTS engine on your device",
                            Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    myTTS.setLanguage(Locale.UK);
                    myTTS.setSpeechRate(1);
                    aiVoice("Welcome to blindeye, Touch the screen and say 1 for voice recognize calculator, 2 for attempt the quiz, 3 for teachers mode, 4 for virtual key board & 5 for repeat this. Thank you");
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
        String speak = "Let's Talk";

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, speak);

        try
        {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

        }
        catch (Exception e)
        {

        }
    }

    private void invalidRecognize()
    {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                if(myTTS.getEngines().size() == 0)
                {
                    Toast.makeText(MainActivity.this, "There is no TTS engine on your device",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.US);
                    aiVoice("unrecognized word, try again");
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

                    if(text1.equals("1") || text1.equals("one"))
                    {
                        try
                        {
                            Intent intent = new Intent(MainActivity.this, Calculator.class);
                            startActivity(intent);
                            break;
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                    else if(text1.equals("2") || text1.equals("two") || text1.equals("do") || text1.equals("Tu"))
                    {
                        Intent intent = new Intent(MainActivity.this, Quiz.class);
                        startActivity(intent);
                        break;

                    }
                    else if(text1.equals("3") || text1.equals("three") || text1.equals("free") || text1.equals("tree"))
                    {
                        Intent intent = new Intent(MainActivity.this, TeachersMode.class);
                        startActivity(intent);
                        break;
                    }
                    else if(text1.equals("4") || text1.equals("for") || text1.equals("foot") || text1.equals("full"))
                    {
                        Intent intent = new Intent(MainActivity.this, VirtualKeyB.class);
                        startActivity(intent);
                        break;
                    }
                    else if(text1.equals("5"))
                    {
                        initializeTextToSpeech();
                        break;
                    }
                    else
                    {
                        invalidRecognize();
                    }
                }

            }
        }
    }


    @Override
    protected void onPause(){
        super.onPause();
        myTTS.shutdown();
    }
}
