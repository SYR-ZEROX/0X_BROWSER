package com.zerox.browser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class ProxyChecker {

    // ===== نموذج نتيجة الفحص =====
    public static class ProxyInfo {
        public boolean works;
        public String countryCode;
        public String countryName;
        public String city;
        public double latitude;
        public double longitude;
        public String timezone;
        public String locale;
        public String isp;
        public String ip;
        public String errorMessage;

        public ProxyInfo() {
            this.works = false;
            this.countryCode = "US";
            this.countryName = "United States";
            this.city = "New York";
            this.latitude = 40.7128;
            this.longitude = -74.0060;
            this.timezone = "America/New_York";
            this.locale = "en-US";
            this.isp = "";
            this.ip = "";
            this.errorMessage = "";
        }
    }

    // ===== خريطة الدولة → اللغة =====
    private static final Map<String, String> COUNTRY_TO_LOCALE = new HashMap<>();
    static {
        COUNTRY_TO_LOCALE.put("US", "en-US");
        COUNTRY_TO_LOCALE.put("GB", "en-GB");
        COUNTRY_TO_LOCALE.put("CA", "en-CA");
        COUNTRY_TO_LOCALE.put("AU", "en-AU");
        COUNTRY_TO_LOCALE.put("DE", "de-DE");
        COUNTRY_TO_LOCALE.put("FR", "fr-FR");
        COUNTRY_TO_LOCALE.put("ES", "es-ES");
        COUNTRY_TO_LOCALE.put("IT", "it-IT");
        COUNTRY_TO_LOCALE.put("NL", "nl-NL");
        COUNTRY_TO_LOCALE.put("SE", "sv-SE");
        COUNTRY_TO_LOCALE.put("NO", "nb-NO");
        COUNTRY_TO_LOCALE.put("DK", "da-DK");
        COUNTRY_TO_LOCALE.put("FI", "fi-FI");
        COUNTRY_TO_LOCALE.put("PL", "pl-PL");
        COUNTRY_TO_LOCALE.put("RU", "ru-RU");
        COUNTRY_TO_LOCALE.put("TR", "tr-TR");
        COUNTRY_TO_LOCALE.put("SA", "ar-SA");
        COUNTRY_TO_LOCALE.put("AE", "ar-AE");
        COUNTRY_TO_LOCALE.put("EG", "ar-EG");
        COUNTRY_TO_LOCALE.put("MA", "ar-MA");
        COUNTRY_TO_LOCALE.put("DZ", "ar-DZ");
        COUNTRY_TO_LOCALE.put("TN", "ar-TN");
        COUNTRY_TO_LOCALE.put("JO", "ar-JO");
        COUNTRY_TO_LOCALE.put("LB", "ar-LB");
        COUNTRY_TO_LOCALE.put("IQ", "ar-IQ");
        COUNTRY_TO_LOCALE.put("KW", "ar-KW");
        COUNTRY_TO_LOCALE.put("QA", "ar-QA");
        COUNTRY_TO_LOCALE.put("BH", "ar-BH");
        COUNTRY_TO_LOCALE.put("OM", "ar-OM");
        COUNTRY_TO_LOCALE.put("YE", "ar-YE");
        COUNTRY_TO_LOCALE.put("SY", "ar-SY");
        COUNTRY_TO_LOCALE.put("PS", "ar-PS");
        COUNTRY_TO_LOCALE.put("IN", "en-IN");
        COUNTRY_TO_LOCALE.put("PK", "en-PK");
        COUNTRY_TO_LOCALE.put("BD", "bn-BD");
        COUNTRY_TO_LOCALE.put("CN", "zh-CN");
        COUNTRY_TO_LOCALE.put("HK", "zh-HK");
        COUNTRY_TO_LOCALE.put("TW", "zh-TW");
        COUNTRY_TO_LOCALE.put("JP", "ja-JP");
        COUNTRY_TO_LOCALE.put("KR", "ko-KR");
        COUNTRY_TO_LOCALE.put("TH", "th-TH");
        COUNTRY_TO_LOCALE.put("VN", "vi-VN");
        COUNTRY_TO_LOCALE.put("ID", "id-ID");
        COUNTRY_TO_LOCALE.put("MY", "ms-MY");
        COUNTRY_TO_LOCALE.put("SG", "en-SG");
        COUNTRY_TO_LOCALE.put("PH", "en-PH");
        COUNTRY_TO_LOCALE.put("BR", "pt-BR");
        COUNTRY_TO_LOCALE.put("PT", "pt-PT");
        COUNTRY_TO_LOCALE.put("MX", "es-MX");
        COUNTRY_TO_LOCALE.put("AR", "es-AR");
        COUNTRY_TO_LOCALE.put("CO", "es-CO");
        COUNTRY_TO_LOCALE.put("CL", "es-CL");
        COUNTRY_TO_LOCALE.put("PE", "es-PE");
        COUNTRY_TO_LOCALE.put("ZA", "en-ZA");
        COUNTRY_TO_LOCALE.put("NG", "en-NG");
        COUNTRY_TO_LOCALE.put("KE", "en-KE");
        COUNTRY_TO_LOCALE.put("IL", "he-IL");
        COUNTRY_TO_LOCALE.put("IR", "fa-IR");
        COUNTRY_TO_LOCALE.put("UA", "uk-UA");
        COUNTRY_TO_LOCALE.put("CZ", "cs-CZ");
        COUNTRY_TO_LOCALE.put("HU", "hu-HU");
        COUNTRY_TO_LOCALE.put("RO", "ro-RO");
        COUNTRY_TO_LOCALE.put("GR", "el-GR");
        COUNTRY_TO_LOCALE.put("BG", "bg-BG");
        COUNTRY_TO_LOCALE.put("HR", "hr-HR");
        COUNTRY_TO_LOCALE.put("RS", "sr-RS");
        COUNTRY_TO_LOCALE.put("SK", "sk-SK");
        COUNTRY_TO_LOCALE.put("SI", "sl-SI");
    }

    // ===== الدالة الرئيسية: فحص البروكسي =====
    public static ProxyInfo check(String proxyUrl) {
        ProxyInfo info = new ProxyInfo();

        if (proxyUrl == null || proxyUrl.isEmpty()) {
            info.errorMessage = "البروكسي فارغ";
            return info;
        }

        try {
            // 1. تحليل البروكسي
            // الصيغة: socks5://user:pass@ip:port
            String cleanUrl = proxyUrl;
            if (cleanUrl.startsWith("socks5://")) {
                cleanUrl = cleanUrl.substring(9);
            } else if (cleanUrl.startsWith("socks://")) {
                cleanUrl = cleanUrl.substring(8);
            } else if (cleanUrl.startsWith("http://")) {
                cleanUrl = cleanUrl.substring(7);
            }

            String user = "";
            String pass = "";
            String hostPort = cleanUrl;

            // استخراج user:pass
            if (cleanUrl.contains("@")) {
                String[] userPassParts = cleanUrl.split("@");
                hostPort = userPassParts[1];
                String[] userPass = userPassParts[0].split(":");
                if (userPass.length >= 2) {
                    user = userPass[0];
                    pass = userPass[1];
                } else if (userPass.length == 1) {
                    user = userPass[0];
                }
            }

            // استخراج host:port
            String host;
            int port;
            if (hostPort.contains(":")) {
                String[] hp = hostPort.split(":");
                host = hp[0];
                port = Integer.parseInt(hp[1]);
            } else {
                host = hostPort;
                port = 1080;
            }

            // 2. إنشاء Proxy
            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(host, port));

            // 3. الاتصال بـ ip-api.com لجلب المعلومات
            URL url = new URL("http://ip-api.com/json/?fields=status,country,countryCode,city,lat,lon,timezone,isp,query");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy);
            conn.setConnectTimeout(15000);
            conn.setReadTimeout(15000);
            conn.setRequestMethod("GET");

            // إضافة المصادقة إذا وجدت
            if (!user.isEmpty() && !pass.isEmpty()) {
                String auth = user + ":" + pass;
                String encodedAuth = android.util.Base64.encodeToString(auth.getBytes(), android.util.Base64.NO_WRAP);
                conn.setRequestProperty("Proxy-Authorization", "Basic " + encodedAuth);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 4. تحليل JSON
                JSONObject json = new JSONObject(response.toString());
                if ("success".equals(json.optString("status"))) {
                    info.works = true;
                    info.countryCode = json.optString("countryCode", "US");
                    info.countryName = json.optString("country", "United States");
                    info.city = json.optString("city", "New York");
                    info.latitude = json.optDouble("lat", 40.7128);
                    info.longitude = json.optDouble("lon", -74.0060);
                    info.timezone = json.optString("timezone", "America/New_York");
                    info.isp = json.optString("isp", "");
                    info.ip = json.optString("query", "");

                    // 5. تحويل الدولة إلى لغة
                    info.locale = COUNTRY_TO_LOCALE.getOrDefault(info.countryCode, "en-US");
                } else {
                    info.errorMessage = "فشل جلب المعلومات";
                }
            } else {
                info.errorMessage = "HTTP " + responseCode;
            }
            conn.disconnect();

        } catch (Exception e) {
            info.errorMessage = e.getMessage();
            e.printStackTrace();
        }

        return info;
    }
}