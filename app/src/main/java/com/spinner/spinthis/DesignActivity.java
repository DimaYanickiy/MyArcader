package com.spinner.spinthis;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;

public class DesignActivity extends AppCompatActivity implements SaveInterface{

        private ValueCallback<Uri[]> callback;
        private String photoPath;

        private WebView webView;
        private ProgressBar progressBar;
        private SharedPreferences sharedPreferences;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            webView = new WebView(this);
            progressBar = new ProgressBar(this);
            sharedPreferences = getSharedPreferences("PREF", MODE_PRIVATE);

            String pointReference = getPoint(sharedPreferences);

            RelativeLayout layout = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            layout.setLayoutParams(layoutParams);
            webView.setLayoutParams(layoutParams);

            setSettings();

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("mailto:")) {
                        Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                        startActivity(i);
                        return true;
                    } else if (url.startsWith("tg:") || url.startsWith("https://t.me") || url.startsWith("https://telegram.me")) {
                        try {
                            WebView.HitTestResult result = view.getHitTestResult();
                            String data = result.getExtra();
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
                            view.getContext().startActivity(intent);
                        } catch (Exception ex) {
                        }
                        return true;
                    } else {
                        return false;
                    }
                }

                @TargetApi(Build.VERSION_CODES.N)
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    return overrideUrl(view, request.getUrl().toString());
                }

                private boolean overrideUrl(WebView view, String toString) {
                    return overrideUrl(view, toString);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (firstParam(sharedPreferences)) {
                        setPoint(url, sharedPreferences);
                        setFirstParam(false, sharedPreferences);
                        CookieManager.getInstance().flush();
                    }
                    CookieManager.getInstance().flush();
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                }
            });

            webView.setWebChromeClient(new WebChromeClient() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                public void checkPermission() {
                    ActivityCompat.requestPermissions(
                            DesignActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA},
                            1);
                }

                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                                 FileChooserParams fileChooserParams) {
                    int permissionStatus = ContextCompat.checkSelfPermission(DesignActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        if (callback != null) {
                            callback.onReceiveValue(null);
                        }
                        callback = filePathCallback;
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                                takePictureIntent.putExtra("PhotoPath", photoPath);
                            } catch (IOException ex) {
                            }
                            if (photoFile != null) {
                                photoPath = "file:" + photoFile.getAbsolutePath();
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                        Uri.fromFile(photoFile));
                            } else {
                                takePictureIntent = null;
                            }
                        }
                        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        contentSelectionIntent.setType("image/*");
                        Intent[] intentArray;
                        if (takePictureIntent != null) {
                            intentArray = new Intent[]{takePictureIntent};
                        } else {
                            intentArray = new Intent[0];
                        }
                        Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                        chooser.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
                        chooser.putExtra(Intent.EXTRA_TITLE, "Photo");
                        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                        startActivityForResult(chooser, 1);
                        return true;
                    } else
                        checkPermission();
                    return false;
                }

                private File createImageFile() throws IOException {
                    File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere");
                    if (!imageStorageDir.exists())
                        imageStorageDir.mkdirs();
                    imageStorageDir = new File(imageStorageDir + File.separator + "Photo_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
                    return imageStorageDir;
                }


                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    progressBar.setActivated(true);
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                        progressBar.setActivated(false);
                    }
                }
            });

            layout.addView(webView, layoutParams);
            layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            progressBar.setFitsSystemWindows(true);
            layout.addView(progressBar, layoutParams);

            setContentView(layout);

            webView.loadUrl(pointReference);

        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode != 1 || callback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            if (resultCode == Activity.RESULT_OK) {
                if (data == null || data.getData() == null) {
                    if (photoPath != null) {
                        results = new Uri[]{Uri.parse(photoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            callback.onReceiveValue(results);
            callback = null;
        }


        @Override
        protected void onPause() {
            super.onPause();
            CookieManager.getInstance().flush();
        }

        @Override
        protected void onResume() {
            super.onResume();
            CookieManager.getInstance().flush();
        }

    @Override
    public void onBackPressed() {
        if(!webView.canGoBack()){
            finish();
        }else{
            webView.goBack();
        }
    }

    private void setSettings() {
            webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            webView.requestFocus(View.FOCUS_DOWN);
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            webView.getSettings().setUserAgentString(webView.getSettings().getUserAgentString());
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setAppCacheEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setDatabaseEnabled(true);
            webView.getSettings().setSupportZoom(false);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.getSettings().setAllowContentAccess(true);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.acceptCookie();
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            cookieManager.flush();
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setSavePassword(true);
        }

    @Override
    public boolean firstRun(SharedPreferences sp) {
        return sp.getBoolean("p1", true);
    }

    @Override
    public void setFirstRun(boolean firstRun, SharedPreferences sp) {
        sp.edit().putBoolean("p1", firstRun).apply();
    }

    @Override
    public boolean firstFl(SharedPreferences sp) {
        return sp.getBoolean("p2", true);
    }

    @Override
    public void setFirstFl(boolean firstFl, SharedPreferences sp) {
        sp.edit().putBoolean("p2", firstFl).apply();
    }

    @Override
    public boolean firstParam(SharedPreferences sp) {
        return sp.getBoolean("p3", true);
    }

    @Override
    public void setFirstParam(boolean firstParam, SharedPreferences sp) {
        sp.edit().putBoolean("p3", firstParam).apply();
    }

    @Override
    public String getPoint(SharedPreferences sp) {
        return sp.getString("p4", "");
    }

    @Override
    public void setPoint(String point, SharedPreferences sp) {
        sp.edit().putString("p4", point).apply();
    }

    @Override
    public boolean isPhonePluggedIn(Context context) {
        return false;
    }
}

