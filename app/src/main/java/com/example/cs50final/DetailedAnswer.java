package com.example.cs50final;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

public class DetailedAnswer extends AppCompatActivity {
    TextView detailedAnswerTW;
    TextView loadingTW;
    String apiCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.detailed);
        loadingTW = findViewById(R.id.loadingTextViewDetailed);
        detailedAnswerTW = findViewById(R.id.detailedAnswerTW);
        Intent intent = getIntent();
        apiCall = intent.getStringExtra("apiCall");
        callGeminiAPI(detailedAnswerTW);
    }
    public void callGeminiAPI(TextView view) {
        GenerativeModel gm = new GenerativeModel(
                "gemini-1.5-flash",
                "AIzaSyCSesB2lSa0JB1759-Bz5dTyQCbwXuUNcg");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(apiCall + "Give your information with details, arguments and links. No more than 150 words.")
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    loadingTW.setVisibility(View.INVISIBLE);
                    String resultText = result.getText();
                    view.setText(resultText);
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    loadingTW.setVisibility(View.INVISIBLE);
                    view.setText("Failure!");
                }
            }, this.getMainExecutor());
        }
    }
}
