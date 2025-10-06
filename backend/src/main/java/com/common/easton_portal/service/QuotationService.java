package com.common.easton_portal.service;

import com.common.core.web.utility.LobHelper;
import com.common.easton_portal.data.QuotationInfo;
import com.common.easton_portal.entity.QuotationEntity;
import com.common.easton_portal.enumerate.EmailType;
import com.common.easton_portal.misc.EmailNotification;
import com.common.easton_portal.repos.QuotationRepository;
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
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
public class QuotationService {

    @Autowired private QuotationRepository mQuotationRepository;
    @Autowired private RequestRepository mRequestRepository;
    @Autowired private EmailNotification mEmail;

    @Value("${app.email.from}")
    private String mMailTo;

    @Retryable(value = {LockAcquisitionException.class }, maxAttemptsExpression = "${retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${retry.maxDelay}"))
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void create(String quoteNum, String assigneeEmail, String assigneeName,
                       String base64Profile, long requestId) throws Exception {
        if (requestId < 0) throw new Exception("invalid request id");
        var requestEntity = mRequestRepository.findById(requestId).orElseThrow(() -> new Exception("Request does not exist"));
        var data = Base64.getDecoder().decode(base64Profile);

        var quotationEntity = QuotationEntity.builder().quoteNum(quoteNum).assigneeEmail(assigneeEmail).document(LobHelper.toLob(data))
                .build();
        requestEntity.setQuotation(quotationEntity);

        mEmail.sendEmailNotification(requestEntity.getCustomerEmail(), new SimpleDateFormat("yyyy-MM-dd").format(new Date()),
                assigneeEmail, assigneeName, requestEntity.getCustomerName(), EmailType.reply);
        
        mQuotationRepository.saveAndFlush(quotationEntity);
    }

}
