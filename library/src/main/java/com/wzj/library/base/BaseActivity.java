package com.wzj.library.base;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected AppCompatActivity mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        BaseApplication.getActivityManager().addActivity(this);
    }

    protected void showMsg(CharSequence msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    protected void showLongMsg(CharSequence msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 跳转页面
     */
    protected void jumpActivity(final Class<?> clazz) {
        startActivity(new Intent(mContext, clazz));
    }

    /**
     * 跳转并关闭当前页面
     */
    protected void jumpActivityFinish(final Class<?> clazz) {
        startActivity(new Intent(mContext, clazz));
        finish();
    }

    protected void back(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void backAndFinish(Toolbar toolbar) {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * 检查是否拥有某项权限名称
     *
     * @param permission
     * @return true有 false没有
     */
    protected boolean hasPermission(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 退出应用程序
     */
    protected void exitTheProgram() {
        BaseApplication.getActivityManager().finishAll();
    }

    protected void setFullScreenImmersion() {
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        window.getDecorView().setSystemUiVisibility(option);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }


}
