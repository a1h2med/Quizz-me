package com.example.quizz_me;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class showFinalScore extends AppCompatActivity {
    private TextView score;
    private Button done;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_final_score);
        score = findViewById(R.id.score);
        done = findViewById(R.id.done);

        String score_str = getIntent().getStringExtra("SCORE");
        score.setText(score_str);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(showFinalScore.this, MainActivity.class);
                showFinalScore.this.startActivity(intent);
                showFinalScore.this.finish();
            }
        });
    }
}