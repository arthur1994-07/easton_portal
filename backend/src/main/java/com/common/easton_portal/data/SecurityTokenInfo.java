package com.common.easton_portal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecurityTokenInfo {
    @JsonProperty
    private String accessToken;
    @JsonProperty private long expiration;
    @JsonProperty private String idToken;


    public SecurityTokenInfo() {}
    public SecurityTokenInfo(String accessToken, long expiration, String idToken) {
        this.accessToken = accessToken;
        this.expiration = expiration;
        this.idToken = idToken;
    }

    public SecurityTokenInfo(String accessToken, long expiration) {
        this(accessToken, expiration, null);
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public long getExpiration() { return expiration; }
    public void setExpiration(long expiration) { this.expiration = expiration;}

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken;}
}
