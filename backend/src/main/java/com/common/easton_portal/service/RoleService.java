package com.common.easton_portal.service;

import com.common.core.web.permission.PermissionSystem;
import com.common.core.web.permission.RightAnnotation;
import com.common.easton_portal.data.RoleInfo;
import com.common.easton_portal.entity.RoleEntity;
import com.common.easton_portal.entity.UserEntity;
import com.common.easton_portal.repos.RoleRepository;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RoleService {
    @Autowired private PermissionSystem<RightAnnotation> mPermissionSystem;
    @Autowired private RoleRepository mRepository;

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void create(String name) throws Exception {
        if (mRepository.findByName(name).isPresent()) throw new Exception("Name is already exist");

        mRepository.save(RoleEntity.builder().name(name).build());
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public RoleInfo.Base remove(long id) throws Exception {
        var entity = mRepository.findById(id).orElseThrow(() -> new Exception("The role does not exist"));

        var usedList = entity.getUsers().stream().map(UserEntity::getNameRepresent).toList();
        if (!usedList.isEmpty()) {
            throw new Exception(String.format("User [%s] is used for this role", String.join(",", usedList)));
        }
        mRepository.delete(entity);

        return new RoleInfo.Base(entity);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public RoleInfo.Data[] list() throws Exception {
        return mRepository.findAll().stream().map(RoleInfo.Data::new).toArray(RoleInfo.Data[]::new);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public RoleInfo.Data get(long id) throws Exception {
        var entity = mRepository.findById(id).orElseThrow(() -> new Exception("Role does not exist"));
        return new RoleInfo.Data(entity);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void rename(long id, String name) throws Exception {
        var entity = mRepository.findById(id).orElseThrow(() -> new Exception("Role does not exist"));
        var nameEntity = mRepository.findByName(name).orElse(null);
        if (nameEntity != null && !Objects.equals(nameEntity.getId(), entity.getId())) throw new Exception("The role does not exist");
        entity.setName(name);
        mRepository.saveAndFlush(entity);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateRights(long id, String[] rights) throws Exception {
        var entity = mRepository.findById(id).orElseThrow(() -> new Exception("Role does not exist"));
        var filterRights = Arrays.stream(rights).filter(s -> mPermissionSystem.getStream().anyMatch(t -> t.databaseKey.equals(s))).collect(Collectors.toSet());
        entity.getRights().clear();
        entity.getRights().addAll(filterRights);
        mRepository.saveAndFlush(entity);
    }
}
