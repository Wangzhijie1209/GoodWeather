package com.wzj.goodweather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wzj.goodweather.databinding.ActivityMainBinding;
import com.wzj.goodweather.location.LocationCallback;
import com.wzj.goodweather.location.MyLocationListener;

public class MainActivity extends AppCompatActivity implements LocationCallback {
    private ActivityMainBinding binding;

    public LocationClient mLocationClient = null;
    private final MyLocationListener myListener = new MyLocationListener();

    //权限数组
    private final String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerIntent();//检测多个权限
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initLocation();
        requestPermission();
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        try {
            mLocationClient = new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
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
        binding.tvAddressDetail.setText(addr);
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
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //开始权限请求
            requestPermissionIntent.launch(permissions);
            return;
        }
        startLocation();
    }
}