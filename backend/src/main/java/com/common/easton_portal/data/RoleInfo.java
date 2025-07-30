package com.common.easton_portal.data;

import com.common.easton_portal.entity.RoleEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleInfo {
    public static class Base {
        @JsonProperty public long id;
        @JsonProperty public String name;

        public Base() {}
        public Base(long id, String name) { this.id = id; this.name = name; }
        public Base(RoleEntity entity) {
            this.id = entity.getId();
            this.name = entity.getName();
        }
    }
}
