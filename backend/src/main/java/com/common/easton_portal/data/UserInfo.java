package com.common.easton_portal.data;

import com.common.easton_portal.entity.UserEntity;
import com.common.easton_portal.model.UserModel;
import com.fasterxml.jackson.annotation.JsonProperty;


public class UserInfo {
    public static class Base {
        @JsonProperty public long id;
        @JsonProperty public long domainId;
        @JsonProperty public String accountName;
        @JsonProperty public String username;
        @JsonProperty public String email;

        public Base() {}
        public Base(UserEntity entity) {
            this.id = entity.getId();
            this.domainId = entity.getDomain().getId();
            this.accountName = entity.getAccountName();
            this.username = entity.getUsername();
            this.email = entity.getEmail();
        }
    }

    public static class Data extends Base {
        @JsonProperty boolean disable;
        @JsonProperty boolean administrator;
        @JsonProperty RoleInfo.Base[] roles;

        public Data() {}
        public Data(UserEntity entity) {
            super(entity);
            disable = entity.isDisable();
            administrator = entity.isAdministrator();
            roles = entity.getRoles().stream().map(RoleInfo.Base::new).toArray(RoleInfo.Base[]::new);
        }
    }

    public static class Profile {
        @JsonProperty public long uuid;
        @JsonProperty public String username;

        public Profile() {}
        public Profile(UserModel model) {
            this.username = model.getIdentity().getName();
            this.uuid = model.getUuid();
        }
    }
}
