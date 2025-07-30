package com.common.easton_portal.service;

import com.common.core.base.helper.StringHelper;
import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.controller.OAuthController;
import com.common.easton_portal.core.EncryptedProvider;
import com.common.easton_portal.data.OAuthDomainInfo;
import com.common.easton_portal.entity.OAuthEntity;
import com.common.easton_portal.repos.OAuthRepository;
import io.swagger.v3.oas.annotations.Operation;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;

@Service
public class OAuthService {
    @Autowired private OAuthRepository mOAuthRepository;
    @Autowired private EncryptedProvider mEncryptor;

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public OAuthDomainInfo.Viewable[] getOAuthDomains() {
        return mOAuthRepository.findAll().stream().map(OAuthDomainInfo.Viewable::new)
                .sorted(Comparator.comparing(s -> s.name)).toArray(OAuthDomainInfo.Viewable[]::new);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void addDomain(OAuthDomainInfo.Input input) throws Exception {
        if (StringHelper.isNullOrEmpty(input.clientSecret)) throw new Exception("Client Secret cannot be empty");
        mOAuthRepository.saveAndFlush(OAuthEntity.builder()
                .name(input.name)
                .clientId(input.clientId)
                .clientSecret(mEncryptor.encryptToBase64(input.clientSecret))
                .authUrl(input.authUrl)
                .tokenUrl(input.tokenUrl)
                .logoutUrl(input.logoutUrl)
                .build());
    }

    public OAuthDomainInfo.Details getOAuthDetails(long id) throws Exception {
        var entity = mOAuthRepository.findById(id).orElseThrow(() -> new Exception("OAuth domain not supported"));
        return new OAuthDomainInfo.Details(entity);
    }
}
