package com.rein.android.ReynTestApp;

import android.annotation.SuppressLint;

import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.ClientCertRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


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

            myWebView.loadUrl("https://ya.ru");

        }

    private static class SSLTolerentWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            //super.onReceivedSslError(view, handler, error);
            handler.proceed(); // Ignore SSL certificate errors
        }

    };



    }