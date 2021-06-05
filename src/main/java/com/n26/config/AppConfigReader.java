package com.n26.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "transactions")
public class AppConfigReader {

    private Integer window;

    public void setWindow(Integer window) {
        this.window = window;
    }

    public Integer getWindow() {
        return window;
    }
}
