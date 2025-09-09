package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.core.web.utility.LobHelper;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.QuotationInfo;
import com.common.easton_portal.data.RequestInfo;
import com.common.easton_portal.service.QuotationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/quotation")
public class QuotationController {
    public static class IDRequest {
        public long id;
    }
    public static class CreateRequest {
        public String quoteNum;
        public String assignee;
        public String base64Profile;
        public long requestId;
    }

    @Autowired private QuotationService mQuotationService;

    @Operation(summary = "Create a quotation", description = "Create a quotation")
    @PostMapping(value = "/create")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> create(@RequestBody CreateRequest request) throws Throwable {
        if (request.quoteNum == null) throw new Exception("quotation number missing");
        if (request.assignee == null) throw new Exception("assignee missing");
        if (request.base64Profile == null) throw new Exception("file missing");
        if (request.requestId < 0) throw new Exception("Invalid request ID");

        mQuotationService.create(request.quoteNum, request.assignee, request.base64Profile, request.requestId);

        return ResponseEntity.ok(new JsonRespond<>(null));
    }

    @Operation(summary = "list quotation request", description = "list quotation request")
    @PostMapping(value = "/list")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<List<QuotationInfo.Base>>> list() throws Exception {
        return ResponseEntity.ok(new JsonRespond<>(mQuotationService.list()));
    }

}
