package com.example.tapiwa.collegebuddy.classContents.notes;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognizerIntent;
import android.content.ActivityNotFoundException;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
import com.example.tapiwa.collegebuddy.classContents.classContentsMain.ClassContentsMainActivity;
import com.example.tapiwa.collegebuddy.miscellaneous.GenericServices;
import com.example.tapiwa.collegebuddy.R;

import java.util.ArrayList;
import java.util.Locale;

import es.dmoral.toasty.Toasty;


public class NewNote extends AppCompatActivity {
    
    private NotesSQLiteDBHelper dbHelper;
    private EditText noteTitle;
    private EditText noteContents;
    private Toolbar mToolBar;
    private String card_color;
    private FloatingActionButton mic;
    private final int REQUEST_CODE_SPEECH_INPUT = 100;
    private final int REQUEST_MICROPHONE = 1023;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        
        noteTitle = (EditText) findViewById(R.id.noteTitle);
        mToolBar = (Toolbar) findViewById(R.id.create_new_note_toolbar);
        mic = (FloatingActionButton) findViewById(R.id.microphone);
        mToolBar.setTitle("Create New Note");
        setSupportActionBar(mToolBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        noteContents = (EditText) findViewById(R.id.editNewNote);


        dbHelper = new NotesSQLiteDBHelper(this);
        card_color = dbHelper.getNoteColor
                (ClassContentsMainActivity.className, noteTitle.getText().toString());

        mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkForMicPermision();
                promptForSpeeach();

            }
        });

        noteContents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {

                Linkify.addLinks(noteContents, Linkify.ALL);

            }
        });


       // FloatingActionButton saveNote = (FloatingActionButton) findViewById(R.id.saveNote);
     /*   saveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        }); */
    }

    private void checkForMicPermision() {
        if (ContextCompat.checkSelfPermission(NewNote.this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(NewNote.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_MICROPHONE);

        } else {
            promptForSpeeach();
        }
    }

    private void promptForSpeeach() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_prompt), //correct this part
                    Toast.LENGTH_SHORT).show();
        }



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    noteContents.append(result.get(0));
                    AppUsageAnalytics.incrementPageVisitCount("Notes_Mic");
                }
                break;
            }

        }
    }
    
    
    public void saveNote() {

        AppUsageAnalytics.incrementPageVisitCount("Notes_Created");

        if(noteTitle.getText().toString().trim().equals("")) {
           noteTitle.setError("Please enter unique note title");
            return;
        }

        dbHelper.insertNote(ClassContentsMainActivity.className,
                noteTitle.getText().toString(),
                noteContents.getText().toString(),
                GenericServices.date(),
                "yellow");

        Toasty.success(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();

        NewNote.this.finish();
    }

    private void addBullet() {
        noteContents.append("\n\n" + "\u2022" + " ");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_note_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.cancel_note_creation) {
            finish();
        }

        if (id == R.id.save_new_note_icon) {
            saveNote();
        }

        if(id == R.id.add_bullet) {
            addBullet();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        AppUsageAnalytics.incrementPageVisitCount("New_Note");
    }
}
