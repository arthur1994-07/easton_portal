package com.common.easton_portal;

import com.common.core.base.log.Log;
import com.common.core.base.log.SystemAppender;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@PropertySources({
        @PropertySource("/database/application.properties"),
        @PropertySource("/database/application-${database.custom.type}.properties"),
})
public class MainApplication {
    public static void main(String[] args) {
        Log.addAppender(new SystemAppender());
        SpringApplication.run(MainApplication.class, args);
    }
}