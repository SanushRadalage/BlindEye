package com.mad.calculator;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class Calculator extends AppCompatActivity {

    EditText n1, n2;
    ConstraintLayout cons;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    private TextToSpeech myTTS;
    String speak;
    ArrayList<String> answers = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);

        n1 = findViewById(R.id.combination);
        n2 = findViewById(R.id.answer);
        cons = findViewById(R.id.calculatorTouch);
        final Vibrator vibe = (Vibrator) Calculator.this.getSystemService(Context.VIBRATOR_SERVICE);

        cons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vibe.vibrate(200);
                speak();

            }
        });
        speak = "I'm calculator, please tap on the screen and tell your problem";
        initializeTextToSpeech();
    }

    private void initializeTextToSpeech()
    {
        myTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status)
            {
                if(myTTS.getEngines().size() == 0)
                {
                    Toast.makeText(Calculator.this, "There is no TTS engine on your device",
                            Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    myTTS.setLanguage(Locale.UK);
                    myTTS.setSpeechRate(1);
                    aiVoice(speak);

                }
            }
        });
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
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case REQUEST_CODE_SPEECH_INPUT: {
                if(resultCode == RESULT_OK && null != data){
                    {

                        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                        // break
                        String[] rst = new String[result.size()];
                        rst[0] = result.get(0);
                        int min = rst[0].length();
                        String minimum =rst[0];
                        String answer="";

                        for (int i = 1; i < result.size(); i++ ){
                            rst[i] = result.get(i);
                            if ( min > rst[i].length()) {
                                min = rst[i].length();
                                minimum = rst[i];
                            }
                        }
                        answer = minimum;
                        n1.setText(answer);

                        String[] nums;
                        nums = answer.split(" ");


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
                                else if (nums[1].equals("divide") || nums[1].equals("/")){
                                    total = Integer.valueOf(nums[0]) / Integer.valueOf(nums[2]);
                                }
                                else if(nums[0].equals("+") || nums[0].equals("-") || nums[0].equals("*") || nums[0].equals("/") || nums[0].equals("="))
                                {
                                    System.out.print(" ");
                                }

                                String a = Integer.toString(total);
                                n2.setText(a);
                                String an = n2.getText().toString();
                                myTTS.speak(an+ " is the answer", TextToSpeech.QUEUE_FLUSH, null);
                            }
                            else {
                                int total = 0;
                                if (true){
                                    total = Integer.valueOf(nums[0]) + Integer.valueOf(nums[1]);
                                    //Toast.makeText(this, Integer.valueOf(nums[1]), Toast.LENGTH_SHORT).show();
                                }


                                String a = Integer.toString(total);
                                n2.setText(a);
                                String an = n2.getText().toString();
                                myTTS.speak(an+ " is the answer", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }catch (Exception i ){
                            Toast.makeText(this, i.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                }
                break;
            }
        }


    }
}
