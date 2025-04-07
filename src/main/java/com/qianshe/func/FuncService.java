package com.qianshe.func;

import org.springframework.stereotype.Service;

import java.util.function.Function;


@Deprecated
@Service
public class FuncService implements Function<FuncService.Request, FuncService.Response> {

    @Override
    public Response apply(Request request) {
        System.out.println("FuncService.apply() called with request: " + request);
        return new Response("ok");
    }

    public record Request(String func, String param) {
    }

    public record Response(String result) {

    }
}
