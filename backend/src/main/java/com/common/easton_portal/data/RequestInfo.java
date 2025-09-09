package com.common.easton_portal.data;

import com.common.easton_portal.entity.QuotationEntity;
import com.common.easton_portal.entity.RequestEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestInfo {
    public static class Base {
        @JsonProperty public long id;
        @JsonProperty public long customerId;
        @JsonProperty public String customerName;
        @JsonProperty public String customerEmail;
        @JsonProperty public String remark;
        @JsonProperty public long createdDate;
        @JsonProperty public String collectionName;

        public Base(long id, long customerId, String customerName, String customerEmail, String remark, long createdDate, String collectionName) {
            this.id = id;
            this.customerId = customerId;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.remark = remark;
            this.createdDate = createdDate;
            this.collectionName = collectionName;
        }

        public Base(RequestEntity entity) {
            this.id = entity.getId();
            this.customerId = entity.getCustomerId();
            this.customerName = entity.getCustomerName();
            this.customerEmail = entity.getCustomerEmail();
            this.remark = entity.getRemarks();
            this.createdDate = entity.getCreatedDate();
            this.collectionName = entity.getCollectionName();
        }
    }

    public static class Data extends Base {
        @JsonProperty public QuotationInfo.Base quotation;

        public Data(RequestEntity entity) {
            super(entity);
            this.quotation = new QuotationInfo.Base(entity.getQuotation());
        }
    }
}
