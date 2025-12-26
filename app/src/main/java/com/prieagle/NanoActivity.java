package com.prieagle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Stack;

public class NanoActivity extends Activity {

    private EditText editor;
    private TextView fileNameView, statusView;
    private String currentFilePath = "";
    private String originalContent = "";
    private boolean isModified = false;
    private Stack<String> undoStack = new Stack<String>();
    private boolean isHistoryAction = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                           WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.an);

        editor = (EditText) findViewById(R.id.nanoEditor);
        fileNameView = (TextView) findViewById(R.id.nanoFileName);
        statusView = (TextView) findViewById(R.id.nanoStatus);
        
        editor.setTypeface(Typeface.MONOSPACE);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        // Get path from Intent (Sent by Terminal)
        currentFilePath = getIntent().getStringExtra("file_path");
        
        if (currentFilePath != null && !currentFilePath.isEmpty()) {
            File file = new File(currentFilePath);
            fileNameView.setText(file.getName());
            if (file.exists()) {
                loadFileData(file);
            } else {
                statusView.setText(" [ New File ] ");
                originalContent = "";
            }
        }

        setupTextWatcher();
    }

    private void loadFileData(File file) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            originalContent = sb.toString();
            editor.setText(originalContent);
            isModified = false;
        } catch (IOException e) {
            statusView.setText(" [ Error Loading ] ");
        }
    }

    private void saveFile() {
        if (currentFilePath == null || currentFilePath.isEmpty()) {
            statusView.setText(" [ No Path Defined ] ");
            return;
        }

        try {
            File file = new File(currentFilePath);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                String data = editor.getText().toString();
                bw.write(data);
                originalContent = data;
                isModified = false;
                statusView.setText(" [ Saved: " + file.getName() + " ] ");
            }
        } catch (IOException e) {
            statusView.setText(" [ Save Error: " + e.getMessage() + " ] ");
        }
    }

    private void setupTextWatcher() {
        editor.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isHistoryAction) {
                    undoStack.push(s.toString());
                    if (undoStack.size() > 30) undoStack.remove(0);
                }
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isModified = !s.toString().equals(originalContent);
                if (isModified) statusView.setText(" [ Modified ] ");
            }
            public void afterTextChanged(Editable s) {}
        });
    }

    private void handleExit() {
        if (isModified) {
            AlertDialog.Builder ab = new AlertDialog.Builder(this);
            ab.setTitle("Save Changes?");
            ab.setMessage("Save " + fileNameView.getText().toString() + " before exiting?");
            ab.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int w) {
                    saveFile();
                    finish();
                }
            });
            ab.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface d, int w) {
                    finish();
                }
            });
            ab.setNeutralButton("Cancel", null);
            ab.show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            saveFile();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (!undoStack.isEmpty()) {
                isHistoryAction = true;
                editor.setText(undoStack.pop());
                isHistoryAction = false;
            }
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}