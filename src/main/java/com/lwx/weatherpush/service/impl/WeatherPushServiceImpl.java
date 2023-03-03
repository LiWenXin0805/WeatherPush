package com.lwx.weatherpush.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lwx.weatherpush.config.ApiConfig;
import com.lwx.weatherpush.config.WxConfig;
import com.lwx.weatherpush.entity.User;
import com.lwx.weatherpush.repository.UserRepository;
import com.lwx.weatherpush.service.WeatherPushService;
import com.lwx.weatherpush.util.CacheUtils;
import com.lwx.weatherpush.util.HttpClientUtils;
import com.lwx.weatherpush.util.PreconditionUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author LiWenXin
 * @date 2022/12/12
 */
@Slf4j
@Service
public class WeatherPushServiceImpl implements WeatherPushService {

    private UserRepository userRepository;

    private WxConfig wxConfig;
    private ApiConfig apiConfig;
    private Random random;

    @Override
    public boolean push(String account) {
        PreconditionUtils.checkNotBlank(account, "微信号为空！");
        User user = findByAccount(account);

        //设置推送数据
        JSONObject data = new JSONObject();
        //问候语
        setGreeting(data, user);
        //日期、城市、天气、气温
        setWeather(data, user);
        //名言
        setSaying(data);
        //生日
        setBirthday(data, user);
        //纪念日

        //推送内容
        JSONObject pushContent = new JSONObject();
        pushContent.put("name", user.getName());
        pushContent.put("touser", user.getAccount());
        pushContent.put("template_id", user.getTemplateId());
        pushContent.put("data", data);

        //推送
        log.info("开始给用户 " + user.getName() + " 进行推送！");
        String response = HttpClientUtils.doPost(wxConfig.getSendMsgUrl() + CacheUtils.getAccessToken(), pushContent, null);
        log.info("推送信息：" + pushContent.toJSONString());
        log.info("推送结果：" + response);
        return true;
    }

    @Override
    public List<String> pushAll(List<String> accounts) {
        List<User> userList;
        if (CollectionUtils.isEmpty(accounts)) {
            userList = userRepository.findAll();
        } else {
            userList = userRepository.findAllByAccountIn(accounts);
        }
        accounts.clear();
        for (User user : userList) {
            try {
                push(user.getAccount());
            } catch (Exception e) {
                log.error("定时推送 " + user.getName() + " 时出现异常！");
                log.error(e.getMessage(), e);
                accounts.add(user.getAccount());
            }
        }
        return accounts;
    }


    private void setGreeting(JSONObject data, User user) {
        data.put("greeting", formatValue(user.getGreeting().replace("?", user.getPetName())));
    }

    private void setWeather(JSONObject data, User user) {
        JSONObject weather = getWeather(user.getCity());
        JSONObject result = weather.getJSONObject("result");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        JSONObject today = new JSONObject();
        for (Object forecast : result.getJSONArray("forecasts")) {
            JSONObject e = (JSONObject) forecast;
            if (currentDate.equals(e.getString("date"))) {
                today = e;
                break;
            }
        }
        //日期
        data.put("date", formatValue(today.getString("date") + " " + today.getString("week")));
        //城市
        data.put("city", formatValue(result.getJSONObject("location").getString("name")));
        //天气
        JSONObject now = result.getJSONObject("now");
        data.put("weather", formatValue(now.getString("text")));
        data.put("wind_dir", formatValue(now.getString("wind_dir")));
        data.put("wind_class", formatValue(now.getString("wind_class")));
        //气温
        data.put("now_temperature", formatValue(now.get("temp") + "℃"));
        data.put("max_temperature", formatValue(today.getString("high") + "℃"));
        data.put("min_temperature", formatValue(today.getString("low") + "℃"));
    }

    /**
     * 设置名言、情话等
     *
     * @param data 推送数据
     */
    private void setSaying(JSONObject data) {
        String content = "content";
        JSONObject yy = getYy();
        data.put("hitokoto", formatValue(yy.getString("hitokoto")));
        //天行英语一句话
        JSONObject tianEnSentence = getTianEnSentence();
        data.put("en_sentence_en", formatValue(tianEnSentence.getString("en")));
        data.put("en_sentence_zh", formatValue(tianEnSentence.getString("zh")));
        //天行古代情诗
        JSONObject tianQingShi = getTianQingShi();
        data.put("qing_shi", formatValue(tianQingShi.getString(content)));
        //天行土味情话
        JSONObject tianSayLove = getTianSayLove();
        data.put("say_love", formatValue(tianSayLove.getString(content)));
        //天行早安心语
        JSONObject tianZaoAn = getTianZaoAn();
        data.put("zao_an", formatValue(tianZaoAn.getString(content)));
        //天行彩虹屁
        JSONObject tianCaiHongPi = getTianCaiHongPi();
        data.put("cai_hong_pi", formatValue(tianCaiHongPi.getString(content)));
    }

