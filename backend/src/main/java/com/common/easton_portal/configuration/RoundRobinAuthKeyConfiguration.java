package com.common.easton_portal.configuration;

import com.common.core.web.security.base.CertificateSigner;
import com.common.core.web.security.jwt.JwtSigner;
import com.common.core.web.security.misc.KeyPair;
import com.common.easton_portal.constants.SignerNameConstant;
import com.common.easton_portal.core.SignerProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@EnableScheduling
public class RoundRobinAuthKeyConfiguration {
    public static final int K_ALL_READ_SIGNER_MAXSIZE = 3;

    public static class ConcreteSignerProvider implements SignerProvider {
        @Value("${custom.token_expiration}")
        private long mExpirationTime;
        private CertificateSigner mCurrentSigner;
        private CertificateSigner[] mAllReadSigners;

        public ConcreteSignerProvider() {
            var keyPair = KeyPair.generate(JwtSigner.Type.RSA512.factory, JwtSigner.Type.RSA512.size);
            if (keyPair != null) {
                mCurrentSigner = new JwtSigner(keyPair, JwtSigner.Type.RSA512);
                mAllReadSigners = new CertificateSigner[] { mCurrentSigner };
            }
        }

        public void generateKey() {
            var keyPair = KeyPair.generate(JwtSigner.Type.RSA512.factory, JwtSigner.Type.RSA512.size);
            if (keyPair == null) return;
            mCurrentSigner = new JwtSigner(keyPair, JwtSigner.Type.RSA512);

            mAllReadSigners = Stream.concat(IntStream.range(mAllReadSigners.length >= K_ALL_READ_SIGNER_MAXSIZE ? 1 : 0,
                            Math.min(mAllReadSigners.length, K_ALL_READ_SIGNER_MAXSIZE))
                    .mapToObj(s -> mAllReadSigners[s]), Stream.of(mCurrentSigner)).toArray(CertificateSigner[] :: new);
        }

        @Override public CertificateSigner getWriteSigner() { return mCurrentSigner; }
        @Override public CertificateSigner[] getReadSigners() { return mAllReadSigners; }
        @Override public Duration getExpirationPeriod() { return Duration.ofSeconds(mExpirationTime); }
    }

    @Bean(name= SignerNameConstant.authentication)
    public static SignerProvider getAuthenticateSignerProvider() { return new ConcreteSignerProvider(); }

    @Autowired
    @Qualifier(SignerNameConstant.authentication)
    private SignerProvider mAuthenticateSignerProvider;

    @Scheduled(fixedRate = 1000 * 3600 * 4, initialDelay = 1000 * 3600 * 4)
    private void roundRobinAuthentication() { ((ConcreteSignerProvider) mAuthenticateSignerProvider).generateKey(); }
}
