package com.wzj.goodweather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wzj.goodweather.bean.NowResponse;
import com.wzj.goodweather.bean.SearchCityResponse;
import com.wzj.goodweather.databinding.ActivityMainBinding;
import com.wzj.goodweather.location.LocationCallback;
import com.wzj.goodweather.location.MyLocationListener;
import com.wzj.goodweather.viewmodel.MainViewModel;
import com.wzj.library.base.NetWorkActivity;

import java.util.List;

public class MainActivity extends NetWorkActivity<ActivityMainBinding> implements LocationCallback {
    //权限数组
    private final String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;
    public LocationClient mLocationClient = null;
    private final MyLocationListener myListener = new MyLocationListener();

    private MainViewModel viewModel;

    /**
     * 注册意图
     */
    @Override
    public void onRegister() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocation = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION));
            boolean writeStorage = Boolean.TRUE.equals(result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE));
            if (fineLocation && writeStorage) {
                //权限已经获取到，开始定位
                startLocation();
            }
        });
    }


    /**
     * 初始化定位
     */
    private void initLocation() {
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLocationClient != null) {
            myListener.setCallback(this);
            //注册定位监听
            mLocationClient.registerLocationListener(myListener);
            LocationClientOption option = new LocationClientOption();
            //如果开发者需要获得当前点的地址信息,此处必须为true
            option.setIsNeedAddress(true);
            //可选,设置是否需要最新版本的地址信息,默认不需要,即参数为false
            option.setNeedNewVersionRgc(true);
            //将配置好的LocationClientOption对象,通过setLocOption方法传递给LocationClient对象使用
            mLocationClient.setLocOption(option);
        }
    }

    /**
     * 通过dbLocation得到一些需要的信息
     *
     * @param bdLocation 定位数据
     */
    @Override
    public void onReceiveLocation(BDLocation bdLocation) {
        double latitude = bdLocation.getLatitude();//获得纬度信息
        double longitude = bdLocation.getLongitude();//获取经度信息
        float radius = bdLocation.getRadius();//获取定位精度,默认值为0.0f
        String coorType = bdLocation.getCoorType();
        //获取经纬度坐标类型, 以LocationClientOption中设置过的坐标类型为准
        int errorCode = bdLocation.getLocType();//161 表示网络定位结果
        String addr = bdLocation.getAddrStr();//获取详细地址信息
        String country = bdLocation.getCountry();//获取国家
        String province = bdLocation.getProvince();//获取省份
        String city = bdLocation.getCity();//获取城市
        String district = bdLocation.getDistrict();//获取区县
        String street = bdLocation.getStreet();//获取街道信息
        String locationDescribe = bdLocation.getLocationDescribe();//获取位置描述信息

        if (viewModel != null && district != null) {
            //搜索城市
            viewModel.searchCity(district, true);
        } else {
            Log.e("TAG", "district: " + district);
        }
    }

    /**
     * 启动定位
     */
    private void startLocation() {
        if (mLocationClient != null) {
            mLocationClient.start();
        }
    }

    /**
     * 检测是否申请了访问 ·
     * 使用Activity Result API中的用法,这个组件也是Jetpack中的
     * 这个意图需要在Activity初始化之前进行注册
     * android.Manifest.permission.ACCESS_FINE_LOCATION 用于访问设备精确位置的权限
     * android.Manifest.permission.WRITE_EXTERNAL_STORAGE  用于写入外部存储器的权限
     */
    private void registerIntent() {
        //请求权限意图
        requestPermissionIntent = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            boolean fineLocation = Boolean.TRUE.equals(result.get(android.Manifest.permission.ACCESS_FINE_LOCATION));
            boolean writeStorage = Boolean.TRUE.equals(result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE));
            if (fineLocation && writeStorage) {
                //权限已经获取到,开始定位
                startLocation();
            }
        });
    }

    /**
     * 检测是否获取了 精确位置 和 用于写入外部存储器的权限
     * 如果没有 就请求权限
     */
    private void requestPermission() {
        //因为项目的最低版本API是23，所以肯定需要动态请求危险权限，只需要判断权限是否拥有即可
        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return;
        }
        //开始定位
        startLocation();
    }

    /**
     * 初始化
     */
    @Override
    protected void onCreate() {
        setFullScreenImmersion();
        initLocation();
        requestPermission();
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    /**
     * 数据观察
     */
    @Override
    protected void onObserveData() {
        if (viewModel != null) {
            //城市数据返回
            viewModel.searchCityResponseMutableLiveData.observe(this, searchCityResponse -> {
                List<SearchCityResponse.LocationBean> location = searchCityResponse.getLocation();
                if (location != null && location.size() > 0) {
                    String id = location.get(0).getId();
                    //获取到城市的ID
                    if (id != null) {
                        //通过城市ID查询城市实时天气
                        viewModel.nowWeather(id);
                    }
                }
            });
            //实况天气返回
            viewModel.nowResponseMutableLiveData.observe(this, nowResponse -> {
                NowResponse.NowBean now = nowResponse.getNow();
                if (now != null) {
                    binding.tvInfo.setText(now.getText());
                    binding.tvTemp.setText(now.getText());
                    binding.tvUpdateTime.setText("最近更新时间:" + nowResponse.getUpdateTime());
                }
            });
            //错误信息返回
            viewModel.failed.observe(this, this::showLongMsg);
        }
    }

}