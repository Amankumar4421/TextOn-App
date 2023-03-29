package com.amankr.textrecognition;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MaterialButton inputImageBtn;
    private  MaterialButton recognizeTextBtn;
    private ShapeableImageView imageIv;
    private EditText recognizedTextEt;

    TextToSpeech tts;
    // Tag
    String recognizedText;

    private static final String TAG="MAIN_TAG";

    private Uri imageUri=null;

    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=101;

    private String[] cameraPermissions;
    private String[] storagePermissions;

    // progress dialog
    private ProgressDialog progressDialog;
    boolean f=false;

    private TextRecognizer textRecognizer;
    private static final int REQUEST_PERMISSION_CODE = 1;
    SpeakBro obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputImageBtn = findViewById(R.id.inputImageBtn);
        recognizeTextBtn = findViewById(R.id.recognizeTextBtn);
//        identifybtn = findViewById(R.id.identify);
        imageIv = findViewById(R.id.imageIv);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.baseline_home_24);
        getSupportActionBar().setTitle("  HOME");



//        EditText t=findViewById(R.id.recognizeTextEt);
//        Button b=findViewById(R.id.voicebtn);

//        b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
//                    @Override
//                    public void onInit(int i) {
//                        if(i==TextToSpeech.SUCCESS){
//                            tts.setLanguage(Locale.US);
//                            tts.setSpeechRate(1);
//                            tts.speak(t.getText().toString(),TextToSpeech.QUEUE_ADD,null);
//                        }
//                    }
//                });
//            }
//        });

        obj=new SpeakBro();
        obj.speakGivenText("To take image say open camera",MainActivity.this);
        Thread thread = new Thread(){
            public void run(){
                try {
                    sleep(4000);
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
                        Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        thread.start();



        // init array of permission for camera,galery
        cameraPermissions=new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);




        textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);



        inputImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showInputImageDialog();
            }
        });


        recognizeTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageUri==null)
                {
                    Toast.makeText(MainActivity.this, "Pick image first.....", Toast.LENGTH_SHORT).show();
                }else {
                    recognizeTextFromImage();
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
                if(uid.equals("open camera")){
                    pickImageCamera();
                }
                else{
                    obj.speakGivenText("Ok Your wish",getApplicationContext());
                }
            }
        }
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

    private void recognizeTextFromImage() {
        Log.d(TAG,"recognizeTextFromImage: ");

        progressDialog.setMessage("Preparing image.......");
        progressDialog.show();

        try {
            InputImage inputImage=InputImage.fromFilePath(this,imageUri);
            progressDialog.setMessage("Recognizing text....");

            Task<Text> textTaskResult= textRecognizer.process(inputImage).addOnSuccessListener(new OnSuccessListener<Text>() {
                        @Override
                        public void onSuccess(Text text) {

                            progressDialog.dismiss();
                            recognizedText=text.getText();
                            Log.d(TAG,"onSuccess: recognizedText: "+recognizedText);
//                            Toast.makeText(MainActivity.this, recognizedText, Toast.LENGTH_LONG).show();
//                            obj.speakGivenText(recognizedText,getApplicationContext());
                            Intent intent = new Intent(getApplicationContext(),ResultActivity.class);
                            intent.putExtra("text",recognizedText);
                            startActivity(intent);

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Log.d(TAG,"onFailure: ",e);
                            Toast.makeText(MainActivity.this, "Failed recognizing text due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }catch (Exception e){
            progressDialog.dismiss();
            Log.d(TAG,"recognizeTextFromImage: ",e);

            Toast.makeText(this, "Failed preparing image due to "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void showInputImageDialog() {
        PopupMenu popupMenu=new PopupMenu(this,inputImageBtn);


        popupMenu.getMenu().add(Menu.NONE,1,1,"CAMERA");
        popupMenu.getMenu().add(Menu.NONE,2,2,"GALLERY");


        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id=menuItem.getItemId();
                if (id==1){
                    Log.d(TAG,"onMenuItemClik: Camera Clicked.....");

                    if(checkCameraPermissions()){
                        pickImageCamera();
                    }else {
                        requestCameraPermissions();
                    }

                }else if (id==2){

                    Log.d(TAG,"onMenuItemClik: Gallery Clicked.....");


                    if(checkStoragePermission()){
                        pickImageGallery();

                    }else {
                        requestStoragePermission();
                    }
                }
                return true;
            }
        });

    }


    private void pickImageGallery(){
        Log.d(TAG,"pickImageGallery: ");
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);

    }
    private ActivityResultLauncher<Intent> galleryActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){
                Intent data=result.getData();
                imageUri=result.getData().getData();
                Log.d(TAG,"onActivityResult: imageUri "+imageUri);
                imageIv.setImageURI(imageUri);

            }else {
                Log.d(TAG,"onActivityResult: Cancelled ");
                Toast.makeText(MainActivity.this, "Cancelled..", Toast.LENGTH_SHORT).show();
            }

        }
    });

    private void pickImageCamera(){
        Log.d(TAG,"pickImageCamera: ");
        ContentValues values=new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Sample Title");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Sample DESCRIPTION");

        imageUri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        cameraActivityResultLauncher.launch(intent);
    }

    private ActivityResultLauncher<Intent> cameraActivityResultLauncher=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){
                Log.d(TAG,"onActivityResult: imageUri "+imageUri);
                imageIv.setImageURI(imageUri);
                recognizeTextFromImage();
            }else{
                Log.d(TAG,"onActivityResult: Cancelled ");
                Toast.makeText(MainActivity.this, "Cancelled..", Toast.LENGTH_SHORT).show();

            }

        }
    });

    private boolean checkStoragePermission(){

        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean cameraResult=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean storageResult=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)== (PackageManager.PERMISSION_GRANTED);

        return cameraResult && storageResult;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);

    }
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        switch(requestCode){

            case CAMERA_REQUEST_CODE:{
                if(grantResults.length>0)
                {
                    boolean cameraAccepted = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted)
                    {
                        pickImageCamera();


                    }else {

                        Toast.makeText(MainActivity.this, "camera & storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Cancelled..", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length>0)
                {
                    boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickImageGallery();
                    }else {
                        Toast.makeText(MainActivity.this, "Storage permissions is required", Toast.LENGTH_SHORT).show();

                    }
                }

            }
            break;
        }
    }

}