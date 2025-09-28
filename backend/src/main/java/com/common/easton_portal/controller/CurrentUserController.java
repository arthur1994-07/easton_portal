package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.data.UserInfo;
import com.common.easton_portal.model.UserModel;
import com.common.easton_portal.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/current-user")
public class CurrentUserController {

    @Autowired private OAuthService mOAuthService;


    @Operation(summary = "Get current user rights", description = "Get Current user right set")
    @PostMapping(value = "/permission")
    public ResponseEntity<JsonRespond<String[]>> permission() throws Throwable {
        var current = UserModel.getCurrent();
        if (current == null) throw new Exception("Cannot get the auth details");
        return ResponseEntity.ok(new JsonRespond<>(current.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new)));
    }

    @Operation(summary = "Get logout url", description = "Get logout url")
    @PostMapping(value = "/logout-url")
    public ResponseEntity<JsonRespond<String>> logoutUrl() throws Throwable {
        var current = UserModel.getCurrent();
        if (current == null) throw new Exception("Cannot get the auth details");

        String url = null;
        var id = current.getIdentity().getOauthId();
        var detail = id == 0 ? null : mOAuthService.getOAuthDetails(id);
        url = detail == null ? null : detail.logoutUrl;
        return ResponseEntity.ok(new JsonRespond<>(url));
    }


    @Operation(summary = "Get current user", description = "Get current user")
    @PostMapping(value = "/get-current")
    public ResponseEntity<JsonRespond<UserInfo.Profile>> getProfile() throws Throwable {
        var current = UserModel.getCurrent();
        if (current == null) throw new Exception("current user not found");

        return ResponseEntity.ok(new JsonRespond<>(new UserInfo.Profile(current)));
    }
}
