package com.common.easton_portal.controller;

import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.UserInfo;
import com.common.easton_portal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/user")
public class UserController {
    public static class ListRequest {
        public Long domainId;
    }

    public static class IDRequest {
        public long id;
    }

    public static class UserActiveRequest extends IDRequest {
        public boolean active;
    }

    public static class ChangeUserRolesRequest extends IDRequest {
        public boolean administrator;
        public long[] roles;
    }

    @Autowired private UserService mService;

    @Operation(summary = "List all users", description = "This Api lists out all users in the system")
    @PostMapping(value = "/list")
    public ResponseEntity<JsonRespond<UserInfo.Data[]>> list(@RequestBody ListRequest request) throws Throwable {
        return ResponseEntity.ok(new JsonRespond<>(request.domainId == null ? mService.list() : mService.list(request.domainId)));
    }

    @Operation(summary = "Remove a user", description = "Remove the user for given id")
    @PostMapping(value = "/remove")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> remove(@RequestBody IDRequest request) throws Throwable {
        var user = mService.remove(request.id);

//        mAuditLog.log(CatalogID.K_ID_USER_REMOVE, StringHelper.format("User name with '{0}' is removed", user.username));
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

    @Operation(summary = "Change the active state of a user", description = "Disable/Active a user for given id for login")
    @PostMapping(value = "/change-active")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> changeActive(@RequestBody UserActiveRequest request) throws Throwable {
        var user = mService.changeActive(request.id, request.active);
//        mAuditLog.log(CatalogID.K_ID_USER_UPDATE,
//                StringHelper.format("User name with '{0}' is '{1}'", user.username, request.active ? "activated" : "disabled"));
        return ResponseEntity.ok(new JsonRespond<>(null));
    }
    @Operation(summary = "Change role for user", description = "Change role group for given user id")
    @PostMapping(value = "/change-role")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> changeRole(@RequestBody ChangeUserRolesRequest request) throws Throwable {
        mService.changeRole(request.id, request.administrator, request.roles);
//        mAuditLog.log(CatalogID.K_ID_USER_UPDATE, "User role is updated");
        return ResponseEntity.ok(new JsonRespond<>(null));
    }
}
