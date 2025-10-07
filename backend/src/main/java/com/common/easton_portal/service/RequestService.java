package com.common.easton_portal.service;

import com.common.core.base.helper.StringHelper;
import com.common.easton_portal.data.RequestInfo;
import com.common.easton_portal.entity.RequestEntity;
import com.common.easton_portal.enumerate.EmailType;
import com.common.easton_portal.misc.EmailNotification;
import com.common.easton_portal.repos.RequestRepository;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class RequestService {
    @Autowired private RequestRepository mRequestRepository;
    @Autowired private EmailNotification mEmail;

    @Value("${app.email.sales}")
    private String mMailTo;

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void create(long customerId, String customerName, String customerEmail, String collectionName, String remarks) throws Exception {
        if (customerId < 0) throw new Exception("Invalid user ID");
        if (StringHelper.isNullOrEmpty(customerEmail) ||
            StringHelper.isNullOrEmpty(collectionName))
                throw new Exception("Must provide email / collection");

        var currentTime = System.currentTimeMillis();

        if (mRequestRepository.findByCollectionNameAndCustomerId(collectionName, customerId) != null)
            throw new Exception("Request already exist");

        var entity = RequestEntity.builder().customerId(customerId).customerName(customerName).customerEmail(customerEmail)
                .collectionName(collectionName).remarks(remarks).createdDate(currentTime).build();

        mRequestRepository.saveAndFlush(entity);

        mEmail.sendEmailNotificationAsync(mMailTo, new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                customerEmail, customerName, null, EmailType.request);
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<RequestInfo.Base> getUntreatedRequest() {
        var entities = mRequestRepository.findAll().stream().filter(s -> s.getQuotation() == null).toList();
        return entities.stream().map(RequestInfo.Base::new).toList();
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(readOnly = true, isolation = Isolation.READ_COMMITTED)
    public List<RequestInfo.Data> getTreatedRequest(long id) {
        var test = mRequestRepository.findByCustomerId(id);
        var entities = mRequestRepository.findByCustomerId(id).stream().filter(s -> s.getQuotation() != null).toList();
        return entities.stream().map(RequestInfo.Data::new).toList();
    }

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void remove(long id) throws Exception {
        var entity = mRequestRepository.findById(id).orElseThrow(() -> new Exception("The role does not exist"));
        mRequestRepository.delete(entity);
    }
}
