package com.example.quizz_me;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestionsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView question, questionCounter, timer;
    private Button option1, option2, option3, option4;
    private List<questionModel> questionsList;
    private CountDownTimer countDownTimer;
    private List<String> userAnswers;
    private FirebaseFirestore firestore;
    private int questionNumber = 0, score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_questions);
        question = findViewById(R.id.questionText);
        questionCounter = findViewById(R.id.questionNumbers);
        timer = findViewById(R.id.timer);
        option1 = findViewById(R.id.firstChoice);
        option2 = findViewById(R.id.secondChoice);
        option3 = findViewById(R.id.thirdChoice);
        option4 = findViewById(R.id.fourthChoice);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);

        firestore = FirebaseFirestore.getInstance();

        getQuestionsList();
    }

    private void getQuestionsList(){
        questionsList = new ArrayList<>();
//        questionsList.add(new questionModel("Question 1", "A", "B", "C", "D", "A"));
//        questionsList.add(new questionModel("Question 2", "A", "B", "C", "D", "A"));
//        questionsList.add(new questionModel("Question 3", "A", "B", "C", "D", "A"));
//        questionsList.add(new questionModel("Question 4", "A", "B", "C", "D", "A"));
        firestore.collection("Quiz-me").document("Questions").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                   DocumentSnapshot questions = task.getResult();
                   questionsList.add(new questionModel(questions.getString("Question"),
                           questions.getString("A"),
                           questions.getString("B"),
                           questions.getString("C"),
                           questions.getString("D"),
                           questions.getString("Answer")));
                    setQuestionList();
                } else{
                    TastyToast.makeText(getApplicationContext(), task.getException().getMessage(), TastyToast.LENGTH_LONG, TastyToast.ERROR);
                }
            }
        });
    }
    @SuppressLint("SetTextI18n")
    private void setQuestionList(){
        timer.setText(String.valueOf(10));

        question.setText(questionsList.get(questionNumber).getQuestion());
        option1.setText(questionsList.get(questionNumber).getOptionA());
        option2.setText(questionsList.get(questionNumber).getOptionB());
        option3.setText(questionsList.get(questionNumber).getOptionC());
        option4.setText(questionsList.get(questionNumber).getOptionD());

        questionCounter.setText((questionNumber+1) + "/" + (questionsList.size()));
        startTimer();
    }
    private void startTimer(){
        countDownTimer = new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long l) {
                if (l < 10000)
                    timer.setText(String.valueOf(l/1000));
            }

            @Override
            public void onFinish() {
                changeQuestion();
            }
        };
        countDownTimer.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View view) {
        String selectedOption = "";
        switch (view.getId()){
            case R.id.firstChoice:
                selectedOption = "A";
                break;

            case R.id.secondChoice:
                selectedOption = "B";
                break;

            case R.id.thirdChoice:
                selectedOption = "C";
                break;

            case R.id.fourthChoice:
                selectedOption = "D";
                break;
        }
        countDownTimer.cancel();
        storeAnswer(selectedOption, view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void storeAnswer(String selectedOption, View view){
        userAnswers.add(selectedOption);
        ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.BLUE));
        if (selectedOption.equals(questionsList.get(questionNumber).getAnswer())){
            score += 1;
            ((Button)view).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(String.valueOf(R.drawable.rectangle_button))));
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
            }
        }, 1000);
    }
    private void changeQuestion(){
        if (questionNumber < questionsList.size() - 1){
            questionNumber += 1;
            nextQuestion();
        }
        else {
            Intent intent = new Intent(QuizQuestionsActivity.this, showFinalScore.class);
            intent.putExtra("SCORE", String.valueOf(score)+"/"+String.valueOf(questionsList.size()));
            startActivity(intent);
            QuizQuestionsActivity.this.finish();
        }
    }
    @SuppressLint("SetTextI18n")
    private void nextQuestion(){

        question.setText(questionsList.get(questionNumber).getQuestion());
        option1.setText(questionsList.get(questionNumber).getOptionA());
        option2.setText(questionsList.get(questionNumber).getOptionB());
        option3.setText(questionsList.get(questionNumber).getOptionC());
        option4.setText(questionsList.get(questionNumber).getOptionD());

        questionCounter.setText((questionNumber + 1) + "/" + (questionsList.size()));
        timer.setText(String.valueOf(10));
        startTimer();
    }
}