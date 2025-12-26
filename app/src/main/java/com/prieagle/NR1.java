package com.prieagle;

import android.app.Activity;
import java.io.File;

public class NR1 {
	
	public static boolean handleCommand(String cmd, Activity activity) {
		String input = cmd.trim();
		
		if (input.startsWith("python ") && !input.equals("python --version") && !input.equals("python -c")) {
			TerminalHelper.executeLocal(input, activity, false);
			return true;
		}
		
		if (input.startsWith("edit ")) {
			String filePath = input.substring(5).trim();
			if (!filePath.isEmpty()) {
				android.content.Intent intent = new android.content.Intent(activity, TextEditor.class);
				
				intent.putExtra("file_path", filePath);
				activity.startActivity(intent);
			} else {
				TerminalHelper.appendRichUI("Usage: edit [file_path]\n", activity);
			}
			return true;
		}
		
		switch (input) {
			
			
			case "edit":
			TerminalHelper.appendRichUI("Usage: edit /sdcard/path/to/file.txt\n", activity);
			return true;
			
			case "type":
			TerminalHelper.appendRichUI(android.os.Build.TYPE + "\n", activity);
			return true;
			
			case "user":
			TerminalHelper.appendRichUI(android.os.Build.USER + "\n", activity);
			return true;
			
			case "host":
			TerminalHelper.appendRichUI(android.os.Build.HOST + "\n", activity);
			return true;
			
			case "encoding":
			TerminalHelper.appendRichUI(System.getProperty("file.encoding") + "\n", activity);
			return true;
			
			case "java_vm":
			TerminalHelper.appendRichUI(System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version") + "\n", activity);
			return true;
			
			case "java_home":
			TerminalHelper.appendRichUI(System.getProperty("java.home") + "\n", activity);
			return true;
			
			case "os_arch":
			TerminalHelper.appendRichUI(System.getProperty("os.arch") + "\n", activity);
			return true;
			
			case "os_name":
			TerminalHelper.appendRichUI(System.getProperty("os.name") + "\n", activity);
			return true;
			
			case "pip":
			TerminalHelper.appendUI("uPip v0.1\n", activity, TerminalHelper.C_GOLD);
			TerminalHelper.appendRichUI("Usage: pip install [module]\n", activity);
			return true;
			
			case "ls":
			TerminalHelper.appendRichUI("bin\netc\nlib\nusr\ntmp\nvar\n", activity);
			return true;
			
			case "ls -a":
			TerminalHelper.appendRichUI(".\n..\n.bashrc\n.python_history\nconfig\ncache\n", activity);
			return true;
			
			case "ls -l":
			TerminalHelper.appendRichUI("drwxr-xr-x 2 user 4096 Jan 01 00:00 bin\n-rwx------ 1 user 5242 Jan 01 00:00 python\n", activity);
			return true;
			
			case "ls -lh":
			TerminalHelper.appendRichUI("-rwxr-xr-x 1 user 3.2M Jan 01 00:00 micropython\n", activity);
			return true;
			
			case "clear":
			TerminalHelper.clear(activity);
			return true;
			
			case "whoami -a":
			TerminalHelper.appendRichUI("android_user (uid=1000 gid=1000 groups=3003,1015)\n", activity);
			return true;
			
			case "id":
			TerminalHelper.appendRichUI("uid=1000(android) gid=1000(android) groups=1000(android)\n", activity);
			return true;
			
			case "hostname":
			TerminalHelper.appendRichUI("android_device\n", activity);
			return true;
			
			case "uname -a":
			TerminalHelper.appendRichUI("Linux android 4.19.0-perf #1 SMP PREEMPT 2025 aarch64\n", activity);
			return true;
			
			case "whoami":
			TerminalHelper.appendRichUI("u0_a" + android.os.Process.myUid() + "\n", activity);
			return true;
			
			case "uname":
			TerminalHelper.appendRichUI("Linux " + System.getProperty("os.version") + "\n", activity);
			return true;
			
			case "pwd":
			TerminalHelper.appendRichUI(activity.getFilesDir().getAbsolutePath() + "\n", activity);
			return true;
			
			case "model":
			TerminalHelper.appendRichUI(android.os.Build.MODEL + "\n", activity);
			return true;
			
			case "brand":
			TerminalHelper.appendRichUI(android.os.Build.BRAND + "\n", activity);
			return true;
			
			case "manufacturer":
			TerminalHelper.appendRichUI(android.os.Build.MANUFACTURER + "\n", activity);
			return true;
			
			case "board":
			TerminalHelper.appendRichUI(android.os.Build.BOARD + "\n", activity);
			return true;
			
			case "hardware":
			TerminalHelper.appendRichUI(android.os.Build.HARDWARE + "\n", activity);
			return true;
			
			case "arch":
			TerminalHelper.appendRichUI(android.os.Build.CPU_ABI + "\n", activity);
			return true;
			
			case "cores":
			TerminalHelper.appendRichUI(Runtime.getRuntime().availableProcessors() + " cores active\n", activity);
			return true;
			
			case "meminfo":
			TerminalHelper.appendRichUI("JVM Total: " + (Runtime.getRuntime().totalMemory() / 1048576) + "MB\n", activity);
			return true;
			
			case "disk":
			TerminalHelper.appendRichUI("Total: " + (new java.io.File("/data").getTotalSpace() / 1073741824) + "GB\n", activity);
			return true;
			
			case "path":
			TerminalHelper.appendRichUI(System.getenv("PATH") + "\n", activity);
			return true;
			
			case "shell":
			TerminalHelper.appendRichUI(System.getenv("SHELL") + "\n", activity);
			return true;
			
			case "home":
			TerminalHelper.appendRichUI(android.os.Environment.getExternalStorageDirectory().getPath() + "\n", activity);
			return true;
			
			case "tmp":
			TerminalHelper.appendRichUI(System.getProperty("java.io.tmpdir") + "\n", activity);
			return true;
			
			case "lang":
			TerminalHelper.appendRichUI(java.util.Locale.getDefault().getLanguage() + "\n", activity);
			return true;
			
			case "tz":
			TerminalHelper.appendRichUI(java.util.TimeZone.getDefault().getID() + "\n", activity);
			return true;
			
			case "pid":
			TerminalHelper.appendRichUI(android.os.Process.myPid() + "\n", activity);
			return true;
			
			case "serial":
			TerminalHelper.appendRichUI(android.os.Build.SERIAL + "\n", activity);
			return true;
			
			case "bootloader":
			TerminalHelper.appendRichUI(android.os.Build.BOOTLOADER + "\n", activity);
			return true;
			
			case "device":
			TerminalHelper.appendRichUI(android.os.Build.DEVICE + "\n", activity);
			return true;
			
			case "display":
			TerminalHelper.appendRichUI(android.os.Build.DISPLAY + "\n", activity);
			return true;
			
			case "fingerprint":
			TerminalHelper.appendRichUI(android.os.Build.FINGERPRINT + "\n", activity);
			return true;
			
			case "product":
			TerminalHelper.appendRichUI(android.os.Build.PRODUCT + "\n", activity);
			return true;
			
			case "tags":
			TerminalHelper.appendRichUI(android.os.Build.TAGS + "\n", activity);
			return true;
			
			case "ping":
			TerminalHelper.appendRichUI("64 bytes from 127.0.0.1: icmp_seq=1 ttl=64 time=0.04 ms\n", activity);
			return true;
			
			case "sdcard":
			TerminalHelper.appendRichUI(System.getenv("EXTERNAL_STORAGE") + "\n", activity);
			return true;
			
			case "root":
			TerminalHelper.appendRichUI("Binary: " + (new java.io.File("/system/bin/su").exists() ? "Found" : "Missing") + "\n", activity);
			return true;
			
			case "net_dns":
			TerminalHelper.appendRichUI("8.8.8.8, 8.8.4.4\n", activity);
			return true;
			
			case "cache_dir":
			TerminalHelper.appendRichUI(activity.getCacheDir().getAbsolutePath() + "\n", activity);
			return true;
			
			case "tasks":
			TerminalHelper.appendRichUI("Active Threads: " + Thread.activeCount() + "\n", activity);
			return true;
			
			case "battery":
			TerminalHelper.appendRichUI("Status: Discharging (Check notification bar)\n", activity);
			return true;
			
			case "proc_list":
			TerminalHelper.appendRichUI("Total active tasks: " + ((android.app.ActivityManager) activity.getSystemService(activity.ACTIVITY_SERVICE)).getRunningAppProcesses().size() + "\n", activity);
			return true;
			
			case "sys_type":
			TerminalHelper.appendRichUI("Android Dalvik/ART Runtime\n", activity);
			return true;
			
			case "kernel":
			TerminalHelper.appendRichUI(System.getProperty("os.name") + " " + System.getProperty("os.version") + "\n", activity);
			return true;
			
			
			case "date":
			TerminalHelper.appendRichUI(new java.util.Date().toString() + "\n", activity);
			return true;
			
			case "date -u":
			TerminalHelper.appendRichUI("UTC " + new java.util.Date().toString() + "\n", activity);
			return true;
			
			case "uptime":
			TerminalHelper.appendRichUI("up 1 day, 14:22, load average: 0.12, 0.05, 0.02\n", activity);
			return true;
			
			case "free -h":
			TerminalHelper.appendRichUI("      total   used   free   avail\nMem:   3.8G   1.4G   1.2G    2.1G\n", activity);
			return true;
			
			case "df -h":
			TerminalHelper.appendRichUI("Filesystem     Size  Used  Avail Use% Mounted on\n/dev/block/dm-0 64G   18G   46G   28% /data\n", activity);
			return true;
			
			case "ping -c 1 8.8.8.8":
			TerminalHelper.appendRichUI("PING 8.8.8.8 (8.8.8.8): 56 data bytes\n64 bytes from 8.8.8.8: seq=0 ttl=114 time=28.4 ms\n", activity);
			return true;
			
			case "ifconfig":
			TerminalHelper.appendRichUI("wlan0: inet 192.168.1.10 netmask 255.255.255.0\nlo: inet 127.0.0.1 netmask 255.0.0.0\n", activity);
			return true;
			
			case "netstat":
			TerminalHelper.appendRichUI("Active Internet connections\nProto Recv-Q Local Address      Foreign Address    State\ntcp        0 192.168.1.10:44321 8.8.8.8:443        ESTABLISHED\n", activity);
			return true;
			
			case "ps":
			TerminalHelper.appendRichUI("PID  USER   COMMAND\n1    root   init\n1024 user   com.terminal\n", activity);
			return true;
			
			case "top":
			TerminalHelper.appendRichUI("PID CPU% MEM% COMMAND\n1024 4.5  1.8  com.terminal\n", activity);
			return true;
			
			case "printenv":
			TerminalHelper.appendRichUI("PATH=/usr/bin:/system/bin\nHOME=/data/user/0/com.terminal/files\n", activity);
			return true;
			
			case "echo hello":
			TerminalHelper.appendRichUI("hello\n", activity);
			return true;
			
			case "version":
			TerminalHelper.appendRichUI("Terminal Emulator v1.2.5\nPython: MicroPython 1.20\n", activity);
			return true;
			
			case "dmesg":
			TerminalHelper.appendRichUI("[    0.000000] Booting Linux on physical CPU 0x0000000000 [0x410fd034]\n[    0.000000] Linux version 4.19.0-perf\n", activity);
			return true;
			case "touch":
			TerminalHelper.appendRichUI("Usage: touch [file_name]\n", activity);
			return true;
			case "mkdir":
			TerminalHelper.appendRichUI("Usage: mkdir [directory_name]\n", activity);
			return true;
			case "rm":
			TerminalHelper.appendRichUI("Usage: rm [file_name]\n", activity);
			return true;
			case "cp":
			TerminalHelper.appendRichUI("Usage: cp [source] [destination]\n", activity);
			return true;
			case "mv":
			TerminalHelper.appendRichUI("Usage: mv [source] [destination]\n", activity);
			return true;
			case "chmod":
			TerminalHelper.appendRichUI("Usage: chmod [mode] [file]\n", activity);
			return true;
			case "chown":
			TerminalHelper.appendRichUI("Usage: chown [owner]:[group] [file]\n", activity);
			return true;
			case "alias":
			TerminalHelper.appendRichUI("alias ll='ls -l'\nalias py='python'\n", activity);
			return true;
			case "grep":
			TerminalHelper.appendRichUI("Usage: grep [pattern] [file]\n", activity);
			return true;
			case "head":
			TerminalHelper.appendRichUI("Usage: head -n [number] [file]\n", activity);
			return true;
			case "tail":
			TerminalHelper.appendRichUI("Usage: tail -n [number] [file]\n", activity);
			return true;
			case "wc":
			TerminalHelper.appendRichUI("Usage: wc [file]\n", activity);
			return true;
			case "diff":
			TerminalHelper.appendRichUI("Usage: diff [file1] [file2]\n", activity);
			return true;
			case "tar":
			TerminalHelper.appendRichUI("Usage: tar -cvf [archive.tar] [files]\n", activity);
			return true;
			case "zip":
			TerminalHelper.appendRichUI("Usage: zip [archive.zip] [files]\n", activity);
			return true;
			case "unzip":
			TerminalHelper.appendRichUI("Usage: unzip [archive.zip]\n", activity);
			return true;
			case "find":
			TerminalHelper.appendRichUI("Usage: find [path] -name [filename]\n", activity);
			return true;
			case "locate":
			TerminalHelper.appendRichUI("Usage: locate [filename]\n", activity);
			return true;
			case "mount":
			TerminalHelper.appendRichUI("/dev/block/bootdevice/by-name/system on /system type ext4 (ro,relatime)\n", activity);
			return true;
			case "umount":
			TerminalHelper.appendRichUI("Usage: umount [target]\n", activity);
			return true;
			case "ln -s":
			TerminalHelper.appendRichUI("Usage: ln -s [target] [link_name]\n", activity);
			return true;
			case "readlink":
			TerminalHelper.appendRichUI("Usage: readlink [file]\n", activity);
			return true;
			case "du -sh":
			TerminalHelper.appendRichUI("124M    .\n", activity);
			return true;
			case "stat":
			TerminalHelper.appendRichUI("File: 'config'\nSize: 1024 Blocks: 8 IO Block: 4096 regular file\nDevice: 801h/2049d Inode: 12345 Links: 1\n", activity);
			return true;
			case "file":
			TerminalHelper.appendRichUI("test.py: Python script, ASCII text executable\n", activity);
			return true;
			case "env":
			TerminalHelper.appendRichUI("SHELL=/system/bin/sh\nUSER=u0_a1000\nLANG=en_US.UTF-8\n", activity);
			return true;
			case "export":
			TerminalHelper.appendRichUI("Usage: export NAME=VALUE\n", activity);
			return true;
			case "unset":
			TerminalHelper.appendRichUI("Usage: unset [variable_name]\n", activity);
			return true;
			case "sudo":
			TerminalHelper.appendRichUI("sudo: command not found (try 'su' instead)\n", activity);
			return true;
			case "groups":
			TerminalHelper.appendRichUI("u0_a1000 cache dbus inet net_raw sdcard_rw\n", activity);
			return true;
			case "kill":
			TerminalHelper.appendRichUI("Usage: kill [pid]\n", activity);
			return true;
			case "pkill":
			TerminalHelper.appendRichUI("Usage: pkill [process_name]\n", activity);
			return true;
			case "jobs":
			TerminalHelper.appendRichUI("[1]+ Running    python background_task.py &\n", activity);
			return true;
			case "fg":
			TerminalHelper.appendRichUI("Usage: fg [job_id]\n", activity);
			return true;
			case "bg":
			TerminalHelper.appendRichUI("Usage: bg [job_id]\n", activity);
			return true;
			case "nohup":
			TerminalHelper.appendRichUI("Usage: nohup [command] &\n", activity);
			return true;
			case "man":
			TerminalHelper.appendRichUI("Manual pages not installed. Use 'help' for command list.\n", activity);
			return true;
			case "whatis":
			TerminalHelper.appendRichUI("ls: list directory contents\n", activity);
			return true;
			case "whereis":
			TerminalHelper.appendRichUI("python: /data/user/0/com.terminal/files/usr/bin/python\n", activity);
			return true;
			case "which":
			TerminalHelper.appendRichUI("/system/bin/sh\n", activity);
			return true;
			case "clear-history":
			TerminalHelper.appendRichUI("Command history cleared.\n", activity);
			return true;
			case "nslookup":
			TerminalHelper.appendRichUI("Server: 8.8.8.8\nAddress: 8.8.8.8#53\nNon-authoritative answer:\nName: https://www.google.com/search?q=google.com\nAddress: 142.250.190.46\n", activity);
			return true;
			
			case "traceroute":
			TerminalHelper.appendRichUI("traceroute to https://www.google.com/search?q=google.com (142.250.190.46), 30 hops max\n1  192.168.1.1 (192.168.1.1) 1.2 ms\n", activity);
			return true;
			case "curl":
			TerminalHelper.appendRichUI("Usage: curl [URL]\n", activity);
			return true;
			case "wget":
			TerminalHelper.appendRichUI("Usage: wget [URL]\n", activity);
			return true;
			case "ssh":
			TerminalHelper.appendRichUI("Usage: ssh [user]@[host]\n", activity);
			return true;
			case "scp":
			TerminalHelper.appendRichUI("Usage: scp [file] [user]@[host]:[path]\n", activity);
			return true;
			
			case "lsusb":
			TerminalHelper.appendRichUI("Bus 001 Device 001: ID 1d6b:0002 Linux Foundation 2.0 hub\n", activity);
			return true;
			
			case "lsblk":
			TerminalHelper.appendRichUI("NAME   MAJ:MIN RM  SIZE RO TYPE MOUNTPOINT\nsda      8:0    0   64G  0 disk /data\n", activity);
			return true;
			
			case "cat /proc/cpuinfo":
			TerminalHelper.appendRichUI("Processor: AArch64 Processor rev 4 (v8l)\nFeatures: fp asimd evtstrm aes pmull sha1 sha2 crc32\n", activity);
			return true;
			
			case "help":
			StringBuilder sb = new StringBuilder();
			sb.append(TerminalHelper.C_GOLD + " ALL AVAILABLE COMMANDS\n" + TerminalHelper.C_WHITE);
			sb.append(
			"pip, ls, ls -a, ls -l, ls -lh, clear, whoami, whoami -a, id, hostname, uname, uname -a, pwd, model, brand, manufacturer, board, hardware, arch, cores,\n" +
			"meminfo, disk, path, shell, home, tmp, lang, tz, pid, serial, bootloader, device, display, fingerprint, product, tags, type, user, host, encoding,\n" +
			"java_vm, java_home, os_arch, os_name, ping, ping -c 1 8.8.8.8, sdcard, root, net_dns, cache_dir, tasks, battery, proc_list, sys_type, kernel,\n" +
			"date, date -u, uptime, free -h, df -h, ifconfig, netstat, ps, top, printenv, echo hello, version, dmesg, touch, mkdir, rm, cp, mv, chmod, chown,\n" +
			"alias, edit, create, create edit, grep, head, tail, wc, diff, tar, zip, unzip, find, locate, mount, umount, ln -s, readlink, du -sh, stat, file, env, export, unset, sudo, groups,\n" +
			"kill, pkill, jobs, fg, bg, nohup, man, whatis, whereis, which, clear-history, nslookup, traceroute, curl, wget, ssh, scp, lsusb, lsblk, cat /proc/cpuinfo,\n" +
			"battery_level, battery_health, battery_temperature, wifi_status, bluetooth_status, network_type, mac_address, locale, timezone, screen_density, orientation, screen_rotation, supported_abis,\n" +
			"sensor_info, motion_sensors\n"
			);
			TerminalHelper.appendRichUI(sb.toString(), activity);
			return true;
			
			
			default:
			return false;
		}
	}
}
