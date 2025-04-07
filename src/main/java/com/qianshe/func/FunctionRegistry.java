package com.qianshe.func;

import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Deprecated
@Configuration
public class FunctionRegistry {

    @Bean
    public FunctionCallback getCurrentDateTime() {
        return FunctionCallback.builder()
                .function("getCurrentDateTime", new FuncService())
                .description("获取用户时区中的当前日期和时间")
                .inputType(FuncService.Request.class)
                .build();
    }

}
