package com.wzj.library.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 继承自BaseActivity,主要就是反射拿到具体的编译时类,然后设置内容视图, 同时将onRegister放了进来,这不是一个必须实现的方法
 * 后面如果我们需要使用意图出去,就可以在子类中直接重写父类的方法达到同样的效果
 * @param <VB>
 */
public abstract class BaseVBActivity<VB extends ViewBinding> extends BaseActivity {
    protected VB binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        onRegister();
        super.onCreate(savedInstanceState);
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            try {
                Class<VB> clazz = (Class<VB>) ((ParameterizedType) type).getActualTypeArguments()[0];
                //反射
                Method method = clazz.getMethod("inflate", LayoutInflater.class);
                binding = (VB) method.invoke(null, getLayoutInflater());
            } catch (Exception e) {
                e.printStackTrace();
            }
            setContentView(binding.getRoot());
        }
        initData();
    }

    protected abstract void initData();

    protected void onRegister() {

    }
}
