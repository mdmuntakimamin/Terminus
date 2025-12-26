package com.prieagle;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.DisplayMetrics;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;

public class NR2 {
	
	public static boolean handleCommand(String cmd, Activity activity) {
		
		String input = cmd.trim();
		String[] args = input.split("\\s+");
		
		try {
			
			if (input.equals("pwd")) {
				TerminalHelper.appendRichUI(activity.getFilesDir().getAbsolutePath() + "\n", activity);
				return true;
			}
			
			if (input.equals("ls")) {
				File[] files = activity.getFilesDir().listFiles();
				StringBuilder out = new StringBuilder();
				if (files != null) {
					for (File f : files) {
						out.append(f.getName()).append("\n");
					}
				}
				TerminalHelper.appendRichUI(out.toString(), activity);
				return true;
			}
			
			if (input.equals("stat .")) {
				File dir = activity.getFilesDir();
				TerminalHelper.appendRichUI(
				"Path: " + dir.getAbsolutePath() + "\n" +
				"Directory: " + dir.isDirectory() + "\n" +
				"Readable: " + dir.canRead() + "\n" +
				"Writable: " + dir.canWrite() + "\n",
				activity
				);
				return true;
			}
			
			if (input.startsWith("create edit ")) {
				String filePath = input.substring(12).trim();
				if (!filePath.isEmpty()) {
					try {
						File file = new File(filePath);
						if (!file.exists()) {
							file.createNewFile();
							TerminalHelper.appendRichUI("File created: " + filePath + "\n", activity);
						}
						android.content.Intent intent = new android.content.Intent(activity, TextEditor.class);
						intent.putExtra("file_path", filePath);
						activity.startActivity(intent);
					} catch (java.io.IOException e) {
						TerminalHelper.appendRichUI("Error: Could not create file\n", activity);
					}
				} else {
					TerminalHelper.appendRichUI("Usage: create edit [path]\n", activity);
				}
				return true;
			}
			
			if (input.startsWith("create ")) {
				String filePath = input.substring(7).trim();
				if (!filePath.isEmpty()) {
					try {
						File file = new File(filePath);
						if (file.createNewFile()) {
							TerminalHelper.appendRichUI("File created successfully.\n", activity);
						} else {
							TerminalHelper.appendRichUI("File already exists.\n", activity);
						}
					} catch (java.io.IOException e) {
						TerminalHelper.appendRichUI("Error: " + e.getMessage() + "\n", activity);
					}
				} else {
					TerminalHelper.appendRichUI("Usage: create [path]\n", activity);
				}
				return true;
			}
			
			
			if (args.length == 2 && args[0].equals("wc")) {
				File file = new File(activity.getFilesDir(), args[1]);
				int lines = 0, words = 0, chars = 0;
				
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					lines++;
					words += line.trim().isEmpty() ? 0 : line.split("\\s+").length;
					chars += line.length() + 1;
				}
				br.close();
				
				TerminalHelper.appendRichUI(lines + " " + words + " " + chars + " " + args[1] + "\n", activity);
				return true;
			}
			
			if (input.startsWith("nano ")) {
				String filePath = input.substring(5).trim();
				if (!filePath.isEmpty()) {
					android.content.Intent intent = new android.content.Intent(activity, NanoActivity.class);
					intent.putExtra("file_path", filePath);
					activity.startActivity(intent);
				} else {
					TerminalHelper.appendRichUI("Usage: nano [file_name]\n", activity);
				}
				return true;
			}
			
			if (args.length == 2 && args[0].equals("head")) {
				File file = new File(activity.getFilesDir(), args[1]);
				BufferedReader br = new BufferedReader(new FileReader(file));
				StringBuilder out = new StringBuilder();
				
				for (int i = 0; i < 10; i++) {
					String line = br.readLine();
					if (line == null) break;
					out.append(line).append("\n");
				}
				br.close();
				
				TerminalHelper.appendRichUI(out.toString(), activity);
				return true;
			}
			
			if (args.length == 2 && args[0].equals("tail")) {
				File file = new File(activity.getFilesDir(), args[1]);
				List<String> lines = new ArrayList<>();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				br.close();
				
				StringBuilder out = new StringBuilder();
				for (int i = Math.max(0, lines.size() - 10); i < lines.size(); i++) {
					out.append(lines.get(i)).append("\n");
				}
				
				TerminalHelper.appendRichUI(out.toString(), activity);
				return true;
			}
			
			if (args.length == 2 && args[0].equals("sort")) {
				File file = new File(activity.getFilesDir(), args[1]);
				List<String> lines = new ArrayList<>();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				br.close();
				
				Collections.sort(lines);
				TerminalHelper.appendRichUI(TextUtils.join("\n", lines) + "\n", activity);
				return true;
			}
			
			if (args.length == 2 && args[0].equals("uniq")) {
				File file = new File(activity.getFilesDir(), args[1]);
				LinkedHashSet<String> set = new LinkedHashSet<>();
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				
				while ((line = br.readLine()) != null) {
					set.add(line);
				}
				br.close();
				
				TerminalHelper.appendRichUI(TextUtils.join("\n", set) + "\n", activity);
				return true;
			}
			
			if (input.equals("date")) {
				TerminalHelper.appendRichUI(new Date().toString() + "\n", activity);
				return true;
			}
			
			if (input.equals("uptime_ms")) {
				TerminalHelper.appendRichUI(android.os.SystemClock.uptimeMillis() + " ms\n", activity);
				return true;
			}
			
			if (input.equals("cpu_count")) {
				TerminalHelper.appendRichUI(Runtime.getRuntime().availableProcessors() + "\n", activity);
				return true;
			}
			
			if (input.equals("sdk_ver")) {
				TerminalHelper.appendRichUI(android.os.Build.VERSION.SDK_INT + "\n", activity);
				return true;
			}
			
			if (input.equals("screen_size")) {
				DisplayMetrics dm = activity.getResources().getDisplayMetrics();
				TerminalHelper.appendRichUI(dm.widthPixels + "x" + dm.heightPixels + " px\n", activity);
				return true;
			}
			
			if (input.equals("battery_level")) {
				Intent i = activity.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
				int level = i != null ? i.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
				TerminalHelper.appendRichUI(level + "%\n", activity);
				return true;
			}
			
			if (input.equals("locale")) {
				TerminalHelper.appendRichUI(
				java.util.Locale.getDefault().toString() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("timezone")) {
				TerminalHelper.appendRichUI(
				java.util.TimeZone.getDefault().getID() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("current_time_iso")) {
				java.text.SimpleDateFormat sdf =
				new java.text.SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss",
				java.util.Locale.US
				);
				TerminalHelper.appendRichUI(
				sdf.format(new java.util.Date()) + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("total_ram")) {
				android.app.ActivityManager am =
				(android.app.ActivityManager) activity.getSystemService(
				android.content.Context.ACTIVITY_SERVICE
				);
				if (am != null) {
					android.app.ActivityManager.MemoryInfo mi =
					new android.app.ActivityManager.MemoryInfo();
					am.getMemoryInfo(mi);
					TerminalHelper.appendRichUI(
					(mi.totalMem / 1048576L) + " MB\n",
					activity
					);
				}
				return true;
			}
			
			if (input.equals("avail_ram")) {
				android.app.ActivityManager am =
				(android.app.ActivityManager) activity.getSystemService(
				android.content.Context.ACTIVITY_SERVICE
				);
				if (am != null) {
					android.app.ActivityManager.MemoryInfo mi =
					new android.app.ActivityManager.MemoryInfo();
					am.getMemoryInfo(mi);
					TerminalHelper.appendRichUI(
					(mi.availMem / 1048576L) + " MB\n",
					activity
					);
				}
				return true;
			}
			
			if (input.equals("is_low_ram")) {
				android.app.ActivityManager am =
				(android.app.ActivityManager) activity.getSystemService(
				android.content.Context.ACTIVITY_SERVICE
				);
				if (am != null) {
					android.app.ActivityManager.MemoryInfo mi =
					new android.app.ActivityManager.MemoryInfo();
					am.getMemoryInfo(mi);
					TerminalHelper.appendRichUI(
					mi.lowMemory + "\n",
					activity
					);
				}
				return true;
			}
			
			if (input.equals("wifi_status")) {
				android.net.ConnectivityManager cm =
				(android.net.ConnectivityManager) activity.getSystemService(
				android.content.Context.CONNECTIVITY_SERVICE
				);
				boolean connected = false;
				if (cm != null) {
					android.net.NetworkInfo ni =
					cm.getNetworkInfo(android.net.ConnectivityManager.TYPE_WIFI);
					connected = ni != null && ni.isConnected();
				}
				TerminalHelper.appendRichUI(
				connected + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("screen_orientation")) {
				int o = activity.getResources()
				.getConfiguration()
				.orientation;
				TerminalHelper.appendRichUI(
				(o == android.content.res.Configuration.ORIENTATION_PORTRAIT
				? "portrait"
				: "landscape") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("density_dpi")) {
				TerminalHelper.appendRichUI(
				activity.getResources()
				.getDisplayMetrics()
				.densityDpi + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("is_tablet")) {
				int layout =
				activity.getResources()
				.getConfiguration()
				.screenLayout
				& android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
				boolean tablet =
				layout >= android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
				TerminalHelper.appendRichUI(
				tablet + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("thread_name")) {
				TerminalHelper.appendRichUI(
				Thread.currentThread().getName() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("process_name")) {
				TerminalHelper.appendRichUI(
				activity.getApplicationInfo().processName + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("uid")) {
				TerminalHelper.appendRichUI(
				activity.getApplicationInfo().uid + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("app_package")) {
				TerminalHelper.appendRichUI(
				activity.getPackageName() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("app_version")) {
				try {
					android.content.pm.PackageInfo pi =
					activity.getPackageManager().getPackageInfo(
					activity.getPackageName(), 0);
					TerminalHelper.appendRichUI(
					pi.versionName + " (" + pi.versionCode + ")\n",
					activity
					);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("unknown\n", activity);
				}
				return true;
			}
			
			if (input.equals("target_sdk")) {
				TerminalHelper.appendRichUI(
				activity.getApplicationInfo().targetSdkVersion + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("min_sdk")) {
				if (android.os.Build.VERSION.SDK_INT >= 24) {
					TerminalHelper.appendRichUI(
					activity.getApplicationInfo().minSdkVersion + "\n",
					activity
					);
				} else {
					TerminalHelper.appendRichUI("unknown\n", activity);
				}
				return true;
			}
			
			
			if (input.equals("storage_internal")) {
				File dir = activity.getFilesDir();
				long free = dir.getFreeSpace() / 1048576L;
				long total = dir.getTotalSpace() / 1048576L;
				TerminalHelper.appendRichUI(
				free + "MB free / " + total + "MB total\n",
				activity
				);
				return true;
			}
			
			if (input.equals("external_storage_state")) {
				TerminalHelper.appendRichUI(
				android.os.Environment.getExternalStorageState() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("network_type")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				String type = tm != null ? tm.getNetworkOperatorName() : "unknown";
				TerminalHelper.appendRichUI(type + "\n", activity);
				return true;
			}
			
			if (input.equals("airplane_mode")) {
				int mode = android.provider.Settings.Global.getInt(
				activity.getContentResolver(),
				android.provider.Settings.Global.AIRPLANE_MODE_ON,
				0
				);
				TerminalHelper.appendRichUI((mode == 1) + "\n", activity);
				return true;
			}
			
			if (input.equals("bluetooth_supported")) {
				android.bluetooth.BluetoothAdapter bt =
				android.bluetooth.BluetoothAdapter.getDefaultAdapter();
				TerminalHelper.appendRichUI((bt != null) + "\n", activity);
				return true;
			}
			
			if (input.equals("bluetooth_enabled")) {
				android.bluetooth.BluetoothAdapter bt =
				android.bluetooth.BluetoothAdapter.getDefaultAdapter();
				TerminalHelper.appendRichUI(
				(bt != null && bt.isEnabled()) + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("nfc_supported")) {
				android.nfc.NfcAdapter nfc =
				android.nfc.NfcAdapter.getDefaultAdapter(activity);
				TerminalHelper.appendRichUI((nfc != null) + "\n", activity);
				return true;
			}
			
			if (input.equals("gps_enabled")) {
				android.location.LocationManager lm =
				(android.location.LocationManager)
				activity.getSystemService(android.content.Context.LOCATION_SERVICE);
				boolean enabled =
				lm != null && lm.isProviderEnabled(
				android.location.LocationManager.GPS_PROVIDER);
				TerminalHelper.appendRichUI(enabled + "\n", activity);
				return true;
			}
			
			if (input.equals("sensor_count")) {
				android.hardware.SensorManager sm =
				(android.hardware.SensorManager)
				activity.getSystemService(Activity.SENSOR_SERVICE);
				int count = sm != null
				? sm.getSensorList(android.hardware.Sensor.TYPE_ALL).size()
				: 0;
				TerminalHelper.appendRichUI(count + "\n", activity);
				return true;
			}
			
			if (input.equals("is_emulator")) {
				boolean emu =
				android.os.Build.FINGERPRINT.contains("generic")
				|| android.os.Build.MODEL.contains("Emulator");
				TerminalHelper.appendRichUI(emu + "\n", activity);
				return true;
			}
			
			if (input.equals("heap_usage")) {
				Runtime r = Runtime.getRuntime();
				long used = (r.totalMemory() - r.freeMemory()) / 1048576L;
				long max = r.maxMemory() / 1048576L;
				TerminalHelper.appendRichUI(
				used + "MB / " + max + "MB\n",
				activity
				);
				return true;
			}
			
			if (input.equals("app_label")) {
				CharSequence label =
				activity.getApplicationInfo()
				.loadLabel(activity.getPackageManager());
				TerminalHelper.appendRichUI(label + "\n", activity);
				return true;
			}
			
			if (input.equals("is_debuggable")) {
				boolean dbg =
				(activity.getApplicationInfo().flags
				& android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0;
				TerminalHelper.appendRichUI(dbg + "\n", activity);
				return true;
			}
			
			if (input.equals("memory_class")) {
				android.app.ActivityManager am =
				(android.app.ActivityManager)
				activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
				int mc = am != null ? am.getMemoryClass() : 0;
				TerminalHelper.appendRichUI(mc + " MB\n", activity);
				return true;
			}
			
			if (input.equals("is_monkey")) {
				TerminalHelper.appendRichUI(
				android.app.ActivityManager.isUserAMonkey() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("screen_width_dp")) {
				TerminalHelper.appendRichUI(
				activity.getResources().getConfiguration().screenWidthDp + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("screen_height_dp")) {
				TerminalHelper.appendRichUI(
				activity.getResources().getConfiguration().screenHeightDp + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("font_scale")) {
				TerminalHelper.appendRichUI(
				activity.getResources().getConfiguration().fontScale + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("xdpi")) {
				TerminalHelper.appendRichUI(
				activity.getResources().getDisplayMetrics().xdpi + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("ydpi")) {
				TerminalHelper.appendRichUI(
				activity.getResources().getDisplayMetrics().ydpi + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("timezone_long")) {
				TerminalHelper.appendRichUI(
				java.util.TimeZone.getDefault().getDisplayName() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("is_dst")) {
				boolean dst =
				java.util.TimeZone.getDefault()
				.inDaylightTime(new java.util.Date());
				TerminalHelper.appendRichUI(dst + "\n", activity);
				return true;
			}
			
			if (input.equals("thread_count")) {
				TerminalHelper.appendRichUI(
				Thread.activeCount() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("gc")) {
				System.gc();
				TerminalHelper.appendRichUI("ok\n", activity);
				return true;
			}
			
			if (input.equals("vm_name")) {
				TerminalHelper.appendRichUI(
				System.getProperty("java.vm.name") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("vm_version")) {
				TerminalHelper.appendRichUI(
				System.getProperty("java.vm.version") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("java_vendor")) {
				TerminalHelper.appendRichUI(
				System.getProperty("java.vendor") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("os_version")) {
				TerminalHelper.appendRichUI(
				android.os.Build.VERSION.RELEASE + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("abi_list")) {
				TerminalHelper.appendRichUI(
				java.util.Arrays.toString(android.os.Build.SUPPORTED_ABIS) + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("boot_time_ms")) {
				TerminalHelper.appendRichUI(
				android.os.SystemClock.elapsedRealtime() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("uptime_seconds")) {
				TerminalHelper.appendRichUI(
				(android.os.SystemClock.uptimeMillis() / 1000) + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("device_model")) {
				TerminalHelper.appendRichUI(
				android.os.Build.MODEL + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("device_brand")) {
				TerminalHelper.appendRichUI(
				android.os.Build.BRAND + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("device_manufacturer")) {
				TerminalHelper.appendRichUI(
				android.os.Build.MANUFACTURER + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("device_fingerprint")) {
				TerminalHelper.appendRichUI(
				android.os.Build.FINGERPRINT + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("hardware")) {
				TerminalHelper.appendRichUI(
				android.os.Build.HARDWARE + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("board")) {
				TerminalHelper.appendRichUI(
				android.os.Build.BOARD + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("product")) {
				TerminalHelper.appendRichUI(
				android.os.Build.PRODUCT + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("radio_version")) {
				TerminalHelper.appendRichUI(
				android.os.Build.getRadioVersion() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("is_64bit")) {
				if (android.os.Build.VERSION.SDK_INT >= 23) {
					TerminalHelper.appendRichUI(
					android.os.Process.is64Bit() + "\n",
					activity
					);
				} else {
					TerminalHelper.appendRichUI("unknown\n", activity);
				}
				return true;
			}
			
			if (input.equals("cpu_abi")) {
				TerminalHelper.appendRichUI(
				android.os.Build.CPU_ABI + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("cpu_abi2")) {
				TerminalHelper.appendRichUI(
				android.os.Build.CPU_ABI2 + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("security_patch")) {
				if (android.os.Build.VERSION.SDK_INT >= 23) {
					TerminalHelper.appendRichUI(
					android.os.Build.VERSION.SECURITY_PATCH + "\n",
					activity
					);
				} else {
					TerminalHelper.appendRichUI("unknown\n", activity);
				}
				return true;
			}
			
			if (input.equals("kernel_version")) {
				TerminalHelper.appendRichUI(
				System.getProperty("os.version") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("hostname")) {
				TerminalHelper.appendRichUI(
				java.net.InetAddress.getLocalHost().getHostName() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("env_path")) {
				TerminalHelper.appendRichUI(
				System.getenv("PATH") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("user_name")) {
				TerminalHelper.appendRichUI(
				System.getProperty("user.name") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("user_home")) {
				TerminalHelper.appendRichUI(
				System.getProperty("user.home") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("tmp_dir")) {
				TerminalHelper.appendRichUI(
				System.getProperty("java.io.tmpdir") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("class_path")) {
				TerminalHelper.appendRichUI(
				System.getProperty("java.class.path") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("path_separator")) {
				TerminalHelper.appendRichUI(
				System.getProperty("path.separator") + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("file_separator")) {
				TerminalHelper.appendRichUI(
				System.getProperty("file.separator") + "\n",
				activity
				);
				return true;
			} 
			
			if (input.equals("network_operator")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				String op = tm != null ? tm.getNetworkOperatorName() : "";
				TerminalHelper.appendRichUI(op + "\n", activity);
				return true;
			}
			
			if (input.equals("network_country")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				String iso = tm != null ? tm.getNetworkCountryIso() : "";
				TerminalHelper.appendRichUI(iso + "\n", activity);
				return true;
			}
			
			if (input.equals("sim_operator")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				String sim = tm != null ? tm.getSimOperator() : "";
				TerminalHelper.appendRichUI(sim + "\n", activity);
				return true;
			}
			
			if (input.equals("sim_state")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				int state = tm != null ? tm.getSimState() : -1;
				TerminalHelper.appendRichUI(state + "\n", activity);
				return true;
			}
			
			if (input.equals("phone_type")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				int type = tm != null ? tm.getPhoneType() : -1;
				TerminalHelper.appendRichUI(type + "\n", activity);
				return true;
			}
			
			if (input.equals("data_activity")) {
				android.telephony.TelephonyManager tm =
				(android.telephony.TelephonyManager)
				activity.getSystemService(android.content.Context.TELEPHONY_SERVICE);
				int da = tm != null ? tm.getDataActivity() : -1;
				TerminalHelper.appendRichUI(da + "\n", activity);
				return true;
			}
			
			if (input.equals("has_telephony")) {
				boolean has =
				activity.getPackageManager().hasSystemFeature(
				android.content.pm.PackageManager.FEATURE_TELEPHONY);
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("has_camera")) {
				boolean has =
				activity.getPackageManager().hasSystemFeature(
				android.content.pm.PackageManager.FEATURE_CAMERA_ANY);
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("has_microphone")) {
				boolean has =
				activity.getPackageManager().hasSystemFeature(
				android.content.pm.PackageManager.FEATURE_MICROPHONE);
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("has_gps")) {
				boolean has =
				activity.getPackageManager().hasSystemFeature(
				android.content.pm.PackageManager.FEATURE_LOCATION_GPS);
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("has_bluetooth")) {
				boolean has =
				activity.getPackageManager().hasSystemFeature(
				android.content.pm.PackageManager.FEATURE_BLUETOOTH);
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("has_nfc")) {
				boolean has =
				activity.getPackageManager().hasSystemFeature(
				android.content.pm.PackageManager.FEATURE_NFC);
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("vibrator_supported")) {
				android.os.Vibrator v =
				(android.os.Vibrator)
				activity.getSystemService(android.content.Context.VIBRATOR_SERVICE);
				boolean has = v != null && v.hasVibrator();
				TerminalHelper.appendRichUI(has + "\n", activity);
				return true;
			}
			
			if (input.equals("safe_mode")) {
				boolean safe = activity.getPackageManager().isSafeMode();
				TerminalHelper.appendRichUI(safe + "\n", activity);
				return true;
			}
			
			if (input.equals("monkey")) {
				TerminalHelper.appendRichUI(
				android.app.ActivityManager.isUserAMonkey() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("time_ms")) {
				TerminalHelper.appendRichUI(
				System.currentTimeMillis() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("nano_time")) {
				TerminalHelper.appendRichUI(
				System.nanoTime() + "\n",
				activity
				);
				return true;
			}
			
			if (input.equals("device_model")) {
				TerminalHelper.appendRichUI(android.os.Build.MODEL + "\n", activity);
				return true;
			}
			
			if (input.equals("manufacturer")) {
				TerminalHelper.appendRichUI(android.os.Build.MANUFACTURER + "\n", activity);
				return true;
			}
			
			if (input.equals("android_version")) {
				TerminalHelper.appendRichUI(android.os.Build.VERSION.RELEASE + "\n", activity);
				return true;
			}
			
			if (input.equals("available_memory")) {
				android.app.ActivityManager am = (android.app.ActivityManager) activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
				android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
				am.getMemoryInfo(mi);
				TerminalHelper.appendRichUI((mi.availMem / (1024 * 1024)) + " MB\n", activity);
				return true;
			}
			
			if (input.equals("total_memory")) {
				android.app.ActivityManager am = (android.app.ActivityManager) activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
				android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
				am.getMemoryInfo(mi);
				TerminalHelper.appendRichUI((mi.totalMem / (1024 * 1024)) + " MB\n", activity);
				return true;
			}
			
			if (input.equals("is_charging")) {
				android.content.IntentFilter ifilter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
				android.content.Intent batteryStatus = activity.registerReceiver(null, ifilter);
				int status = batteryStatus != null ? batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) : -1;
				boolean charging = status == android.os.BatteryManager.BATTERY_STATUS_CHARGING || status == android.os.BatteryManager.BATTERY_STATUS_FULL;
				TerminalHelper.appendRichUI(charging ? "YES\n" : "NO\n", activity);
				return true;
			}
			
			if (input.equals("uptime_sec")) {
				long uptime = android.os.SystemClock.uptimeMillis() / 1000;
				TerminalHelper.appendRichUI(uptime + " sec\n", activity);
				return true;
			}
			
			if (input.equals("cpu_arch")) {
				TerminalHelper.appendRichUI(System.getProperty("os.arch") + "\n", activity);
				return true;
			}
			
			if (input.equals("java_version")) {
				TerminalHelper.appendRichUI(System.getProperty("java.version") + "\n", activity);
				return true;
			}
			
			if (input.equals("internal_storage")) {
				java.io.File path = activity.getFilesDir();
				long total = path.getTotalSpace() / (1024 * 1024);
				long free = path.getFreeSpace() / (1024 * 1024);
				TerminalHelper.appendRichUI("Total: " + total + " MB, Free: " + free + " MB\n", activity);
				return true;
			}
			
			if (input.equals("external_storage")) {
				java.io.File path = android.os.Environment.getExternalStorageDirectory();
				if (path.exists()) {
					long total = path.getTotalSpace() / (1024 * 1024);
					long free = path.getFreeSpace() / (1024 * 1024);
					TerminalHelper.appendRichUI("Total: " + total + " MB, Free: " + free + " MB\n", activity);
				} else {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("cpu_cores")) {
				int cores = java.lang.Runtime.getRuntime().availableProcessors();
				TerminalHelper.appendRichUI(cores + "\n", activity);
				return true;
			}
			
			if (input.equals("ip_address")) {
				try {
					java.net.InetAddress ip = java.net.InetAddress.getLocalHost();
					TerminalHelper.appendRichUI(ip.getHostAddress() + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("is_rooted")) {
				boolean rooted = new java.io.File("/system/bin/su").exists() || new java.io.File("/system/xbin/su").exists();
				TerminalHelper.appendRichUI(rooted ? "YES\n" : "NO\n", activity);
				return true;
			}
			
			if (input.equals("device_name")) {
				try {
					TerminalHelper.appendRichUI(android.os.Build.DEVICE + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("android_id")) {
				try {
					TerminalHelper.appendRichUI(android.provider.Settings.Secure.getString(
					activity.getContentResolver(),
					android.provider.Settings.Secure.ANDROID_ID) + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("screen_resolution")) {
				try {
					DisplayMetrics dm = activity.getResources().getDisplayMetrics();
					TerminalHelper.appendRichUI(dm.widthPixels + "x" + dm.heightPixels + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("density_dpi")) {
				try {
					TerminalHelper.appendRichUI(activity.getResources().getDisplayMetrics().densityDpi + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("is_emulator")) {
				try {
					boolean emu = android.os.Build.FINGERPRINT.contains("generic") ||
					android.os.Build.MODEL.contains("Emulator") ||
					android.os.Build.MANUFACTURER.contains("Genymotion");
					TerminalHelper.appendRichUI(emu ? "Yes\n" : "No\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("boot_time")) {
				try {
					TerminalHelper.appendRichUI("Booted " + (android.os.SystemClock.elapsedRealtime() / 1000) + " seconds ago\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("cpu_abi")) {
				try {
					String abi = android.os.Build.CPU_ABI;
					TerminalHelper.appendRichUI(abi + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("available_memory")) {
				try {
					android.app.ActivityManager am = (android.app.ActivityManager) activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
					android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
					am.getMemoryInfo(mi);
					TerminalHelper.appendRichUI(mi.availMem / 1024 / 1024 + " MB\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("total_memory")) {
				try {
					android.app.ActivityManager am = (android.app.ActivityManager) activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
					android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
					am.getMemoryInfo(mi);
					TerminalHelper.appendRichUI(mi.totalMem / 1024 / 1024 + " MB\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("cpu_count_runtime")) {
				try {
					int cores = Runtime.getRuntime().availableProcessors();
					TerminalHelper.appendRichUI(cores + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("uptime_seconds")) {
				try {
					long uptime = android.os.SystemClock.uptimeMillis() / 1000;
					TerminalHelper.appendRichUI(uptime + " seconds\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("battery_status")) {
				try {
					android.content.IntentFilter ifilter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
					android.content.Intent batteryStatus = activity.registerReceiver(null, ifilter);
					int status = batteryStatus != null ? batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) : -1;
					String stat = (status == android.os.BatteryManager.BATTERY_STATUS_CHARGING) ? "Charging" :
					(status == android.os.BatteryManager.BATTERY_STATUS_DISCHARGING) ? "Discharging" :
					(status == android.os.BatteryManager.BATTERY_STATUS_FULL) ? "Full" : "Unknown";
					TerminalHelper.appendRichUI(stat + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("is_tablet")) {
				try {
					boolean isTablet = (activity.getResources().getConfiguration().screenLayout
					& android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK)
					>= android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
					TerminalHelper.appendRichUI(isTablet ? "Yes\n" : "No\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("ram_info")) {
				try {
					android.app.ActivityManager am = (android.app.ActivityManager) activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
					android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
					if (am != null) {
						am.getMemoryInfo(mi);
						TerminalHelper.appendRichUI("Available RAM: " + mi.availMem / (1024 * 1024) + " MB\n", activity);
						TerminalHelper.appendRichUI("Total RAM: " + mi.totalMem / (1024 * 1024) + " MB\n", activity);
					} else {
						TerminalHelper.appendRichUI("Unavailable\n", activity);
					}
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("cpu_info")) {
				try {
					int cores = Runtime.getRuntime().availableProcessors();
					TerminalHelper.appendRichUI("CPU Cores: " + cores + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("uptime_seconds")) {
				try {
					long uptime = android.os.SystemClock.uptimeMillis() / 1000;
					TerminalHelper.appendRichUI("Uptime: " + uptime + " s\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("is_charging")) {
				try {
					android.content.IntentFilter ifilter = new android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED);
					android.content.Intent batteryStatus = activity.registerReceiver(null, ifilter);
					int status = batteryStatus != null ? batteryStatus.getIntExtra(android.os.BatteryManager.EXTRA_PLUGGED, -1) : -1;
					TerminalHelper.appendRichUI((status > 0 ? "Yes\n" : "No\n"), activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("ram_info")) {
				try {
					android.app.ActivityManager am = (android.app.ActivityManager) activity.getSystemService(android.content.Context.ACTIVITY_SERVICE);
					android.app.ActivityManager.MemoryInfo mi = new android.app.ActivityManager.MemoryInfo();
					if (am != null) {
						am.getMemoryInfo(mi);
						TerminalHelper.appendRichUI("Total RAM: " + mi.totalMem / 1048576 + " MB\n" +
						"Available RAM: " + mi.availMem / 1048576 + " MB\n" +
						"Low Memory: " + mi.lowMemory + "\n", activity);
					} else {
						TerminalHelper.appendRichUI("Unavailable\n", activity);
					}
				} catch (Exception e) {
					TerminalHelper.appendRichUI("error\n", activity);
				}
				return true;
			}
			
			if (input.equals("internal_storage")) {
				try {
					java.io.File path = activity.getFilesDir();
					long total = path.getTotalSpace();
					long free = path.getFreeSpace();
					TerminalHelper.appendRichUI("Total: " + total / 1048576 + " MB\n" +
					"Free: " + free / 1048576 + " MB\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("external_storage")) {
				try {
					java.io.File path = android.os.Environment.getExternalStorageDirectory();
					long total = path.getTotalSpace();
					long free = path.getFreeSpace();
					TerminalHelper.appendRichUI("Total: " + total / 1048576 + " MB\n" +
					"Free: " + free / 1048576 + " MB\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("battery_health")) {
				try {
					IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
					Intent batteryStatus = activity.registerReceiver(null, ifilter);
					int health = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1) : -1;
					String healthStatus;
					switch (health) {
						case BatteryManager.BATTERY_HEALTH_GOOD: healthStatus = "GOOD"; break;
						case BatteryManager.BATTERY_HEALTH_OVERHEAT: healthStatus = "OVERHEAT"; break;
						case BatteryManager.BATTERY_HEALTH_DEAD: healthStatus = "DEAD"; break;
						case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE: healthStatus = "OVER VOLTAGE"; break;
						case BatteryManager.BATTERY_HEALTH_UNKNOWN: healthStatus = "UNKNOWN"; break;
						default: healthStatus = "UNAVAILABLE"; break;
					}
					TerminalHelper.appendRichUI(healthStatus + "\n", activity);
				} catch (Exception e) {
					TerminalHelper.appendRichUI("Unavailable\n", activity);
				}
				return true;
			}
			
			if (input.equals("sensor_info")) {
				try {
					android.hardware.SensorManager sm = (android.hardware.SensorManager) activity.getSystemService(android.content.Context.SENSOR_SERVICE);
					if (sm != null) {
						android.hardware.Sensor light = sm.getDefaultSensor(android.hardware.Sensor.TYPE_LIGHT);
						android.hardware.Sensor prox = sm.getDefaultSensor(android.hardware.Sensor.TYPE_PROXIMITY);
						String lightInfo = (light != null) ? ("Light sensor max: " + light.getMaximumRange()) : "No light sensor";
						String proxInfo = (prox != null) ? ("Proximity sensor max: " + prox.getMaximumRange()) : "No proximity sensor";
						TerminalHelper.appendRichUI(lightInfo + "\n" + proxInfo + "\n", activity);
					} else {
						TerminalHelper.appendRichUI("Sensors unavailable\n", activity);
					}
				} catch (Exception e) {
					TerminalHelper.appendRichUI("error\n", activity);
				}
				return true;
			}
			
			if (input.equals("motion_sensors")) {
				try {
					android.hardware.SensorManager sm = (android.hardware.SensorManager) activity.getSystemService(android.content.Context.SENSOR_SERVICE);
					if (sm != null) {
						android.hardware.Sensor accel = sm.getDefaultSensor(android.hardware.Sensor.TYPE_ACCELEROMETER);
						android.hardware.Sensor gyro = sm.getDefaultSensor(android.hardware.Sensor.TYPE_GYROSCOPE);
						String accelInfo = (accel != null) ? ("Accelerometer max: " + accel.getMaximumRange()) : "No accelerometer";
						String gyroInfo = (gyro != null) ? ("Gyroscope max: " + gyro.getMaximumRange()) : "No gyroscope";
						TerminalHelper.appendRichUI(accelInfo + "\n" + gyroInfo + "\n", activity);
					} else {
						TerminalHelper.appendRichUI("Sensors unavailable\n", activity);
					}
				} catch (Exception e) {
					TerminalHelper.appendRichUI("error\n", activity);
				}
				return true;
			}
			
		} catch (Exception e) {
			TerminalHelper.appendRichUI("error\n", activity);
			return true;
		}
		
		return false;
	}
}
