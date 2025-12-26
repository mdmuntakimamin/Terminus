package com.prieagle;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextEditor extends Activity {

    private EditText editorMain;
    private TextView fileNameTitle, editorStatus;
    private String currentFilePath;
    private static final int STORAGE_PERMISSION_CODE = 1000;

    // --- Theme Colors ---
    private final int CLR_KEYWORD = Color.parseColor("#FF79C6");
    private final int CLR_NUMBER  = Color.parseColor("#BD93F9");
    private final int CLR_STRING  = Color.parseColor("#F1FA8C");
    private final int CLR_COMMENT = Color.parseColor("#6272A4");
    private final int CLR_FUNC    = Color.parseColor("#50FA7B");
    private final int CLR_TAG     = Color.parseColor("#8BE9FD");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.te);

        editorMain = (EditText) findViewById(R.id.editorMain);
        fileNameTitle = (TextView) findViewById(R.id.fileNameTitle);
        editorStatus = (TextView) findViewById(R.id.editorStatus);

        currentFilePath = getIntent().getStringExtra("file_path");

        if (checkPermission()) {
            initEditor();
        } else {
            requestPermission();
        }
    }

    private void initEditor() {
        if (currentFilePath != null) {
            File file = new File(currentFilePath);
            fileNameTitle.setText(file.getName());
            
            if (file.exists()) {
                loadFile(file);
            } else {
                editorStatus.setText("New File Buffer");
            }
            
            setupSyntaxHighlighter(file.getName().toLowerCase());
        }
    }

    private void setupSyntaxHighlighter(final String fileName) {
        editorMain.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) { applySyntax(s, fileName); }
        });
        applySyntax(editorMain.getText(), fileName);
    }

    private void applySyntax(Editable s, String fileName) {
        ForegroundColorSpan[] spans = s.getSpans(0, s.length(), ForegroundColorSpan.class);
        for (ForegroundColorSpan span : spans) { s.removeSpan(span); }

        // Code logic for Java, Python, C
        if (fileName.endsWith(".java") || fileName.endsWith(".py") || fileName.endsWith(".c")) {
            highlight(s, "\\b(public|private|static|void|class|import|package|def|if|else|for|while|return|int|String)\\b", CLR_KEYWORD);
            highlight(s, "\\b\\d+\\b", CLR_NUMBER);
            highlight(s, "\".*?\"", CLR_STRING);
            highlight(s, "//.*|#.*", CLR_COMMENT);
            highlight(s, "\\b\\w+(?=\\()", CLR_FUNC);
        } 
        // XML/HTML Logic
        else if (fileName.endsWith(".xml") || fileName.endsWith(".html")) {
            highlight(s, "<[^>]+>", CLR_TAG);
            highlight(s, "\".*?\"", CLR_STRING);
        }
    }

    private void highlight(Editable s, String regex, int color) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(s);
        while (m.find()) {
            s.setSpan(new ForegroundColorSpan(color), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    private void loadFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            StringBuilder sb = new StringBuilder();
            String l;
            while ((l = br.readLine()) != null) sb.append(l).append("\n");
            editorMain.setText(sb.toString());
            editorStatus.setText("File Loaded");
        } catch (IOException e) { editorStatus.setText("Load Failed"); }
    }

    private void saveFile() {
        if (currentFilePath == null) return;
        try (FileWriter w = new FileWriter(new File(currentFilePath))) {
            w.write(editorMain.getText().toString());
            editorStatus.setText("Saved successfully");
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) { editorStatus.setText("Save Error"); }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            saveFile();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // --- Standard Permissions ---
    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) return Environment.isExternalStorageManager();
        return checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, STORAGE_PERMISSION_CODE);
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }
}