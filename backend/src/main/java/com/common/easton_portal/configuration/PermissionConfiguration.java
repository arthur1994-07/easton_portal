package com.common.easton_portal.configuration;

import com.common.core.web.permission.PermissionSystem;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.permission.LoginRightAnnotation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionConfiguration {
    public static class DefaultPermissionSystem extends PermissionSystem<LoginRightAnnotation> {
        public DefaultPermissionSystem() throws Exception {
            super(PermissionConstant.class, LoginRightAnnotation.class);
        }
    }
    @Bean
    private static PermissionSystem<LoginRightAnnotation> getPermissionRight() throws Exception { return new DefaultPermissionSystem(); }
}
