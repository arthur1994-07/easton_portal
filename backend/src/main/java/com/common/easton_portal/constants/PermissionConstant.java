package com.common.easton_portal.constants;

import com.common.core.web.permission.RightAnnotation;

public class PermissionConstant {
    @RightAnnotation(displayKey = "editUser")
    public final static String EDIT_USER = "edit_user";
    @RightAnnotation(displayKey = "editTrialUser")
    public final static String EDIT_TRIAL_USER = "edit_trial_user";
    @RightAnnotation(displayKey = "editAuditLog")
    public final static String EDIT_AUDIT_LOG = "edit_audit_log";
    @RightAnnotation(displayKey = "editCollection")
    public final static String EDIT_COLLECTION = "edit_collection";
    @RightAnnotation(displayKey = "editSystemConfig")
    public final static String EDIT_SYSTEM_CONFIG = "edit_system_config";
}