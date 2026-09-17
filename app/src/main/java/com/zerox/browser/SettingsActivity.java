package com.zerox.browser;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    private EditText proxyIpInput;
    private EditText proxyPortInput;
    private EditText proxyUserInput;
    private EditText proxyPassInput;
    private EditText latitudeInput;
    private EditText longitudeInput;
    private EditText timezoneInput;
    private EditText localeInput;
    private RadioButton modeAuto;
    private RadioButton modeManual;
    private RadioButton modeHybrid;
    private Button checkProxyBtn;
    private Button saveSettingsBtn;

    private String editingProfileName = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // ربط العناصر
        proxyIpInput = findViewById(R.id.proxyIpInput);
        proxyPortInput = findViewById(R.id.proxyPortInput);
        proxyUserInput = findViewById(R.id.proxyUserInput);
        proxyPassInput = findViewById(R.id.proxyPassInput);
        latitudeInput = findViewById(R.id.latitudeInput);
        longitudeInput = findViewById(R.id.longitudeInput);
        timezoneInput = findViewById(R.id.timezoneInput);
        localeInput = findViewById(R.id.localeInput);
        modeAuto = findViewById(R.id.modeAuto);
        modeManual = findViewById(R.id.modeManual);
        modeHybrid = findViewById(R.id.modeHybrid);
        checkProxyBtn = findViewById(R.id.checkProxyBtn);
        saveSettingsBtn = findViewById(R.id.saveSettingsBtn);

        // معرفة وضع الدخول
        if (getIntent().hasExtra("edit_profile")) {
            editingProfileName = getIntent().getStringExtra("edit_profile");
            loadProfileData();
        } else if (getIntent().hasExtra("new_profile")) {
            editingProfileName = "profile_" + System.currentTimeMillis();
        } else {
            editingProfileName = ProfileManager.getActiveProfile(this).name;
            loadProfileData();
        }

        // زر الرجوع
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());

        // زر فحص البروكسي
        checkProxyBtn.setOnClickListener(v -> checkProxy());

        // زر حفظ الإعدادات
        saveSettingsBtn.setOnClickListener(v -> saveSettings());
    }

    private void loadProfileData() {
        Profile profile = ProfileManager.getProfile(this, editingProfileName);

        // البروكسي
        if (profile.proxy != null && !profile.proxy.isEmpty()) {
            String proxy = profile.proxy;
            if (proxy.startsWith("socks5://")) proxy = proxy.substring(9);
            if (proxy.startsWith("socks://")) proxy = proxy.substring(8);

            String user = "";
            String pass = "";
            String hostPort = proxy;

            if (proxy.contains("@")) {
                String[] up = proxy.split("@");
                hostPort = up[1];
                String[] creds = up[0].split(":");
                if (creds.length >= 2) {
                    user = creds[0];
                    pass = creds[1];
                } else if (creds.length == 1) {
                    user = creds[0];
                }
            }

            if (hostPort.contains(":")) {
                String[] hp = hostPort.split(":");
                proxyIpInput.setText(hp[0]);
                proxyPortInput.setText(hp[1]);
            } else {
                proxyIpInput.setText(hostPort);
            }

            proxyUserInput.setText(user);
            proxyPassInput.setText(pass);
        }

        // الموقع
        latitudeInput.setText(String.valueOf(profile.latitude));
        longitudeInput.setText(String.valueOf(profile.longitude));
        timezoneInput.setText(profile.timezone);
        localeInput.setText(profile.locale);

        // الوضع
        if ("auto".equals(profile.locationMode)) modeAuto.setChecked(true);
        else if ("manual".equals(profile.locationMode)) modeManual.setChecked(true);
        else modeHybrid.setChecked(true);
    }

    private void checkProxy() {
        String ip = proxyIpInput.getText().toString().trim();
        String port = proxyPortInput.getText().toString().trim();
        String user = proxyUserInput.getText().toString().trim();
        String pass = proxyPassInput.getText().toString().trim();

        if (ip.isEmpty() || port.isEmpty()) {
            Toast.makeText(this, "❌ أدخل IP والمنفذ", Toast.LENGTH_SHORT).show();
            return;
        }

        String proxyUrl = "socks5://";
        if (!user.isEmpty() && !pass.isEmpty()) {
            proxyUrl += user + ":" + pass + "@";
        }
        proxyUrl += ip + ":" + port;

        Toast.makeText(this, "📡 جاري الفحص...", Toast.LENGTH_SHORT).show();

        final String finalProxyUrl = proxyUrl;
        new Thread(() -> {
            ProxyChecker.ProxyInfo info = ProxyChecker.check(finalProxyUrl);
            runOnUiThread(() -> {
                if (info.works) {
                    // تعبئة الحقول تلقائياً
                    latitudeInput.setText(String.valueOf(info.latitude));
                    longitudeInput.setText(String.valueOf(info.longitude));
                    timezoneInput.setText(info.timezone);
                    localeInput.setText(info.locale);

                    Toast.makeText(this,
                            "✅ البروكسي يعمل\n📍 " + info.city + ", " + info.countryName +
                            "\n🕒 " + info.timezone +
                            "\n🗣️ " + info.locale,
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this,
                            "❌ البروكسي لا يعمل\n" + info.errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }

    private void saveSettings() {
        Profile profile = new Profile();
        profile.name = editingProfileName;

        // البروكسي
        String ip = proxyIpInput.getText().toString().trim();
        String port = proxyPortInput.getText().toString().trim();
        String user = proxyUserInput.getText().toString().trim();
        String pass = proxyPassInput.getText().toString().trim();

        if (!ip.isEmpty() && !port.isEmpty()) {
            String proxyUrl = "socks5://";
            if (!user.isEmpty() && !pass.isEmpty()) {
                proxyUrl += user + ":" + pass + "@";
            }
            proxyUrl += ip + ":" + port;
            profile.proxy = proxyUrl;
        } else {
            profile.proxy = "";
        }

        // الموقع
        try {
            profile.latitude = Double.parseDouble(latitudeInput.getText().toString().trim());
        } catch (Exception e) { profile.latitude = 40.7128; }

        try {
            profile.longitude = Double.parseDouble(longitudeInput.getText().toString().trim());
        } catch (Exception e) { profile.longitude = -74.0060; }

        profile.timezone = timezoneInput.getText().toString().trim();
        if (profile.timezone.isEmpty()) profile.timezone = "America/New_York";

        profile.locale = localeInput.getText().toString().trim();
        if (profile.locale.isEmpty()) profile.locale = "en-US";

        // الوضع
        if (modeAuto.isChecked()) profile.locationMode = "auto";
        else if (modeManual.isChecked()) profile.locationMode = "manual";
        else profile.locationMode = "hybrid";

        // حفظ
        ProfileManager.saveProfile(this, profile);
        Toast.makeText(this, "✅ تم الحفظ: " + editingProfileName, Toast.LENGTH_SHORT).show();
        finish();
    }
}