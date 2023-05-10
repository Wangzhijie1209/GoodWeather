package com.wzj.library.network.interceptor;

import android.annotation.SuppressLint;

import com.wzj.library.network.INetworkRequiredInfo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 这是一个发送拦截器
 */
public class RequestInterceptor implements Interceptor {
    //网络请求信息
    private final INetworkRequiredInfo iNetworkRequiredInfo;

    public RequestInterceptor(INetworkRequiredInfo iNetworkRequiredInfo) {
        this.iNetworkRequiredInfo = iNetworkRequiredInfo;
    }


    /**
     * intercept方法会在发送之前被调用,这个方法通过chain.request()获取原始请求,然后使用newBuilder()方法创建一个
     * 新的Request.Builder对象,接下来,它使用addHeader方法向新的请求添加自定义标头,最后它使用build()方法构建新的请求,
     * 并使用chain.proceed方法将其发送
     *
     * 可以通过检查intercept方法中的代码来确定它是在发送之前还是之后被调用,如果它修改了请求并使用了chain.proceed方法
     * 将其发送 则说明它是在发送请求之前被调用
     *
     *
     * 当一个网络请求被发出时,它会被传递给第一个拦截器。第一个拦截器会调用它的intercept方法来处理这个请求,
     * 在intercept方法中 拦截器可以对这个请求进行修改,然后调用chain.proceed(request)方法将修改后的请求传递
     * 给下一个拦截器,这个过程会一直重复,直到所有拦截器都处理完毕,最后一个拦截器将请求发送到服务器,并返回服务器的响应
     * 因此 intercept方法会在每次发出网络请求时被调用
     * @param chain
     * @return
     * @throws IOException
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        //构建器
        Request.Builder builder = chain.request().newBuilder();
        //添加使用环境
        builder.addHeader("os", "android");
        //添加版本号
        builder.addHeader("appVersionCode", this.iNetworkRequiredInfo.getAppVersionCode());
        //添加版本名
        builder.addHeader("appVersionName", this.iNetworkRequiredInfo.getAppVersionName());
        //添加日期事件
        builder.addHeader("datetime", getNowDateTime());
        //返回
        return chain.proceed(builder.build());
    }

    public static String getNowDateTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
}
