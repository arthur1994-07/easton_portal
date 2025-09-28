package com.common.easton_portal.controller;

import com.common.core.base.helper.StringHelper;
import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.RequestInfo;
import com.common.easton_portal.service.RequestService;
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
@RequestMapping("/request")
public class RequestController {
    public static class IDRequest {
        public long id;
    }
    public static class CreateRequest extends IDRequest {
        public long customerId;
        public String customerName;
        public String customerEmail;
        public String remarks;
        public String collectionName;
    }

    @Autowired private RequestService mRequestService;

    @Operation(summary = "Create a quotation request", description = "Create a quotation request")
    @PostMapping(value = "/create")
    public ResponseEntity<JsonRespond<Void>> create(@RequestBody CreateRequest request) throws Throwable {
        if (request.customerId < 0) throw new Exception("Invalid customer ID");
        if (StringHelper.isNullOrEmpty(request.collectionName) ||
                StringHelper.isNullOrEmpty(request.customerEmail))
            throw new Exception("Cannot request quotation: collection / email missing");

        mRequestService.create(request.customerId, request.customerName, request.customerEmail,
                request.collectionName, request.remarks);
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

    @Operation(summary = "get un-responded quotation request", description = "")
    @PostMapping(value = "/get-untreated-request")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_QUOTATION + "')")
    public ResponseEntity<JsonRespond<List<RequestInfo.Base>>> getUntreatedRequest() throws Throwable {
        return ResponseEntity.ok(new JsonRespond<>(mRequestService.getUntreatedRequest()));
    }

    @Operation(summary = "get responded quotation request", description = "")
    @PostMapping(value = "/get-treated-request")
    public ResponseEntity<JsonRespond<List<RequestInfo.Data>>> getTreatedRequest(@RequestBody IDRequest request) throws Throwable {
        return ResponseEntity.ok(new JsonRespond<>(mRequestService.getTreatedRequest(request.id)));
    }

    @Operation(summary = "Remove request", description = "Remove a request for a given id")
    @PostMapping(value = "/delete")
    public ResponseEntity<JsonRespond<Void>> remove(@RequestBody IDRequest request) throws Exception {
        mRequestService.remove(request.id);
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

}
