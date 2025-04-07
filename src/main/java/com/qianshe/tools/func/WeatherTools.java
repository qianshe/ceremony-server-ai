package com.qianshe.tools.func;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 * Dynamic Specification: @Bean
 * 动态规范: 即使用时直接用 “currentWeather”
 */
@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
class WeatherTools {

    final WeatherService weatherService;
    public static final String CURRENT_WEATHER_TOOL = "currentWeather";

    @Bean(CURRENT_WEATHER_TOOL)
    @Description("Get the weather in location")
    Function<WeatherRequest, WeatherResponse> currentWeather() {
        return weatherService;
    }

}