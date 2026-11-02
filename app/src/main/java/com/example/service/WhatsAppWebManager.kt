package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed interface WhatsAppWebAuthStatus {
  data object Initializing : WhatsAppWebAuthStatus
  data class Loading(val progress: Int) : WhatsAppWebAuthStatus
  data object ReadyToScan : WhatsAppWebAuthStatus
  data class Authenticated(val userName: String = "WhatsApp Web Companion") : WhatsAppWebAuthStatus
  data class Error(val message: String) : WhatsAppWebAuthStatus
}

/**
 * Singleton manager to control embedded WhatsApp Web (`https://web.whatsapp.com`).
 * Uses Chrome Desktop User-Agent so WhatsApp serves the genuine pairing interface (both QR code and 8-character phone pairing code).
 */
@SuppressLint("StaticFieldLeak")
object WhatsAppWebManager {

  private const val TAG = "WhatsAppWebManager"
  const val WA_WEB_URL = "https://web.whatsapp.com"

  const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36"

  private var activeWebView: WebView? = null
  private val mainHandler = Handler(Looper.getMainLooper())

  private val _authStatus = MutableStateFlow<WhatsAppWebAuthStatus>(WhatsAppWebAuthStatus.Initializing)
  val authStatus: StateFlow<WhatsAppWebAuthStatus> = _authStatus.asStateFlow()

  private val _detectedPairingCode = MutableStateFlow<String>("")
  val detectedPairingCode: StateFlow<String> = _detectedPairingCode.asStateFlow()

  private var onAuthSuccessListener: (() -> Unit)? = null

  fun setOnAuthSuccessListener(listener: () -> Unit) {
    onAuthSuccessListener = listener
  }

