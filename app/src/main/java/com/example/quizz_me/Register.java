package com.example.quizz_me;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.File;
import java.io.IOException;

import static android.view.View.GONE;

public class Register extends AppCompatActivity {
    TextInputLayout textField, textFieldPass;
    EditText editText, editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private ProgressDialog mProgress;
    private String tempEmail;
    private int flag = 0;
    Button nextButton, uploadButton;
    private MediaRecorder recorder;
    private String fileName;
    private StorageReference storageReference;
    private static final String LOG_TAG = "Record_Log";
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        textField = findViewById(R.id.userName);
        editText = findViewById(R.id.usernameTextField);
        nextButton = findViewById(R.id.registerNext);
        uploadButton = findViewById(R.id.upload_audio);
        TextView loginFromReg = findViewById(R.id.loginFromReg);
        editTextPassword = findViewById(R.id.userPassword);
        textFieldPass = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName += "/recorded_audio.3gp";
        storageReference = FirebaseStorage.getInstance().getReference();
        mProgress = new ProgressDialog(this);

        textField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        textFieldPass.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPassword.setText("");
            }
        });

        loginFromReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this, SignIn.class));
            }
        });

        uploadButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    return true;
                }
                return false;
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registration();
                if (flag == 1) {
                    Intent intent = new Intent(Register.this, QuizQuestionsActivity.class);
                    startActivity(intent);
                }
                else {
                    TastyToast.makeText(getApplicationContext(), "please record your voice first.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    progressBar.setVisibility(GONE);
                }
            }
        });
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        uploadAudio();
    }

    private void uploadAudio(){
        mProgress.setMessage("Uploading Audio...");
        mProgress.show();
        flag = 1;
        StorageReference filePath = storageReference.child("Audio/"+tempEmail+".3gp");
        Uri uri = Uri.fromFile(new File(fileName));
        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                mProgress.dismiss();
            }
        });
    }

    private void registration(){
        final String email = editText.getText().toString().trim();
        tempEmail = email;
        final String password = editTextPassword.getText().toString().trim();
        if (email.isEmpty()){
            editText.setError("Email is required!");
            editText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editText.setError("Please provide a valid email!");
            editText.requestFocus();
            return;
        }
        if (password.isEmpty()){
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if (password.length() < 6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User(email, password);
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            TastyToast.makeText(getApplicationContext(), "User has been registered successfully!", TastyToast.LENGTH_LONG, TastyToast.SUCCESS);
                                progressBar.setVisibility(GONE);
                            }
                            else {
                                    TastyToast.makeText(getApplicationContext(), "Failed to register! Try again!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                    progressBar.setVisibility(GONE);
                                }
                            }
                        });
                    }
                    else {
//                    TastyToast.makeText(getApplicationContext(), "Failed to register!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                        progressBar.setVisibility(GONE);
                    }
                    }
                });
            }
        }