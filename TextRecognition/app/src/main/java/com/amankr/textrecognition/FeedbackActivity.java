package com.amankr.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class FeedbackActivity extends AppCompatActivity {
    EditText name1,feed;
    SpeakBro obj;
    Button b;
    private static final int REQUEST_PERMISSION_CODE = 1;
    DatabaseReference db=FirebaseDatabase.getInstance().getReferenceFromUrl("https://text-recognition-91155-default-rtdb.firebaseio.com/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        name1=findViewById(R.id.editTextTextPersonName);
        feed=findViewById(R.id.editTextTextMultiLine);

        obj = new SpeakBro();
        CreateAccountActivity abc = new CreateAccountActivity();
        String nam = abc.name;
        name1.setText(nam);
        obj = new SpeakBro();
        obj.speakGivenText("Tell Your FeedbacK.",getApplicationContext());

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
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listenning...");
                    try {
                        startActivityForResult(intent, REQUEST_PERMISSION_CODE);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(FeedbackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        thread.start();





        b=findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String s1=name1.getText().toString();
                final String s2=feed.getText().toString();
                if(s1.isEmpty()||s2.isEmpty()){
                    Toast.makeText(FeedbackActivity.this, "Please fill all details", Toast.LENGTH_SHORT).show();
                }
                else{
                    db.child("feedback").child(s1).child("Response").setValue(s2);
                    SpeakBro obj = new SpeakBro();
                    obj.speakGivenText("Thanks for your valuable feedback",getApplicationContext());
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
    });
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
                feed.setText(uid);
                }
            }
        }



}