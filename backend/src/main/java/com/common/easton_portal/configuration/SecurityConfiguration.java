package com.common.easton_portal.configuration;


import com.common.easton_portal.constants.SignerNameConstant;
import com.common.easton_portal.core.AuthenticationTokenFilter;
import com.common.easton_portal.core.SignerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableRetry
@EnableWebSecurity
public class SecurityConfiguration {
    @Autowired private AuthenticationTokenFilter mAuthTokenFilter;

    @Bean public static PasswordEncoder getPasswordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .authorizeRequests()
                .antMatchers("/**/public/**").permitAll()
                .anyRequest().authenticated().and().addFilterBefore(mAuthTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.headers().frameOptions().disable();
        return http.build();
    }

    @Bean
    public static AuthenticationTokenFilter getAuthTokenFilter(
            @Autowired @Qualifier(SignerNameConstant.authentication) SignerProvider signer) {
        return new AuthenticationTokenFilter(signer);
    }


//    @Bean(name= SignerNameConstant.authentication)
//    public static SignerProvider authSigner() {
//        return new SignerProvider() {
//            @Value("${custom.token_expiration}")
//            private long mExpirationTime;
//            private CertificateSigner mSigner;
//            {
//                var method = JwtSigner.Type.RSA384;
//                try(var privateKey = getClass().getResourceAsStream("/key/private_key.pem")) {
//                    try(var publicKey = getClass().getResourceAsStream("/key/public_key.pem")){
//                        var key = KeyPair.readFrom(privateKey, publicKey, method.factory);
//                        if (key != null) mSigner = new JwtSigner(key, method);
//                    }
//                } catch (IOException ignored) {}
//            }
//
//            @Override public CertificateSigner getWriteSigner() { return mSigner; }
//            @Override public CertificateSigner[] getReadSigners() { return new CertificateSigner[] { mSigner }; }
//            @Override public Duration getExpirationPeriod() { return Duration.ofSeconds(mExpirationTime); }
//        };
//    }


}
