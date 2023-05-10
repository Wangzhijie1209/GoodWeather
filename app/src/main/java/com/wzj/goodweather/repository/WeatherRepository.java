package com.wzj.goodweather.repository;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.wzj.goodweather.ApiService;
import com.wzj.goodweather.Constant;
import com.wzj.goodweather.bean.NowResponse;
import com.wzj.library.network.ApiType;
import com.wzj.library.network.NetworkApi;
import com.wzj.library.network.observer.BaseObserver;

@SuppressLint("CheckResult")
public class WeatherRepository {
    private static final String TAG = WeatherRepository.class.getSimpleName();

    public void nowWeather(MutableLiveData<NowResponse> responseMutableLiveData, MutableLiveData<String> failed, String cityId) {
        String type = "实时天气-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).nowWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<NowResponse>() {
                    @Override
                    public void onSuccess(NowResponse nowResponse) {
                        if (nowResponse == null) {
                            failed.postValue("实况天气数据为null,请检查城市ID是否正确.");
                            return;
                        }
                        //请求接口成功返回数据,失败返回状态码
                        if (Constant.SUCCESS.equals(nowResponse.getCode())) {
                            responseMutableLiveData.postValue(nowResponse);
                        } else {
                            failed.postValue(type + nowResponse.getCode());
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        Log.e(TAG, "onFailure: " + e.getMessage());
                        failed.postValue(type + e.getMessage());
                    }
                }));
    }
}
