package com.wzj.goodweather;

import android.app.Application;

import com.wzj.library.network.INetworkRequiredInfo;

public class NetworkRequiredInfo implements INetworkRequiredInfo {
    private final Application application;

    public NetworkRequiredInfo(Application application) {
        this.application = application;
    }

    /**
     * 版本名
     *
     * @return
     */
    @Override
    public String getAppVersionName() {
        return BuildConfig.VERSION_NAME;
    }

    /**
     * 版本号
     *
     * @return
     */
    @Override
    public String getAppVersionCode() {
        return String.valueOf(BuildConfig.VERSION_CODE);
    }

    /**
     * 是否为debug
     *
     * @return
     */
    @Override
    public boolean isDebug() {
        return BuildConfig.DEBUG;
    }

    /**
     * 应用全局上下文
     *
     * @return
     */
    @Override
    public Application getApplicationContext() {
        return application;
    }
}
