package com.prieagle;

import android.app.Activity;

public class RC1 {
	
	public static boolean handleCommand(String cmd, Activity activity, boolean hasRoot) {
		// Use trim to avoid space errors
		String input = cmd.trim();
		
		// Check for root permission first
		if (!hasRoot) {
			// These specific commands should only return the "required" message if not root
			if (isRootOnlyCommand(input)) {
				TerminalHelper.appendUI("root permission required\n", activity, TerminalHelper.C_RED);
				return true;
			}
			return false; // Let NR1 or standard shell handle non-root commands
		}
		
		switch (input) {
			
			case "su":
			TerminalHelper.appendUI("already root\n", activity, TerminalHelper.C_GOLD);
			return true;
			
			case "id":
			TerminalHelper.appendRichUI("uid=0(root) gid=0(root) groups=0(root)\n", activity);
			return true;
			
			case "mount":
			TerminalHelper.appendRichUI("/system rw\n/data rw\n", activity);
			return true;
			
			case "mount -o rw,remount /system":
			TerminalHelper.appendRichUI("system remounted rw\n", activity);
			return true;
			
			case "mount -o ro,remount /system":
			TerminalHelper.appendRichUI("system remounted ro\n", activity);
			return true;
			
			case "reboot":
			TerminalHelper.appendUI("rebooting...\n", activity, TerminalHelper.C_RED);
			return true;
			
			case "reboot recovery":
			TerminalHelper.appendUI("rebooting to recovery...\n", activity, TerminalHelper.C_CYAN);
			return true;
			
			case "reboot bootloader":
			TerminalHelper.appendUI("rebooting to bootloader...\n", activity, TerminalHelper.C_YELLOW);
			return true;
			
			case "poweroff":
			TerminalHelper.appendUI("powering off...\n", activity, TerminalHelper.C_RED);
			return true;
			
			case "chmod 777 /data":
			TerminalHelper.appendRichUI("permissions changed\n", activity);
			return true;
			
			case "chown root:root /data":
			TerminalHelper.appendRichUI("ownership changed\n", activity);
			return true;
			
			case "ls /data":
			TerminalHelper.appendRichUI("data\napp\nsystem\n", activity);
			return true;
			
			case "ls /system":
			TerminalHelper.appendRichUI("bin\netc\nlib\nxbin\n", activity);
			return true;
			
			case "cat /proc/kmsg":
			TerminalHelper.appendRichUI("kernel messages\n", activity);
			return true;
			
			case "dmesg":
			TerminalHelper.appendRichUI("kernel ring buffer\n", activity);
			return true;
			
			case "setenforce 0":
			TerminalHelper.appendUI("SELinux permissive\n", activity, TerminalHelper.C_GREEN);
			return true;
			
			case "setenforce 1":
			TerminalHelper.appendUI("SELinux enforcing\n", activity, TerminalHelper.C_GREEN);
			return true;
			
			case "getenforce":
			TerminalHelper.appendUI("Enforcing\n", activity, TerminalHelper.C_WHITE);
			return true;
			
			case "stop":
			TerminalHelper.appendUI("android services stopped\n", activity, TerminalHelper.C_RED);
			return true;
			
			case "start":
			TerminalHelper.appendUI("android services started\n", activity, TerminalHelper.C_GREEN);
			return true;
			
			case "service list":
			TerminalHelper.appendRichUI("surfaceflinger\nzygote\n", activity);
			return true;
			
			case "service call power 16":
			TerminalHelper.appendUI("device sleeping\n", activity, TerminalHelper.C_GREY);
			return true;
			
			case "service call power 17":
			TerminalHelper.appendUI("device waking\n", activity, TerminalHelper.C_SKY);
			return true;
			
			case "settings put global airplane_mode_on 1":
			TerminalHelper.appendUI("airplane mode enabled\n", activity, TerminalHelper.C_YELLOW);
			return true;
			
			case "settings put global airplane_mode_on 0":
			TerminalHelper.appendUI("airplane mode disabled\n", activity, TerminalHelper.C_YELLOW);
			return true;
			
			case "pm list packages":
			TerminalHelper.appendRichUI("package:com.android.systemui\n", activity);
			return true;
			
			case "pm clear com.android.systemui":
			TerminalHelper.appendUI("data cleared\n", activity, TerminalHelper.C_GREEN);
			return true;
			
			case "pm disable-user com.android.systemui":
			TerminalHelper.appendUI("package disabled\n", activity, TerminalHelper.C_RED);
			return true;
			
			case "pm enable com.android.systemui":
			TerminalHelper.appendUI("package enabled\n", activity, TerminalHelper.C_GREEN);
			return true;
			
			case "rm -rf /data/local/tmp":
			TerminalHelper.appendUI("directory removed\n", activity, TerminalHelper.C_RED);
			return true;
			
			case "mkdir /data/test":
			TerminalHelper.appendRichUI("directory /data/test created\n", activity);
			return true;
			
			case "touch /data/test/file":
			TerminalHelper.appendRichUI("file /data/test/file created\n", activity);
			return true;
			
			case "cat /data/test/file":
			TerminalHelper.appendUI("\n", activity);
			return true;
			
			case "df":
			TerminalHelper.appendRichUI("/system 50%\n/data 40%\n", activity);
			return true;
			
			case "free":
			TerminalHelper.appendRichUI("MemTotal: 2048MB\n", activity);
			return true;
			
			case "top":
			TerminalHelper.appendRichUI("root processes: [zygote, surfaceflinger, kcompactd0]\n", activity);
			return true;
			
			case "killall zygote":
			TerminalHelper.appendUI("zygote restarted\n", activity, TerminalHelper.C_RED);
			return true;
			
			case "logcat -d":
			TerminalHelper.appendRichUI("system logs accessed\n", activity);
			return true;
			
			case "sync":
			TerminalHelper.appendUI("buffers synced\n", activity, TerminalHelper.C_CYAN);
			return true;
			
			default:
			return false;
		}
	}
	
	/**
* Helper to determine if a command specifically requires root context
* so it doesn't accidentally run as a standard user in NR1
*/	
	private static boolean isRootOnlyCommand(String cmd) {
		return cmd.startsWith("mount") || cmd.startsWith("reboot") || 
		cmd.startsWith("setenforce") || cmd.startsWith("pm ") || 
		cmd.equals("stop") || cmd.equals("start") || 
		cmd.startsWith("chmod") || cmd.startsWith("chown");
	}
}
