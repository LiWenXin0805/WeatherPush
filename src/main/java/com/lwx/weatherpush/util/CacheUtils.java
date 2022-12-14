package com.lwx.weatherpush.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Maps;
import com.lwx.weatherpush.config.WxConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author LiWenXin
 * @date 2022/12/14
 */
@Slf4j
@SuppressWarnings("all")
public class CacheUtils {

    private CacheUtils() {
    }

    private static final String ACCESS_TOKEN_MAP = "ACCESS_TOKEN_MAP";
    private static final String ACCESS_TOKEN = "access_token";

    private static final LoadingCache<String, Map<String, Object>> CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .removalListener((RemovalListener<String, Object>) notification -> log.info("{} has expired now.", notification.getKey()))
            .build(new CacheLoader<String, Map<String, Object>>() {
                @Override
                public Map<String, Object> load(String key) {
                    log.info("Load {} now.", key);
                    switch (key) {
                        case ACCESS_TOKEN_MAP:
                            return generateAccessToken();
                        default:
                            return Maps.newHashMap();
                    }
                }
            });

    @SneakyThrows
    public static void refresh(String key) {
        CACHE.invalidate(key);
    }

    @SneakyThrows
    public static Map<String, Object> getByKey(String key) {
        return CACHE.get(key);
    }

    /**
     * 获取微信的 access_token
     *
     * @return 微信的 access_token
     */
    public static String getAccessToken() {
        return (String) getByKey(ACCESS_TOKEN_MAP).get(ACCESS_TOKEN);
    }

    private static Map<String, Object> generateAccessToken() {
        WxConfig wxConfig = SpringContextUtils.getBean(WxConfig.class);
        String getTokenUrl = wxConfig.getGetAccessTokenUrl() + "&appid=" + wxConfig.getAppId() + "&secret=" + wxConfig.getAppSecret();
        log.info("获取access_token！");
        JSONObject response = JSON.parseObject(HttpClientUtils.doGet(getTokenUrl, null));
        log.info("获取到access_token！");
        Map<String, Object> accessTokenMap = new HashMap<>();
        accessTokenMap.put(ACCESS_TOKEN, response.getString(ACCESS_TOKEN));
        return accessTokenMap;
    }

}
