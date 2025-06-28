package com.common.easton_portal.configuration;

import com.common.core.web.permission.PermissionChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.Serializable;
import java.util.stream.Collectors;

@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
@EnableWebMvc
public class WebMvcConfiguration extends GlobalMethodSecurityConfiguration implements WebMvcConfigurer {
    @ControllerAdvice
    public class DefaultExceptionHandle extends ExceptionBaseHandler {}

    @Autowired
    private PermissionEvaluator mPermissionEvaluator;
    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        var expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(mPermissionEvaluator);
        return expressionHandler;
    }

    @Bean
    public static PermissionEvaluator getPermissionEvaluator() {
        return new PermissionEvaluator() {
            @Override
            public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
                var ownRight = auth == null ? null : auth.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toList());
                return PermissionChecker.allow(ownRight, permission);
            }

            @Override
            public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
                return hasPermission(auth, targetType, permission);
            }
        };
    }
}