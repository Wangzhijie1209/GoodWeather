package com.wzj.library.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * 继承自ViewModel,然后写一个可变数据的LiveData,方便继承者直接使用这个failed
 */
public class BaseViewModel extends ViewModel {
    public MutableLiveData<String> failed = new MutableLiveData<>();
}
