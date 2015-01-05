package com.leec.tools.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;
import android.widget.Toast;

public class AppUtils {
	private static final String TAG = AppUtils.class.getSimpleName();
	
	public static final String COMPONENT_ENABLED_STATE_ENABLED = "1";
	public static final String COMPONENT_ENABLED_STATE_DISABLED = "2";
	
	public static List<Map<String, Object>> getPackageInfos(Boolean state, PackageManager pm) {

		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES | PackageManager.GET_DISABLED_COMPONENTS
				| PackageManager.GET_UNINSTALLED_PACKAGES);

		String status;
		for (PackageInfo p : packages) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("application_icon", pm.getApplicationIcon(p.applicationInfo));
			map.put("application_label", pm.getApplicationLabel(p.applicationInfo).toString());
			map.put("package_name", p.applicationInfo.packageName);
			status = getPackageEnabledState(p.applicationInfo.packageName, pm);
			map.put("enable_state", status);

			if (state == null)
				datas.add(map);
			else if (state && COMPONENT_ENABLED_STATE_ENABLED.equals(status)) {
				datas.add(map);
			} else if (!state && COMPONENT_ENABLED_STATE_DISABLED.equals(status)) {
				datas.add(map);
			}
		}
		Collections.sort(datas, new MapComparator<Object>(new String[]{"package_name"}));
		
		return datas;
	}
	
	public static List<Map<String, Object>> getPackageDetails(String packageName, PackageManager pm, int flags) {
		
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		try {
			PackageInfo p = pm.getPackageInfo(packageName, flags | PackageManager.GET_DISABLED_COMPONENTS);
			
			PackageItemInfo[] cis = null;

			if ((flags & PackageManager.GET_ACTIVITIES) == PackageManager.GET_ACTIVITIES && p.activities != null) {
				cis = p.activities;
			} else if ((flags & PackageManager.GET_SERVICES) == PackageManager.GET_SERVICES && p.services != null) {
				cis = p.services;
			} else if ((flags & PackageManager.GET_RECEIVERS) == PackageManager.GET_RECEIVERS && p.receivers != null) {
				cis = p.receivers;
			}
			
			if (cis != null) {
				for (PackageItemInfo ci : cis) {
					Map<String, Object> map = new HashMap<String, Object>();
					ComponentName component = new ComponentName(ci.packageName, ci.name);
					map.put("component", component);
					map.put("component_name", ci.name);
					map.put("enable_state", getComponentEnabledState(component, pm));
					datas.add(map);
				}
			}
			
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getPackageDetails Unkonw package: " + packageName);
		}
		Collections.sort(datas, new MapComparator<Object>(new String[]{"component_name"}));
		
		return datas;
	}
	
	public static List<List<Map<String, Object>>> getPermissions(String packageName, PackageManager pm) {
		
		List<List<Map<String, Object>>>  datas = new ArrayList<List<Map<String, Object>>>();
		List<Map<String, Object>> rFeatures = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> cPermissions = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> rPermissions = new ArrayList<Map<String, Object>>();
		datas.add(rFeatures);
		datas.add(cPermissions);
		datas.add(rPermissions);
		
		try {
			PackageInfo p = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS | PackageManager.GET_CONFIGURATIONS);

			if (p.requestedPermissions != null) {
				for(String rp : p.requestedPermissions) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("permission_name", rp);
					rPermissions.add(map);
				}
				Collections.sort(rPermissions, new MapComparator<Object>(new String[]{"permission_name"}));
			}
			
			if (p.permissions != null) {
				for(PermissionInfo permission : p.permissions) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("permission_name", permission.name);
					cPermissions.add(map);
				}
				Collections.sort(cPermissions, new MapComparator<Object>(new String[]{"permission_name"}));
			}
			
			if (p.reqFeatures != null) {
				for(FeatureInfo reqFeature : p.reqFeatures) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("permission_name", reqFeature.name);
					rFeatures.add(map);
				}
				Collections.sort(rFeatures, new MapComparator<Object>(new String[]{"permission_name"}));
			}
			
		} catch (NameNotFoundException e) {
			Log.e(TAG, "getPermissions Unkonw package: " + packageName);
		}
		
		return datas;
	}
	
	public static List<Map<String, Object>> getRequestedPermissions(String packageName, PackageManager pm) {

		List<Map<String, Object>> permissions = new ArrayList<Map<String, Object>>();
		try {
			PackageInfo p = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
			if (p.requestedPermissions != null) {
				for(String rp : p.requestedPermissions) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("permission_name", rp);
					permissions.add(map);
				}
			}

		} catch (NameNotFoundException e) {
			Log.e(TAG, "getPermissions Unkonw package: " + packageName);
		}
		Collections.sort(permissions, new MapComparator<Object>(new String[]{"permission_name"}));
		return permissions;
	}
	
	public static List<Map<String, Object>> queryActionActivities(String action, PackageManager pm) {
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		
		Intent intent = new Intent(action);
		
		List<ResolveInfo> ris;
		if (Intent.ACTION_BOOT_COMPLETED.equals(action) || AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
			ris = pm.queryBroadcastReceivers(intent, PackageManager.GET_INTENT_FILTERS | PackageManager.GET_DISABLED_COMPONENTS);
            if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
                ris.addAll(pm.queryIntentActivities(new Intent(Intent.ACTION_CREATE_SHORTCUT), PackageManager.GET_INTENT_FILTERS | PackageManager.GET_DISABLED_COMPONENTS));
            }
		} else {
			if (Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action)) {
				intent.setType("*/*");
			} else if (Intent.ACTION_MAIN.equals(action)) {
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
			}

			ris = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS | PackageManager.GET_DISABLED_COMPONENTS);
		}
		
		/*if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
			AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
			List<AppWidgetProviderInfo> windgets = widgetManager.getInstalledProviders();
			for (AppWidgetProviderInfo windget : windgets) {
				ComponentName provider = windget.provider;
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("package_name", provider.getPackageName());
					
				map.put("component_icon", pm.getDrawable(provider.getPackageName(), windget.icon, null));
				
				map.put("component_label", windget.label);
				map.put("component_name", provider.getClassName());
				map.put("component", provider);
				map.put("enable_state", getComponentEnabledState(windget.provider, pm));
				
				datas.add(map);
			}
		}*/
		
		for (ResolveInfo ri : ris) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("package_name", ri.activityInfo.packageName);
			map.put("component_icon", ri.loadIcon(pm));
			map.put("component_label", ri.loadLabel(pm));
			map.put("component_name", ri.activityInfo.name);
			ComponentName component = new ComponentName(ri.activityInfo.packageName, ri.activityInfo.name);
			map.put("component", component);
			map.put("enable_state", getComponentEnabledState(component, pm));
			datas.add(map);
		}
		Collections.sort(datas, new MapComparator<Object>(new String[]{"package_name"}));
		return datas;
	}
	
	public static List<Map<String, Object>> queryPackageForPermission(String permission, PackageManager pm) {

		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		for (PackageInfo p : packages) {
			if (p.requestedPermissions != null) {
				for(String rp : p.requestedPermissions) {
					if (rp.equals(permission)) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("application_icon", pm.getApplicationIcon(p.applicationInfo));
						map.put("application_label", pm.getApplicationLabel(p.applicationInfo).toString());
						map.put("package_name", p.applicationInfo.packageName);
						map.put("enable_state", getPackageEnabledState(p.applicationInfo.packageName, pm));
						datas.add(map);
						break;
					}
				}
			}
		}
		Collections.sort(datas, new MapComparator<Object>(new String[]{"package_name"}));
		
		return datas;
	}
	
	public static void setApplicationEnabledState(List<String> packages, int state, PackageManager pm) {
		Log.d(TAG, "[BEGIN] setApplicationEnabledState");
    	for (String packageName : packages) {
    		pm.setApplicationEnabledSetting(packageName, state, 0);
			Log.d(TAG, "Set package=" + packageName + " to new state " + state);
    	}
	}
	
	public static void setComponentEnabledState(List<ComponentName> componentNames, int state, PackageManager pm) {
		Log.d(TAG, "[BEGIN] setComponentEnabledState");
    	for (ComponentName componentName : componentNames) {
    		pm.setComponentEnabledSetting(componentName, state, 0);
			Log.d(TAG, "Set component=" + componentName.getClassName() + " to new state " + state);
    	}
	}
	
	public static void processWithSecurity(Context context, Processor processor) {
		try {
			processor.process(context);
		} catch (SecurityException e) {
			Toast.makeText(context, "Can't grant permissions, Please move apk to system folder", Toast.LENGTH_LONG).show();
			Log.e(TAG, "No permissions...", e);
		}
	}
	
	public static interface Processor {
		public void process(Context context);
	}
	
	public static String getPackageEnabledState(String packageName, PackageManager pm) {
		int status = pm.getApplicationEnabledSetting(packageName);
		return getEnabledState(status);
	}
	
	public static String getComponentEnabledState(ComponentName componentName, PackageManager pm) {
		int status = pm.getComponentEnabledSetting(componentName);
		return getEnabledState(status);
	}
	
	public static String getEnabledState(int status) {
		if (status == PackageManager.COMPONENT_ENABLED_STATE_ENABLED || status == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
			return COMPONENT_ENABLED_STATE_ENABLED;
		} else {
			return COMPONENT_ENABLED_STATE_DISABLED;
		}
	}
	
	public static Object decode(Object expr, Object... pat) {
    	for(int i = 0; i < pat.length; i++) {
    		if(i + 1 >= pat.length) //default
    			return pat[i];
    		
    		if(expr.equals(pat[i++]))
    			return pat[i];
    	}
    	return null;
    }
	
}
