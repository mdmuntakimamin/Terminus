package com.prieagle;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.*;
import java.util.*;

public class PythonActivity extends Activity {

    private TextView console;
    private ScrollView scroller;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Process process;
    
    private final String DATA_PATH = "/data/data/com.terminal/files";
    private final String USR_PATH = DATA_PATH + "/usr";
    // Target file changed to python.nanorc
    private final String NANO_RC_FILE = USR_PATH + "/share/nano/python.nanorc";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        
        scroller = new ScrollView(this);
        scroller.setBackgroundColor(0xFF000000);
        scroller.setFillViewport(true);
        
        console = new TextView(this);
        console.setBackgroundColor(0xFF000000);
        console.setTextColor(0xFF39FF14);
        console.setTypeface(Typeface.MONOSPACE);
        console.setTextSize(12);
        console.setPadding(15, 15, 15, 15);
        
        scroller.addView(console);
        setContentView(scroller);

        initializeEnvironment();
    }

    private void initializeEnvironment() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File rcFile = new File(NANO_RC_FILE);
                    if (!rcFile.exists()) {
                        updateUI("[SYSTEM]: Deploying bootstrap and python.nanorc...\n");
                        syncAssets("bootstrap-aarch64", getFilesDir());
                        updatePermissions(new File(USR_PATH));
                    }
                    
                    if (rcFile.exists()) {
                        updateUI("[SYSTEM]: python.nanorc found at " + NANO_RC_FILE + "\n");
                        runTerminalProcess();
                    } else {
                        updateUI("[ERROR]: python.nanorc still missing after sync!\n");
                    }
                } catch (Exception e) {
                    updateUI("\nFATAL ERROR: " + e.getMessage());
                }
            }
        }).start();
    }

    private void syncAssets(String assetDir, File targetDir) throws IOException {
        String[] items = getAssets().list(assetDir);
        if (items == null || items.length == 0) {
            copyAssetFile(assetDir, targetDir);
        } else {
            // Mapping bootstrap-aarch64 folder to 'usr' internally
            String folderName = assetDir.equals("bootstrap-aarch64") ? "usr" : assetDir.substring(assetDir.lastIndexOf("/") + 1);
            File newFolder = new File(targetDir, folderName);
            if (!newFolder.exists()) newFolder.mkdirs();
            for (String item : items) {
                syncAssets(assetDir + "/" + item, newFolder);
            }
        }
    }

    private void copyAssetFile(String assetPath, File targetDir) throws IOException {
        File outFile = new File(targetDir, assetPath.substring(assetPath.lastIndexOf("/") + 1));
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getAssets().open(assetPath);
            out = new FileOutputStream(outFile);
            byte[] buf = new byte[32768];
            int len;
            while ((len = in.read(buf)) != -1) out.write(buf, 0, len);
        } finally {
            if (in != null) in.close();
            if (out != null) out.close();
        }
    }

    private void updatePermissions(File file) {
        if (file.isDirectory()) {
            File[] list = file.listFiles();
            if (list != null) {
                for (File child : list) updatePermissions(child);
            }
        } else {
            String path = file.getPath();
            if (path.contains("/bin/") || path.contains("/lib/") || path.endsWith(".so")) {
                file.setExecutable(true, false);
                file.setReadable(true, false);
            }
        }
    }

    private void runTerminalProcess() {
        try {
            // Executing python binary using the nanorc file as an argument or environment check
            List<String> command = new ArrayList<String>();
            command.add(USR_PATH + "/bin/python");
            
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(new File(DATA_PATH));

            Map<String, String> env = pb.environment();
            env.put("PATH", USR_PATH + "/bin:" + System.getenv("PATH"));
            env.put("LD_LIBRARY_PATH", USR_PATH + "/lib");
            env.put("PREFIX", USR_PATH);
            env.put("HOME", DATA_PATH);
            env.put("PYTHONHOME", USR_PATH);
            
            // Forcing nano and other apps to use python.nanorc
            env.put("NANORC", NANO_RC_FILE);
            env.put("TERM", "xterm-256color");

            pb.redirectErrorStream(true);
            process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                updateUI(line + "\n");
            }
            process.waitFor();
            updateUI("\n[Process Finished]");
        } catch (Exception e) {
            updateUI("\nExecution Error: " + e.getMessage());
        }
    }

    private void updateUI(final String text) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                console.append(text);
                scroller.post(new Runnable() {
                    @Override
                    public void run() {
                        scroller.fullScroll(ScrollView.FOCUS_DOWN);
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (process != null) process.destroy();
        super.onDestroy();
    }
}