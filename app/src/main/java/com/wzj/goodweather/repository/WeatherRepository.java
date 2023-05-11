package com.wzj.goodweather.repository;


import android.annotation.SuppressLint;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.wzj.goodweather.ApiService;
import com.wzj.goodweather.Constant;
import com.wzj.goodweather.bean.DailyResponse;
import com.wzj.goodweather.bean.NowResponse;
import com.wzj.library.network.ApiType;
import com.wzj.library.network.NetworkApi;
import com.wzj.library.network.observer.BaseObserver;

@SuppressLint("CheckResult")
public class WeatherRepository {

    private static final String TAG = WeatherRepository.class.getSimpleName();

    //单例 防止重复创建WeatherRepository
    private static final class WeatherRepositoryHolder {
        private static final WeatherRepository mInstance = new WeatherRepository();
    }

    public static WeatherRepository getInstance() {
        return WeatherRepositoryHolder.mInstance;
    }

    /**
     * 实况天气
     *
     * @param responseLiveData 成功数据
     * @param failed           错误信息
     * @param cityId           城市ID
     */
    public void nowWeather(MutableLiveData<NowResponse> responseLiveData,
                           MutableLiveData<String> failed, String cityId) {
        String type = "实时天气-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).nowWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<NowResponse>() {
                    @Override
                    public void onSuccess(NowResponse nowResponse) {
                        if (nowResponse == null) {
                            failed.postValue("实况天气数据为null，请检查城市ID是否正确。");
                            return;
                        }
                        //请求接口成功返回数据，失败返回状态码
                        if (Constant.SUCCESS.equals(nowResponse.getCode())) {
                            responseLiveData.postValue(nowResponse);
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

    /**
     * 天气预报 7天
     */
    public void dailyWeather(MutableLiveData<DailyResponse> responseMutableLiveData, MutableLiveData<String> failed, String cityId) {
        String type = "天气预报-->";
        NetworkApi.createService(ApiService.class, ApiType.WEATHER).dailyWeather(cityId)
                .compose(NetworkApi.applySchedulers(new BaseObserver<DailyResponse>() {
                    @Override
                    public void onSuccess(DailyResponse dailyResponse) {
                        if (dailyResponse == null) {
                            failed.postValue("天气预报数据为null,请检查城市ID是否正确");
                            return;
                        }
                        //请求接口成功返回数据,失败返回状态码
                        if (Constant.SUCCESS.equals(dailyResponse.getCode())) {
                            responseMutableLiveData.postValue(dailyResponse);
                        } else {
                            failed.postValue(type + dailyResponse.getCode());
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