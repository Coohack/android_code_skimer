/**
 * Created by hack on 7/3/15.
 * Updated by hack on 7/3/15.
 * <p/>
 * Package com.hush.ocean.activity
 */
package com.hush.ocean.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.hush.ocean.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private final long rePressBackExitTimeOut = 2000;
    private long previousPressBackTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        // set click listener on main button
        this.findViewById(R.id.mainMenuButton).setOnClickListener(new MainButtonClickListener());

        this.mainWebView = (WebView) this.findViewById(R.id.mainWebView);
        this.mainWebView.getSettings().setJavaScriptEnabled(true);
        this.mainWebView.setWebChromeClient(new WebChromeClient());
        this.mainWebView.addJavascriptInterface(new JavaScriptInterface(), "getFile");
        this.mainWebView.loadUrl("file:///android_asset/template.html");
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - previousPressBackTime < rePressBackExitTimeOut) {
            this.finish();
            System.exit(0);
        }
        previousPressBackTime = System.currentTimeMillis();
        Toast.makeText(getApplicationContext(), "press again exit", Toast.LENGTH_SHORT).show();
    }

    private WebView mainWebView = null;
    private String fileString = "hello";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != 0 || data == null)
            return;
        String path = data.getStringExtra("path");
        File file = new File(path);
        if (!file.exists())
            return;
        try {
            InputStream in = new FileInputStream(file);
            StringBuilder out = new StringBuilder();
            byte[] b = new byte[4096];
            int n;
            while ((n = in.read(b)) != -1) {
                out.append(new String(b, 0, n));
            }
            fileString = out.toString();
        } catch (IOException e) {
            return;
        }
        mainWebView.loadUrl("javascript:refresh()");
    }

    class JavaScriptInterface {
        @JavascriptInterface
        public String getFileString() {
            return fileString;
        }
    }

    class MainButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getApplicationContext(), SelectFileActivity.class);
            startActivityForResult(intent, 0);
        }
    }
}
