package com.common.easton_portal.misc;

public enum NotificationType {
    QuotationRequest("Quotation Request", 1),
    QuotationReply("Quotation Reply", 2);

    public final String mode;
    public final long id;

    NotificationType(String mode, long id) {
        this.mode = mode;
        this.id = id;
    }
}
