package com.common.easton_portal.controller;

import com.common.core.base.helper.StringHelper;
import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.data.OAuthDomainInfo;
import com.common.easton_portal.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/oAuth")
public class OAuthController {
    public static class IDRequest {
        public long id;
    }

    public static class AuthInfoRespond {
        public String clientId;
        public String authUrl;

        public AuthInfoRespond() {}
        public AuthInfoRespond(String clientId, String authUrl) {
            this.clientId = clientId;
            this.authUrl = authUrl;
        }
    }

    @Autowired private OAuthService mService;


    @Operation(summary = "Add oauth domain", description = "add a new open authentication domain", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/public/add")
    public ResponseEntity<JsonRespond<Void>> addOAuthDomain(@RequestBody @Valid OAuthDomainInfo.Input request) throws Throwable {
        mService.addDomain(request);
        return ResponseEntity.ok(new JsonRespond<>(null));
    }


    @Operation(summary = "Get all active domain list", description = "Get all active open authentication domains list")
    @PostMapping("/public/domains")
    public ResponseEntity<JsonRespond<OAuthDomainInfo.Base[]>> getDomains() throws Throwable {
        var domains = mService.getOAuthDomains();
        var allow  = Arrays.stream(domains).filter(s ->
                !s.disable).toArray(OAuthDomainInfo.Base[]::new);
        return ResponseEntity.ok(new JsonRespond<>(allow));
    }

    @Operation(summary = "Get oauth details", description = "Get open authentication domain details for given id")
    @PostMapping("/public/auth-info")
    public ResponseEntity<JsonRespond<AuthInfoRespond>> getAuthInfo(@RequestBody IDRequest request) throws Throwable {
        var info = mService.getOAuthDetails(request.id);
        return ResponseEntity.ok(new JsonRespond<>(new AuthInfoRespond(info.clientId, info.authUrl)));
    }
}
