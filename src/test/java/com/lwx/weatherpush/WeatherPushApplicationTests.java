package com.lwx.weatherpush;

import com.alibaba.fastjson.JSONObject;
import com.lwx.weatherpush.config.ApiConfig;
import com.lwx.weatherpush.config.WxConfig;
import com.lwx.weatherpush.util.HttpClientUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
@Slf4j
class WeatherPushApplicationTests {

    @Autowired
    private WxConfig wxConfig;

    @Autowired
    private ApiConfig apiConfig;

    @Test
    void contextLoads() {
    }

    @Test
    @SneakyThrows
    public void getWeather() {

        String baiduWeather = "https://api.map.baidu.com/weather/v1/?district_id=" + 222405 + "&data_type=all&ak=" + apiConfig.getBaiduWeatherKey();

        String s = HttpClientUtils.doGet(baiduWeather, null);

        System.out.println(s);

        JSONObject jsonObject = JSONObject.parseObject(s);
        String result = String.valueOf(jsonObject.getJSONObject("result").getJSONObject("now").get("temp"));
        System.out.println(result);
    }

    @Test
    @SneakyThrows
    public void getYy() {
        String response = HttpClientUtils.doGet(apiConfig.getHitoUrl(), null);
        System.out.println(response);
    }

    @Test
    @SneakyThrows
    public void testTian() {
        String url = apiConfig.getTianApiEnSentenceUrl() + "?key=" + apiConfig.getTianApiKey();
        String response = HttpClientUtils.doGet(url, null);
        log.info("response：" + response);

    }

    @Test
    @SneakyThrows
    public void test() {
        String target = "1998-12-15";
        if (StringUtils.isEmpty(target)) {
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        String current = dateFormat.format(new Date());
        target = current.split("-")[0] + target.substring(4);

        Calendar targetCalendar = Calendar.getInstance();
        targetCalendar.setTime(datetimeFormat.parse(target + " 00:00:00"));
        Calendar currentCalender = Calendar.getInstance();
        currentCalender.setTime(datetimeFormat.parse(current + " 00:00:00"));
        if (currentCalender.compareTo(targetCalendar) > 0) {
            targetCalendar.add(Calendar.YEAR, 1);
        }
        long remain = Math.abs((targetCalendar.getTime().getTime() - currentCalender.getTime().getTime()) / 86400000);
        System.out.println(remain);
    }

    @Test
    @SneakyThrows
    public void getAge() {
        String birthDay = "1998-08-05";
        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        Calendar birth = Calendar.getInstance();
        birth.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(birthDay + " 00:00:00"));
        int i = current.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + 1;
        System.out.println(i);
    }

}
