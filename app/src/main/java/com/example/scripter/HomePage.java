package com.example.scripter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class HomePage extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_CODE = 1;

    private SpeechRecognizer speechRecognizer;
    private boolean isRecording = false;
    private boolean isPaused = true;
    private StringBuilder transcribedText = new StringBuilder();

    private ImageButton micButton, pauseOrPlayButton, finishButton, cancelButton, recordlinglistButton;

    private TextView timeTextView;
    private Handler timerHandler = new Handler();
    private int seconds = 0;
    private boolean isTimerRunning = false;
    private Runnable timerRunnable = new Runnable() {
        public void run() {
            if (isRecording && !isPaused) {
                seconds++;
                updateTimerText();
                timerHandler.postDelayed(this, 1000); // Update every second
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        micButton = findViewById(R.id.micButton);
        pauseOrPlayButton = findViewById(R.id.pauseOrPlayButton);
        finishButton = findViewById(R.id.finishButton);
        cancelButton = findViewById(R.id.cancelButton);
        recordlinglistButton = findViewById(R.id.recordlinglistButton);

        requestMicrophonePermission();
        setupSpeechRecognizer();

        micButton.setOnClickListener(v -> toggleMic());
        pauseOrPlayButton.setOnClickListener(v -> togglePause());
        finishButton.setOnClickListener(v -> finishRecording());
        cancelButton.setOnClickListener(v -> cancelRecording());

        timeTextView = findViewById(R.id.timeTextView);

        updateButtonsState(false);
    }

    private void requestMicrophonePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_PERMISSION_CODE);
        }
    }

    private void setupSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}


            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    transcribedText.append(matches.get(0)).append("\n");
                    Log.d("SpeechRecognizer", "Transcription: " + matches.get(0));
                }
            }

        });
    }

    private void toggleMic() {
        if (isRecording) {
            return;
        }
        transcribedText.setLength(0);
        isRecording = true;
        micButton.setImageResource(R.drawable.mic_button_on);

        updateButtonsState(true);
        recordlinglistButton.setClickable(false);
        recordlinglistButton.setImageResource(R.drawable.recordinglist_button_locked);

        isPaused = false;
        pauseOrPlayButton.setImageResource(R.drawable.pause_button_states);
        startListening();
        startTimer();
    }

    private void togglePause() {
        isPaused = !isPaused;

        pauseOrPlayButton.setImageResource(isPaused ? R.drawable.continue_button_states : R.drawable.pause_button_states);
        if (isPaused) {
            stopListening();
            stopTimer();
        } else {
            startListening();
            startTimer();
        }
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        speechRecognizer.startListening(intent);
    }

    private void stopListening() {
        speechRecognizer.stopListening();
    }

    private void finishRecording() {
        if (isRecording) {
            stopListening();
            isRecording = false;
        }
        stopTimer();
        resetTimer();
        micButton.setImageResource(R.drawable.mic_button_off);
        updateButtonsState(false);
        recordlinglistButton.setClickable(true);
        recordlinglistButton.setImageResource(R.drawable.recordinglist_button_states);

        Toast.makeText(this, "Processing... Please wait.", Toast.LENGTH_SHORT).show();

        new android.os.Handler().postDelayed(() -> {
            String finalText = transcribedText.toString();
            Intent intent = new Intent(HomePage.this, RecordedScriptPage.class);
            intent.putExtra("RECORDED_SCRIPT", finalText);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 5000);
    }

    private void cancelRecording() {
        stopListening();
        transcribedText.setLength(0);
        isRecording = false;
        stopTimer();
        resetTimer();
        micButton.setImageResource(R.drawable.mic_button_off);
        updateButtonsState(false);
        recordlinglistButton.setClickable(true);
        recordlinglistButton.setImageResource(R.drawable.recordinglist_button_states);
    }

    private void updateButtonsState(boolean isEnabled) {
        pauseOrPlayButton.setClickable(isEnabled);
        pauseOrPlayButton.setImageResource(isEnabled && !isPaused ? R.drawable.continue_button_states : R.drawable.pause_button_locked);

        finishButton.setClickable(isEnabled);
        finishButton.setImageResource(isEnabled ? R.drawable.finish_button_states : R.drawable.finish_button_locked);

        cancelButton.setClickable(isEnabled);
        cancelButton.setImageResource(isEnabled ? R.drawable.cancel_button_states : R.drawable.cancel_button_locked);
    }

    @Override
    protected void onDestroy() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    public void goToList(View view) {
        Intent intent = new Intent(HomePage.this, RecordingListPage.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    private void startTimer() {
        isTimerRunning = true;
        timerHandler.postDelayed(timerRunnable, 1000);
    }

    private void stopTimer() {
        isTimerRunning = false;
        timerHandler.removeCallbacks(timerRunnable);
    }

    private void resetTimer() {
        seconds = 0;
        updateTimerText();
    }

    private void updateTimerText() {
        int minutes = seconds / 60;
        int sec = seconds % 60;
        String time = String.format("%02d:%02d", minutes, sec);
        timeTextView.setText(time);
    }
}