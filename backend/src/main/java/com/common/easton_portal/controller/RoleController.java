package com.common.easton_portal.controller;

import com.common.core.web.permission.PermissionSystem;
import com.common.core.web.permission.RightAnnotation;
import com.common.core.web.struct.JsonRespond;
import com.common.easton_portal.constants.PermissionConstant;
import com.common.easton_portal.data.RoleInfo;
import com.common.easton_portal.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/role")
public class RoleController {
    public static class CreateRequest {
        public String name;
    }

    public static class IDRequest {
        public long id;
    }

    public static class RenameRequest extends IDRequest {
        public String name;
    }

    public static class UpdateRightRequest extends IDRequest {
        public String[] rights;
    }

    @Autowired private PermissionSystem<RightAnnotation> mPermissionSystem;
    @Autowired private RoleService mService;

    @Operation(summary = "Add role", description = "Create a role for contains list of right")
    @PostMapping(value = "/add")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> create(@RequestBody CreateRequest request) throws Exception {
        mService.create(request.name);
//        mAuditLog.log(CatalogID.K_ID_ROLE_CHANGE,
//                StringHelper.format("Role name with '{0}' is added", request.name));
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

    @Operation(summary = "Remove role", description = "Remove a role for a given id")
    @PostMapping(value = "/delete")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> remove(@RequestBody IDRequest request) throws Exception {
        var role = mService.remove(request.id);

//        mAuditLog.log(CatalogID.K_ID_ROLE_REMOVE,
//                StringHelper.format("Role name with '{0}' is removed", role.name));
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

    @Operation(summary = "Get roles", description = "Get all roles in the system")
    @PostMapping(value = "/list")
    public ResponseEntity<JsonRespond<RoleInfo.Data[]>> getRoles() throws Exception {
        return ResponseEntity.ok(new JsonRespond<>(mService.list()));
    }

    @Operation(summary = "Get role information", description = "Get role information for given id")
    @PostMapping(value = "/get")
    public ResponseEntity<JsonRespond<RoleInfo.Data>> get(@RequestBody IDRequest request) throws Exception {
        return ResponseEntity.ok(new JsonRespond<>(mService.get(request.id)));
    }

    @Operation(summary = "Rename role", description = "Get role information for given id")
    @PostMapping(value = "/rename")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> rename(@RequestBody RenameRequest request) throws Exception {
        if (request.name == null || request.name.isEmpty()) throw new Exception("Invalid parameter for name");
        var role = mService.get(request.id);
        mService.rename(request.id, request.name);

//        mAuditLog.log(CatalogID.K_ID_ROLE_UPDATE,
//                StringHelper.format("Role name with '{0}' is renamed to '{1}'", role.name, request.name));
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

    @Operation(summary = "Get rights", description = "Get right permission in the system and used for assign to role")
    @PostMapping(value = "/right/list")
    public ResponseEntity<JsonRespond<RoleInfo.Right[]>> rightList() throws Exception {
        var rights= mPermissionSystem.getStream().map(RoleInfo.Right::new).toArray(RoleInfo.Right[]::new);
        return ResponseEntity.ok(new JsonRespond<>(rights));
    }

    @Operation(summary = "Update Rights", description = "Change the collections set of right for given role id")
    @PostMapping(value = "/right/update")
    @PreAuthorize("hasPermission('null', '" + PermissionConstant.EDIT_USER + "')")
    public ResponseEntity<JsonRespond<Void>> changeRights(@RequestBody UpdateRightRequest request) throws Exception {
        mService.updateRights(request.id, request.rights);
//        mAuditLog.log(CatalogID.K_ID_ROLE_UPDATE, "Role Rights is updated");
        return ResponseEntity.ok(new JsonRespond<>(null));
    }

}
