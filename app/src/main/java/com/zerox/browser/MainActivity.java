package com.zerox.browser;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView activeProfileName;
    private TextView activeProxy;
    private TextView activeLocation;
    private TextView activeLocale;
    private Button startBrowsingBtn;
    private Button manageProfilesBtn;
    private Button settingsBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ربط العناصر
        activeProfileName = findViewById(R.id.activeProfileName);
        activeProxy = findViewById(R.id.activeProxy);
        activeLocation = findViewById(R.id.activeLocation);
        activeLocale = findViewById(R.id.activeLocale);
        startBrowsingBtn = findViewById(R.id.startBrowsingBtn);
        manageProfilesBtn = findViewById(R.id.manageProfilesBtn);
        settingsBtn = findViewById(R.id.settingsBtn);

        // تحديث البطاقة عند كل فتح
        updateActiveProfileCard();

        // زر بدء التصفح
        startBrowsingBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, BrowserActivity.class);
            startActivity(intent);
        });

        // زر إدارة البروفايلات
        manageProfilesBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfilesActivity.class);
            startActivity(intent);
        });

        // زر الإعدادات
        settingsBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateActiveProfileCard();
    }

    private void updateActiveProfileCard() {
        Profile profile = ProfileManager.getActiveProfile(this);

        // اسم البروفايل
        activeProfileName.setText("🔒 " + profile.name);

        // البروكسي
        if (profile.proxy == null || profile.proxy.isEmpty()) {
            activeProxy.setText("🌐 البروكسي: غير محدد");
        } else {
            String proxyDisplay = profile.proxy;
            // إخفاء كلمة المرور
            if (proxyDisplay.contains("@")) {
                String[] parts = proxyDisplay.split("@");
                proxyDisplay = "***@" + parts[1];
            }
            activeProxy.setText("🌐 البروكسي: " + proxyDisplay);
        }

        // الموقع
        if (profile.city != null && !profile.city.isEmpty()) {
            activeLocation.setText("📍 الموقع: " + profile.city + ", " + profile.countryName);
        } else {
            activeLocation.setText("📍 الموقع: " + profile.latitude + ", " + profile.longitude);
        }

        // اللغة
        activeLocale.setText("🌍 اللغة: " + profile.locale);
    }
}