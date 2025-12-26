package com.prieagle;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;

public class MainActivity extends Activity {
    private EditText commandInput;
    private TextView outputText;
    private Process suProcess;
    private DataOutputStream suIn;
    private boolean hasRoot = false;
    private boolean isRootActive = false; // New state to track current shell mode

    private final int COLOR_WHITE = 0xFFFFFFFF;
    private final int COLOR_GREEN = 0xFF00FF00;
    private final int COLOR_RED = 0xFFFF0000;
    private final int COLOR_YELLOW = 0xFFFFFF00;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.BLACK);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.am);

        commandInput = (EditText) findViewById(R.id.commandInput);
        outputText = (TextView) findViewById(R.id.outputText);
        final ScrollView terminalScroll = (ScrollView) findViewById(R.id.terminalScroll);

        TerminalHelper.init(outputText, terminalScroll);
        TerminalHelper.clear(this);
        startTerminalService();

        if (Build.VERSION.SDK_INT >= 33) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        TerminalHelper.appendUI("Prieagle Terminal Session\n", this, COLOR_YELLOW);
        startRootShell();

        commandInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(final TextView v, int actionId, KeyEvent event) {
                if (actionId == 6 || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String cmd = commandInput.getText().toString().trim();
                    commandInput.setText("");
                    
                    if (!cmd.isEmpty()) {
                        handleCommand(cmd);
                    }
                    
                    v.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            v.requestFocus();
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                            }
                        }
                    }, 50);
                    return true;
                }
                return false;
            }
        });
    }

    private void startTerminalService() {
        Intent intent = new Intent(this, TerminalService.class);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        } catch (Exception ignored) {}
    }

    private void handleCommand(String cmd) {
        // --- Prompt UI Logic ---
        if (isRootActive) {
            TerminalHelper.appendUI("~ ", this, COLOR_WHITE);
            TerminalHelper.appendUI("# ", this, COLOR_RED);
        } else {
            TerminalHelper.appendUI("~ $ ", this, COLOR_GREEN);
        }
        TerminalHelper.appendUI(cmd + "\n", this, COLOR_WHITE);

        // --- Handle 'su' command to enter root mode ---
        if (cmd.equalsIgnoreCase("su")) {
            if (hasRoot) {
                isRootActive = true;
                TerminalHelper.appendUI("Root shell activated.\n", this, COLOR_YELLOW);
            } else {
                TerminalHelper.appendUI("su: permission denied (No Root binary found)\n", this, COLOR_RED);
            }
            return;
        }

        // --- Exit logic for both modes ---
        if (cmd.equalsIgnoreCase("exit")) {
            if (isRootActive) {
                isRootActive = false; // Just leave root mode
                TerminalHelper.appendUI("Exited root session.\n", this, COLOR_YELLOW);
            } else {
                // Exit app completely
                TerminalHelper.clear(this);
                stopService(new Intent(this, TerminalService.class));
                commandInput.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        } else {
                            finishAffinity();
                        }
                    }
                }, 200);
            }
            return;
        }

        if (cmd.equalsIgnoreCase("clear")) {
            TerminalHelper.clear(this);
            return;
        }

        // --- Standard Commands ---
        if (cmd.startsWith("edit ")) {
            String filePath = cmd.substring(5).trim();
            if (!filePath.isEmpty()) {
                Intent intent = new Intent(this, TextEditor.class);
                intent.putExtra("file_path", filePath);
                startActivity(intent);
            }
            return;
        }

        if (cmd.startsWith("python")) {
            String scriptPath = "";
            if (cmd.length() > 7) {
                String fileName = cmd.substring(7).trim();
                File file = new File(getExternalFilesDir(null), fileName);
                scriptPath = file.getAbsolutePath();
            }
            Intent intent = new Intent(this, PythonActivity.class);
            intent.putExtra("script_path", scriptPath);
            startActivity(intent);
            return;
        }

        // --- Mode-based Execution ---
        if (isRootActive) {
            // Only execute root commands when in # mode
            runRootCommand(cmd);
        } else {
            // Only execute non-root commands when in $ mode
            boolean handled = NR1.handleCommand(cmd, this) || NR2.handleCommand(cmd, this);
            if (!handled) {
                TerminalHelper.appendUI("prieagle: " + cmd + ": not found\n", this, COLOR_RED);
            }
        }
    }

    private void startRootShell() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    suProcess = Runtime.getRuntime().exec("su");
                    suIn = new DataOutputStream(suProcess.getOutputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(suProcess.getErrorStream()));
                    
                    suIn.writeBytes("id\n");
                    suIn.flush();
                    
                    String line = reader.readLine();
                    if (line != null && line.contains("uid=0")) {
                        hasRoot = true;
                        TerminalHelper.appendUI("Access: ROOT granted\n", MainActivity.this, COLOR_GREEN);
                    } else {
                        TerminalHelper.appendUI("Access: LOCAL shell\n", MainActivity.this, COLOR_YELLOW);
                    }
                    
                    startListener(reader, COLOR_WHITE);
                    startListener(errorReader, COLOR_RED);
                } catch (Exception e) {
                    TerminalHelper.appendUI("Native shell active.\n", MainActivity.this, COLOR_YELLOW);
                }
            }
        }).start();
    }

    private void startListener(final BufferedReader reader, final int color) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TerminalHelper.appendUI(finalLine + "\n", MainActivity.this, color);
                            }
                        });
                    }
                } catch (Exception ignored) {}
            }
        }).start();
    }

    public void runRootCommand(String cmd) {
        try {
            if (suIn != null) {
                suIn.writeBytes(cmd + "\n");
                suIn.flush();
            }
        } catch (Exception e) {
            TerminalHelper.appendUI("Execution error.\n", this, COLOR_RED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (suIn != null) {
                suIn.writeBytes("exit\n");
                suIn.flush();
                suIn.close();
            }
            if (suProcess != null) suProcess.destroy();
        } catch (Exception ignored) {}
    }
}