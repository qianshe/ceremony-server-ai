package com.qianshe.tools.func;

import org.springframework.stereotype.Service;

import java.util.function.Function;


/**
 * Function as tool to get weather information
 */
@Service
public class WeatherService implements Function<WeatherRequest, WeatherResponse> {
    public WeatherResponse apply(WeatherRequest request) {
        return new WeatherResponse(30.0, Unit.C);
    }
}

