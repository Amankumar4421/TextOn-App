package com.amankr.textrecognition;


import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.RegexValidator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator;
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;
import android.speech.tts.TextToSpeech;
public class ResultActivity extends AppCompatActivity {
    private Spinner fromSpinner,toSpinner;
    private TextInputEditText sourceEdt;
    private ImageView mivTv;
    private MaterialButton translateBtn;
    private TextView translatedTv;
    TextToSpeech tts;
    boolean flag=true;
    SpeakBro obj;
    String[] fromLanguages = {"English","Hindi","Afrikaans","Catalan","Bengali","Arabic"};
    String[] toLanguages = {"To","Telugu","English","Bengali","Arabic","Hindi"};
    private static final int REQUEST_PERMISSION_CODE = 1;
    int LanguageCode,fromLanguageCode,toLanguageCode=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.baseline_translate_24);
        getSupportActionBar().setTitle("  Translator");


        TextView t=findViewById(R.id.idTVTranslatedTV);
        Button b=findViewById(R.id.voicebtnffff);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        if(i==TextToSpeech.SUCCESS){
                            tts.setLanguage(Locale.US);
                            tts.setSpeechRate(1);
                            tts.speak(t.getText().toString(),TextToSpeech.QUEUE_ADD,null);
                        }
                    }
                });
            }
        });
        


        fromSpinner = findViewById(R.id.idFromSpinner);
        toSpinner = findViewById(R.id.idToSpinner);
        sourceEdt = findViewById(R.id.idEdtSource);
        mivTv = findViewById(R.id.idIVMic);
        translateBtn = findViewById(R.id.idBtnTranslate);
        translatedTv = findViewById(R.id.idTVTranslatedTV);


        Intent intent = getIntent();
        String recognizedText = intent.getStringExtra("text");
        sourceEdt.setText(recognizedText);

        
        
        obj = new SpeakBro();
        obj.speakGivenText(recognizedText+".This is the text In which language you want to translate say the name else say no",getApplicationContext());
        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(10000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listenning...");
                    try {
                        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ResultActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        thread.start();




        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguageCode = getLanguageCode(fromLanguages[i]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter fromAdapter =new ArrayAdapter(this,R.layout.spinner_item,fromLanguages);
        fromAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner.setAdapter(fromAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguageCode=getLanguageCode(toLanguages[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter toAdapter = new ArrayAdapter(this,R.layout.spinner_item,toLanguages);
        toAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        toSpinner.setAdapter(toAdapter);
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedTv.setText("");
                if(sourceEdt.getText().toString().isEmpty())
                {
                    Toast.makeText(ResultActivity.this, "Plx Enter Your Text to Translate", Toast.LENGTH_SHORT).show();
                }
//                else if(fromLanguageCode==0)
//                {
//                    Toast.makeText(ResultActivity.this, "Please Select Source Language", Toast.LENGTH_SHORT).show();
//                }
                else if(toLanguageCode==0)
                {
                    Toast.makeText(ResultActivity.this, "Plz Select the language to translate", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());
                }
            }
        });


        mivTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Speak to convert into Text");
                try{
                    startActivityForResult(intent,REQUEST_PERMISSION_CODE);
                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(ResultActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }







    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.feed:
                Intent i = new Intent(getApplicationContext(),FeedbackActivity.class);
                startActivity(i);
                break;
            case R.id.info:
                Intent intent = new Intent(getApplicationContext(),InfoActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                Toast.makeText(this, "Hellooooooooooooo", Toast.LENGTH_SHORT).show();
                String sou = result.get(0);
                sou = sou.toLowerCase().trim();
                if(sou.equals("no")||sou.equals("hindi")||sou.equals("bengali")) {
                    switch (sou){
                        case "no" :
                            break;
                        case "hindi":
                            fromLanguageCode = getLanguageCode("English");
                            toLanguageCode = getLanguageCode("Hindi");
                            translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());
                            break;
                        case "bengali":
                            fromLanguageCode = getLanguageCode("English");
                            toLanguageCode = getLanguageCode("Bengali");
                            translateText(fromLanguageCode,toLanguageCode,sourceEdt.getText().toString());
                            break;
                    }
                }
                else {
                    sourceEdt.setText(result.get(0));
                }

            }
        }
    }

    private void translateText(int fromLanguageCode, int toLanguageCode, String source)
    {
        translatedTv.setText("Downloading Model..");
        FirebaseTranslatorOptions options =new FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(fromLanguageCode)
                .setTargetLanguage(toLanguageCode)
                .build();
        FirebaseTranslator translator=FirebaseNaturalLanguage.getInstance().getTranslator(options);

        FirebaseModelDownloadConditions conditions=new FirebaseModelDownloadConditions.Builder().build();
        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                translatedTv.setText("Translating...");
                translator.translate(source).addOnSuccessListener(new OnSuccessListener<String>() {
                    @Override
                    public void onSuccess(String s) {
                        translatedTv.setText(s);
                        obj.speakGivenText(s,getApplicationContext());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResultActivity.this, "Fail to translate", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ResultActivity.this, "Fail to download language model "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public int getLanguageCode(String language)
    {
        int languageCode = 0;
        switch(language)
        {
            case "English":
                languageCode=  FirebaseTranslateLanguage.EN;
                break;
            case "Afrikaans":
                languageCode= FirebaseTranslateLanguage.AF;
                break;
            case "Arabic":
                languageCode= FirebaseTranslateLanguage.AR;
                break;
            case "Bengali":
                languageCode=FirebaseTranslateLanguage.BN;
                break;
            case "Catalan":
                languageCode= FirebaseTranslateLanguage.CA;
                break;
            case "Hindi":
                languageCode=FirebaseTranslateLanguage.HI;
                break;
            case "Telugu":
                languageCode=FirebaseTranslateLanguage.TE;
                break;
            default :
                languageCode=0;
        }
        return  languageCode;
    }
}