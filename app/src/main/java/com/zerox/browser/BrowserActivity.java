package com.zerox.browser;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;

import org.mozilla.geckoview.GeckoRuntime;
import org.mozilla.geckoview.GeckoRuntimeSettings;
import org.mozilla.geckoview.GeckoSession;
import org.mozilla.geckoview.GeckoView;
import org.mozilla.geckoview.GeckoSessionSettings;

public class BrowserActivity extends AppCompatActivity {

    private static final String TAG = "0X_BROWSER";

    private GeckoView geckoView;
    private GeckoSession geckoSession;
    private GeckoRuntime geckoRuntime;
    private EditText urlInput;
    private ImageButton backBtn;
    private ImageButton forwardBtn;
    private ImageButton refreshBtn;
    private Button goBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        // ربط العناصر
        geckoView = findViewById(R.id.geckoView);
        urlInput = findViewById(R.id.urlInput);
        backBtn = findViewById(R.id.backBtn);
        forwardBtn = findViewById(R.id.forwardBtn);
        refreshBtn = findViewById(R.id.refreshBtn);
        goBtn = findViewById(R.id.goBtn);

        // جلب البروفايل النشط
        Profile profile = ProfileManager.getActiveProfile(this);

        // ===== إعداد GeckoRuntime =====
        GeckoRuntimeSettings.Builder runtimeSettings = new GeckoRuntimeSettings.Builder()
                .javaScriptEnabled(!profile.blockJavaScript)
                .remoteDebuggingEnabled(false)
                .locales(new String[]{profile.locale})
                .debugLogging(false);

        // ===== البروكسي =====
        if (profile.proxy != null && !profile.proxy.isEmpty()) {
            String proxyUrl = normalizeProxy(profile.proxy);
            runtimeSettings.arguments(new String[]{proxyUrl});
            Log.i(TAG, "Proxy set: " + proxyUrl);
        }

        // ===== منع تسريب DNS =====
        if (profile.blockDNSLeak) {
            runtimeSettings.arguments(new String[]{
                    "--host-resolver-rules=MAP * ~NOTFOUND"
            });
            Log.i(TAG, "DNS leak protection enabled");
        }

        // ===== تعطيل Prefetch/Preconnect =====
        if (profile.blockPrefetch || profile.blockPreconnect) {
            runtimeSettings.arguments(new String[]{
                    "--disable-features=NetworkPrediction,PreconnectToSearch,Prerender2"
            });
            Log.i(TAG, "Prefetch/Preconnect disabled");
        }

        // ===== تعطيل Service Workers =====
        if (profile.blockServiceWorkers) {
            runtimeSettings.arguments(new String[]{
                    "--disable-features=ServiceWorker"
            });
            Log.i(TAG, "Service Workers disabled");
        }

        geckoRuntime = GeckoRuntime.create(this, runtimeSettings.build());

        // ===== تثبيت إضافة التمويه =====
        geckoRuntime.getWebExtensionController()
                .ensureBuiltIn("resource://android/assets/goj_spoofer/", "0x@browser.com")
                .accept(
                        extension -> Log.i(TAG, "Spoofer installed: " + extension.id),
                        error -> Log.e(TAG, "Spoofer error: " + error.getMessage())
                );

        // ===== إعداد الجلسة =====
        GeckoSessionSettings sessionSettings = new GeckoSessionSettings.Builder()
                .userAgentOverride(profile.userAgent)
                .build();

        geckoSession = new GeckoSession(sessionSettings);
        geckoSession.open(geckoRuntime);
        geckoView.setSession(geckoSession);

        // ===== تحميل الصفحة الرئيسية =====
        geckoSession.loadUri("https://www.google.com");

        // ===== أزرار التنقل =====
        backBtn.setOnClickListener(v -> geckoSession.goBack());
        forwardBtn.setOnClickListener(v -> geckoSession.goForward());
        refreshBtn.setOnClickListener(v -> geckoSession.reload());

        // ===== زر "اذهب" =====
        goBtn.setOnClickListener(v -> {
            String url = urlInput.getText().toString().trim();
            if (!url.isEmpty()) {
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    if (url.contains(".") && !url.contains(" ")) {
                        url = "https://" + url;
                    } else {
                        url = "https://www.google.com/search?q=" + url;
                    }
                }
                geckoSession.loadUri(url);
            }
        });


    // ===== تحويل البروكسي للصيغة الصحيحة =====
    private String normalizeProxy(String proxy) {
        // الصيغة المدخلة: socks5://user:pass@ip:port
        // أو: ip:port:user:pass
        // أو: ip:port

        // إذا كانت الصيغة كاملة
        if (proxy.startsWith("socks5://") || proxy.startsWith("socks://")) {
            return proxy;
        }

        // إذا كانت ip:port:user:pass
        if (proxy.split(":").length == 4) {
            String[] parts = proxy.split(":");
            return "socks5://" + parts[2] + ":" + parts[3] + "@" + parts[0] + ":" + parts[1];
        }

        // إذا كانت ip:port
        if (proxy.split(":").length == 2) {
            return "socks5://" + proxy;
        }

        // افتراضي
        return "socks5://" + proxy;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (geckoSession != null) {
            geckoSession.close();
        }
    }
}