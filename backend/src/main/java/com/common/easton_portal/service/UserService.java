package com.common.easton_portal.service;

import com.common.easton_portal.data.UserInfo;
import com.common.easton_portal.repos.OAuthRepository;
import com.common.easton_portal.repos.RoleRepository;
import com.common.easton_portal.repos.UserRepository;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;

@Service
public class UserService {
    @Autowired private UserRepository mUserRepository;
    @Autowired private OAuthRepository mOAuthRepository;
    @Autowired private RoleRepository mRoleRepository;

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public UserInfo.Data get(long id) throws Exception {
        var entity = mUserRepository.findById(id).orElseThrow(() -> new Exception("userId not found"));
        return new UserInfo.Data(entity);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public UserInfo.Data[] list() {
        return mUserRepository.findAll().stream().map(UserInfo.Data::new).toArray(UserInfo.Data[]::new);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public UserInfo.Data[] list(long domainId) throws Exception {
        var oauthEntity = mOAuthRepository.findById(domainId).orElseThrow(() -> new Exception("OAuth service is not found"));
        return mUserRepository.findByDomain(oauthEntity).stream().map(UserInfo.Data::new).toArray(UserInfo.Data[]::new);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserInfo.Base remove(long id) throws Exception {
        var entity = mUserRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        mUserRepository.delete(entity);
        return new UserInfo.Base(entity);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserInfo.Base changeActive(long id, boolean active) throws Exception {
        var entity = mUserRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        entity.setDisable(!active);
        mUserRepository.saveAndFlush(entity);
        return new UserInfo.Base(entity);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void changeRole(long id, boolean administrator, long[] roles) throws Exception {
        var entity = mUserRepository.findById(id).orElseThrow(() -> new Exception("User not found"));
        entity.setAdministrator(administrator);
        var removeList = entity.getRoles().stream()
                .filter(s -> Arrays.stream(roles).noneMatch(t -> t == s.getId())).toList();
        var addList = Arrays.stream(roles)
                .filter(s -> entity.getRoles().stream().noneMatch(t -> t.getId() == s))
                .boxed().map(s -> mRoleRepository.findById(s).orElse(null)).filter(Objects::nonNull).toList();

        var rolesEntity = entity.getRoles();
        removeList.forEach(rolesEntity::remove);
        rolesEntity.addAll(addList);
        mUserRepository.saveAndFlush(entity);
    }
}
