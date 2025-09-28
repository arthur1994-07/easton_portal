package com.common.easton_portal.data;
import com.common.core.web.utility.LobHelper;
import com.common.easton_portal.entity.QuotationEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Blob;

public class QuotationInfo {
    public static class Base {
        @JsonProperty public long id;
        @JsonProperty public String quoteNum;
        @JsonProperty public String assignee;
        @JsonProperty public byte[] document;

        public Base(long id, String quoteNum, String assignee, Blob document) {
            this.id = id;
            this.quoteNum = quoteNum;
            this.assignee = assignee;
            this.document = LobHelper.getBytes(document);
        }

        public Base(QuotationEntity entity) {
            this.id = entity.getId();
            this.quoteNum = entity.getQuoteNum();
            this.assignee = entity.getAssigneeEmail();
            this.document = LobHelper.getBytes(entity.getDocument());
        }
    }
}
