package com.example.quizz_me;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Locale;

public class Register extends AppCompatActivity {
    TextInputLayout textField;
    EditText editText;
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
        textField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo: Upload user voice to database.
                TastyToast.makeText(getApplicationContext(), "please make sure that you are in a quite place.", TastyToast.LENGTH_LONG, TastyToast.WARNING);
                speak();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo: Take the user to the quiz.
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
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                //ToDo: Upload text here and save it to database locally to compare it.
            }
        }
    }
}