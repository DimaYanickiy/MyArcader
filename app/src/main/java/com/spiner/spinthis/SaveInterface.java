package com.spiner.spinthis;

import android.content.Context;
import android.content.SharedPreferences;

public interface SaveInterface {
    boolean firstRun(SharedPreferences sp);
    void setFirstRun(boolean firstRun, SharedPreferences sp);
    boolean firstFl(SharedPreferences sp);
    void setFirstFl(boolean firstFl, SharedPreferences sp);
    boolean firstParam(SharedPreferences sp);
    void setFirstParam(boolean firstParam, SharedPreferences sp);
    String getPoint(SharedPreferences sp);
    void setPoint(String point, SharedPreferences sp);
    boolean isPhonePluggedIn(Context context);
}
