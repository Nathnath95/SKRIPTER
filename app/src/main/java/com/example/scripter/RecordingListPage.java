package com.example.scripter;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.widget.ArrayAdapter;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

public class RecordingListPage extends AppCompatActivity {
    private ListView recordingListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> recordingNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recording_list_page);

        recordingListView = findViewById(R.id.recordingListView);
        recordingNames = new ArrayList<>();
        loadRecordingsFromFiles();

        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                recordingNames
        );

        recordingListView.setAdapter(adapter);

        recordingListView.setOnItemClickListener((parent, view, position, id) -> {
            String recordingName = adapter.getItem(position);
            if (recordingName != null) {
                Intent intent = new Intent(RecordingListPage.this, ViewRecordingPage.class);
                intent.putExtra("RECORDING_NAME", recordingName);
                startActivity(intent);
            }
        });
    }

    private void loadRecordingsFromFiles() {
        File directory = getExternalFilesDir(null);
        if (directory != null) {
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    // Add file names (without ".txt" extension) to the list
                    String recordingName = file.getName().replace(".txt", "");
                    recordingNames.add(recordingName);
                }
            } else {
                Toast.makeText(this, "No recordings found!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error accessing storage!", Toast.LENGTH_SHORT).show();
        }
    }

    public void back(View view) {
        finish();
    }
}