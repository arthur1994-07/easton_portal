package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.data.OAuthDomainInfo;
import com.common.easton_portal.service.OAuthService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/oAuth")
public class OAuthController {
    public static class IDRequest {
        public long id;
    }

    @Autowired private OAuthService mService;

    @Operation(summary = "Get all active domain list", description = "Get all active open authentication domains list")
    @PostMapping("/public/domains")
    public ResponseEntity<JsonRespond<OAuthDomainInfo.Base[]>> getDomains() throws Throwable {
        var allow  = Arrays.stream(mService.getOAuthDomains()).filter(s ->
                !s.disable).toArray(OAuthDomainInfo.Base[]::new);
        return ResponseEntity.ok(new JsonRespond<>(allow));
    }
}
