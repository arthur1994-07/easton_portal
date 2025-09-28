package com.common.easton_portal.constants;

import com.common.core.web.permission.RightAnnotation;

public class PermissionConstant {
    @RightAnnotation(displayKey = "User Management")
    public final static String EDIT_USER = "edit_user";
    @RightAnnotation(displayKey = "Audit Log Management")
    public final static String EDIT_AUDIT_LOG = "edit_audit_log";
    @RightAnnotation(displayKey = "Quotation Management")
    public final static String EDIT_QUOTATION = "edit_quotation";
    @RightAnnotation(displayKey = "Collection Management")
    public final static String EDIT_COLLECTION = "edit_collection";
    @RightAnnotation(displayKey = "System Configuration")
    public final static String EDIT_SYSTEM_CONFIG = "edit_system_config";
}