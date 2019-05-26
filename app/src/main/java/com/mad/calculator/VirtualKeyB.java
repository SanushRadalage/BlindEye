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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class VirtualKeyB extends AppCompatActivity implements MediaPlayer.OnCompletionListener, SeekBar.OnSeekBarChangeListener {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private static final int  STORAGE_CODE = 1000;

    EditText x, y ;
    Button nu1, nu2, nu3, nu4, nu5, nu6, nu7, nu8, nu9, nu0;
    ImageView voice, answer, submit, ply, rply, save, prev, next;
    Button  ad, sub, div, mul, equ, reset;


    TextToSpeech mTTS;
    String number;
    int i = 1;
    String textVoice;
    String a;
    int g = 0;

    ArrayList<String> answers = new ArrayList<String>();

    private  MediaPlayer mp;
    private SongsManager songManager;
    private Utilities utils;
    private int currentSongIndex = 0;
    private boolean isShuffle = false;
    private boolean isRepeat = false;
    private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

    float mValueOne, mValueTwo;

    boolean crunchifyAddition, mSubtract, crunchifyMultiplication, crunchifyDivision;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_virtualkeyb);

        equ = findViewById(R.id.eqls);
        voice = findViewById(R.id.spk);
        x = findViewById(R.id.n1);
        y = findViewById(R.id.n2);
        nu1 = findViewById(R.id.none);
        nu2 = findViewById(R.id.ntwo);
        nu3 = findViewById(R.id.nthree);
        nu4 = findViewById(R.id.nfour);
        nu5 = findViewById(R.id.nfive);
        nu6 = findViewById(R.id.nsix);
        nu7 = findViewById(R.id.nseven);
        nu8 = findViewById(R.id.neight);
        nu9 = findViewById(R.id.nnine);
        nu0 = findViewById(R.id.nzero);
         ad = findViewById(R.id.add);
         sub = findViewById(R.id.subs);
         div = findViewById(R.id.divide);
         mul = findViewById(R.id.multiply);
         ply = findViewById(R.id.play);
         rply = findViewById(R.id.re);
         reset = findViewById(R.id.reset);
         save = findViewById(R.id.save);
         submit = findViewById(R.id.submit);
         answer = findViewById(R.id.answer);
         prev = findViewById(R.id.backward);
         next = findViewById(R.id.forward);

        int permissionCheck = ContextCompat.checkSelfPermission(VirtualKeyB.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (ContextCompat.checkSelfPermission(VirtualKeyB.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(VirtualKeyB.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(VirtualKeyB.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        permissionCheck);
            }
        }


        mp = new MediaPlayer();
        songManager = new SongsManager();
        utils = new Utilities();
        mp.setOnCompletionListener(this); // Important
        songsList = songManager.getPlayList();


        ply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mp.isPlaying()){
                    if(mp!=null){
                        mp.pause();
                        ply.setImageResource(R.drawable.play);
                        }
                }else
                    {
                    if(mp!=null){
                        mp.start();
                        ply.setImageResource(R.drawable.pause);
                    }
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                answers.add(y.getText().toString());
                Toast.makeText(VirtualKeyB.this, answers.get(g), Toast.LENGTH_SHORT).show();
                g++;
            }
        });

        rply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRepeat){
                    isRepeat = false;
                    //Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();
                }else{
                    isRepeat = true;
                    //Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                    isShuffle = false;
                }
            }
        });



        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS)
                {
                    int result = mTTS.setLanguage(Locale.US);
                    mTTS.setSpeechRate(1);
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

        nu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "1");
                number = "one";
                initializeTextToSpeech();

            }
        });

        nu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "2");
                number = "two";
                initializeTextToSpeech();

            }
        });


        nu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                x.setText(x.getText() + "3");
                number = "three";
                initializeTextToSpeech();

            }
        });
        nu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "4");
                number = "four";
                initializeTextToSpeech();

            }
        });
        nu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "5");
                number = "five";
                initializeTextToSpeech();

            }
        });
        nu6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "6");
                number = "six";
                initializeTextToSpeech();

            }
        });
        nu7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "7");
                number = "seven";
                initializeTextToSpeech();

            }
        });
        nu8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "8");
                number = "eight";
                initializeTextToSpeech();

            }
        });
        nu9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "9");
                number = "nine";
                initializeTextToSpeech();

            }
        });
        nu0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(x.getText() + "0");
                number = "zero";
                initializeTextToSpeech();

            }
        });

        ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (x == null) {
                    x.setText("");
                } else {
                    mValueOne = Float.parseFloat(x.getText() + "");
                    crunchifyAddition = true;
                    x.setText(null);
                }
                number = "addition";
                initializeTextToSpeech();

            }
        });

        sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = "subtract";
                initializeTextToSpeech();

                mValueOne = Float.parseFloat(x.getText() + "");
                mSubtract = true;
                x.setText(null);

            }
        });

        mul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = "multiply";
                initializeTextToSpeech();
                mValueOne = Float.parseFloat(x.getText() + "");
                crunchifyMultiplication = true;
                x.setText(null);
            }
        });

        div.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                number = "divide";
                initializeTextToSpeech();
                mValueOne = Float.parseFloat(x.getText() + "");
                crunchifyDivision = true;
                x.setText(null);

            }
        });

        equ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mValueTwo = Float.parseFloat(x.getText() + "");

                if (crunchifyAddition == true) {
                    y.setText(mValueOne + mValueTwo + "");
                    crunchifyAddition = false;
                }

                if (mSubtract == true) {
                    y.setText(mValueOne - mValueTwo + "");
                    mSubtract = false;
                }

                if (crunchifyMultiplication == true) {
                    y.setText(mValueOne * mValueTwo + "");
                    crunchifyMultiplication = false;
                }

                if (crunchifyDivision == true) {
                    y.setText(mValueOne / mValueTwo + "");
                    crunchifyDivision = false;
                }

                String an = y.getText().toString();
                mTTS.speak(an, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                x.setText(null);
                y.setText(null);
                //z.setText(null);
            }
        });


        voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textVoice = "Tell Your Problem";
                speak(textVoice);
                number = "Tell Your Problem";
                initializeTextToSpeech();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(currentSongIndex > 0){

                    playSong(currentSongIndex - 1);
                    //currentSongIndex = currentSongIndex - 1;
                }else{
                    // play last song
                    playSong(songsList.size());
                    //currentSongIndex = songsList.size() - 1;
                }

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
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
        });

        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                number = "Give your answer";
                initializeTextToSpeech();
                textVoice = "Give Your Answer";

                speak(textVoice);
                y.setText(a);
            }
        });
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
                String mtext = answers.get(d);
                mDoc.add(new Paragraph(d+1 + ") " + mtext));
            }
            mDoc.close();
        }
        catch (Exception e)
        {

        }

    }

    private void initializeTextToSpeech()
    {
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                if(mTTS.getEngines().size() == 0)
                {
                    Toast.makeText(VirtualKeyB.this, "There is no TTS engine on your device",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    mTTS.setLanguage(Locale.US);
                    aiVoice(number);
                }
            }
        });
    }

    private void aiVoice(String s)
    {
        if(Build.VERSION.SDK_INT >= 21)
        {
            mTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else
        {
            mTTS.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }


    private void speak(String x)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, textVoice);

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
        number = "Question number" + String.valueOf(i);

        try {
            mp.reset();
            mp.setDataSource(songsList.get(songIndex).get("songPath"));
            mp.prepare();
            mp.start();
            //String songTitle = songsList.get(songIndex).get("songTitle");

            if(isRepeat)
            {
                number = "Repeat again";
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 100){
            currentSongIndex = data.getExtras().getInt("songIndex");
            // play selected song
            playSong(currentSongIndex);

        }

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT:
                {
                if (resultCode == RESULT_OK && null != data)
                {
                    ArrayList<String> res = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);


                        String text = res.get(0);
                        int min = text.length();
                        String answer="";
                        String[] nums;
                        nums = answer.split(" ");
                        a = nums[0];
                }
            }
        }

        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null != data)
                {

                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    // break
                    String x_test = result.get(0);
                    int min = x_test.length();
                    String answer="";
                    String y_test = result.get(1);
                    if ( min > y_test.length()) {
                        min = y_test.length();
                    }
                    String z_test = result.get(2);
                    if ( min > z_test.length()){
                        min = z_test.length();
                    }

                    if (x_test.length() == min ){
                        x.setText(result.get(0));
                        answer = result.get(0);
                    }
                    else if(y_test.length() == min){
                        x.setText(result.get(1));
                        answer = result.get(1);
                    }
                    else if (z_test.length() == min ){
                        x.setText(result.get(2));
                        answer = result.get(2);
                    }
                    String[] nums;
                    nums = answer.split(" ");
                    y.setText(nums[0]);
                    //z.setText(nums[2]);
                    try
                    {
                        if (nums.length > 2)
                        {

                            int total = 0;
                            if (nums[1].equals("+")){
                                total = Integer.valueOf(nums[0]) + Integer.valueOf(nums[2]);
                            }
                            else if (nums[1].equals("-")){
                                total = Integer.valueOf(nums[0]) - Integer.valueOf(nums[2]);
                            }
                            else if (nums[1].equals("x") || nums[1].equals("*")){
                                total = Integer.valueOf(nums[0]) * Integer.valueOf(nums[2]);
                            }
                            else if (nums[1].equals("divide") || nums[1].equals("/") ){
                                total = Integer.valueOf(nums[0]) / Integer.valueOf(nums[2]);
                            }
                            else if(nums[0].equals("+") || nums[0].equals("-") || nums[0].equals("*") || nums[0].equals("/") || nums[0].equals("="))
                            {
                                System.out.print(" ");
                            }

                            String a = Integer.toString(total);
                            y.setText(a);
                            String an = y.getText().toString();
                            mTTS.speak(an, TextToSpeech.QUEUE_FLUSH, null);
                        }
                        else {
                            int total = 0;
                            if (true){
                                total = Integer.valueOf(nums[0]) + Integer.valueOf(nums[1]);
                                //Toast.makeText(this, Integer.valueOf(nums[1]), Toast.LENGTH_SHORT).show();
                            }


                            String a = Integer.toString(total);
                            y.setText(a);
                            String an = y.getText().toString();
                            mTTS.speak(an, TextToSpeech.QUEUE_FLUSH, null);
                        }
                    }catch (Exception i ){
                        Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mTTS != null)
        {
            mTTS.stop();
            mTTS.shutdown();
        }
        super.onDestroy();
        mp.release();
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
            }
            else{
                // play first song
                playSong(0);
                currentSongIndex = 0;
                Toast.makeText(this, "fuck", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
      //  mHandler.removeCallbacks(mUpdateTimeTask);

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int totalDuration = mp.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        mp.seekTo(currentPosition);

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
