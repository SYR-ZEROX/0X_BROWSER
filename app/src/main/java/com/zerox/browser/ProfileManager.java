package com.zerox.browser;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileManager {
    private static final String PREF_NAME = "ZEROX_PROFILES";
    private static final String KEY_ACTIVE = "active_profile";
    private static final String KEY_PROFILES_LIST = "profiles_list";

    // ===== حفظ بروفايل =====
    public static void saveProfile(Context context, Profile profile) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        try {
            JSONObject json = new JSONObject();
            
            // معلومات أساسية
            json.put("name", profile.name);
            json.put("proxy", profile.proxy);
            
            // الموقع
            json.put("locationMode", profile.locationMode);
            json.put("latitude", profile.latitude);
            json.put("longitude", profile.longitude);
            json.put("timezone", profile.timezone);
            json.put("locale", profile.locale);
            json.put("countryCode", profile.countryCode);
            json.put("countryName", profile.countryName);
            json.put("city", profile.city);
            json.put("isp", profile.isp);
            
            // البصمة
            json.put("deviceModel", profile.deviceModel);
            json.put("userAgent", profile.userAgent);
            json.put("canvasNoise", profile.canvasNoise);
            json.put("webglSpoofing", profile.webglSpoofing);
            json.put("audioNoise", profile.audioNoise);
            json.put("fontSpoofing", profile.fontSpoofing);
            json.put("webglVendor", profile.webglVendor);
            json.put("webglRenderer", profile.webglRenderer);
            json.put("hardwareConcurrency", profile.hardwareConcurrency);
            json.put("deviceMemory", profile.deviceMemory);
            
            // الحماية
            json.put("blockWebRTC", profile.blockWebRTC);
            json.put("blockDNSLeak", profile.blockDNSLeak);
            json.put("blockPrefetch", profile.blockPrefetch);
            json.put("blockPreconnect", profile.blockPreconnect);
            json.put("blockServiceWorkers", profile.blockServiceWorkers);
            
            // الخصوصية
            json.put("blockAds", profile.blockAds);
            json.put("blockTrackers", profile.blockTrackers);
            json.put("blockCookies", profile.blockCookies);
            json.put("blockJavaScript", profile.blockJavaScript);
            json.put("blockImages", profile.blockImages);
            json.put("blockPopups", profile.blockPopups);
            json.put("incognitoMode", profile.incognitoMode);
            json.put("clearOnExit", profile.clearOnExit);
            json.put("autoClearCache", profile.autoClearCache);
            
            // متقدم
            json.put("platform", profile.platform);
            json.put("platformVersion", profile.platformVersion);
            
            // حفظ البروفايل
            prefs.edit().putString(profile.name, json.toString()).apply();
            
            // إضافة للقائمة
            addToProfilesList(prefs, profile.name);
            
            // تعيين كنشط
            prefs.edit().putString(KEY_ACTIVE, profile.name).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== إضافة للقائمة =====
    private static void addToProfilesList(SharedPreferences prefs, String name) {
        try {
            String listJson = prefs.getString(KEY_PROFILES_LIST, "[]");
            JSONArray arr = new JSONArray(listJson);
            boolean exists = false;
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getString(i).equals(name)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                arr.put(name);
                prefs.edit().putString(KEY_PROFILES_LIST, arr.toString()).apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===== جلب البروفايل النشط =====
    public static Profile getActiveProfile(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String activeName = prefs.getString(KEY_ACTIVE, "default");
        return getProfile(context, activeName);
    }

    // ===== جلب بروفايل بالاسم =====
    public static Profile getProfile(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String jsonStr = prefs.getString(name, null);
        
        Profile profile = new Profile();
        profile.name = name;
        
        if (jsonStr != null) {
            try {
                JSONObject json = new JSONObject(jsonStr);
                
                profile.proxy = json.optString("proxy", "");
                
                profile.locationMode = json.optString("locationMode", "hybrid");
                profile.latitude = json.optDouble("latitude", 40.7128);
                profile.longitude = json.optDouble("longitude", -74.0060);
                profile.timezone = json.optString("timezone", "America/New_York");
                profile.locale = json.optString("locale", "en-US");
                profile.countryCode = json.optString("countryCode", "US");
                profile.countryName = json.optString("countryName", "United States");
                profile.city = json.optString("city", "New York");
                profile.isp = json.optString("isp", "");
                
                profile.deviceModel = json.optString("deviceModel", "Samsung Galaxy S23 Ultra");
                profile.userAgent = json.optString("userAgent", "");
                profile.canvasNoise = json.optBoolean("canvasNoise", true);
                profile.webglSpoofing = json.optBoolean("webglSpoofing", true);
                profile.audioNoise = json.optBoolean("audioNoise", true);
                profile.fontSpoofing = json.optBoolean("fontSpoofing", true);
                profile.webglVendor = json.optString("webglVendor", "Qualcomm");
                profile.webglRenderer = json.optString("webglRenderer", "Adreno (TM) 740");
                profile.hardwareConcurrency = json.optInt("hardwareConcurrency", 8);
                profile.deviceMemory = json.optInt("deviceMemory", 12);
                
                profile.blockWebRTC = json.optBoolean("blockWebRTC", true);
                profile.blockDNSLeak = json.optBoolean("blockDNSLeak", true);
                profile.blockPrefetch = json.optBoolean("blockPrefetch", true);
                profile.blockPreconnect = json.optBoolean("blockPreconnect", true);
                profile.blockServiceWorkers = json.optBoolean("blockServiceWorkers", true);
                
                profile.blockAds = json.optBoolean("blockAds", true);
                profile.blockTrackers = json.optBoolean("blockTrackers", true);
                profile.blockCookies = json.optBoolean("blockCookies", false);
                profile.blockJavaScript = json.optBoolean("blockJavaScript", false);
                profile.blockImages = json.optBoolean("blockImages", false);
                profile.blockPopups = json.optBoolean("blockPopups", true);
                profile.incognitoMode = json.optBoolean("incognitoMode", false);
                profile.clearOnExit = json.optBoolean("clearOnExit", true);
                profile.autoClearCache = json.optBoolean("autoClearCache", true);
                
                profile.platform = json.optString("platform", "Android");
                profile.platformVersion = json.optString("platformVersion", "13.0.0");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return profile;
    }

    // ===== جلب كل البروفايلات =====
    public static List<String> getAllProfileNames(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        List<String> names = new ArrayList<>();
        try {
            String listJson = prefs.getString(KEY_PROFILES_LIST, "[]");
            JSONArray arr = new JSONArray(listJson);
            for (int i = 0; i < arr.length(); i++) {
                names.add(arr.getString(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (names.isEmpty()) {
            names.add("default");
        }
        return names;
    }

    // ===== تعيين بروفايل نشط =====
    public static void setActiveProfile(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_ACTIVE, name).apply();
    }

    // ===== حذف بروفايل =====
    public static void deleteProfile(Context context, String name) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(name).apply();
        
        try {
            String listJson = prefs.getString(KEY_PROFILES_LIST, "[]");
            JSONArray arr = new JSONArray(listJson);
            JSONArray newArr = new JSONArray();
            for (int i = 0; i < arr.length(); i++) {
                if (!arr.getString(i).equals(name)) {
                    newArr.put(arr.getString(i));
                }
            }
            prefs.edit().putString(KEY_PROFILES_LIST, newArr.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}