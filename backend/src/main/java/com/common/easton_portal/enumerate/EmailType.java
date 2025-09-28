package com.common.easton_portal.enumerate;

public enum EmailType {
    request("quotation-request-email", 0),
    reply("quotation-reply-email", 1);

    public final String path;
    public final int index;

    EmailType(String path, int index) {
        this.path = path;
        this.index = index;
    }
}
