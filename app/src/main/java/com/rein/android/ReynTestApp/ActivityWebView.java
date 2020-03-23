package com.rein.android.ReynTestApp;

import android.annotation.SuppressLint;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;


public class ActivityWebView extends AppCompatActivity {


        WebView myWebView;
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_webview);

            myWebView = findViewById(R.id.MwebView);
            WebSettings settings = myWebView.getSettings();
            settings.setJavaScriptEnabled(true);
            settings.setDomStorageEnabled(true);
            myWebView.setWebViewClient(new SSLTolerentWebViewClient());
            myWebView.loadUrl("http://developer.alexanderklimov.ru/android/");

        }

    private class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
            super.onReceivedSslError(view, handler, error);

        }
    }



}
