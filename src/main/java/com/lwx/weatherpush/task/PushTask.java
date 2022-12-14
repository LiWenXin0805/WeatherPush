package com.lwx.weatherpush.task;

import com.lwx.weatherpush.service.WeatherPushService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author LiWenXin
 * @date 2022/12/14
 */
@Component
@Slf4j
public class PushTask {

    private WeatherPushService weatherPushService;

    @Scheduled(cron = "0 0 7 * * ?")
    public void pushAll() {
        log.info("开始定时推送，当前时间：" + new Date());
        weatherPushService.pushAll();
    }

    @Autowired
    public void setWeatherPushService(WeatherPushService weatherPushService) {
        this.weatherPushService = weatherPushService;
    }

}
