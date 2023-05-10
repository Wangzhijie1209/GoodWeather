package com.wzj.library.network;

import com.wzj.library.network.errorhandler.ExceptionHandle;
import com.wzj.library.network.errorhandler.HttpErrorHandler;
import com.wzj.library.network.interceptor.RequestInterceptor;
import com.wzj.library.network.interceptor.ResponseInterceptor;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkApi {
    //获取App运行状态及版本信息,用于日志打印
    private static INetworkRequiredInfo iNetworkRequiredInfo;
    //OkHttp客户端
    private static OkHttpClient okHttpClient;
    //retrofitHashMap
    private static final HashMap<String, Retrofit> retrofitHashMap = new HashMap<>();
    //API访问地址
    private static String mBaseUrl;

    /**
     * 初始化
     */
    public static void init(INetworkRequiredInfo networkRequiredInfo) {
        iNetworkRequiredInfo = networkRequiredInfo;
    }

    /**
     * 创建serviceClass的实例
     */
    public static <T> T createService(Class<T> serviceClass, ApiType apiType) {
        getBaseUrl(apiType);
        return getRetrofit(serviceClass).create(serviceClass);
    }

    /**
     * 修改访问地址  根据判断接口传进来的Api类型,从而设置不同的地址头,因为每一个接口都需要有成功和失败的请求调用
     */
    private static void getBaseUrl(ApiType apiType) {
        switch (apiType) {
            case SEARCH:
                mBaseUrl = "https://geoapi.qweather.com";//和风天气 搜索城市
                break;
            case WEATHER:
                mBaseUrl = "dhttps://evapi.qweather.com";//和风 实时天气API
                break;
            default:
                break;
        }
    }

    /**
     * 配置Okhttp
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        //不为空说明已经配置过了,直接返回即可
        if (okHttpClient == null) {
            //Okhttp构建器
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //设置缓存大小
            int cacheSize = 100 * 1024 * 1024;
            //设置OkHttp网络缓存
            builder.cache(new Cache(iNetworkRequiredInfo.getApplicationContext().getCacheDir(), cacheSize));
            //设置网络请求超时时常,这里设置为10s
            builder.connectTimeout(10, TimeUnit.SECONDS);
            builder.readTimeout(20, TimeUnit.SECONDS).build();
            //添加请求拦截器,如果接口有请求头的话,可以放在这个拦截器中
            builder.addInterceptor(new RequestInterceptor(iNetworkRequiredInfo));
            //添加返回拦截器,可用于查看接口的请求耗时,对于网络优化有帮助
            builder.addInterceptor(new ResponseInterceptor());
            //当程序在debug过程中则打印数据日志,方便调试用
            if (iNetworkRequiredInfo != null && iNetworkRequiredInfo.isDebug()) {
                //iNetworkRequiredInfo不为空且处于debug状态下则初始化日志拦截器
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                //设置要打印日志的内容等级,BODY为主要内容,还有BASIC,HEADERS,NONE
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                //将拦截器添加到OkHttp构建器中
                builder.addInterceptor(httpLoggingInterceptor);
            }
            //okhttp配置完成
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 配置Retrofit
     *
     * @param serviceClass 服务类
     * @return Retrofit
     */
    private static Retrofit getRetrofit(Class serviceClass) {
        if (retrofitHashMap.get(mBaseUrl + serviceClass.getName()) != null) {
            //刚才上面定义的Map中键是String,值是Retrofit,当键不为空时,必然有值,有值则直接返回
            return retrofitHashMap.get(mBaseUrl + serviceClass.getName());
        }
        //初始化Retrofit Retrofit是对OkHttp的封装,同行是对网络请求做处理,也可以返回处理数据
        //Retrofit构建器
        Retrofit.Builder builder = new Retrofit.Builder();
        //设置访问地址
        builder.baseUrl(mBaseUrl);
        //设置OkHttp客户端,传入上面写好的方法即可获得配置后的OkHttp客户端
        builder.client(getOkHttpClient());
        //设置数据解析器, 会自动把请求返回的结果(json)字符串 通过Gson转化工厂自动转换成与其结构相符的实体Bean
        builder.addConverterFactory(GsonConverterFactory.create());
        //设置请求回调,使用RxJava 对网络返回进行处理
        builder.addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        //retrofit配置完成
        Retrofit retrofit = builder.build();
        //放入Map中
        retrofitHashMap.put(mBaseUrl + serviceClass.getName(), retrofit);
        //最后返回即可
        return retrofit;
    }

    /**
     * //配置RxJava，完成线程的切换,如果是Kotlin中完全可以直接使用协程
     *
     * @param observer
     * @param <T>      泛型
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers(final Observer<T> observer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                Observable<T> observable = upstream
                        .subscribeOn(Schedulers.io())//线程订阅操作在io线程
                        .observeOn(AndroidSchedulers.mainThread())//观察操作在Android主线程上执行
                        //map 方法将 Observable 映射到一个新的 Observable，该 Observable 会检查是否存在 500 错误。如果存在，
                        // 则调用 getAppErrorHandler 方法处理错误。最后，它使用 onErrorResumeNext 方法处理 400 错误。
                        .map(NetworkApi.<T>getAppErrorHandler())//判断有没有500的错误,有则进入getAppERRORhANDLER
                        .onErrorResumeNext(new HttpErrorHandler<T>());//判断有没有400的错误
                //这里还少了对异常
                //订阅观察者
                observable.subscribe(observer);
                return observable;
            }
        };
    }

    /**
     * 错误码处理
     */
    protected static <T> Function<T, T> getAppErrorHandler() {
        return new Function<T, T>() {
            @Override
            public T apply(T response) throws Exception {
                //当response返回出现500之类的错误时
                if (response instanceof BaseResponse && ((BaseResponse) response).responseCode >= 500) {
                    //通过这个异常处理,得到用户可以知道的原因
                    ExceptionHandle.ServerException exception = new ExceptionHandle.ServerException();
                    exception.code = ((BaseResponse) response).responseCode;
                    exception.message = ((BaseResponse) response).responseError != null ? ((BaseResponse) response).responseError : "";
                    throw exception;
                }
                return response;
            }
        };
    }
}
