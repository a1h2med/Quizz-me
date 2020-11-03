package com.example.quizz_me;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

public class SignIn extends AppCompatActivity {
    TextInputLayout textField;
    EditText editText;
    TextView textView;
    Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        textField = findViewById(R.id.name);
        editText = findViewById(R.id.nameTextField);
        nextButton = findViewById(R.id.SecondNext);
        textView = findViewById(R.id.firstTime);
        textField.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo: Load voice from online database, then PoP-UP a box, asking user to speak, then compare with loaded one.
                //ToDo: If matched load quiz, else ask him to rerecord.
            }
        });
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //ToDo: navigate to registration page
            }
        });
    }
}