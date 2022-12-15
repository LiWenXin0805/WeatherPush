package com.lwx.weatherpush.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author LiWenXin
 * @date 2022/12/14
 */
@Data
@Component
@ConfigurationProperties(prefix = "api")
public class ApiConfig {

    private String baiduWeatherUrl;

    private String baiduWeatherKey;

    private String tianApiKey;

    private String tianApiEveryDayUrl;

    private String tianApiEnSentenceUrl;

    private String tianApiQingShiUrl;

    private String tianApiSayLoveUrl;

    private String tianApiZaoAnUrl;

    private String tianApiCaiHongPiUrl;

    private String hitoUrl;

}
