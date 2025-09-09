package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.WebInfo;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/web")
public class WebController {
    @Value("${custom.easton_url}")
    private String mEastonUrl;

    @PostMapping(value = "/get-info")
    public ResponseEntity<JsonRespond<WebInfo>> getInfo() {
        var info = new WebInfo(mEastonUrl);
        return ResponseEntity.ok(new JsonRespond<>(info));
    }
}
