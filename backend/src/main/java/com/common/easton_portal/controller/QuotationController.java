package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.QuotationInfo;
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
        public String assigneeEmail;
        public String assigneeName;
        public String base64Profile;
        public long requestId;
    }

    @Autowired private QuotationService mQuotationService;

    @Operation(summary = "Create a quotation", description = "Create a quotation")
    @PostMapping(value = "/create")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_QUOTATION + "')")
    public ResponseEntity<JsonRespond<Void>> create(@RequestBody CreateRequest request) throws Throwable {
        if (request.quoteNum == null) throw new Exception("quotation number missing");
        if (request.base64Profile == null) throw new Exception("file missing");
        if (request.requestId < 0) throw new Exception("Invalid request ID");
        if (request.assigneeEmail == null || request.assigneeName == null) throw new Exception("assignee missing");


        mQuotationService.create(request.quoteNum, request.assigneeEmail, request.assigneeName,
                request.base64Profile, request.requestId);

        return ResponseEntity.ok(new JsonRespond<>(null));
    }
}
