package com.example.quizz_me;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.sdsmdg.tastytoast.TastyToast;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    private ProgressDialog mProgress;
    FirebaseAuth mAuth;
    private String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextInputLayout textField = findViewById(R.id.textField);
        editText = findViewById(R.id.idText);
        mProgress = new ProgressDialog(this);
        Button idButton = findViewById(R.id.Next);
        mAuth = FirebaseAuth.getInstance();
        textField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        idButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailChecker();
            }
        });
    }
    private void emailChecker(){
        email = editText.getText().toString().trim();

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
        mProgress.setMessage("Checking email...");
        mProgress.show();
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if (task.getResult().getSignInMethods().size() == 0){
                    mProgress.dismiss();
                    TastyToast.makeText(getApplicationContext(), "please enter a valid teacher email!", TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }else {
                    mProgress.dismiss();
                    startActivity(new Intent(MainActivity.this, SignIn.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    }
}