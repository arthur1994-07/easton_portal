package com.common.easton_portal.configuration;

import com.common.core.web.permission.PermissionSystem;
import com.common.core.web.permission.RightAnnotation;
import com.common.easton_portal.constants.PermissionConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PermissionConfiguration {
    public static class DefaultPermissionSystem extends PermissionSystem<RightAnnotation> {
        public DefaultPermissionSystem() throws Exception {
            super(PermissionConstant.class, RightAnnotation.class);
        }
    }
    @Bean
    private static PermissionSystem<RightAnnotation> getPermissionRight() throws Exception { return new DefaultPermissionSystem(); }
}
