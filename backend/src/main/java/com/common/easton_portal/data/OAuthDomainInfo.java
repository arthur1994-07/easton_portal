package com.common.easton_portal.data;

import com.common.easton_portal.entity.OAuthEntity;
import com.common.easton_portal.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class OAuthDomainInfo {
    public static class Base {
        @JsonProperty
        public long id;
        @JsonProperty public String name;

        public Base() {};
        public Base(long id, String name) {
            this.id = id;
            this.name = name;
        }
        public Base(OAuthEntity entity) {
            this.id = entity.getId();
            this.name = entity.getName();
        }
    }

    public static class Viewable extends Base {
        @JsonProperty public boolean disable;
        @JsonProperty public boolean defaultAdministrator;
        @JsonProperty public long[] defaultRoles;

        public Viewable() {}
        public Viewable(OAuthEntity entity) {
            super(entity);
            this.disable = entity.isDisable();
            this.defaultAdministrator = entity.isDefaultAdministrator();
            this.defaultRoles = entity.getDefaultRoles().stream().mapToLong(RoleEntity::getId).toArray();
        }
    }

    public static class Details extends Viewable {
        @JsonProperty public String clientId;
        @JsonProperty public String authUrl;
        @JsonProperty public String tokenUrl;
        @JsonProperty public String logoutUrl;

        public Details() {}
        public Details(OAuthEntity entity) throws Exception {
            super(entity);
            this.clientId = entity.getClientId();
            this.authUrl = entity.getAuthUrl();
            this.tokenUrl = entity.getTokenUrl();
            this.logoutUrl = entity.getLogoutUrl();
        }
    }

    public static class Input {
        @JsonProperty @NotBlank
        public String name;
        @JsonProperty @NotBlank public String clientId;
        @JsonProperty @NotBlank public String authUrl;
        @JsonProperty @NotBlank public String tokenUrl;
        @JsonProperty public String clientSecret;
        @JsonProperty public String logoutUrl;
        @JsonProperty public boolean defaultAdministrator;
        @JsonProperty public long[] defaultRoles;
    }
}
