package com.example.scripter;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class ViewRecordingPage extends AppCompatActivity {
    private TextView recordingNameTextView, scriptTextView;
    private ImageButton editRecordingName, editRecordingScript, deleteRecording, shareRecording;

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

    public void back(View view) {
        finish();
    }

    public void deleteRecording(View view) {
        String recordingName = recordingNameTextView.getText().toString();
        deleteRecording(recordingName);
        finish();
    }
}