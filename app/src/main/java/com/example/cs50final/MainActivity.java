package com.example.cs50final;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
    TextView aiAnswer;
    TextView loadingTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.button);
        aiAnswer = findViewById(R.id.textView);
        loadingTextView = findViewById(R.id.loadingTextView);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callGeminiAPI(aiAnswer);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void callGeminiAPI(TextView view) {
// The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
        GenerativeModel gm = new GenerativeModel(/* modelName */ "gemini-1.5-flash",
// Access your API key as a Build Configuration variable (see "Set up your API key" above)
                /* apiKey */ "AIzaSyCSesB2lSa0JB1759-Bz5dTyQCbwXuUNcg");
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText("How are you?")
                .build();
        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    String resultText = result.getText();
                    view.setText(resultText);
                    System.out.println(resultText);
                }

                @Override
                public void onFailure(Throwable t) {
                    view.setText("Failure!");
                    t.printStackTrace();
                }
            }, this.getMainExecutor());
        }
    }

}