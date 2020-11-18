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
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.Locale;

public class SignIn extends AppCompatActivity {
    TextInputLayout textField, textFieldPass;
    ArrayList<String> resultedSpeech;
    EditText editText, editTextPass;
    TextView textView;
    Button nextButton;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        textField = findViewById(R.id.name);
        editText = findViewById(R.id.nameTextField);
        nextButton = findViewById(R.id.signingNext);
        textView = findViewById(R.id.firstTime);
        textFieldPass = findViewById(R.id.logPassword);
        editTextPass = findViewById(R.id.logUserPassword);
        progressBar = findViewById(R.id.progressBar2);

        textFieldPass.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextPass.setText("");
            }
        });
        textField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
                //ToDo: Load voice from online database
                //ToDo: If matched load quiz, else ask him to rerecord.
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignIn.this, Register.class);
                startActivity(intent);
            }
        });
        mAuth = FirebaseAuth.getInstance();
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
                resultedSpeech = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            }
        }
    }

    private void Login(){
        String email = editText.getText().toString().trim();
        String password = editTextPass.getText().toString().trim();

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
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    speak();
                } else {
                    TastyToast.makeText(getApplicationContext(), "Failed to register!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

}