package com.example.scripter;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Locale;

public class ViewRecordingPage extends AppCompatActivity {
    private TextView recordingNameTextView, scriptTextView;
    private ImageButton editRecordingName, editRecordingScript, deleteRecording, shareRecording, playBtn;
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_recording_page);

        recordingNameTextView = findViewById(R.id.recordingNameView);
        scriptTextView = findViewById(R.id.scriptTextView);
        editRecordingName = findViewById(R.id.editRecordingName);
        editRecordingScript = findViewById(R.id.editRecordingScript);
        deleteRecording = findViewById(R.id.deleteRecording);
        shareRecording = findViewById(R.id.shareRecording);
        playBtn = findViewById(R.id.playbtn);

        String recordingName = getIntent().getStringExtra("RECORDING_NAME");
        if (recordingName != null) {
            String scriptContent = readRecordingFromFile(recordingName);
            if (scriptContent != null) {
                scriptTextView.setText(scriptContent);
            } else {
                Toast.makeText(this, "Recording not found!", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(this, "No recording selected!", Toast.LENGTH_SHORT).show();
            finish();
        }
        String script = getIntent().getStringExtra("SCRIPT");

        if (recordingName != null) {
            recordingNameTextView.setText(recordingName);
        }
        if (script != null) {
            scriptTextView.setText(script);
        }

        t1 = new TextToSpeech(this, i -> {
            if (i != TextToSpeech.ERROR){
                t1.setLanguage(Locale.CANADA);
            } else {
                Toast.makeText(this, "TextToSpeech initializaiton failed!", Toast.LENGTH_SHORT).show();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (t1 != null) {
                    Log.d("Text", "Play clicked");
                    t1.speak(scriptTextView.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    Log.d("Text", "Not initialized");
                }
            }
        });
    }

    private String readRecordingFromFile(String recordingName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, recordingName + ".txt");
            if (file.exists()) {
                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                    return content.toString().trim();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private void text2speech(String text){
        t1 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i != TextToSpeech.ERROR)
                    t1.setLanguage(Locale.CANADA);
            }
        });

        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void deleteRecording(String recordingName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, recordingName + ".txt");
            if (file.exists() && file.delete()) {
                Toast.makeText(this, "Recording deleted successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to delete recording!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void playRecording(String recordingName) {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File file = new File(directory, recordingName + ".txt");
            if (file.exists() && file.delete()) {
                Toast.makeText(this, "Recording Played successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to play recording!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void back(View view) {
        finish();
    }

    public void deleteRecording(View view) {
        String recordingName = recordingNameTextView.getText().toString();
        deleteRecording(recordingName);
        finish();
    }
}