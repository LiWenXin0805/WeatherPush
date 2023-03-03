package com.lwx.weatherpush.service;

import java.util.List;

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
     * @param accounts 微信号
     * @return 推送失败的用户
     */
    List<String> pushAll(List<String> accounts);

}
