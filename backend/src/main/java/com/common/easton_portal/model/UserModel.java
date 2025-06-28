package com.common.easton_portal.model;

import com.common.core.base.helper.JsonHelper;
import com.common.core.web.security.base.ClaimsInterface;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class UserModel {
    private static final long K_ANONYMOUS_USER_ID = 0;
    public static final String K_ANONYMOUS_USERNAME = "Anonymous User";

    public static class Identity implements ClaimsInterface {
        @JsonProperty private long oauthId;
        @JsonProperty private String idToken;
        @JsonProperty private String name;

        public long getOauthId() { return oauthId; }
        public void setOauthId(long id) { oauthId = id; }

        public String getIdToken() { return idToken; }
        public void setIdToken(String token) { idToken = token; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }


        @Override public String key() { return "identity"; }
        @Override public String pack() {
            return JsonHelper.asString(JsonHelper.toTree(this));
        }
        @Override public void unpack(String content) {
            var data = JsonHelper.parse(JsonHelper.fromString(content), Identity.class);
            if (data == null) return;
            this.oauthId = data.oauthId;
            this.idToken = data.idToken;
            this.name = data.name;
        }
    }

    public static class Authorities implements ClaimsInterface {
        private final List<GrantedAuthority> mAuthorities = new ArrayList<>();

        @Override public String key() { return "authorities"; }
        @Override public String pack() {
            var list = mAuthorities.stream().map(GrantedAuthority::getAuthority).toList();
            return JsonHelper.asString(JsonHelper.toTree(list));
        }
        @Override public void unpack(String content) {
            mAuthorities.clear();
            var data = JsonHelper.parse(JsonHelper.fromString(content), String[].class);
            if (data == null) return;
            Arrays.stream(data).forEach(s -> mAuthorities.add(new SimpleGrantedAuthority(s)));
        }

        public List<GrantedAuthority> getAuthorities() { return mAuthorities; }
        public void set(List<GrantedAuthority> newGranted) {
            mAuthorities.clear();
            mAuthorities.addAll(newGranted);
        }
    }

    private final long mUserId;
    private final Authorities mAuthorities = new Authorities();
    private final Identity mIdentity = new Identity();

    public UserModel(long userId, Consumer<ClaimsInterface> claimsApply) {
        mUserId = userId;
        Arrays.stream(getClaims()).forEach(claimsApply);
    }

    public long getUuid() { return mUserId;}
    public ClaimsInterface[] getClaims() { return new ClaimsInterface[] { mAuthorities, mIdentity };}
    public Collection<GrantedAuthority> getAuthorities() { return mAuthorities.getAuthorities();}
    public boolean isAnonymous() { return mUserId == K_ANONYMOUS_USER_ID; }
    public String getIdToken() { return mIdentity.getIdToken(); }

    public Identity getIdentity() { return mIdentity; }
}
