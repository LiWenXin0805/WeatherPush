package com.lwx.weatherpush.task;

import com.lwx.weatherpush.service.WeatherPushService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author LiWenXin
 * @date 2022/12/14
 */
@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PushTask {

    private final WeatherPushService weatherPushService;

    @Scheduled(cron = "0 0 7 * * ?")
    public void pushAll() {
        log.info("开始定时推送任务，当前时间：" + new Date());
        List<String> accounts = weatherPushService.pushAll(new ArrayList<>());
        log.error("以下用户推送失败！accounts: " + accounts.toString());
        log.info("定时推送任务结束！");
    }

}
