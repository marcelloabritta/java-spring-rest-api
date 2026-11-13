package com.marcelloabritta.restapi.config;

public interface TestConfigs {
    int SERVER_PORT = 8888;

    String HEADER_PARAM_AUTHORIZATION = "Authorization";
    String HEADER_PARAM_ORIGIN = "Origin";

    String ORIGIN_FRONT = "http://localhost:3000";
    String ORIGIN_DENIED = "https://github.com";
    String ORIGIN_LOCAL = "http://localhost:8080";
}