    private void setBirthday(JSONObject data, User user) {
        data.put("self_age", formatValue(getAge(user.getBirthday())));
        data.put("self_birthday_remain", formatValue(countDown(user.getBirthday())));
    }

    /**
     * 从百度地图API获取天气
     *
     * @param districtId 区县的行政区划编码，和location二选一
     * @return 全量的返回内容
     */
    private JSONObject getWeather(String districtId) {
        PreconditionUtils.checkNotBlank(districtId, "请输入区县的行政区划编码！");
        String url = apiConfig.getBaiduWeatherUrl() + "?data_type=all&ak=" + apiConfig.getBaiduWeatherKey() + "&district_id=" + districtId;
        log.info("query weather url：" + url);
        String response = HttpClientUtils.doGet(url, null);
        log.info("query weather response：" + response);
        return JSON.parseObject(response);
    }

    private JSONObject getYy() {
        return JSON.parseObject(HttpClientUtils.doGet(apiConfig.getHitoUrl(), null));
    }

    private JSONObject getTianEnSentence() {
        String url = apiConfig.getTianApiEnSentenceUrl() + getTianApiKey();
        log.info("获取天行API英语一句话！");
        String response = HttpClientUtils.doGet(url, null);
        log.info("天行API英语一句话结果：" + response);
        return getTianApiResult(response);
    }

    private JSONObject getTianQingShi() {
        String url = apiConfig.getTianApiQingShiUrl() + getTianApiKey();
        log.info("获取天行API古代情诗！");
        String response = HttpClientUtils.doGet(url, null);
        log.info("天行API古代情诗结果：" + response);
        return getTianApiResult(response);
    }

    private JSONObject getTianSayLove() {
        String url = apiConfig.getTianApiSayLoveUrl() + getTianApiKey();
        log.info("获取天行API土味情话！");
        String response = HttpClientUtils.doGet(url, null);
        log.info("天行API土味情话结果：" + response);
        return getTianApiResult(response);
    }

    private JSONObject getTianZaoAn() {
        String url = apiConfig.getTianApiZaoAnUrl() + getTianApiKey();
        log.info("获取天行API早安心语！");
        String response = HttpClientUtils.doGet(url, null);
        log.info("天行API早安心语结果：" + response);
        return getTianApiResult(response);
    }

    private JSONObject getTianCaiHongPi() {
        String url = apiConfig.getTianApiCaiHongPiUrl() + getTianApiKey();
        log.info("获取天行API彩虹屁！");
        String response = HttpClientUtils.doGet(url, null);
        log.info("天行API彩虹屁结果：" + response);
        return getTianApiResult(response);
    }

    private JSONObject getTianApiResult(String response) {
        return JSON.parseObject(response).getJSONObject("result");
    }

    private String getTianApiKey() {
        return "?key=" + apiConfig.getTianApiKey();
    }

    private User findByAccount(String account) {
        return userRepository.findByAccount(account).orElseThrow(PreconditionUtils.newBusinessException("微信号不存在：" + account));
    }

    //封装json
    private JSONObject formatValue(Object value) {
        JSONObject json = new JSONObject();
        json.put("value", value);
        json.put("color", randomColor());
        return json;
    }

    @SneakyThrows
    private Integer getAge(String birthDay) {
        if (StringUtils.isEmpty(birthDay)) {
            return 0;
        }
        Calendar current = Calendar.getInstance();
        current.setTime(new Date());
        Calendar birth = Calendar.getInstance();
        birth.setTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(birthDay + " 00:00:00"));
        return current.get(Calendar.YEAR) - birth.get(Calendar.YEAR) + 1;
    }

    @SneakyThrows
    private Long countDown(String target) {
        if (StringUtils.isEmpty(target)) {
            return 0L;
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
        return Math.abs((targetCalendar.getTime().getTime() - currentCalender.getTime().getTime()) / 86400000);
    }

    private String randomColor() {
        String red = Integer.toHexString(random.nextInt(256));
        red = red.length() == 1 ? "0" + red : red;
        red = red.toUpperCase();
        String green = Integer.toHexString(random.nextInt(256));
        green = green.length() == 1 ? "0" + green : green;
        green = green.toUpperCase();
        String blue = Integer.toHexString(random.nextInt(256));
        blue = blue.length() == 1 ? "0" + blue : blue;
        blue = blue.toUpperCase();
        return "#" + red + green + blue;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setWxConfig(WxConfig wxConfig) {
        this.wxConfig = wxConfig;
    }

    @Autowired
    public void setApiConfig(ApiConfig apiConfig) {
        this.apiConfig = apiConfig;
    }

    @Autowired
    @SneakyThrows
    public void setRandom() {
        this.random = SecureRandom.getInstanceStrong();
    }

}
