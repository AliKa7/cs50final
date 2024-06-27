package com.example.cs50final;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;


public class MainActivity extends AppCompatActivity {
    Button button;
    Button knowMoreButton;
    TextView aiAnswerTW;
    TextView loadingTW;

    EditText dateET;
    EditText countryET;
    EditText cityET;
    EditText pointET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        knowMoreButton = findViewById(R.id.knowMoreButton);
        knowMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DetailedAnswer.class);
            intent.putExtra("apiCall", collectCallMessage());
            startActivity(intent);
        });
        button = findViewById(R.id.button);
        knowMoreButton.setVisibility(View.INVISIBLE);
        button.setEnabled(false);
        dateET = findViewById(R.id.dateET);
        countryET = findViewById(R.id.countryET);
        cityET = findViewById(R.id.cityET);
        pointET = findViewById(R.id.pointET);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                button.setEnabled(!dateET.getText().toString().trim().isEmpty() &&
                        !countryET.getText().toString().trim().isEmpty() &&
                        !cityET.getText().toString().trim().isEmpty() &&
                        !pointET.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        dateET.addTextChangedListener(textWatcher);
        countryET.addTextChangedListener(textWatcher);
        cityET.addTextChangedListener(textWatcher);
        pointET.addTextChangedListener(textWatcher);
        aiAnswerTW = findViewById(R.id.aiAnswerTextView);
        loadingTW = findViewById(R.id.loadingTextView);
        aiAnswerTW.setVisibility(View.INVISIBLE);
        loadingTW.setVisibility(View.INVISIBLE);
        button.setOnClickListener(v -> callGeminiAPI(aiAnswerTW));
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void callGeminiAPI(TextView view) {
        loadingTW.setVisibility(View.VISIBLE);
        aiAnswerTW.setVisibility(View.INVISIBLE);
        knowMoreButton.setVisibility(View.INVISIBLE);
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                "AIzaSyCSesB2lSa0JB1759-Bz5dTyQCbwXuUNcg");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(collectCallMessage() + "Give your answer only 'Truth' or 'False information")
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    loadingTW.setVisibility(View.INVISIBLE);
                    String resultText = result.getText();
                    knowMoreButton.setVisibility(View.VISIBLE);
                    view.setVisibility(View.VISIBLE);
                    view.setText(resultText);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    loadingTW.setVisibility(View.INVISIBLE);
                    view.setVisibility(View.VISIBLE);
                    view.setText("Failure!");
                }
            }, this.getMainExecutor());
        }
    }

    public String collectCallMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("I made an app FakeChecker. In that, person type in information about some recent news or fact, that he heard, and YOU should check whether this information is fake, or not.");
        stringBuilder.append("So, you are a fact checker. Now, check this information: ");
        stringBuilder.append("Country, to which this fact belong: ").append(countryET.getText());
        stringBuilder.append("Date: ").append(dateET.getText());
        stringBuilder.append("City or region: ").append(cityET.getText());
        stringBuilder.append("The point of the fact: ").append(pointET.getText());
        return stringBuilder.toString();
    }
}