  @SuppressLint("SetJavaScriptEnabled")
  fun getOrCreateWebView(context: Context): WebView {
    activeWebView?.let { view ->
      (view.parent as? ViewGroup)?.removeView(view)
      return view
    }

    val webView = WebView(context.applicationContext).apply {
      settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        userAgentString = DESKTOP_USER_AGENT
        useWideViewPort = true
        loadWithOverviewMode = true
        cacheMode = WebSettings.LOAD_DEFAULT
        mediaPlaybackRequiresUserGesture = false
        allowFileAccess = true
        allowContentAccess = true
        displayZoomControls = false
        builtInZoomControls = false
      }

      val cookieManager = CookieManager.getInstance()
      cookieManager.setAcceptCookie(true)
      cookieManager.setAcceptThirdPartyCookies(this, true)

      addJavascriptInterface(WhatsAppWebBridge(), "StatusHDNative")

      webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
          return false
        }

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
          super.onPageStarted(view, url, favicon)
          _authStatus.value = WhatsAppWebAuthStatus.Loading(10)
        }

        override fun onPageFinished(view: WebView?, url: String?) {
          super.onPageFinished(view, url)
          _authStatus.value = WhatsAppWebAuthStatus.ReadyToScan
          injectDarkThemeAndBridge(view)
          startSessionObserver(view)
        }
      }

      webChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
          if (newProgress < 100 && _authStatus.value !is WhatsAppWebAuthStatus.Authenticated) {
            _authStatus.value = WhatsAppWebAuthStatus.Loading(newProgress)
          }
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
          consoleMessage?.message()?.let { msg ->
            if (msg.contains("StatusHDNative")) {
              Log.d(TAG, "WA Web Console: $msg")
            }
          }
          return super.onConsoleMessage(consoleMessage)
        }
      }

      loadUrl(WA_WEB_URL)
    }

    activeWebView = webView
    return webView
  }

  fun reload() {
    mainHandler.post {
      _authStatus.value = WhatsAppWebAuthStatus.Initializing
      _detectedPairingCode.value = ""
      activeWebView?.reload()
    }
  }

  /**
   * Automates clicking "Link with phone number", filling in the phone number using
   * React-compatible input property descriptors, and pressing next.
   */
  fun triggerPairWithPhone(context: Context, phoneNumber: String) {
    val webView = getOrCreateWebView(context)
    mainHandler.post {
      val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
      val js = """
        (function() {
          try {
            // Find link with phone number button or enter phone
            const links = Array.from(document.querySelectorAll('span, button, [role="button"], a'));
            const linkWithPhoneBtn = links.find(el => {
              const text = (el.innerText || el.textContent || '').toLowerCase();
              return text.includes('tautkan dengan nomor') ||
                     text.includes('link with phone') ||
                     text.includes('tautkan dengan nomor telepon');
            });
            if (linkWithPhoneBtn) {
              linkWithPhoneBtn.click();
            }

            let checkCount = 0;
            const timer = setInterval(function() {
              checkCount++;
              const phoneInput = document.querySelector('input[type="tel"]') ||
                                 document.querySelector('input[type="text"]');
              if (phoneInput && '$cleanPhone'.length >= 8) {
                clearInterval(timer);
                phoneInput.focus();

                // React-safe value setter
                const nativeInputValueSetter = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;
                if (nativeInputValueSetter) {
                  nativeInputValueSetter.call(phoneInput, '$cleanPhone');
                } else {
                  phoneInput.value = '$cleanPhone';
                }
                phoneInput.dispatchEvent(new Event('input', { bubbles: true }));
                phoneInput.dispatchEvent(new Event('change', { bubbles: true }));

                setTimeout(function() {
                  const nextBtns = Array.from(document.querySelectorAll('button, [role="button"]'));
                  const nextBtn = nextBtns.find(b => {
                    const t = (b.innerText || b.textContent || '').toLowerCase();
                    return t.includes('lanjut') || t.includes('next') || t.includes('berikutnya');
                  });
                  if (nextBtn) {
                    nextBtn.click();
                  }
                }, 500);
              }
              if (checkCount > 20) {
                clearInterval(timer);
              }
            }, 300);
          } catch(e) {
            console.error('StatusHDNative error: ' + e);
          }
        })();
      """.trimIndent()
      webView.evaluateJavascript(js, null)
    }
  }

  private fun injectDarkThemeAndBridge(view: WebView?) {
    val js = """
      (function() {
        try {
          const style = document.createElement('style');
          style.id = 'status-hd-theme-style';
          style.innerHTML = `
            html, body {
              background-color: #06090E !important;
              width: 100% !important;
              height: 100% !important;
              margin: 0 !important;
              padding: 0 !important;
            }
            #app {
              background: transparent !important;
            }
            /* High contrast QR code area and centering */
            canvas {
              border: 4px solid #10E599 !important;
              border-radius: 16px !important;
              box-shadow: 0 0 20px rgba(16, 229, 153, 0.3) !important;
            }
          `;
          document.head.appendChild(style);
        } catch(e) {}
      })();
    """.trimIndent()
    view?.evaluateJavascript(js, null)
  }

  private fun startSessionObserver(view: WebView?) {
    val observerJs = """
      (function() {
        if (window.__statusHdObserverStarted) return;
        window.__statusHdObserverStarted = true;

        setInterval(function() {
          try {
            // 1. Check if user is authenticated (main chat pane exists)
            const chatList = document.querySelector('#pane-side') ||
                             document.querySelector('[data-testid="chat-list"]') ||
                             document.querySelector('[aria-label="Chat list"]') ||
                             document.querySelector('[aria-label="Daftar chat"]');

            if (chatList) {
              if (window.StatusHDNative && window.StatusHDNative.onAuthenticated) {
                window.StatusHDNative.onAuthenticated('WhatsApp Web');
              }
              return;
            }

            // 2. Check for single container pairing code (e.g. "ABCD-1234" or "ABCD1234")
            const codeElements = Array.from(document.querySelectorAll('span, div, [data-testid]'))
              .filter(el => {
                const text = (el.innerText || el.textContent || '').trim();
                return /^[A-Z0-9]{4}-[A-Z0-9]{4}$/.test(text) || /^[A-Z0-9]{8}$/.test(text);
              });

            if (codeElements.length > 0) {
              const codeText = (codeElements[0].innerText || codeElements[0].textContent).trim();
              if (window.StatusHDNative && window.StatusHDNative.onPairingCodeDetected) {
                window.StatusHDNative.onPairingCodeDetected(codeText);
              }
              return;
            }

            // 3. Check for WhatsApp Web split character boxes (8 separate span/div elements)
            const singleChars = Array.from(document.querySelectorAll('span, div'))
              .filter(el => {
                const text = (el.innerText || el.textContent || '').trim();
                return text.length === 1 && /^[A-Z0-9]$/.test(text);
              });

            if (singleChars.length >= 8) {
              // Extract contiguous 8 alphanumeric characters
              for (let i = 0; i <= singleChars.length - 8; i++) {
                const candidate = singleChars.slice(i, i + 8).map(el => (el.innerText || el.textContent).trim()).join('');
                if (/^[A-Z0-9]{8}$/.test(candidate)) {
                  const formatted = candidate.substring(0, 4) + '-' + candidate.substring(4, 8);
                  if (window.StatusHDNative && window.StatusHDNative.onPairingCodeDetected) {
                    window.StatusHDNative.onPairingCodeDetected(formatted);
                  }
                  break;
                }
              }
            }
          } catch(e) {}
        }, 800);
      })();
    """.trimIndent()
    view?.evaluateJavascript(observerJs, null)
  }

  fun clearSession() {
    mainHandler.post {
      _authStatus.value = WhatsAppWebAuthStatus.Initializing
      _detectedPairingCode.value = ""
      activeWebView?.clearCache(true)
      activeWebView?.clearFormData()
      CookieManager.getInstance().removeAllCookies(null)
      CookieManager.getInstance().flush()
      activeWebView?.loadUrl(WA_WEB_URL)
    }
  }

  class WhatsAppWebBridge {
    @JavascriptInterface
    fun onAuthenticated(label: String) {
      mainHandler.post {
        _authStatus.value = WhatsAppWebAuthStatus.Authenticated(label)
        onAuthSuccessListener?.invoke()
      }
    }

    @JavascriptInterface
    fun onPairingCodeDetected(code: String) {
      mainHandler.post {
        _detectedPairingCode.value = code
      }
    }
  }
}
