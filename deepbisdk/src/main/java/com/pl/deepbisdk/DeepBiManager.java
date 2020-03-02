package com.pl.deepbisdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.pl.deepbisdk.localdata.BusEvent;
import com.pl.deepbisdk.network.ApiClient;
import com.pl.deepbisdk.utilities.RandomString;
import com.pl.deepbisdk.utilities.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class DeepBiManager {
    private static final String LOG_TAG = "MonitorService";

    public static String DEVICE_ID = "";
    public static String INGESTION_KEY;
    public static String SESSION_ID;
    public static double SCREEN_SIZE_INCH = 0;
    static Context mAppContext;
    static SharedPreferences mPerference;
    static Activity currentActivity;

    public static void init(Context context, String dataSetKey, String ingestionKey) {
        Log.d("TEST SDK", "Init DeepBi sdk here + dataSetKey=" + dataSetKey + ";ingestionKey=" + ingestionKey);
        mAppContext = context;
        mPerference = context.getSharedPreferences("DeepBiSdk_Preference", Context.MODE_PRIVATE);
        DEVICE_ID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        INGESTION_KEY = ingestionKey;

        ((Application) DeepBiManager.getAppContext()).registerActivityLifecycleCallbacks(lifecycleCallbacks);
        RandomString randomString = new RandomString();
        SESSION_ID = randomString.nextString();
        ApiClient.setBaseUrl(dataSetKey);
    }

    public static void startCollecting(Activity acitivity) {
        MonitorService.startService(acitivity);
    }

    public static void stopCollecting() {
        MonitorService.stopService();
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public static SharedPreferences getPerference() {
        return mPerference;
    }

    public static boolean isTablet() {
        return SCREEN_SIZE_INCH >= 7;
    }

    public static boolean isSmallScreen() {
        return SCREEN_SIZE_INCH < 2.5;
    }

    public static void sendCustomEvent(String eventName, String data) {
        BusEvent be = new BusEvent(MonitorService.ACTION_CUSTOM_EVENT);
        be.putData(MonitorService.PARAM_EVENT_NAME, eventName);
        be.putData(MonitorService.PARAM_EVENT_DATA, data);
        EventBus.getDefault().post(be);
    }

    public static void unregisterLifeCycleCallBack() {
        ((Application) DeepBiManager.getAppContext()).unregisterActivityLifecycleCallbacks(lifecycleCallbacks);
        mPerference.edit().putString("PageOpened1stTime", "").commit();
    }
    static Application.ActivityLifecycleCallbacks lifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            // Call page ping
            Log.d(LOG_TAG, "MonitorService 1 onActivityCreated");
            SCREEN_SIZE_INCH = Utility.getScreenSizeInches(activity);
            currentActivity = activity;

            BusEvent be = new BusEvent(MonitorService.ACTION_ACTIVITY_ONCREATED);
            be.putData(MonitorService.PARAM_ACTIVITY_NAME, activity.getClass().getName());
            EventBus.getDefault().post(be);
        }

        @Override
        public void onActivityStarted(Activity activity) {
            if (!Utility.isMyServiceRunning(activity, MonitorService.class)) {
                mPerference.edit().putString("PageOpened1stTime", activity.getClass().getName()).commit();
                startCollecting(activity);
                return;
            }

            BusEvent be = new BusEvent(MonitorService.ACTION_ACTIVITY_ONSTART);
            be.putData(MonitorService.PARAM_ACTIVITY_NAME, activity.getClass().getName());
            EventBus.getDefault().post(be);
        }

        @Override
        public void onActivityResumed(Activity activity) { }

        @Override
        public void onActivityPaused(Activity activity) { }

        @Override
        public void onActivityStopped(Activity activity) {
            BusEvent be = new BusEvent(MonitorService.ACTION_ACTIVITY_ONSTOP);
            be.putData(MonitorService.PARAM_ACTIVITY_NAME, activity.getClass().getName());
            EventBus.getDefault().post(be);
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }

        @Override
        public void onActivityDestroyed(Activity activity) {
            BusEvent be = new BusEvent(MonitorService.ACTION_ACTIVITY_ONDESTROYED);
            be.putData(MonitorService.PARAM_ACTIVITY_NAME, activity.getClass().getName());
            EventBus.getDefault().post(be);
        }
    };
}
