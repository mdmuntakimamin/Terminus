package com.prieagle;

import android.app.Activity;
import android.widget.ScrollView;
import android.widget.TextView;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TerminalHelper {
    private static TextView outputView;
    private static ScrollView scrollView;
    private static SpannableStringBuilder buffer = new SpannableStringBuilder();
    
    private static Process activeProcess = null;
    private static BufferedWriter activeWriter = null;
    private static boolean isRootMode = false;

    public static final int C_RED = 0xFFFF073A;
    public static final int C_GREEN = 0xFF39FF14;
    public static final int C_YELLOW = 0xFFFFD700;
    public static final int C_CYAN = 0xFF00FFFF;
    public static final int C_SKY = 0xFF87CEEB;
    public static final int C_WHITE = 0xFFFFFFFF;
    public static final int C_GOLD = 0xFFFFD700;
    public static final int C_MINT = 0xFF98FF98;
    public static final int C_GREY = 0xFF888888;
    public static final int C_PINK = 0xFFFF69B4;
    public static final int C_PURPLE = 0xFFAF12FF;
    public static final int C_ORANGE = 0xFFFF8C00;
    public static final int C_LIME = 0xFFC6FF00;
    public static final int C_BLUE = 0xFF2979FF;
    public static final int C_MAGENTA = 0xFFFF00FF;

    public static void init(TextView out, ScrollView sc) {
        outputView = out;
        scrollView = sc;
    }

    public static void managePythonEnvironment(final Activity act) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    File binDir = new File(act.getFilesDir(), "usr/bin");
                    if (!binDir.exists()) binDir.mkdirs();
                    appendUI("[STATUS]: Downloading MicroPython Runtime...\n", act, C_CYAN);
                    URL url = new URL("https://raw.githubusercontent.com/skylot/jadx/master/scripts/python/python.zip");
                    HttpURLConnection c = (HttpURLConnection) url.openConnection();
                    c.setInstanceFollowRedirects(true);
                    InputStream is = c.getInputStream();
                    File zip = new File(act.getFilesDir(), "python.zip");
                    FileOutputStream fos = new FileOutputStream(zip);
                    byte[] b = new byte[16384]; int l;
                    while((l = is.read(b)) != -1) fos.write(b, 0, l);
                    fos.close(); is.close();
                    unzip(zip, act.getFilesDir());
                    new File(binDir, "python").setExecutable(true, false);
                    zip.delete();
                    appendUI("[SUCCESS]: Environment ready for execution.\n", act, C_GREEN);
                } catch (Exception e) {
                    appendUI("[ERROR]: Deployment failed: " + e.getMessage() + "\n", act, C_RED);
                }
            }
        }).start();
    }

    private static void unzip(File z, File t) throws IOException {
        ZipInputStream zis = new ZipInputStream(new FileInputStream(z));
        ZipEntry ze;
        while ((ze = zis.getNextEntry()) != null) {
            File f = new File(t, ze.getName());
            if (ze.isDirectory()) { f.mkdirs(); continue; }
            f.getParentFile().mkdirs();
            FileOutputStream fos = new FileOutputStream(f);
            byte[] b = new byte[16384]; int c;
            while ((c = zis.read(b)) != -1) fos.write(b, 0, c);
            fos.close();
        }
        zis.close();
    }

    public static void executeLocal(final String cmd, final Activity act, final boolean isUnused) {
        if (cmd == null || cmd.trim().isEmpty()) return;
        final String input = cmd.trim();

        if (isRootMode) {
            if (input.equals("exit")) {
                closeRootSession(act);
            } else {
                sendCommandToRoot(input);
            }
            return;
        }

        if (input.equals("su")) {
            openRootSession(act);
        } else if (input.equals("exit")) {
            if (act != null) act.finish();
        } else {
            runNormalCommand(input, act);
        }
    }

    private static void openRootSession(final Activity act) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    activeProcess = Runtime.getRuntime().exec("su");
                    activeWriter = new BufferedWriter(new OutputStreamWriter(activeProcess.getOutputStream()));
                    isRootMode = true;
                    appendUI("# Switching to Root Shell...\n", act, C_YELLOW);
                    final BufferedReader reader = new BufferedReader(new InputStreamReader(activeProcess.getInputStream()));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String line;
                                while (isRootMode && (line = reader.readLine()) != null) {
                                    appendRichUI("# " + line + "\n", act);
                                }
                            } catch (Exception e) {}
                        }
                    }).start();
                } catch (Exception e) {
                    isRootMode = false;
                    appendUI("Root Access Denied.\n", act, C_RED);
                }
            }
        }).start();
    }

    private static void sendCommandToRoot(String cmd) {
        try {
            if (activeWriter != null) {
                activeWriter.write(cmd + "\n");
                activeWriter.flush();
            }
        } catch (Exception e) {}
    }

    private static void closeRootSession(final Activity act) {
        try {
            isRootMode = false;
            if (activeWriter != null) {
                activeWriter.write("exit\n");
                activeWriter.flush();
                activeWriter.close();
            }
            if (activeProcess != null) activeProcess.destroy();
            activeWriter = null;
            activeProcess = null;
            appendUI("$ Root shell closed.\n", act, C_SKY);
        } catch (Exception e) {}
    }

    private static void runNormalCommand(final String cmd, final Activity act) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String finalCmd = cmd;
                    File pyBin = new File(act.getFilesDir(), "usr/bin/python");
                    if (cmd.startsWith("python") && pyBin.exists()) {
                        finalCmd = pyBin.getAbsolutePath() + cmd.substring(6);
                    }
                    Process p = Runtime.getRuntime().exec(new String[]{"/system/bin/sh", "-c", finalCmd});
                    BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while ((line = r.readLine()) != null) {
                        appendRichUI(line + "\n", act);
                    }
                    p.waitFor();
                } catch (Exception e) {
                    appendUI("Execution Error: " + e.getMessage() + "\n", act, C_RED);
                }
            }
        }).start();
    }

    

    public static void appendUI(final String text, final Activity activity) {
        appendUI(text, activity, C_WHITE);
    }

    public static void appendUI(final String text, final Activity activity, final int color) {
        if (activity == null || outputView == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing()) return;
                SpannableString spannable = new SpannableString(text);
                spannable.setSpan(new ForegroundColorSpan(color), 0, text.length(), 0);
                buffer.append(spannable);
                if (buffer.length() > 100000) buffer.delete(0, 20000);
                outputView.setText(buffer);
                scrollDown();
            }
        });
    }

    public static void appendRichUI(final String text, final Activity activity) {
        if (activity == null || outputView == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing()) return;
                SpannableString ss = new SpannableString(text);
                applyRegex(ss, "\\b(def|class|import|from|if|else|elif|for|while|return|try|except|with|as|pass|break|continue|lambda|yield|in|is|not|and|or)\\b", C_GOLD, true);
                applyRegex(ss, "\\b(print|input|len|range|int|str|float|list|dict|set|type|open|abs|sum|min|max)\\b", C_SKY, false);
                applyRegex(ss, "(\".*?\"|'.*?')", C_MINT, false);
                applyRegex(ss, "\\b(True|False|None)\\b", C_PINK, true);
                applyRegex(ss, "(#.*)", C_GREY, false);
                applyRegex(ss, "\\b(\\d+)\\b", C_PURPLE, false);
                applyRegex(ss, "\\b(OK|SUCCESS|DONE|READY|ACTIVE|TRUE)\\b", C_GREEN, true);
                applyRegex(ss, "\\b(ERROR|FAILED|DENIED|FATAL|EXIT|FALSE)\\b", C_RED, true);
                buffer.append(ss);
                if (buffer.length() > 100000) buffer.delete(0, 20000);
                outputView.setText(buffer);
                scrollDown();
            }
        });
    }

    private static void applyRegex(SpannableString ss, String regex, int color, boolean bold) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ss);
        while (matcher.find()) {
            ss.setSpan(new ForegroundColorSpan(color), matcher.start(), matcher.end(), 0);
            if (bold) ss.setSpan(new StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), 0);
        }
    }

    public static void clear(Activity activity) {
        if (activity == null || outputView == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buffer.clear();
                outputView.setText("");
            }
        });
    }

    private static void scrollDown() {
        if (scrollView != null) {
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
        }
    }
}