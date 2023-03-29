package com.amankr.textrecognition;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class CreateAccountActivity extends AppCompatActivity {

    public static String password = "open app";
    SpeakBro obj;
    public static String name="Aman Kumar";
    boolean flag = false;

    CreateAccountActivity createobj;

    private static final int REQUEST_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        getSupportActionBar().hide();

        obj = new SpeakBro();
        obj.speakGivenText("Tell your name",getApplicationContext());

        createobj=new CreateAccountActivity();

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
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listenning tell your name");
                    try {
                        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(CreateAccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        thread.start();
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PERMISSION_CODE)
        {
            if(resultCode==RESULT_OK && data!=null)
            {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String uid = result.get(0);
                uid=uid.toLowerCase().trim();
                if(!uid.equals(name) && flag){
                    obj.speakGivenText("Account Created successfully Now you can login.", getApplicationContext());
                    createobj.password=uid;
                    Thread thread = new Thread(){
                        public void run(){
                            try {
                                sleep(8000);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();
                    Intent i = new Intent(CreateAccountActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    name = uid;
                    obj.speakGivenText("Hello!" + name + ".Please set a password", getApplicationContext());
                    flag=true;
                    Thread thread = new Thread() {
                        public void run() {
                            try {
                                sleep(6000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            } finally {
                                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listenning tell your password");
                                try {
                                    startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(CreateAccountActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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