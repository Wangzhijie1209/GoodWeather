package com.wzj.goodweather.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.wzj.goodweather.bean.DailyResponse;
import com.wzj.goodweather.bean.NowResponse;
import com.wzj.goodweather.bean.SearchCityResponse;
import com.wzj.goodweather.repository.SearchCityRepository;
import com.wzj.goodweather.repository.WeatherRepository;
import com.wzj.library.base.BaseViewModel;

public class MainViewModel extends BaseViewModel {
    public MutableLiveData<SearchCityResponse> searchCityResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<NowResponse> nowResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<DailyResponse> dailyResponseMutableLiveData = new MutableLiveData<>();

    /**
     * 搜索成功
     */
    public void searchCity(String cityName, boolean isExact) {
        new SearchCityRepository().searchCity(searchCityResponseMutableLiveData, failed, cityName);
    }

    /**
     * 实况天气
     * @param cityId
     */
    public void nowWeather(String cityId){
        WeatherRepository.getInstance().nowWeather(nowResponseMutableLiveData,failed,cityId);
    }

    /**
     * 天气预报
     */
    public void dailyWeather(String cityId){
        WeatherRepository.getInstance().dailyWeather(dailyResponseMutableLiveData,failed,cityId);
    }

}
