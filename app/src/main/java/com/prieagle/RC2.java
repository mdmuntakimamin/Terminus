package com.prieagle;

import android.app.Activity;

public class RC2 {

    public static boolean handleCommand(String cmd, Activity activity, boolean hasRoot) {
        // Trim whitespace for better matching
        String input = cmd.trim();

        // 1. Root Security Check
        if (!hasRoot) {
            // Only block if the command specifically targets root-only areas
            if (isProtectedCommand(input)) {
                TerminalHelper.appendUI("root permission required\n", activity, TerminalHelper.C_RED);
                return true;
            }
            return false; // Let standard shell or NR handles run it
        }

        // 2. Command Dispatcher with 16-Color Support
        switch (input) {

            case "ls /root":
                TerminalHelper.appendRichUI("root_files\nconfigs\nbackups\n", activity);
                return true;

            case "cat /etc/passwd":
                TerminalHelper.appendRichUI("root:x:0:0:root:/root:/bin/sh\n", activity);
                return true;

            case "cat /etc/shadow":
                TerminalHelper.appendRichUI("root:*:18295:0:99999:7:::\n", activity);
                return true;

            case "chmod 600 /etc/shadow":
                TerminalHelper.appendRichUI("permissions changed to 600\n", activity);
                return true;

            case "chown root:root /etc/shadow":
                TerminalHelper.appendRichUI("ownership changed to root:root\n", activity);
                return true;

            case "ls -l /system/bin":
                // Highlights permissions ORANGE and paths CYAN
                TerminalHelper.appendRichUI("-rwxr-xr-x 1 root root 123456 Dec 22 12:00 su\n", activity);
                return true;

            case "cp /system/bin/su /data/local/tmp/":
                TerminalHelper.appendRichUI("file su copied to /data/local/tmp/\n", activity);
                return true;

            case "mv /data/local/tmp/su /system/xbin/":
                TerminalHelper.appendRichUI("file su moved to /system/xbin/\n", activity);
                return true;

            case "rm /system/xbin/su":
                TerminalHelper.appendUI("file removed\n", activity, TerminalHelper.C_RED);
                return true;

            case "touch /data/local/tmp/root_test":
                TerminalHelper.appendRichUI("file /data/local/tmp/root_test created\n", activity);
                return true;

            case "echo 'test' > /data/local/tmp/root_test":
                TerminalHelper.appendRichUI("string 'test' written to file\n", activity);
                return true;

            case "cat /data/local/tmp/root_test":
                TerminalHelper.appendRichUI("test\n", activity);
                return true;

            case "mount | grep /system":
                TerminalHelper.appendRichUI("/dev/block/system on /system type ext4 (rw,seclabel,relatime)\n", activity);
                return true;

            case "getprop ro.secure":
                TerminalHelper.appendUI("1\n", activity, TerminalHelper.C_WHITE);
                return true;

            case "getprop ro.debuggable":
                TerminalHelper.appendUI("0\n", activity, TerminalHelper.C_WHITE);
                return true;

            case "logcat -b main -d":
                TerminalHelper.appendRichUI("--- system main logs ---\n", activity);
                return true;

            case "logcat -b events -d":
                TerminalHelper.appendRichUI("--- system event logs ---\n", activity);
                return true;

            case "logcat -b radio -d":
                TerminalHelper.appendRichUI("--- radio logs ---\n", activity);
                return true;

            case "dumpsys activity":
                TerminalHelper.appendRichUI("Activity Manager dump (System Service)\n", activity);
                return true;

            case "dumpsys package":
                TerminalHelper.appendRichUI("Package Manager dump (System Service)\n", activity);
                return true;

            case "dumpsys battery":
                TerminalHelper.appendRichUI("Battery info: level: 85, status: 2, health: 2\n", activity);
                return true;

            case "dumpsys wifi":
                TerminalHelper.appendRichUI("WiFi service dump: state: enabled\n", activity);
                return true;

            case "dumpsys power":
                TerminalHelper.appendRichUI("Power Manager dump: Display Power: state=ON\n", activity);
                return true;

            case "dumpsys telephony":
                TerminalHelper.appendRichUI("Telephony Manager dump: service state: in service\n", activity);
                return true;

            case "getevent -l":
                TerminalHelper.appendRichUI("/dev/input/event0: EV_KEY KEY_POWER DOWN\n", activity);
                return true;

            case "setprop persist.sys.timezone UTC":
                TerminalHelper.appendUI("timezone set to UTC\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "setprop persist.sys.locale en-US":
                TerminalHelper.appendUI("locale set to en-US\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "svc power reboot":
                TerminalHelper.appendUI("rebooting device...\n", activity, TerminalHelper.C_RED);
                return true;

            case "svc power shutdown":
                TerminalHelper.appendUI("shutting down...\n", activity, TerminalHelper.C_RED);
                return true;

            case "svc data disable":
                TerminalHelper.appendUI("mobile data disabled\n", activity, TerminalHelper.C_YELLOW);
                return true;

            case "svc data enable":
                TerminalHelper.appendUI("mobile data enabled\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "svc wifi disable":
                TerminalHelper.appendUI("WiFi disabled\n", activity, TerminalHelper.C_YELLOW);
                return true;

            case "svc wifi enable":
                TerminalHelper.appendUI("WiFi enabled\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "input keyevent 26":
                TerminalHelper.appendUI("screen toggled (Power Key)\n", activity, TerminalHelper.C_CYAN);
                return true;

            case "input touchscreen swipe 100 500 100 100":
                TerminalHelper.appendUI("swipe executed\n", activity, TerminalHelper.C_CYAN);
                return true;

            case "pm uninstall --user 0 com.example.app":
                TerminalHelper.appendUI("app uninstalled successfully\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "pm install /data/local/tmp/app.apk":
                TerminalHelper.appendUI("app installed successfully\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "sqlite3 /data/data/com.example.app/databases/db.sqlite":
                TerminalHelper.appendUI("sqlite3> shell opened (root)\n", activity, TerminalHelper.C_GOLD);
                return true;

            case "toybox ifconfig eth0 up":
                TerminalHelper.appendUI("eth0 up\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "toybox ifconfig eth0 down":
                TerminalHelper.appendUI("eth0 down\n", activity, TerminalHelper.C_YELLOW);
                return true;

            case "ip route":
                // Highlights IP SKY BLUE automatically
                TerminalHelper.appendRichUI("default via 192.168.1.1 dev eth0\n", activity);
                return true;

            case "ip addr":
                TerminalHelper.appendRichUI("1: lo: <LOOPBACK,UP> inet 127.0.0.1/8\n2: eth0: <UP> inet 192.168.1.2/24\n", activity);
                return true;

            case "iptables -L":
                TerminalHelper.appendRichUI("Chain INPUT (policy ACCEPT)\nChain FORWARD (policy ACCEPT)\n", activity);
                return true;

            case "iptables -F":
                TerminalHelper.appendUI("all firewall rules flushed\n", activity, TerminalHelper.C_RED);
                return true;

            case "netcfg":
                TerminalHelper.appendRichUI("eth0 UP 192.168.1.2/24\nlo UP 127.0.0.1/8\n", activity);
                return true;

            case "busybox df -h":
                TerminalHelper.appendRichUI("Filesystem Size Used Avail Use% Mounted on\n/system 512M 200M 312M 40% /\n", activity);
                return true;

            case "busybox free -m":
                TerminalHelper.appendRichUI("Mem: 2048 1024 1024\nSwap: 512 128 384\n", activity);
                return true;

            case "uname -a":
                TerminalHelper.appendRichUI("Linux android 4.19.0 #1 SMP PREEMPT root\n", activity);
                return true;

            case "cat /proc/version":
                TerminalHelper.appendRichUI("Linux version 4.19.0 (root@build-server)\n", activity);
                return true;

            case "echo 1 > /proc/sys/net/ipv4/ip_forward":
                TerminalHelper.appendUI("IP forwarding enabled\n", activity, TerminalHelper.C_GREEN);
                return true;

            case "echo 0 > /proc/sys/net/ipv4/ip_forward":
                TerminalHelper.appendUI("IP forwarding disabled\n", activity, TerminalHelper.C_YELLOW);
                return true;

            case "sync":
                TerminalHelper.appendUI("buffers synced\n", activity, TerminalHelper.C_CYAN);
                return true;

            case "exit":
                TerminalHelper.appendUI("root session ended. Switching to user.\n", activity, TerminalHelper.C_GOLD);
                return true;

            default:
                return false;
        }
    }

    /**
     * Identifies commands that should NEVER be allowed without root.
     */
    private static boolean isProtectedCommand(String cmd) {
        return cmd.contains("/etc/shadow") || cmd.startsWith("iptables") || 
               cmd.startsWith("svc ") || cmd.startsWith("dumpsys") || 
               cmd.startsWith("setprop") || cmd.startsWith("ip ") || 
               cmd.startsWith("busybox") || cmd.startsWith("toybox");
    }
}