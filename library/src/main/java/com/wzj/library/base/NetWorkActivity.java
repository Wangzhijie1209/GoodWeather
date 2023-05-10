package com.wzj.library.base;

import androidx.viewbinding.ViewBinding;

/**
 * 网络请求类,继承自BaseVBActivity,里面有两个抽象方法,
 * onObserveData()就是在使用LiveData的时候有一个观察数据返回的地方
 * @param <VB>
 */
public abstract class NetWorkActivity<VB extends ViewBinding> extends BaseVBActivity<VB> {
    @Override
    protected void initData() {
        onCreate();
        onObserveData();
    }



    protected abstract void onCreate();
    protected abstract void onObserveData();
}
