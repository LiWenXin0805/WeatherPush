package com.lwx.weatherpush.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wx")
public class WxConfig {

    private String appId;

    private String appSecret;

    private String getAccessTokenUrl;

    private String sendMsgUrl;

}
