package com.wzj.goodweather.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.wzj.goodweather.ApiService;
import com.wzj.goodweather.Constant;
import com.wzj.goodweather.bean.SearchCityResponse;
import com.wzj.library.network.ApiType;
import com.wzj.library.network.NetworkApi;
import com.wzj.library.network.observer.BaseObserver;

/**
 * OkHttp做网络请求,Retrofit做接口封装和解析,RxJava做线程切换调度,拿到数据之后我们通过LiveData进行发送
 */
@SuppressLint("CheckResult")
public class SearchCityRepository {
    private static final String TAG = SearchCityRepository.class.getSimpleName();

    public void searchCity(MutableLiveData<SearchCityResponse> responseMutableLiveData,
                           MutableLiveData<String> failed, String cityName, boolean isExact) {
        NetworkApi.createService(ApiService.class, ApiType.SEARCH).searchCity(cityName, isExact ? Constant.EXACT : Constant.FUZZY)
                .compose(NetworkApi.applySchedulers(new BaseObserver<SearchCityResponse>() {
                    @Override
                    public void onSuccess(SearchCityResponse searchCityResponse) {
                        if (searchCityResponse == null) {
                            failed.postValue("搜索城市数据为null,请检查城市名称是否正确");
                            return;
                        }
                        //请求接口成功返回数据,失败返回状态码
                        if (Constant.SUCCESS.equals(searchCityResponse
                                .getCode())) {
                            responseMutableLiveData.postValue(searchCityResponse);
                        } else {
                            failed.postValue(searchCityResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.d(TAG, "onFailure: "+e.getMessage());
                        failed.postValue(e.getMessage());
                    }
                }));
    }
}
