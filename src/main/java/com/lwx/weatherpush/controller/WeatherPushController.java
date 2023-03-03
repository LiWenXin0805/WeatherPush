package com.lwx.weatherpush.controller;

import com.lwx.weatherpush.service.WeatherPushService;
import com.lwx.weatherpush.standard.RestResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author LiWenXin
 * @date 2022/12/13
 */
@RestController
@RequestMapping("/weather-push")
@Api(tags = "天气推送API")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WeatherPushController {

    private final WeatherPushService weatherPushService;

    @PutMapping("/push")
    @ApiOperation("给单个用户推送消息接口")
    public RestResponse<Boolean> push(@RequestParam("account") @ApiParam("用户ID") String account) {
        return RestResponse.ok(weatherPushService.push(account));
    }

    @PutMapping("/push/all")
    @ApiOperation("给全部用户推送消息接口")
    public RestResponse<List<String>> pushAll(@RequestParam("account") @ApiParam("用户ID") List<String> account) {
        return RestResponse.ok(weatherPushService.pushAll(account));
    }

}
