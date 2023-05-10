package com.wzj.goodweather.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.wzj.goodweather.bean.NowResponse;
import com.wzj.goodweather.bean.SearchCityResponse;
import com.wzj.goodweather.repository.SearchCityRepository;
import com.wzj.goodweather.repository.WeatherRepository;
import com.wzj.library.base.BaseViewModel;

public class MainViewModel extends BaseViewModel {
    public MutableLiveData<SearchCityResponse> searchCityResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<NowResponse> nowResponseMutableLiveData = new MutableLiveData<>();

    /**
     * 搜索成功
     */
    public void searchCity(String cityName, boolean isExact) {
        new SearchCityRepository().searchCity(searchCityResponseMutableLiveData, failed, cityName);
    }
    public void nowWeather(String cityId){
        new WeatherRepository().nowWeather(nowResponseMutableLiveData,failed,cityId);
    }
}
