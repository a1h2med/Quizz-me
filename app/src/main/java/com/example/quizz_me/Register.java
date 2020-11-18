package com.example.quizz_me;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Locale;

public class Register extends AppCompatActivity {
    TextInputLayout textField, textFieldPass;
    EditText editText, editTextPassword;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private int flag = 0;
    Button nextButton, uploadButton;
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

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TastyToast.makeText(getApplicationContext(), "please make sure that you are in a quite place.", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                speak();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag == 1) {
                    registration();
                    Intent intent = new Intent(Register.this, QuizQuestionsActivity.class);
                    startActivity(intent);
                }
                else {
                    TastyToast.makeText(getApplicationContext(), "please record your voice first.", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
        });
    }

    private void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Say: I'll be the best one day.");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e){
            TastyToast.makeText(getApplicationContext(), ""+e.getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //ToDo: Upload text here and save it to database locally to compare it.
                flag = 1;
            }
        }
    }

    private void registration(){
        final String email = editText.getText().toString().trim();
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
            editText.setError("Password is required!");
            editText.requestFocus();
            return;
        }
        if (password.length() < 6){
            editText.setError("Min password length should be 6 characters!");
            editText.requestFocus();
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
                                progressBar.setVisibility(View.GONE);
                            }
                            else {
                                TastyToast.makeText(getApplicationContext(), "Failed to register! Try again!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
                else {
                    TastyToast.makeText(getApplicationContext(), "Failed to register!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}