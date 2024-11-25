package com.example.scripter;

import android.bluetooth.BluetoothHeadset;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class RecordedScriptPage extends AppCompatActivity {
    private EditText scriptTextView, recordingName;
    private ImageButton saveRecording, deleteRecording;
    static final HashMap<String, String> recordings = new HashMap<>();
    TextToSpeech t1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recorded_script_page);
        // Text to speech

        scriptTextView = findViewById(R.id.scriptTextView);
        recordingName = findViewById(R.id.recordingName);
        saveRecording = findViewById(R.id.saveRecording);
        deleteRecording = findViewById(R.id.deleteRecording);

        String recordedScript = getIntent().getStringExtra("RECORDED_SCRIPT");
        if (recordedScript != null) {
            scriptTextView.setText(recordedScript);
            String text = scriptTextView.getText().toString();
            Log.d("Text", "SEcond");
            Log.d("Text", text);
        }
        saveRecording.setOnClickListener(v -> saveRecording());
        deleteRecording.setOnClickListener(v -> deleteRecording());
    }

//    public void saveRecording(View view) {
//        String recordingNameText = recordingName.getText().toString().trim();
//        String scriptText = scriptTextView.getText().toString().trim();
//        if (!recordingNameText.isEmpty() && !scriptText.isEmpty()) {
//            recordings.put(recordingNameText, scriptText);
//            Toast.makeText(this, "Recording saved!", Toast.LENGTH_SHORT).show();
//            finish();
//        } else {
//            Toast.makeText(this, "Recording name or script cannot be empty!", Toast.LENGTH_SHORT).show();
//        }
//    }
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

    private void saveRecording() {
        String recordingNameText = recordingName.getText().toString().trim();
        String scriptText = scriptTextView.getText().toString().trim();
        if (!recordingNameText.isEmpty() && !scriptText.isEmpty()) {
            try {
                File directory = getExternalFilesDir(null);
                if (directory != null) {
                    File file = new File(directory, recordingNameText + ".txt");

                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(scriptText.getBytes());
                    fos.close();

                    Toast.makeText(this, "Recording saved!", Toast.LENGTH_SHORT).show();
                    finish();
//                    Toast.makeText(this, "Recording saved to file: " + file.getPath(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Error saving recording to file!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Recording name or script cannot be empty!", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRecording() {
        scriptTextView.setText("");
        recordingName.setText("");
        Toast.makeText(this, "Recording deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

}