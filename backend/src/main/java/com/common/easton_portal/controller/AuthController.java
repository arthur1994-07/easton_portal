package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.data.SecurityTokenInfo;
import com.common.easton_portal.model.UserModel;
import com.common.easton_portal.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {
    public static class OAuthAuthenticationRequest {
        public long id;
        public String code;
        public String redirectUri;
    }

    @Autowired private AuthService mService;


    @Operation(summary = "Generate a new refresh token",
            description = "The api allow to re-geenerate a new token for a new expiration",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/refresh-token")
    public ResponseEntity<JsonRespond<SecurityTokenInfo>> refreshToken() throws Throwable {
        var authUser = UserModel.getCurrent();
        if (authUser == null) throw new Error("Token is expired");
        return ResponseEntity.ok(new JsonRespond<>(mService.refreshToken(authUser)));
    }


    @Operation(summary = "Request oauth login via authorization code flow",
            description = "Request to login for given oauth id and its oauth content")
    @PostMapping("/public/oAuth/authenticate")
    public ResponseEntity<JsonRespond<SecurityTokenInfo>> oAuthAuthentication(@RequestBody OAuthAuthenticationRequest request) throws Throwable {
        var token = mService.authenticateWithOAuth(request.id, request.code, request.redirectUri);
        return ResponseEntity.ok(new JsonRespond<>(token));
    }
}
