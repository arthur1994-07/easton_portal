package com.common.easton_portal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebInfo {
    @JsonProperty public String url;

    public WebInfo(String url) { this.url = url; }
}
