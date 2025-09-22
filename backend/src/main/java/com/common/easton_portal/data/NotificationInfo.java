package com.common.easton_portal.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class NotificationInfo {
    public static class Base {
        @JsonProperty public long id;
        @JsonProperty public JsonNode info;
        @JsonProperty public String requester;
        @JsonProperty public String assignee;

        public Base() {}
        public Base(JsonNode info, long id, String requester, String assignee) {
            this.info = info;
            this.id = id;
            this.requester = requester;
            this.assignee = assignee;
        }
    }

}
