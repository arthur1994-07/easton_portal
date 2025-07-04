package com.common.easton_portal.service;

import com.common.easton_portal.data.OAuthDomainInfo;
import com.common.easton_portal.repos.OAuthRepository;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;

@Service
public class OAuthService {
    @Autowired private OAuthRepository mOAuthRepository;

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public OAuthDomainInfo.Viewable[] getOAuthDomains() {
        return mOAuthRepository.findAll().stream().map(OAuthDomainInfo.Viewable::new)
                .sorted(Comparator.comparing(s -> s.name)).toArray(OAuthDomainInfo.Viewable[]::new);
    }
}
