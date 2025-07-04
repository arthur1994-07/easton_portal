package com.common.easton_portal.constants;

import com.common.core.web.permission.RightAnnotation;

public class PermissionConstant {
    @RightAnnotation(displayKey = "fullAccessPermission")
    public final static String FULL_ACCESS_PERMISSION = "full_access_permission_superuser";
    @RightAnnotation(displayKey = "editUser")
    public final static String EDIT_USER = "edit_user";
}