package com.amankr.textrecognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    TextToSpeech ttsi;
    SpeakBro obj;
    CreateAccountActivity createobj;
    int f=2;
    private static final int REQUEST_PERMISSION_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();



//        Intent i = new Intent(LoginActivity.this,MainActivity.class);
//        startActivity(i);
//        finish();



        obj = new SpeakBro();
        obj.speakGivenText("Tell Your Password or if you don't have account say create account",getApplicationContext());

        createobj = new CreateAccountActivity();

        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(6000);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listenning tell your password");
                    try {
                        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        thread.start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String uid = result.get(0);
                uid=uid.toLowerCase().trim();
                if(uid.equals(createobj.password)){
                    obj.speakGivenText("Correct Password Openning app",getApplicationContext());
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(i);
                    finish();
                }
                else if(uid.equals("create account")){
                    Intent intent = new Intent(LoginActivity.this,CreateAccountActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    obj.speakGivenText("Wrong Password Please try Again",getApplicationContext());
                    Thread thread = new Thread(){
                        public void run(){
                            try {
                                sleep(3000);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }
                            finally {
                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listenning tell your password");
                                try {
                                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };
                    thread.start();
                }
            }
        }
    }




}