package com.wzj.goodweather;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.wzj.goodweather.adapter.DailyAdapter;
import com.wzj.goodweather.adapter.LifestyleAdapter;
import com.wzj.goodweather.bean.DailyResponse;
import com.wzj.goodweather.bean.LifestyleResponse;
import com.wzj.goodweather.bean.NowResponse;
import com.wzj.goodweather.bean.SearchCityResponse;
import com.wzj.goodweather.databinding.ActivityMainBinding;
import com.wzj.goodweather.location.LocationCallback;
import com.wzj.goodweather.location.MyLocationListener;
import com.wzj.goodweather.viewmodel.MainViewModel;
import com.wzj.library.base.NetWorkActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NetWorkActivity<ActivityMainBinding> implements LocationCallback {
    //权限数组
    private final String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
    //请求权限意图
    private ActivityResultLauncher<String[]> requestPermissionIntent;
    public LocationClient mLocationClient = null;
    private final MyLocationListener myListener = new MyLocationListener();

    private MainViewModel viewModel;

    //RecyclerView近七天天气情况
    private final List<DailyResponse.DailyBean> dailyBeanList = new ArrayList<>();
    private final DailyAdapter dailyAdapter = new DailyAdapter(dailyBeanList);
    //生活指数
    private final List<LifestyleResponse.DailyBean> lifestyleList = new ArrayList<>();
    private final LifestyleAdapter lifestyleAdapter = new LifestyleAdapter(lifestyleList);


    private void initView() {
        binding.rvDaily.setLayoutManager(new LinearLayoutManager(this));
        binding.rvDaily.setAdapter(dailyAdapter);
        binding.rvLifestyle.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLifestyle.setAdapter(lifestyleAdapter);
    }

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
        String city = bdLocation.getCity();
        String district = bdLocation.getDistrict();

        if (viewModel != null && district != null) {
            //显示当前定位城市
            binding.tvCity.setText(district);
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
        initView();
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
                        //通过城市ID查询天气预报
                        viewModel.dailyWeather(id);
                        //通过城市ID查询生活指数
                        viewModel.lifestyle(id);
                    }
                }
            });

            //天气预报返回
            viewModel.dailyResponseMutableLiveData.observe(this, dailyResponse -> {
                List<DailyResponse.DailyBean> daily = dailyResponse.getDaily();
                if (daily != null) {
                    if (dailyBeanList.size() > 0) {
                        dailyBeanList.clear();
                    }
                    dailyBeanList.addAll(daily);
                    dailyAdapter.notifyDataSetChanged();
                }
            });

            //实况天气返回
            viewModel.nowResponseMutableLiveData.observe(this, nowResponse -> {
                NowResponse.NowBean now = nowResponse.getNow();
                if (now != null) {
                    binding.tvInfo.setText(now.getText());
                    binding.tvTemp.setText(now.getTemp());
                    binding.tvUpdateTime.setText("最近更新时间:" + nowResponse.getUpdateTime());

                    binding.tvWindDirection.setText("风向   "+now.getWindDir());//风向
                    binding.tvWindPower.setText("风力   "+now.getWindScale()+"级");//风力
                    binding.wwBig.startRotate();//大风车开始转动
                    binding.wwSmall.startRotate();//小风车开始转动
                }
            });
            //错误信息返回
            viewModel.failed.observe(this, this::showLongMsg);
        }
        //生活指数
        viewModel.lifestyleResponseMutableLiveData.observe(this,lifestyleResponse -> {
            List<LifestyleResponse.DailyBean> daily = lifestyleResponse.getDaily();
            if(daily!=null){
             if(lifestyleList.size()>0){
                 lifestyleList.clear();
             }
             lifestyleList.addAll(daily);
             lifestyleAdapter.notifyDataSetChanged();
            }
        });
    }


}