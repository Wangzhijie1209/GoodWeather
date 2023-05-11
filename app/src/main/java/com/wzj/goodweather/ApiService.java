package com.wzj.goodweather;

import static com.wzj.goodweather.Constant.API_KEY;

import com.wzj.goodweather.bean.DailyResponse;
import com.wzj.goodweather.bean.LifestyleResponse;
import com.wzj.goodweather.bean.NowResponse;
import com.wzj.goodweather.bean.SearchCityResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    /**
     * 搜索城市 模糊搜索 国内范围 返回10条数据
     *
     * @param location 城市名
     * @param mode     exact 精准搜索  fuzzy模糊搜索
     * @return NewSearchCityResponse 搜索城市数据返回
     */
    @GET("/v2/city/lookup?key=" + API_KEY + "&range=cn")
    Observable<SearchCityResponse> searchCity(@Query("location") String location);


    @GET("/v7/weather/now?key=" + API_KEY)
    Observable<NowResponse> nowWeather(@Query("location") String location);

    @GET("/v7/weather/7d?key=" + API_KEY)
    Observable<DailyResponse> dailyWeather(@Query("location") String location);

    //生活指数API
    @GET("/v7/indices/1d?key="+API_KEY)
    Observable<LifestyleResponse> lifestyle(@Query("type")String type,@Query("location")String location);

}
