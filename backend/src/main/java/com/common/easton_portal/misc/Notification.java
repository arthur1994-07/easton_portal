package com.common.easton_portal.misc;

import com.common.core.base.helper.JsonHelper;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.NotificationInfo;
import com.common.easton_portal.model.UserModel;
import com.common.easton_portal.repos.QuotationRepository;
import com.common.easton_portal.repos.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class Notification {

    // check if request entity is empty, if not then generate notification.
    // generate notification for:
    // unhandled quotation request to the admin side and handled quotation request to requester side

    @Autowired private RequestRepository mRequestRepository;
    @Autowired private QuotationRepository mQuotationRepository;


    public List<NotificationInfo.Base> getData() throws Exception {
        var result = new ArrayList<NotificationInfo.Base>();
        try {
            result.addAll(getQuotationReply());
            result.addAll(getQuotationRequestNotification());
            return result;
        } catch (Exception ignored){}
        return result;
    }

    private List<NotificationInfo.Base> getQuotationRequestNotification() throws Exception {
        var currentUser = UserModel.getCurrent();
        if (currentUser == null) throw new Exception("Invalid current user");

        var quotationRight = currentUser.getAuthorities().stream()
                .anyMatch(s -> s.getAuthority().equals(PermissionConstant.EDIT_QUOTATION));

        // unhandled quotation request, if request does not have quotation --> unhandled request
        if (!quotationRight) return null;

        var requests = mRequestRepository.findByCustomerId(currentUser.getUuid()).stream()
                .filter(s -> s.getQuotation() == null).toList();

        if (requests == null) return null;
        return requests.stream().map(s ->
                new NotificationInfo.Base(JsonHelper.toTree(s.getCollectionName()),
                        NotificationType.QuotationRequest.id, s.getCustomerName(), null)).toList();
    }

    private List<NotificationInfo.Base> getQuotationReply() throws Exception {
        var currentUser = UserModel.getCurrent();
        if (currentUser == null) throw new Exception("Invalid current user");

        var requests = mRequestRepository.findByCustomerId(currentUser.getUuid()).stream()
                .filter(s -> s.getQuotation() != null).toList();

        return requests.stream().map(s ->
            new NotificationInfo.Base(JsonHelper.toTree(s.getCollectionName()),
                    NotificationType.QuotationReply.id, null, s.getQuotation().getAssigneeEmail())).toList();
    }
}
