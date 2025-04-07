package com.qianshe.ceremonyservermcp;

import jakarta.annotation.Resource;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;

@SpringBootApplication
public class CeremonyServerMcpApplication implements CommandLineRunner {

    @Resource
    private ToolCallbackProvider toolCallbackProvider;


    public static void main(String[] args) {
        SpringApplication.run(CeremonyServerMcpApplication.class, args);
    }


    @Override
    public void run(String... args) {
        FunctionCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();

        System.out.println("ToolCallbackProvider: " +
                Arrays.toString(toolCallbacks));
    }
}
