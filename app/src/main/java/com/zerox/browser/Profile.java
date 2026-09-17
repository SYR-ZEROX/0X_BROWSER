package com.zerox.browser;

public class Profile {
    // ===== معلومات أساسية =====
    public String name;
    public String proxy;
    
    // ===== الموقع =====
    public String locationMode;   // "auto" / "manual" / "hybrid"
    public double latitude;
    public double longitude;
    public String timezone;
    public String locale;
    public String countryCode;
    public String countryName;
    public String city;
    public String isp;
    
    // ===== البصمة =====
    public String deviceModel;
    public String userAgent;
    public boolean canvasNoise;
    public boolean webglSpoofing;
    public boolean audioNoise;
    public boolean fontSpoofing;
    public String webglVendor;
    public String webglRenderer;
    public int hardwareConcurrency;
    public int deviceMemory;
    
    // ===== الحماية =====
    public boolean blockWebRTC;
    public boolean blockDNSLeak;
    public boolean blockPrefetch;
    public boolean blockPreconnect;
    public boolean blockServiceWorkers;
    
    // ===== الخصوصية =====
    public boolean blockAds;
    public boolean blockTrackers;
    public boolean blockCookies;
    public boolean blockJavaScript;
    public boolean blockImages;
    public boolean blockPopups;
    public boolean incognitoMode;
    public boolean clearOnExit;
    public boolean autoClearCache;
    
    // ===== متقدم =====
    public String platform;
    public String platformVersion;

    public Profile() {
        // ===== الافتراضيات =====
        this.name = "default";
        this.proxy = "";
        
        this.locationMode = "hybrid";
        this.latitude = 40.7128;
        this.longitude = -74.0060;
        this.timezone = "America/New_York";
        this.locale = "en-US";
        this.countryCode = "US";
        this.countryName = "United States";
        this.city = "New York";
        this.isp = "";
        
        this.deviceModel = "Samsung Galaxy S23 Ultra";
        this.userAgent = "Mozilla/5.0 (Linux; Android 13; SM-S918B) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36";
        this.canvasNoise = true;
        this.webglSpoofing = true;
        this.audioNoise = true;
        this.fontSpoofing = true;
        this.webglVendor = "Qualcomm";
        this.webglRenderer = "Adreno (TM) 740";
        this.hardwareConcurrency = 8;
        this.deviceMemory = 12;
        
        this.blockWebRTC = true;
        this.blockDNSLeak = true;
        this.blockPrefetch = true;
        this.blockPreconnect = true;
        this.blockServiceWorkers = true;
        
        this.blockAds = true;
        this.blockTrackers = true;
        this.blockCookies = false;
        this.blockJavaScript = false;
        this.blockImages = false;
        this.blockPopups = true;
        this.incognitoMode = false;
        this.clearOnExit = true;
        this.autoClearCache = true;
        
        this.platform = "Android";
        this.platformVersion = "13.0.0";
    }
}