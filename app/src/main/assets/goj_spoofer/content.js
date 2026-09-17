// ============================================================
// 0X BROWSER - Fingerprint Spoofing Script
// ============================================================

(function() {
    'use strict';

    // ============================================================
    // 1. Canvas Fingerprint Spoofing
    // ============================================================
    const origToDataURL = HTMLCanvasElement.prototype.toDataURL;
    HTMLCanvasElement.prototype.toDataURL = function() {
        try {
            const ctx = this.getContext('2d');
            if (ctx) {
                ctx.fillStyle = 'rgba(255, 255, 255, 0.01)';
                ctx.fillText('0X_BROWSER', 0, 42);
            }
        } catch (e) {}
        return origToDataURL.apply(this, arguments);
    };

    const origGetImageData = CanvasRenderingContext2D.prototype.getImageData;
    CanvasRenderingContext2D.prototype.getImageData = function() {
        const data = origGetImageData.apply(this, arguments);
        try {
            for (let i = 0; i < data.data.length; i += 100) {
                data.data[i] = (data.data[i] + 1) % 255;
            }
        } catch (e) {}
        return data;
    };

    // ============================================================
    // 2. WebGL Fingerprint Spoofing
    // ============================================================
    const origGetParameter = WebGLRenderingContext.prototype.getParameter;
    WebGLRenderingContext.prototype.getParameter = function(param) {
        // UNMASKED_VENDOR_WEBGL
        if (param === 37445) return 'Qualcomm';
        // UNMASKED_RENDERER_WEBGL
        if (param === 37446) return 'Adreno (TM) 740';
        return origGetParameter.apply(this, arguments);
    };

    // WebGL2
    if (typeof WebGL2RenderingContext !== 'undefined') {
        const origGetParameter2 = WebGL2RenderingContext.prototype.getParameter;
        WebGL2RenderingContext.prototype.getParameter = function(param) {
            if (param === 37445) return 'Qualcomm';
            if (param === 37446) return 'Adreno (TM) 740';
            return origGetParameter2.apply(this, arguments);
        };
    }

    // ============================================================
    // 3. AudioContext Fingerprint Spoofing
    // ============================================================
    const origGetChannelData = AudioBuffer.prototype.getChannelData;
    AudioBuffer.prototype.getChannelData = function() {
        const data = origGetChannelData.apply(this, arguments);
        try {
            for (let i = 0; i < data.length; i += 100) {
                data[i] = data[i] + 0.0000001;
            }
        } catch (e) {}
        return data;
    };

    // ============================================================
    // 4. Hardware Fingerprint Spoofing
    // ============================================================
    Object.defineProperty(navigator, 'hardwareConcurrency', {
        get: () => 8
    });

    Object.defineProperty(navigator, 'deviceMemory', {
        get: () => 12
    });

    Object.defineProperty(navigator, 'platform', {
        get: () => 'Android'
    });

    // ============================================================
    // 5. WebDriver Hiding
    // ============================================================
    Object.defineProperty(navigator, 'webdriver', {
        get: () => undefined
    });

    // ============================================================
    // 6. Languages Spoofing
    // ============================================================
    Object.defineProperty(navigator, 'languages', {
        get: () => ['en-US', 'en']
    });

    Object.defineProperty(navigator, 'language', {
        get: () => 'en-US'
    });

    // ============================================================
    // 7. WebRTC Blocking
    // ============================================================
    try {
        delete window.RTCPeerConnection;
        delete window.webkitRTCPeerConnection;
        delete window.mozRTCPeerConnection;
    } catch (e) {}

    // ============================================================
    // 8. Chrome Object Spoofing
    // ============================================================
    if (!window.chrome) {
        window.chrome = {
            runtime: {},
            loadTimes: function() {},
            csi: function() {},
            app: {}
        };
    }

    // ============================================================
    // 9. Permissions Spoofing
    // ============================================================
    if (navigator.permissions) {
        const origQuery = navigator.permissions.query;
        navigator.permissions.query = function(parameters) {
            if (parameters.name === 'notifications') {
                return Promise.resolve({ state: 'denied' });
            }
            return origQuery.apply(this, arguments);
        };
    }

    // ============================================================
    // 10. Plugins Spoofing
    // ============================================================
    Object.defineProperty(navigator, 'plugins', {
        get: () => [1, 2, 3, 4, 5]
    });

    // ============================================================
    // 11. MimeTypes Spoofing
    // ============================================================
    Object.defineProperty(navigator, 'mimeTypes', {
        get: () => [1, 2, 3, 4]
    });

    // ============================================================
    // 12. Screen Spoofing
    // ============================================================
    Object.defineProperty(screen, 'colorDepth', {
        get: () => 32
    });

    Object.defineProperty(screen, 'pixelDepth', {
        get: () => 32
    });

    // ============================================================
    // 13. Timezone Spoofing (احتياطي)
    // ============================================================
    const origDateTimeFormat = Intl.DateTimeFormat;
    Intl.DateTimeFormat = function() {
        const args = Array.from(arguments);
        if (args.length > 0 && args[0] && typeof args[0] === 'object') {
            args[0].timeZone = 'America/New_York';
        } else {
            args.unshift({ timeZone: 'America/New_York' });
        }
        return new origDateTimeFormat(...args);
    };

    console.log('[0X BROWSER] Fingerprint spoofing active');
})();