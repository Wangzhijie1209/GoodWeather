package com.wzj.library.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {
    //保存所有创建的Activity
    private final List<Activity> allArraylists = new ArrayList<>();

    /**
     * 添加activity到管理器
     */
    public void addActivity(Activity activity) {
        if (activity != null) {
            allArraylists.add(activity);
        }
    }

    /**
     * 从管理器中移除Activity
     */
    public void removeActivity(Activity activity) {
        if (activity != null) {
            allArraylists.remove(activity);
        }
    }

    /**
     * 关闭所有Activity
     */
    public void finishAll() {
        for (Activity activity : allArraylists) {
            activity.finish();
        }
    }

    public Activity getTaskTop() {
        return allArraylists.get(allArraylists.size() - 1);
    }
}
