package com.lwx.weatherpush.service;

import com.alibaba.fastjson.JSONObject;

/**
 * @author LiWenXin
 * @date 2022/12/12
 */
public interface WeatherPushService {

    /**
     * 给单个用户推送消息
     *
     * @param account 微信号
     * @return 是否推送成功
     */
    boolean push(String account);

    /**
     * 给全部用户推送消息
     *
     * @return 是否推送成功
     */
    boolean pushAll();

    /**
     * @param districtId 区县的行政区划编码，和location二选一
     * @return 全量的返回内容
     */
    JSONObject getWeather(String districtId);

}
